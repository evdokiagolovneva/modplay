package com.example.player_api.engine

import com.example.player_api.manifest.ModManifest
import com.example.player_api.api.ModAPI
import android.content.Context
import 	dalvik.system.DexClassLoader
import java.util.zip.ZipFile
import java.lang.System
import android.content.pm.PackageManager
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.example.player_api.api.PlayerHostAPI
import com.example.player_api.api.PlayerHostContext
import okhttp3.OkHttpClient


interface ModEngine {

    fun readManifest(path: String?): ModManifest

    fun loadMod(context: Context, assetName: String)

    fun unloadMod(context: Context, id: String): Boolean

    @OptIn(UnstableApi::class)
    fun enableMod(
        path: String?,
        parentLoader: ClassLoader?,
        hostContext: PlayerHostContext
    )

    fun disableMod(id: String)

    // Точно ли это должно быть методом в составе интерфейса?
    //fun listMods(): List<ModManifest>

    fun getModApi(id: String): ModAPI?


    // Давай дополним как-то engine?

}