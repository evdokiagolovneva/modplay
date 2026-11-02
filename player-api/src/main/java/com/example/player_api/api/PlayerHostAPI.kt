package com.example.player_api.api

import androidx.media3.common.MediaItem
import com.example.player_api.engine.Track
import com.example.player_api.api.MetadataProvider


interface PlayerHostAPI {

    // Пока не подрублен Media3, а так будет работать с MediaMetadata
    // И с более широким классом player
    // Вообще почему ты писала именно GetCurrentTrack?
    fun getCurrentTrack(): com.example.player_api.engine.Track?

    fun playTrack(trackId: String)
    fun next()
    fun pause()
    fun random()
    fun registerAudioEffect(effect: AudioEffect)
    fun registerMediaDataProvider(provider: MetadataProvider)
    fun registerUiSlot(slot: UiSlot)

    fun unregisterAudioEffect(effect: AudioEffect)
    fun unregisterMediaDataProvider(provider: MetadataProvider)
    fun unregisterUiSlot(slot: UiSlot)

    suspend fun fetchUrl(url: String): String?
    fun readMusicFile()

}