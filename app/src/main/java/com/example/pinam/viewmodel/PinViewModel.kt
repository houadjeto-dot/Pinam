package com.example.pinam.viewmodel

import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pinam.data.model.Pin
import com.example.pinam.data.repository.LocationHelper
import com.example.pinam.data.repository.PinRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class DropPinState(
    val photoBitmap: Bitmap? = null,
    val lat: Double? = null,
    val lng: Double? = null,
    val placeName: String = "",
    val caption: String = "",
    val vibeTags: List<String> = emptyList(),
    val customVibeTag: String = "",
    val landmarkNote: String = "",
    val ghanaPostAddress: String = "",
    val transportTip: String = "",
    val budgetRange: String = "", // "UNDER_20" | "FROM_20_TO_50" | "OVER_50"
    val isFetchingLocation: Boolean = false,
    val isUploading: Boolean = false,
    val errorMessage: String? = null
)

data class CirclePinsState(
    val pins: List<Pin> = emptyList(),
    val filteredPins: List<Pin> = emptyList(),
    val selectedFilterTag: String? = null,
    val selectedFilterBudget: String? = null,
    val selectedPin: Pin? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class PinViewModel(
    private val pinRepository: PinRepository = PinRepository()
) : ViewModel() {

    private val _dropPinState = MutableStateFlow(DropPinState())
    val dropPinState: StateFlow<DropPinState> = _dropPinState.asStateFlow()

    private val _pinsState = MutableStateFlow(CirclePinsState())
    val pinsState: StateFlow<CirclePinsState> = _pinsState.asStateFlow()

    val presetVibeTags = listOf(
        "🍲 Chop bar",
        "🍛 Waakye spot",
        "🫓 Banku joint",
        "🍗 Cheap chop",
        "📚 Study spot",
        "📶 Good wifi",
        "📱 MoMo accepted",
        "💡 Has light",
        "🚐 Trotro accessible",
        "🍹 Chill spot",
        "⛪ Church-friend hangout",
        "❤️ Date spot",
        "🛋️ Cozy"
    )

    private val ghanaPostRegex = Regex("^[A-Z]{2}-\\d{3,4}-\\d{4}$")

    fun onPhotoCaptured(bitmap: Bitmap) {
        _dropPinState.value = _dropPinState.value.copy(photoBitmap = bitmap, errorMessage = null)
    }

    fun fetchCurrentLocation(context: Context) {
        _dropPinState.value = _dropPinState.value.copy(isFetchingLocation = true, errorMessage = null)
        viewModelScope.launch {
            val helper = LocationHelper(context)
            val location = helper.getCurrentLocation()
            if (location != null) {
                val placeName = helper.getPlaceName(location.latitude, location.longitude)
                _dropPinState.value = _dropPinState.value.copy(
                    lat = location.latitude,
                    lng = location.longitude,
                    placeName = placeName,
                    isFetchingLocation = false
                )
            } else {
                _dropPinState.value = _dropPinState.value.copy(
                    lat = 5.6037, // Accra, Ghana default coordinates
                    lng = -0.1870,
                    placeName = "East Legon, Accra",
                    isFetchingLocation = false
                )
            }
        }
    }

    fun onCaptionChanged(value: String) {
        _dropPinState.value = _dropPinState.value.copy(caption = value)
    }

    fun toggleVibeTag(tag: String) {
        val current = _dropPinState.value.vibeTags.toMutableList()
        if (current.contains(tag)) {
            current.remove(tag)
        } else {
            if (current.size < 3) { // Max 3 vibe tags per pin
                current.add(tag)
            }
        }
        _dropPinState.value = _dropPinState.value.copy(vibeTags = current)
    }

    fun onCustomVibeTagChanged(value: String) {
        _dropPinState.value = _dropPinState.value.copy(customVibeTag = value)
    }

    fun addCustomVibeTag() {
        val tag = _dropPinState.value.customVibeTag.trim()
        if (tag.isNotBlank()) {
            val formattedTag = if (tag.startsWith("✨")) tag else "✨ $tag"
            toggleVibeTag(formattedTag)
            _dropPinState.value = _dropPinState.value.copy(customVibeTag = "")
        }
    }

    fun onLandmarkNoteChanged(value: String) {
        if (value.length <= 120) { // Max 120 chars
            _dropPinState.value = _dropPinState.value.copy(landmarkNote = value)
        }
    }

    fun onGhanaPostAddressChanged(value: String) {
        val uppercaseVal = value.uppercase().trim()
        _dropPinState.value = _dropPinState.value.copy(ghanaPostAddress = uppercaseVal)
    }

    fun onTransportTipChanged(value: String) {
        if (value.length <= 80) { // Max 80 chars
            _dropPinState.value = _dropPinState.value.copy(transportTip = value)
        }
    }

    fun selectBudgetRange(range: String) {
        val newRange = if (_dropPinState.value.budgetRange == range) "" else range
        _dropPinState.value = _dropPinState.value.copy(budgetRange = newRange)
    }

    fun dropPin(
        circleId: String,
        authorId: String,
        authorName: String,
        context: Context? = null,
        onSuccess: () -> Unit
    ) {
        val state = _dropPinState.value
        val bitmap = state.photoBitmap
        if (bitmap == null) {
            _dropPinState.value = state.copy(errorMessage = "Please add a photo first")
            return
        }

        if (state.lat == null || state.lng == null) {
            _dropPinState.value = state.copy(errorMessage = "Location is required to drop a pin")
            return
        }

        // Validate Ghana Post Address if provided
        val postAddress = state.ghanaPostAddress
        if (postAddress.isNotBlank() && !ghanaPostRegex.matches(postAddress)) {
            _dropPinState.value = state.copy(errorMessage = "Invalid Ghana Post format (e.g. GA-123-4567)")
            return
        }

        if (state.vibeTags.isEmpty()) {
            _dropPinState.value = state.copy(errorMessage = "Please select at least 1 vibe tag (max 3)")
            return
        }

        _dropPinState.value = state.copy(isUploading = true, errorMessage = null)
        viewModelScope.launch {
            val photoUrl = pinRepository.uploadPhoto(bitmap, context)
            val result = pinRepository.createPin(
                circleId = circleId,
                authorId = authorId,
                authorName = authorName,
                photoUrl = photoUrl,
                lat = state.lat,
                lng = state.lng,
                placeName = state.placeName.ifBlank { "Accra Location" },
                caption = state.caption,
                vibeTags = state.vibeTags,
                landmarkNote = state.landmarkNote,
                ghanaPostAddress = postAddress,
                transportTip = state.transportTip,
                budgetRange = state.budgetRange
            )

            result.onSuccess {
                _dropPinState.value = DropPinState()
                onSuccess()
            }.onFailure { error ->
                _dropPinState.value = _dropPinState.value.copy(
                    isUploading = false,
                    errorMessage = error.localizedMessage ?: "Failed to drop pin"
                )
            }
        }
    }

    fun loadPinsForCircle(circleId: String) {
        _pinsState.value = _pinsState.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            pinRepository.getPinsForCircle(circleId).onSuccess { list ->
                _pinsState.value = _pinsState.value.copy(
                    pins = list,
                    filteredPins = applyFilters(list, _pinsState.value.selectedFilterTag, _pinsState.value.selectedFilterBudget),
                    isLoading = false
                )
            }.onFailure { error ->
                _pinsState.value = _pinsState.value.copy(
                    isLoading = false,
                    errorMessage = error.localizedMessage ?: "Failed to load pins"
                )
            }
        }
    }

    fun setTagFilter(tag: String?) {
        val newTag = if (_pinsState.value.selectedFilterTag == tag) null else tag
        val currentPins = _pinsState.value.pins
        _pinsState.value = _pinsState.value.copy(
            selectedFilterTag = newTag,
            filteredPins = applyFilters(currentPins, newTag, _pinsState.value.selectedFilterBudget)
        )
    }

    fun setBudgetFilter(budget: String?) {
        val newBudget = if (_pinsState.value.selectedFilterBudget == budget) null else budget
        val currentPins = _pinsState.value.pins
        _pinsState.value = _pinsState.value.copy(
            selectedFilterBudget = newBudget,
            filteredPins = applyFilters(currentPins, _pinsState.value.selectedFilterTag, newBudget)
        )
    }

    private fun applyFilters(list: List<Pin>, tag: String?, budget: String?): List<Pin> {
        return list.filter { pin ->
            val matchesTag = tag == null || pin.displayVibeTags.any { it.contains(tag, ignoreCase = true) }
            val matchesBudget = budget == null || pin.budgetRange == budget
            matchesTag && matchesBudget
        }
    }

    fun selectPin(pin: Pin) {
        _pinsState.value = _pinsState.value.copy(selectedPin = pin)
    }

    fun updatePin(
        pinId: String,
        caption: String,
        vibeTags: List<String>,
        landmarkNote: String,
        ghanaPostAddress: String,
        transportTip: String,
        budgetRange: String,
        circleId: String
    ) {
        viewModelScope.launch {
            pinRepository.updatePin(
                pinId = pinId,
                caption = caption,
                vibeTags = vibeTags,
                landmarkNote = landmarkNote,
                ghanaPostAddress = ghanaPostAddress,
                transportTip = transportTip,
                budgetRange = budgetRange
            ).onSuccess {
                loadPinsForCircle(circleId)
            }
        }
    }

    fun deletePin(pinId: String, circleId: String, onDeleteSuccess: () -> Unit) {
        viewModelScope.launch {
            pinRepository.deletePin(pinId).onSuccess {
                loadPinsForCircle(circleId)
                onDeleteSuccess()
            }
        }
    }
}
