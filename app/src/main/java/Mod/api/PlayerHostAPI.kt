package Mod.api

import androidx.media3.common.MediaItem
import Mod.engine.Track
import Mod.api.MetadataProvider

interface PlayerHostAPI {

    fun log(message: String)

    // Пока не подрублен Media3, а так будет работать с MediaMetadata
    // И с более широким классом player
    // Вообще почему ты писала именно GetCurrentTrack?
    fun getCurrentTrack(): Track?

    // Нужен ли нам set в составе интерфейса?

    // Что такое вообще AudioEffect?
    // Почему он в интерфейсе и обязан быть у каждого мода?
    fun registerAudioEffect(effect: AudioEffect)

    // Вопросы к нему в MetadataProvider
    fun registerMediaDataProvider(provider: MetadataProvider)

    // Почему у нас идет философия построения UI через слоты?
    // Просто интересно мнение. UI Columns в foobar2000 с похожей идеей
    fun registerUiSlot()

    fun httpGet(url: String): String?

    fun readMusicFile()


}