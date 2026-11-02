package com.example.player_api.api

import androidx.annotation.OptIn
import androidx.compose.runtime.Composable
import androidx.media3.common.audio.AudioProcessor
import androidx.media3.common.util.UnstableApi
import okhttp3.OkHttpClient

interface PlayerHostContext {
    val okHttpClient: OkHttpClient

    val playerControl: PlayerHostAPI

    fun registerUiSlot(slotId: String, content: @Composable () -> Unit)

    @OptIn(UnstableApi::class)
    fun registerAudioProcessor(processor: AudioProcessor)
}