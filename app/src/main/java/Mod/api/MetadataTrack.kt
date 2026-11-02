package Mod.api

data class MetadataTrack(
    val id: String,
    val title: String,
    val artist: String,
    val album: String?,
    val durationMs: Long
)