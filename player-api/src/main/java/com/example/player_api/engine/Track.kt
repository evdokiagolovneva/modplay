package com.example.player_api.engine

import android.net.Uri

data class Track(
    val id: String,
    val title: String,
    val artist: String,
    val album: String?,
    val durationMs: Long,
    val mediaUri: Uri?
)
