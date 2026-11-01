package Mod.engine

import Mod.api.AudioEffect
import Mod.api.MetadataProvider
import Mod.api.PlayerHostAPI
import Mod.api.UiSlot
import androidx.media3.exoplayer.ExoPlayer
import android.util.Log

class PlayerHostAPIRealization(
    private val player: ExoPlayer,
    private val audioEngine: AudioEngine,
    private val uiManager: UiSlotManager
) : PlayerHostAPI {

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

    override fun log(message: String) {
        checkExpiration()
        Log.d("ModSandbox", "[MOD LOG]: $message")
    }

    override fun getCurrentTrack(): Track? {
        checkExpiration()
        val item = player.currentMediaItem ?: return null
        return Track(
            id = item.mediaId,
            title = item.mediaMetadata.title?.toString() ?: "Unknown",
            artist = item.mediaMetadata.artist?.toString() ?: "Unknown Artist",
            album = item.mediaMetadata.albumTitle?.toString() ?: "Unknown Album",
            durationMs = item.mediaMetadata.durationMs?.toLong() ?: 0,
            mediaUri = item.mediaMetadata.artworkUri?.toString() ?: ""
        )
    }

    override fun registerAudioEffect(effect: AudioEffect) {
        checkExpiration()
        // передаем аудиоэффект в движок обработки звука
        audioEngine.addAudioProcessor(effect)
        Log.i("ModEngine", "Эффект успешно зарегистрирован в аудио-тракте.")
    }

    override fun registerMediaDataProvider(provider: MetadataProvider) {
        checkExpiration()
        // регистрация поставщика метаданных
        Log.i("ModEngine", "MetadataProvider успешно зарегистрирован.")
    }

    override fun registerUiSlot(slot: UiSlot) {
        checkExpiration()
        // передаем UI-слот мода в менеджер интерфейса
        uiManager.injectModUi(slot.targetSlotId, slot.content)
        Log.i("ModEngine", "UI-слот ${slot.targetSlotId} успешно зарегистрирован.")
    }

    override fun httpGet(url: String): String? {
        checkExpiration()
        // реализация сетевого запроса для модов
        return null
    }

    override fun readMusicFile() {
        checkExpiration()
        // чтение музыкальных файлов
    }
}