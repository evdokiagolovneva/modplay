package com.example.moddyplayer.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlists")
data class Playlist(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val createdAt: Long = System.currentTimeMillis()
)

//трек лежит в плейлисте
@Entity(tableName = "playlist_tracks", primaryKeys = ["playlistId", "trackId"])
data class PlaylistTrack(
    val playlistId: Long,
    val trackId: Long,
    val position: Int = 0
)

@Entity(tableName = "play_history")
data class PlayHistory(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val trackId: Long,
    val playedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "favorites")
data class Favorite(
    @PrimaryKey val trackId: Long,
    val addedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "mods")
data class ModEntity(
    @PrimaryKey val id: String,
    val name: String,
    val version: String,
    val author: String? = null,
    val enabled: Boolean = false
)

@Entity(tableName = "mod_settings", primaryKeys = ["modId", "key"])
data class ModSetting(
    val modId: String,
    val key: String,
    val value: String
)