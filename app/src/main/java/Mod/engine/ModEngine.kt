package Mod.engine

import Mod.manifest.ModManifest
import com.example.player_api.api.ModAPI
import android.content.Context
import 	dalvik.system.DexClassLoader
import java.util.zip.ZipFile
import java.lang.System
import android.content.pm.PackageManager
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.example.moddyplayer.player.PlayerCore
import okhttp3.OkHttpClient


interface ModEngine {

    fun readManifest(path: String?): ModManifest

    fun loadMod(context: Context, assetName: String)

    fun unloadMod(context: Context, id: String): Boolean

    @OptIn(UnstableApi::class)
    fun enableMod(path: String?,
                  parentLoader: ClassLoader?,
                  player: ExoPlayer,
                  audioEngine: AudioEngine,
                  uiManager: UiSlotManager,
                  okHttpClient: OkHttpClient,
                  playerCore: PlayerCore
    )
    fun disableMod(id: String)

    // Точно ли это должно быть методом в составе интерфейса?
    //fun listMods(): List<ModManifest>

    fun getModApi(id: String): ModAPI?


    // Давай дополним как-то engine?

}