package com.example.pinam.data.repository

import android.content.Context
import android.graphics.Bitmap
import com.example.pinam.data.model.Pin
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

class PinRepository {

    suspend fun uploadPhoto(bitmap: Bitmap, context: Context? = null): String {
        return try {
            if (context != null) {
                val file = File.createTempFile("pin_${UUID.randomUUID().toString().take(6)}_", ".jpg", context.cacheDir)
                FileOutputStream(file).use { out ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 85, out)
                }
                file.toURI().toString()
            } else {
                "https://picsum.photos/seed/${UUID.randomUUID()}/800/600"
            }
        } catch (e: Exception) {
            "https://picsum.photos/seed/${UUID.randomUUID()}/800/600"
        }
    }

    suspend fun createPin(
        circleId: String,
        authorId: String,
        authorName: String,
        photoUrl: String,
        lat: Double,
        lng: Double,
        placeName: String,
        caption: String,
        vibeTags: List<String>,
        landmarkNote: String = "",
        ghanaPostAddress: String = "",
        transportTip: String = "",
        budgetRange: String = ""
    ): Result<Pin> {
        return try {
            val pinId = "pin_${UUID.randomUUID().toString().take(8)}"
            val primaryTag = vibeTags.firstOrNull() ?: ""
            val pin = Pin(
                id = pinId,
                circleId = circleId,
                authorId = authorId,
                authorName = authorName,
                photoUrl = photoUrl,
                lat = lat,
                lng = lng,
                placeName = placeName,
                caption = caption,
                vibeTag = primaryTag,
                vibeTags = vibeTags,
                landmarkNote = landmarkNote,
                ghanaPostAddress = ghanaPostAddress,
                transportTip = transportTip,
                budgetRange = budgetRange,
                createdAt = System.currentTimeMillis()
            )
            InMemoryStore.pins.add(0, pin)
            LocalStoreManager.save()
            Result.success(pin)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getPinsForCircle(circleId: String): Result<List<Pin>> {
        return try {
            val circlePins = InMemoryStore.pins.filter { it.circleId == circleId }
                .sortedByDescending { it.createdAt }
            Result.success(circlePins)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getPinById(pinId: String): Result<Pin?> {
        return try {
            val pin = InMemoryStore.pins.find { it.id == pinId }
            Result.success(pin)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updatePin(
        pinId: String,
        caption: String,
        vibeTags: List<String>,
        landmarkNote: String = "",
        ghanaPostAddress: String = "",
        transportTip: String = "",
        budgetRange: String = ""
    ): Result<Unit> {
        return try {
            val index = InMemoryStore.pins.indexOfFirst { it.id == pinId }
            if (index != -1) {
                val pin = InMemoryStore.pins[index]
                val primaryTag = vibeTags.firstOrNull() ?: pin.vibeTag
                InMemoryStore.pins[index] = pin.copy(
                    caption = caption,
                    vibeTag = primaryTag,
                    vibeTags = vibeTags,
                    landmarkNote = landmarkNote,
                    ghanaPostAddress = ghanaPostAddress,
                    transportTip = transportTip,
                    budgetRange = budgetRange
                )
            }
            LocalStoreManager.save()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deletePin(pinId: String): Result<Unit> {
        return try {
            InMemoryStore.pins.removeAll { it.id == pinId }
            LocalStoreManager.save()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
