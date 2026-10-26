package Mod.engine

import Mod.manifest.ModManifest
import Mod.api.ModAPI
import android.content.Context
import 	dalvik.system.DexClassLoader
import java.util.zip.ZipFile
import java.lang.System
import android.content.pm.PackageManager




interface ModEngine {

    fun readManifest(path: String?): ModManifest

    fun loadMod(context: Context, assetName: String)

    fun unloadMod(context: Context, id: String): Boolean

    fun enableMod(path: String?, parentLoader: ClassLoader?)

    fun disableMod(id: String)

    // Точно ли это должно быть методом в составе интерфейса?
    //fun listMods(): List<ModManifest>

    fun getModApi(id: String): ModAPI?


    // Давай дополним как-то engine?

}