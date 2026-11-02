package com.example.player_api.api


interface ModContext {
    val logger: ModLogger?
    val metadataProvider: MetadataProvider?
    val currentTrackMetadata: String?
    val isPlaying: Boolean
    val volume: Int
    val playSpeed: Int
}
