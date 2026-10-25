package com.example.moddyplayer.data.db.network

data class NetworkTrackDto(
    val id: String,
    val title: String,
    val artist: String,
    val audioUrl: String,
    val coverUrl: String?
)