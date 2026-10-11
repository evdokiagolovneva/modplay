package Mod.engine

import Mod.manifest.ModManifest
import Mod.api.ModAPI

interface ModEngine {

    fun loadMod(path: String): ModManifest?

    fun unloadMod(id: String)

    fun enableMod(id: String)

    fun disableMod(id: String)

    // Точно ли это должно быть методом в составе интерфейса?
    fun listMods(): List<ModManifest>

    fun getModApi(): ModAPI?


    // Давай дополним как-то engine?

}