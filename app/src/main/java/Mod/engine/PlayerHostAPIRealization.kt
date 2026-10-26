package Mod.engine

import Mod.api.AudioEffect
import Mod.api.PlayerHostAPI
import android.media.browse.MediaBrowser
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import Mod.engine.Track

class PlayerHostAPIRealization(private val player: ExoPlayer,
                               private val audioEngine: AudioEngine,
                               private val uiManager: UiSlotManager)
    : PlayerHostAPI {

    @Volatile
    private var isExpired: Boolean = false

    fun expire() {
        isExpired = true
    }

    private fun checkExpiration() {
        if (isExpired) {
            throw IllegalStateException("This PlayerHostAPI instance has been deactivated by the player core.")
        }
    }

    fun log(message: String)

    // Пока не подрублен Media3, а так будет работать с MediaMetadata
    // И с более широким классом player
    // Вообще почему ты писала именно GetCurrentTrack?
    override fun getCurrentTrack(): Track? {
        val item = player.currentMediaItem ?: return null
        return Track(
            id = item.mediaId,
            title = item.mediaMetadata.title?.toString() ?: "Unknown",
            artist = item.mediaMetadata.artist?.toString() ?: "Unknown Artist",
            album = item.mediaMetadata.albumTitle?.toString() ?: "Unknown Album",
            durationMs = item.mediaMetadata.durationMs?.toLong()?: 0,
            mediaUri = item.mediaMetadata
        )
    }
    // Нужен ли нам set в составе интерфейса?

    // Что такое вообще AudioEffect?
    // Почему он в интерфейсе и обязан быть у каждого мода?
    fun registerAudioEffect(effect: AudioEffect)
    {

    }


    // Вопросы к нему в MediaDataProvider
    fun registerMediaDataProvider(provider: MetadataProvider)

    // Почему у нас идет философия построения UI через слоты?
    // Просто интересно мнение. UI Columns в foobar2000 с похожей идеей
    fun registerUiSlot()

    fun httpGet(url: String): String?

    fun readMusicFile()

}




/*

override fun log(message: String) {
        checkExpiration()
        // Перенаправляем логи мода в системный лог вашего плеера
        Log.d("ModSandbox", "[MOD LOG]: $message")
    }

    override fun getCurrentTrack(): Track? {
        checkExpiration()
        // Берем реальный трек из плеера (например, MediaItem из Media3)
        // и маппим/превращаем его в безопасную структуру данных Track из пакета api
        val currentMediaItem = playbackManager.getCurrentMediaItem() ?: return null

        return Track(
            id = currentMediaItem.mediaId,
            title = currentMediaItem.mediaMetadata.title.toString(),
            artist = currentMediaItem.mediaMetadata.artist.toString(),
            mediaUri = currentMediaItem.localConfiguration?.uri.toString()
        )
    }

    override fun registerAudioEffect(effect: AudioEffect) {
        checkExpiration()
        // Берем сторонний алгоритм обработки звука и встраиваем его в цепочку нашего движка
        audioEngine.addAudioProcessor(effect)
        Log.i("ModEngine", "Эффект '${effect.effectName}' успешно интегрирован в аудио-трак.")
    }

    override fun registerMediaDataProvider(provider: MetadataProvider) {
        checkExpiration()
        // Регистрируем кастомный источник треков (например, SoundCloud плагин) в медиатеку плеера
        playbackManager.mediaRepository.addCustomProvider(provider)
    }

    override fun registerUiSlot(slot: UiSlot) {
        checkExpiration()
        // Передаем Compose-лямбду или View мода в менеджер интерфейса плеера
        uiManager.injectModUi(slot.targetSlotId, slot.content)
    }
}
 */