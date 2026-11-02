package Mod.manifest
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*

@Serializable
data class ModManifest(
    val id: String,
    val name: String,
    val version: String,
    val authorName: String,
    val description: String,
    val apkName: String,
    val entryPoint: String,
    val minApiVersion: String = "",
    val permissions: List<String> = emptyList(),
) {

}