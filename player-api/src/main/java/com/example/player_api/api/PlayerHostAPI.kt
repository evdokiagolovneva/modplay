package com.example.player_api.api

import androidx.media3.common.MediaItem
import com.example.player_api.engine.Track
import com.example.player_api.api.MetadataProvider

interface PlayerHostAPI {

    fun getCurrentTrack(): com.example.player_api.engine.Track?

    fun playTrack(trackId: String)
    {}
    fun next()
    {}
    fun pause()
    {}
    fun random()
    {}
    fun registerAudioEffect(effect: AudioEffect)

    // Вопросы к нему в MetadataProvider
    fun registerMetadataProvider(provider: MetadataProvider)

    fun registerUiSlot(slot: UiSlot)

    fun unregisterAudioEffect(effect: AudioEffect)

    fun unregisterMetadataProvider(provider: MetadataProvider)

    fun unregisterUiSlot(slot: UiSlot)


    suspend fun fetchUrl(url: String): String?


    fun readMusicFile()


}