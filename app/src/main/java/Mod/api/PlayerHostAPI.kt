package Mod.api

import androidx.media3.common.MediaItem
import Mod.engine.Track
import Mod.api.MetadataProvider

interface PlayerHostAPI {

    fun getCurrentTrack(): Track?

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