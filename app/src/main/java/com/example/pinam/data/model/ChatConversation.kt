package com.example.pinam.data.model

data class ChatConversation(
    val id: String = "",
    val name: String = "",
    val isGroup: Boolean = false,
    val memberIds: List<String> = emptyList(),
    val lastMessageText: String = "",
    val lastMessageTime: Long = System.currentTimeMillis()
)
