package com.example.pinam.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pinam.data.model.ChatConversation
import com.example.pinam.data.model.ChatMessage
import com.example.pinam.data.repository.ChatRepository
import com.example.pinam.data.repository.LocationHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ChatUiState(
    val conversations: List<ChatConversation> = emptyList(),
    val activeConversation: ChatConversation? = null,
    val messages: List<ChatMessage> = emptyList(),
    val messageText: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class ChatViewModel(
    private val repository: ChatRepository = ChatRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    fun loadConversations(userId: String) {
        _uiState.value = _uiState.value.copy(isLoading = true)
        viewModelScope.launch {
            repository.getConversations(userId).onSuccess { list ->
                _uiState.value = _uiState.value.copy(
                    conversations = list,
                    isLoading = false
                )
            }
        }
    }

    fun selectConversation(conversation: ChatConversation) {
        _uiState.value = _uiState.value.copy(activeConversation = conversation)
        loadMessages(conversation.id)
    }

    fun loadMessages(conversationId: String) {
        viewModelScope.launch {
            repository.getMessages(conversationId).onSuccess { msgList ->
                _uiState.value = _uiState.value.copy(messages = msgList)
            }
        }
    }

    fun onMessageTextChanged(value: String) {
        _uiState.value = _uiState.value.copy(messageText = value)
    }

    fun sendMessage(senderId: String, senderName: String) {
        val convId = _uiState.value.activeConversation?.id ?: return
        val text = _uiState.value.messageText.trim()
        if (text.isBlank()) return

        viewModelScope.launch {
            repository.sendMessage(convId, senderId, senderName, text).onSuccess {
                _uiState.value = _uiState.value.copy(messageText = "")
                loadMessages(convId)
            }
        }
    }

    fun shareLocation(context: Context, senderId: String, senderName: String) {
        val convId = _uiState.value.activeConversation?.id ?: return
        viewModelScope.launch {
            val helper = LocationHelper(context)
            val loc = helper.getCurrentLocation()
            val lat = loc?.latitude ?: 5.6037 // Default Accra location fallback
            val lng = loc?.longitude ?: -0.1870
            val placeName = if (loc != null) helper.getPlaceName(lat, lng) else "East Legon, Accra"

            repository.sendMessage(
                conversationId = convId,
                senderId = senderId,
                senderName = senderName,
                text = "📍 Live Location Shared",
                locationLat = lat,
                locationLng = lng,
                locationPlaceName = placeName
            ).onSuccess {
                loadMessages(convId)
            }
        }
    }

    fun startDirectChat(currentUserId: String, friendId: String, friendName: String, onChatOpened: () -> Unit) {
        viewModelScope.launch {
            repository.createOrGetDirectChat(currentUserId, friendId, friendName).onSuccess { conv ->
                selectConversation(conv)
                onChatOpened()
            }
        }
    }
}
