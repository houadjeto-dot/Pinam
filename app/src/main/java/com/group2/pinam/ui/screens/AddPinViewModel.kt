package com.group2.pinam.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.group2.pinam.data.ServiceLocator
import com.group2.pinam.model.Pin
import com.group2.pinam.model.Vibe
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AddPinViewModel : ViewModel() {

    private val _placeName = MutableStateFlow("")
    val placeName: StateFlow<String> = _placeName.asStateFlow()

    private val _caption = MutableStateFlow("")
    val caption: StateFlow<String> = _caption.asStateFlow()

    private val _selectedVibe = MutableStateFlow(Vibe.COZY)
    val selectedVibe: StateFlow<Vibe> = _selectedVibe.asStateFlow()

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun onPlaceNameChange(value: String) {
        _placeName.value = value
        _errorMessage.value = null
    }

    fun onCaptionChange(value: String) {
        _caption.value = value
    }

    fun onVibeChange(value: Vibe) {
        _selectedVibe.value = value
    }

    fun savePin(
        circleId: String,
        photoUri: String?,
        onDone: () -> Unit
    ) {
        if (_placeName.value.isBlank()) {
            _errorMessage.value = "Please enter the place name."
            return
        }

        val user = ServiceLocator.auth.currentUser.value

        if (user == null) {
            _errorMessage.value = "Please sign in before adding a pin."
            return
        }

        viewModelScope.launch {
            _isSaving.value = true
            _errorMessage.value = null

            val pin = Pin(
                circleId = circleId,
                authorId = user.id,
                authorName = user.name,
                placeName = _placeName.value.trim(),
                vibe = _selectedVibe.value.label,
                caption = _caption.value.trim(),
                photoUrl = "",
                lat = 0.0,
                lng = 0.0
            )

            val result = ServiceLocator.pins.addPin(pin, photoUri)

            result.fold(
                onSuccess = {
                    _isSaving.value = false
                    onDone()
                },
                onFailure = { error ->
                    _errorMessage.value =
                        error.message ?: "Could not save your pin."
                    _isSaving.value = false
                }
            )
        }
    }
}