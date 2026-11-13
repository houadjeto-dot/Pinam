package com.example.pinam.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pinam.data.model.Circle
import com.example.pinam.data.model.User
import com.example.pinam.data.repository.CircleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class CircleUiState(
    val circles: List<Circle> = emptyList(),
    val activeCircle: Circle? = null,
    val members: List<User> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val showCreateDialog: Boolean = false,
    val showJoinDialog: Boolean = false
)

class CircleViewModel(
    private val repository: CircleRepository = CircleRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(CircleUiState())
    val uiState: StateFlow<CircleUiState> = _uiState.asStateFlow()

    val campusSuggestions = listOf(
        "Academic City University",
        "University of Ghana (Legon)",
        "KNUST (Kumasi)",
        "UCC (Cape Coast)",
        "Ashesi University",
        "GIMPA",
        "UPSA"
    )

    fun loadCircles(userId: String) {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            repository.getUserCircles(userId).onSuccess { list ->
                val active = _uiState.value.activeCircle ?: list.firstOrNull()
                _uiState.value = _uiState.value.copy(
                    circles = list,
                    activeCircle = active,
                    isLoading = false
                )
                active?.let { loadCircleMembers(it.memberIds) }
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = error.localizedMessage ?: "Failed to load circles"
                )
            }
        }
    }

    fun selectCircle(circle: Circle) {
        _uiState.value = _uiState.value.copy(activeCircle = circle)
        loadCircleMembers(circle.memberIds)
    }

    fun loadCircleMembers(memberIds: List<String>) {
        viewModelScope.launch {
            repository.getCircleMembers(memberIds).onSuccess { memberList ->
                _uiState.value = _uiState.value.copy(members = memberList)
            }
        }
    }

    fun toggleCreateDialog(show: Boolean) {
        _uiState.value = _uiState.value.copy(showCreateDialog = show, errorMessage = null)
    }

    fun toggleJoinDialog(show: Boolean) {
        _uiState.value = _uiState.value.copy(showJoinDialog = show, errorMessage = null)
    }

    fun createCircle(
        name: String,
        description: String,
        type: String,
        campusName: String,
        userId: String
    ) {
        if (name.isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Circle name cannot be empty")
            return
        }
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            repository.createCircle(name, description, type, campusName, userId).onSuccess { newCircle ->
                toggleCreateDialog(false)
                loadCircles(userId)
                selectCircle(newCircle)
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = error.localizedMessage ?: "Failed to create circle"
                )
            }
        }
    }

    fun joinCircle(inviteCode: String, userId: String) {
        if (inviteCode.isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Invite code is required")
            return
        }
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            repository.joinCircleByCode(inviteCode, userId).onSuccess { joinedCircle ->
                toggleJoinDialog(false)
                loadCircles(userId)
                selectCircle(joinedCircle)
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = error.localizedMessage ?: "Failed to join circle. Check invite code."
                )
            }
        }
    }

    fun updateCircle(circleId: String, name: String, description: String, userId: String) {
        viewModelScope.launch {
            repository.updateCircle(circleId, name, description).onSuccess {
                loadCircles(userId)
            }
        }
    }

    fun removeMember(circleId: String, memberId: String, userId: String) {
        viewModelScope.launch {
            repository.removeMember(circleId, memberId).onSuccess {
                loadCircles(userId)
            }
        }
    }

    fun leaveCircle(circleId: String, userId: String, onLeft: () -> Unit) {
        viewModelScope.launch {
            repository.leaveCircle(circleId, userId).onSuccess {
                _uiState.value = _uiState.value.copy(activeCircle = null)
                loadCircles(userId)
                onLeft()
            }
        }
    }
}
