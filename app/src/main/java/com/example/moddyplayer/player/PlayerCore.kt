package com.example.moddyplayer.player

import androidx.annotation.OptIn
import androidx.media3.common.audio.AudioProcessor
import androidx.media3.common.util.UnstableApi
import com.example.moddyplayer.data.db.dataa.Track
import kotlinx.coroutines.flow.StateFlow

interface PlayerCore {
    fun play(track: Track)
    fun pause()
    fun seekTo(ms: Long)
    val state: StateFlow<PlayerState>

    @OptIn(UnstableApi::class)
    fun registerAudioProcessor(processor: AudioProcessor)
}