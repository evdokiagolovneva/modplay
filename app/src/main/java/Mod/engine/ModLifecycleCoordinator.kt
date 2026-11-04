package Mod.engine
import Mod.manifest.ModManifest
import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.example.moddyplayer.player.PlayerCore
import okhttp3.OkHttpClient
import java.io.File

@OptIn(UnstableApi::class)
class ModLifecycleCoordinator
    (
    private val context: Context,
    private val modEngine: ModEngine,
    private val player: ExoPlayer,
    private val audioEngine: AudioEngine,
    private val uiSlotManager: UiSlotManager,
    private val okHttpClient: OkHttpClient,
    private val playerCore: PlayerCore,
    private val currentApiVersion: String = "1.0.0"
)
{
    fun requestEnableMod(id: String, apkPath: String): Result<Unit> {
        return try {
            val manifest = modEngine.readManifest(apkPath)
            if (!isCompatible(manifest.minApiVersion)) {
                return Result.failure(
                    IllegalArgumentException("Модуль требует версию API не ниже ${manifest.minApiVersion}, текущая версия: $currentApiVersion")
                )
            }

            // TODO("Window to ask for Permissions")
            modEngine.enableMod(
                path = apkPath,
                parentLoader = context.classLoader,
                player = player,
                audioEngine = audioEngine,
                uiManager = uiSlotManager,
                okHttpClient = okHttpClient,
                playerCore = playerCore
            )

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun requestDisableMod(id: String) {
        try {
            modEngine.disableMod(id)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun isCompatible(requiredVersion: String): Boolean {
        if (requiredVersion.isEmpty())
            return true

        return currentApiVersion >= requiredVersion
    }
}
