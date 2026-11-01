package com.example.moddyplayer.data.db.dataa

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index


@Entity(
    tableName = "tracks",
    indices = [Index(value = ["uri"], unique = true)]
)
data class Track(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val artist: String? = null,
    val album: String? = null,
    val durationMs: Long = 0L,
    val uri: String
)