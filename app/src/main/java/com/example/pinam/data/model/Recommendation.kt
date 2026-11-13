package com.example.pinam.data.model

data class Recommendation(
    val placeName: String = "",
    val lat: Double = 0.0,
    val lng: Double = 0.0,
    val photoUrl: String = "",
    val vibeTag: String = "",
    val friendCount: Int = 0,
    val score: Double = 0.0,
    val descriptionText: String = "",
    val samplePinId: String = ""
)
