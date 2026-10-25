package Mod.engine

//internal class {
    //inner class ModEngineRealization

    //fun ModEngine() {
    //}
//}


import Mod.manifest.ModManifest
import Mod.api.ModAPI

internal class ModEngineRealization : ModEngine {
    override fun loadMod(path: String): ModManifest? = null
    override fun unloadMod(id: String) {}
    override fun enableMod(id: String) {}
    override fun disableMod(id: String) {}
    override fun listMods(): List<ModManifest> = emptyList()
    override fun getModApi(): ModAPI? = null
}
