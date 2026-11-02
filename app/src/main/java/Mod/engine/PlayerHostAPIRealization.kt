package Mod.engine

import Mod.api.AudioEffect
import Mod.api.MetadataProvider
import Mod.api.PlayerHostAPI
import Mod.api.UiSlot
import Mod.manifest.ModManifest
import Mod.permissions.ModPermissions
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.example.moddyplayer.player.PlayerCore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.coroutines.executeAsync
import okio.IOException
import kotlin.coroutines.cancellation.CancellationException

class PlayerHostAPIRealization @OptIn(UnstableApi::class) constructor
    (                          private val modId: String,
                               private val player: ExoPlayer,
                               private val audioEngine: AudioEngine,
                               private val uiManager: UiSlotManager,
                               private val okHttpClient: OkHttpClient,
                               private val modManifest: ModManifest,
                               private val playerCore: PlayerCore,
                               private val  metadataManager: MetadataProvidersManager
)
    : PlayerHostAPI {

    private val apiJob = SupervisorJob()

    private val apiScope = CoroutineScope(Dispatchers.IO + apiJob)

    @Volatile
    private var isExpired: Boolean = false

    fun expire() {
        isExpired = true
        apiJob.cancel()
        uiManager.unregisterSlotsForMod(modId)
    }

    private fun checkExpiration() {
        if (isExpired) {
            throw IllegalStateException("This PlayerHostAPI instance has been deactivated by the player core.")
        }
    }

    private fun checkPermission(permission: ModPermissions) {
        if (!modManifest.permissions.contains(permission.name)) {
            throw SecurityException("Мод $modId не имеет задекларированного разрешения: ${permission.name}")
        }
    }


    // Пока не подрублен Media3, а так будет работать с MediaMetadata
    // И с более широким классом player
    // Вообще почему ты писала именно GetCurrentTrack?
    override fun getCurrentTrack(): Track? {
        if (isExpired) return null
        checkPermission(ModPermissions.METADATA_READ)

        val item = player.currentMediaItem ?: return null
        return Track(
            id = item.mediaId,
            title = item.mediaMetadata.title?.toString() ?: "Unknown",
            artist = item.mediaMetadata.artist?.toString() ?: "Unknown Artist",
            album = item.mediaMetadata.albumTitle?.toString() ?: "Unknown Album",
            durationMs = item.mediaMetadata.durationMs?.toLong()?: 0,
            mediaUri = item.mediaMetadata.artworkUri
        )
    }

    override fun playTrack(trackId: String)
    {
        checkExpiration()
        val mediaItem = MediaItem.Builder()
            .setMediaId(trackId)
            .build()

        player.setMediaItem(mediaItem)
        player.prepare()
        player.play()

    }
    override fun next()
    {
        checkExpiration()
        if (player.hasNextMediaItem()) {
            player.seekToNextMediaItem()
        }
    }
    override fun pause()
    {
        checkExpiration()
        if (player.isPlaying) {
            player.pause()
        } else {
            player.play()
        }
    }
    override fun random()
    {
        checkExpiration()
        player.shuffleModeEnabled = !player.shuffleModeEnabled
    }


    @OptIn(UnstableApi::class)
    override fun registerAudioEffect(effect: AudioEffect)
    {
        checkExpiration()
        checkPermission(ModPermissions.AUDIO_EFFECTS)
        audioEngine.registerModEffect(modId, effect)

    }


    // Вопросы к нему в MediaDataProvider
    override fun registerMetadataProvider(provider: MetadataProvider)
    {
        checkExpiration()
        checkPermission(ModPermissions.METADATA_READ)
        metadataManager.registerProvider(modId, provider)
    }

    override fun registerUiSlot(slot: UiSlot)
    {
        checkExpiration()
        checkPermission(ModPermissions.UI_SLOT)
        uiManager.registerSlot(slot)
    }

    @OptIn(UnstableApi::class)
    override fun unregisterAudioEffect(effect: AudioEffect): Unit
    {
        checkExpiration()
        checkPermission(ModPermissions.AUDIO_EFFECTS)
        audioEngine.unregisterModEffect(modId, effect)
    }

    override fun unregisterMetadataProvider(provider: MetadataProvider)
    {
        checkExpiration()
        checkPermission(ModPermissions.METADATA_READ)
        metadataManager.unregisterProvider(modId, provider)
    }

    override fun unregisterUiSlot(slot: UiSlot)
    {
        checkExpiration()
        checkPermission(ModPermissions.AUDIO_EFFECTS)
        uiManager.unregisterSingleSlot(slot)
    }

    @OptIn(UnstableApi::class)
    fun release() {
        if (isExpired) return

        metadataManager.unregisterAllForMod(modId)

        audioEngine.removeEffectsForMod(modId)

        uiManager.unregisterSlotsForMod(modId)

        isExpired = true

        apiJob.cancel()
    }

    override suspend fun fetchUrl(url: String): String? {
    return try {
        checkExpiration()
        checkPermission(ModPermissions.NETWORK)

        val request = Request.Builder()
            .url(url)
            .build()

        val response = okHttpClient.newCall(request).executeAsync()

        if (isExpired) return null

        response.use {
            it.body?.string()
        }

    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        null
    }
}

    override fun readMusicFile()
    {}

}




/*

override fun log(message: String) {
        checkExpiration()
        // Перенаправляем логи мода в системный лог вашего плеера
        Log.d("ModSandbox", "[MOD LOG]: $message")
    }
*/