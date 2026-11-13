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

    fun loadRecommendations(userId: String) {
        if (userId.isBlank()) return
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

        viewModelScope.launch {
            try {
                // 1. Fetch user's circles & calculate friend connection weights (shared circle count)
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

                // 2. Fetch pins across all user's circles
                val allPins = mutableListOf<Pin>()
                for (c in circles) {
                    val pinsResult = pinRepository.getPinsForCircle(c.id)
                    pinsResult.getOrNull()?.let { allPins.addAll(it) }
                }

                if (allPins.isEmpty()) {
                    _uiState.value = _uiState.value.copy(recommendations = emptyList(), isLoading = false)
                    return@launch
                }

                // 3. Fetch reactions for these pins
                val pinIds = allPins.map { it.id }
                val reactions = reactionRepository.getReactionsForPins(pinIds).getOrDefault(emptyList())

                // 4. Group pins by normalized place name
                val placeGroups = allPins.groupBy { it.placeName.trim().lowercase() }
                val recList = mutableListOf<Recommendation>()

                for ((_, pinsAtPlace) in placeGroups) {
                    val placeName = pinsAtPlace.first().placeName
                    val samplePin = pinsAtPlace.first()

                    // Collect all unique friends who interacted (dropped pin or reacted)
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

                    // Weight calculation based on friend connection closeness
                    for (fId in interactingFriends) {
                        placeScore += friendWeights[fId] ?: 1.0
                    }

                    val friendCount = interactingFriends.size
                    val desc = when {
                        friendCount == 1 -> "1 of your friends loved this place"
                        friendCount > 1 -> "$friendCount of your friends loved this place"
                        else -> "Popular in your circles"
                    }

                    recList.add(
                        Recommendation(
                            placeName = placeName,
                            lat = samplePin.lat,
                            lng = samplePin.lng,
                            photoUrl = samplePin.photoUrl,
                            vibeTag = samplePin.vibeTag,
                            friendCount = friendCount,
                            score = placeScore,
                            descriptionText = desc,
                            samplePinId = samplePin.id
                        )
                    )
                }

                // Rank recommendations by score and friend count
                val sortedRecs = recList.sortedByDescending { it.score * 10 + it.friendCount }

                _uiState.value = _uiState.value.copy(
                    recommendations = sortedRecs,
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
