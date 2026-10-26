package Mod.engine

data class Track(
    val id: String,
    val title: String,
    val artist: String,
    val album: String?,
    val durationMs: Long,
    val mediaUri: String
)
