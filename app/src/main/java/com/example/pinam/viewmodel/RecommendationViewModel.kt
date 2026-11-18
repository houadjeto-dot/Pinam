package com.example.pinam.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pinam.data.model.Pin
import com.example.pinam.data.model.Recommendation
import com.example.pinam.data.repository.CircleRepository
import com.example.pinam.data.repository.PinRepository
import com.example.pinam.data.repository.ReactionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class RecommendationUiState(
    val recommendations: List<Recommendation> = emptyList(),
    val filteredRecommendations: List<Recommendation> = emptyList(),
    val isStudyModeFilter: Boolean = false,
    val selectedBudgetFilter: String? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class RecommendationViewModel(
    private val circleRepository: CircleRepository = CircleRepository(),
    private val pinRepository: PinRepository = PinRepository(),
    private val reactionRepository: ReactionRepository = ReactionRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecommendationUiState())
    val uiState: StateFlow<RecommendationUiState> = _uiState.asStateFlow()

    fun toggleStudyModeFilter() {
        val newMode = !_uiState.value.isStudyModeFilter
        _uiState.value = _uiState.value.copy(
            isStudyModeFilter = newMode,
            filteredRecommendations = applyFilters(_uiState.value.recommendations, newMode, _uiState.value.selectedBudgetFilter)
        )
    }

    fun setBudgetFilter(budget: String?) {
        val newBudget = if (_uiState.value.selectedBudgetFilter == budget) null else budget
        _uiState.value = _uiState.value.copy(
            selectedBudgetFilter = newBudget,
            filteredRecommendations = applyFilters(_uiState.value.recommendations, _uiState.value.isStudyModeFilter, newBudget)
        )
    }

    private fun applyFilters(list: List<Recommendation>, studyMode: Boolean, budget: String?): List<Recommendation> {
        var filtered = list
        if (studyMode) {
            filtered = filtered.filter { rec ->
                rec.vibeTag.contains("Study", ignoreCase = true) ||
                rec.vibeTag.contains("wifi", ignoreCase = true) ||
                rec.vibeTag.contains("light", ignoreCase = true)
            }
        }
        return filtered
    }

    fun loadRecommendations(userId: String) {
        if (userId.isBlank()) return
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

        viewModelScope.launch {
            try {
                val circlesResult = circleRepository.getUserCircles(userId)
                val circles = circlesResult.getOrDefault(emptyList())

                val friendWeights = mutableMapOf<String, Double>()
                for (c in circles) {
                    for (memberId in c.memberIds) {
                        if (memberId != userId) {
                            friendWeights[memberId] = (friendWeights[memberId] ?: 0.0) + 1.0
                        }
                    }
                }

                val allPins = mutableListOf<Pin>()
                for (c in circles) {
                    val pinsResult = pinRepository.getPinsForCircle(c.id)
                    pinsResult.getOrNull()?.let { allPins.addAll(it) }
                }

                if (allPins.isEmpty()) {
                    _uiState.value = _uiState.value.copy(
                        recommendations = emptyList(),
                        filteredRecommendations = emptyList(),
                        isLoading = false
                    )
                    return@launch
                }

                val pinIds = allPins.map { it.id }
                val reactions = reactionRepository.getReactionsForPins(pinIds).getOrDefault(emptyList())

                val placeGroups = allPins.groupBy { it.placeName.trim().lowercase() }
                val recList = mutableListOf<Recommendation>()

                for ((_, pinsAtPlace) in placeGroups) {
                    val samplePin = pinsAtPlace.first()

                    val interactingFriends = mutableSetOf<String>()
                    var placeScore = 0.0

                    for (p in pinsAtPlace) {
                        if (p.authorId != userId && friendWeights.containsKey(p.authorId)) {
                            interactingFriends.add(p.authorId)
                        }
                    }

                    for (p in pinsAtPlace) {
                        val pinReactions = reactions.filter { it.pinId == p.id }
                        for (r in pinReactions) {
                            if (r.userId != userId && friendWeights.containsKey(r.userId)) {
                                interactingFriends.add(r.userId)
                            }
                        }
                    }

                    for (fId in interactingFriends) {
                        placeScore += friendWeights[fId] ?: 1.0
                    }

                    // Boost places tagged "Good wifi" or "Has light"
                    val tagsJoined = samplePin.displayVibeTags.joinToString(" ")
                    if (tagsJoined.contains("wifi", ignoreCase = true) || tagsJoined.contains("light", ignoreCase = true)) {
                        placeScore += 2.0
                    }

                    val friendCount = interactingFriends.size
                    val desc = when {
                        friendCount == 1 -> "1 of your friends loved this place"
                        friendCount > 1 -> "$friendCount of your friends loved this place"
                        else -> "Popular in your circles 🇬🇭"
                    }

                    recList.add(
                        Recommendation(
                            placeName = samplePin.placeName,
                            lat = samplePin.lat,
                            lng = samplePin.lng,
                            photoUrl = samplePin.photoUrl,
                            vibeTag = samplePin.displayVibeTags.joinToString(", "),
                            friendCount = friendCount,
                            score = placeScore,
                            descriptionText = desc,
                            samplePinId = samplePin.id
                        )
                    )
                }

                val sortedRecs = recList.sortedByDescending { it.score * 10 + it.friendCount }

                _uiState.value = _uiState.value.copy(
                    recommendations = sortedRecs,
                    filteredRecommendations = applyFilters(sortedRecs, _uiState.value.isStudyModeFilter, _uiState.value.selectedBudgetFilter),
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.localizedMessage ?: "Failed to load recommendations"
                )
            }
        }
    }
}
