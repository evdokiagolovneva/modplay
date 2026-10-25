package com.example.moddyplayer.player
import com.example.moddyplayer.data.db.dataa.Track

data class PlayerState(
    val currentTrack: Track? = null,
    val isPlaying: Boolean = false,
    val positionMs: Long = 0L,
    val durationMs: Long = 0L
)