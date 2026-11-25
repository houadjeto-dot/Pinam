package com.group2.pinam.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.group2.pinam.data.ServiceLocator
import com.group2.pinam.domain.Recommendation
import com.group2.pinam.domain.buildRecommendations
import com.group2.pinam.model.Pin
import com.group2.pinam.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RecommendationsViewModel(private val circleId: String) : ViewModel() {

    var selectedTabIndex by mutableIntStateOf(0)
        private set

    var isLoading by mutableStateOf(true)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    val currentUser: StateFlow<User?> = ServiceLocator.auth.currentUser

    private val _pins = MutableStateFlow<List<Pin>>(emptyList())
    val pins: StateFlow<List<Pin>> = _pins.asStateFlow()

    val friendsPicks: StateFlow<List<Recommendation>> = combine(pins, currentUser) { pinList, user ->
        val userId = user?.id ?: ""
        buildRecommendations(pinList, userId)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val wishlist: StateFlow<List<Pin>> = combine(pins, currentUser) { pinList, user ->
        val userId = user?.id ?: ""
        pinList.filter { userId in it.wantToGo }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        loadPins()
    }

    fun selectTab(index: Int) {
        selectedTabIndex = index
    }

    private fun loadPins() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                ServiceLocator.pins.observePins(circleId)
                    .catch { e ->
                        errorMessage = e.message ?: "Failed to load pins"
                        isLoading = false
                    }
                    .collect { pinList ->
                        _pins.value = pinList
                        isLoading = false
                    }
            } catch (e: Exception) {
                errorMessage = e.message ?: "Failed to load pins"
                isLoading = false
            }
        }
    }

    fun onToggleWantToGo(pin: Pin) {
        val user = currentUser.value ?: return
        viewModelScope.launch {
            try {
                ServiceLocator.pins.toggleWantToGo(pin.circleId, pin.id, user.id)
            } catch (e: Exception) {
                errorMessage = e.message
            }
        }
    }

    fun onToggleWantToGoForRecommendation(rec: Recommendation) {
        val user = currentUser.value ?: return
        val matchingPin = _pins.value.firstOrNull { it.placeName.trim().equals(rec.placeName.trim(), ignoreCase = true) } ?: return
        viewModelScope.launch {
            try {
                ServiceLocator.pins.toggleWantToGo(matchingPin.circleId, matchingPin.id, user.id)
            } catch (e: Exception) {
                errorMessage = e.message
            }
        }
    }

    class Factory(private val circleId: String) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return RecommendationsViewModel(circleId) as T
        }
    }
}
