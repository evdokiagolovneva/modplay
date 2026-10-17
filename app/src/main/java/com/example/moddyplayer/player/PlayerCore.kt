package com.example.moddyplayer.player

import androidx.annotation.OptIn
import androidx.media3.common.audio.AudioProcessor
import androidx.media3.common.util.UnstableApi
import com.example.moddyplayer.data.db.Track
import kotlinx.coroutines.flow.StateFlow

interface PlayerCore {
    fun play(track: Track)
    fun playQueue(tracks: List<Track>, startIndex: Int = 0)
    fun pause()
    fun resume()
    fun skipToNext()
    fun skipToPrevious()
    fun seekTo(ms: Long)
    fun setPlaybackSpeed(speed: Float)
    fun toggleShuffle(): Boolean
    fun toggleRepeatMode(): RepeatMode

    val state: StateFlow<PlayerState>

    @OptIn(UnstableApi::class)
    fun registerAudioProcessor(processor: AudioProcessor)
}

enum class RepeatMode {
    OFF, ALL, ONE
}