package com.example.pinam.data.repository

import com.example.pinam.data.model.ChatConversation
import com.example.pinam.data.model.ChatMessage
import java.util.UUID

class ChatRepository {
    private val conversations = mutableListOf<ChatConversation>()
    private val messages = mutableListOf<ChatMessage>()

    init {
        // Pre-populate sample group conversation
        val sampleConv = ChatConversation(
            id = "conv_demo_1",
            name = "Weekend Explorers Group ☕",
            isGroup = true,
            memberIds = listOf("demo_user_1", "demo_user_2", "demo_user_3"),
            lastMessageText = "Are we meeting at Cozy Corner Bakery today?",
            lastMessageTime = System.currentTimeMillis() - 3600000
        )
        conversations.add(sampleConv)

        messages.add(
            ChatMessage(
                id = "m1",
                conversationId = "conv_demo_1",
                senderId = "demo_user_2",
                senderName = "Sam",
                text = "Hey guys, checking out the new spot!",
                timestamp = System.currentTimeMillis() - 7200000
            )
        )
        messages.add(
            ChatMessage(
                id = "m2",
                conversationId = "conv_demo_1",
                senderId = "demo_user_3",
                senderName = "Taylor",
                text = "Are we meeting at Cozy Corner Bakery today?",
                locationLat = 37.7749,
                locationLng = -122.4194,
                locationPlaceName = "Cozy Corner Bakery",
                timestamp = System.currentTimeMillis() - 3600000
            )
        )
    }

    suspend fun getConversations(userId: String): Result<List<ChatConversation>> {
        val userConvs = conversations.filter { it.memberIds.contains(userId) || userId.isBlank() }
        return Result.success(userConvs)
    }

    suspend fun getMessages(conversationId: String): Result<List<ChatMessage>> {
        val convMessages = messages.filter { it.conversationId == conversationId }
            .sortedBy { it.timestamp }
        return Result.success(convMessages)
    }

    suspend fun sendMessage(
        conversationId: String,
        senderId: String,
        senderName: String,
        text: String,
        locationLat: Double? = null,
        locationLng: Double? = null,
        locationPlaceName: String = ""
    ): Result<ChatMessage> {
        val msg = ChatMessage(
            id = "msg_${UUID.randomUUID().toString().take(8)}",
            conversationId = conversationId,
            senderId = senderId,
            senderName = senderName,
            text = text,
            locationLat = locationLat,
            locationLng = locationLng,
            locationPlaceName = locationPlaceName,
            timestamp = System.currentTimeMillis()
        )
        messages.add(msg)

        val convIndex = conversations.indexOfFirst { it.id == conversationId }
        if (convIndex != -1) {
            val conv = conversations[convIndex]
            conversations[convIndex] = conv.copy(
                lastMessageText = if (text.isNotBlank()) text else "Shared location: $locationPlaceName",
                lastMessageTime = msg.timestamp
            )
        }
        return Result.success(msg)
    }

    suspend fun createOrGetDirectChat(currentUserId: String, friendId: String, friendName: String): Result<ChatConversation> {
        val existing = conversations.find { !it.isGroup && it.memberIds.containsAll(listOf(currentUserId, friendId)) }
        if (existing != null) return Result.success(existing)

        val newConv = ChatConversation(
            id = "conv_${UUID.randomUUID().toString().take(8)}",
            name = friendName,
            isGroup = false,
            memberIds = listOf(currentUserId, friendId),
            lastMessageText = "Started a conversation",
            lastMessageTime = System.currentTimeMillis()
        )
        conversations.add(0, newConv)
        return Result.success(newConv)
    }
}
