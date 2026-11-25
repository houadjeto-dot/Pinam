package com.group2.pinam.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.group2.pinam.data.ServiceLocator
import com.group2.pinam.model.Circle
import com.group2.pinam.model.User
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

data class CircleWithPinCount(
    val circle: Circle,
    val pinCount: Int
)

class CirclesViewModel : ViewModel() {

    private val authRepository = ServiceLocator.auth
    private val circleRepository = ServiceLocator.circles
    private val pinRepository = ServiceLocator.pins

    private val _circles = MutableStateFlow<List<CircleWithPinCount>>(emptyList())
    val circles: StateFlow<List<CircleWithPinCount>> = _circles.asStateFlow()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _createdInviteCode = MutableStateFlow<String?>(null)
    val createdInviteCode: StateFlow<String?> = _createdInviteCode.asStateFlow()

    init {
        observeCurrentUser()
    }

    private fun observeCurrentUser() {
        viewModelScope.launch {
            authRepository.currentUser.collect { user ->
                _currentUser.value = user

                if (user != null) {
                    observeCircles(user.id)
                } else {
                    _circles.value = emptyList()
                }
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeCircles(userId: String) {
        viewModelScope.launch {
            circleRepository.observeMyCircles(userId)
                .flatMapLatest { circleList ->

                    if (circleList.isEmpty()) {
                        flowOf(emptyList())
                    } else {
                        val pinFlows = circleList.map { circle ->
                            pinRepository.observePins(circle.id)
                        }

                        combine(pinFlows) { pinLists ->
                            circleList.mapIndexed { index, circle ->
                                CircleWithPinCount(
                                    circle = circle,
                                    pinCount = pinLists[index].size
                                )
                            }
                        }
                    }
                }
                .collect { result ->
                    _circles.value = result
                }
        }
    }

    fun createCircle(name: String) {
        val user = _currentUser.value

        if (user == null) {
            _errorMessage.value = "You must be logged in to create a circle."
            return
        }

        if (name.isBlank()) {
            _errorMessage.value = "Please enter a circle name."
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            circleRepository.createCircle(name.trim(), user)
                .onSuccess { circle ->
                    _createdInviteCode.value = circle.inviteCode
                }
                .onFailure { error ->
                    _errorMessage.value =
                        error.message ?: "Unable to create circle."
                }

            _isLoading.value = false
        }
    }

    fun joinCircle(code: String) {
        val user = _currentUser.value

        if (user == null) {
            _errorMessage.value = "You must be logged in to join a circle."
            return
        }

        if (code.isBlank()) {
            _errorMessage.value = "Please enter an invitation code."
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            circleRepository.joinByCode(code.trim(), user)
                .onFailure { error ->
                    _errorMessage.value =
                        error.message ?: "Unable to join circle."
                }

            _isLoading.value = false
        }
    }

    fun leaveCircle(circleId: String) {
        val user = _currentUser.value ?: return

        viewModelScope.launch {
            circleRepository.removeMember(circleId, user.id)
                .onFailure { error ->
                    _errorMessage.value =
                        error.message ?: "Unable to leave circle."
                }
        }
    }

    fun removeMember(circleId: String, memberId: String) {
        viewModelScope.launch {
            circleRepository.removeMember(circleId, memberId)
                .onFailure { error ->
                    _errorMessage.value =
                        error.message ?: "Unable to remove member."
                }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun clearInviteCode() {
        _createdInviteCode.value = null
    }
}