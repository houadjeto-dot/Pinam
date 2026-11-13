package com.example.pinam.data.model

data class ChatMessage(
    val id: String = "",
    val conversationId: String = "",
    val senderId: String = "",
    val senderName: String = "",
    val text: String = "",
    val locationLat: Double? = null,
    val locationLng: Double? = null,
    val locationPlaceName: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
