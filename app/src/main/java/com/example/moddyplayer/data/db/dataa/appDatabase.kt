package com.example.moddyplayer.data.db.dataa

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        Track::class, Playlist::class, PlaylistTrack::class,
        PlayHistory::class, Favorite::class, ModEntity::class, ModSetting::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun trackDao(): TrackDao
    abstract fun playlistDao(): PlaylistDao
    abstract fun historyDao(): HistoryDao
    abstract fun favoriteDao(): FavoriteDao
    abstract fun modDao(): ModDao
}