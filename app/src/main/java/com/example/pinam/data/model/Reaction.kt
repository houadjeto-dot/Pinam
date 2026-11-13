package com.example.pinam.data.model

data class Reaction(
    val id: String = "",
    val pinId: String = "",
    val userId: String = "",
    val type: String = "been_there" // "been_there" | "want_to_go"
)
