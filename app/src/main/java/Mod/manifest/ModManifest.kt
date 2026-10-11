package Mod.manifest
import kotlinx.serialization.Serializable
// import kotlinx.serialization.json.*

@Serializable
data class ModManifest(
    var id: String,
    var name: String,
    var version: String,
    var authorName: String,
    var description: String,
    var entryPoint: String,
    var minApiVersion: String,
    var permissions: Collection<String>,
) {

}