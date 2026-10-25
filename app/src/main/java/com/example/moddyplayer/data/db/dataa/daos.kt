package com.example.moddyplayer.data.db.dataa

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(track: Track): Long

    // Меняем имя, чтобы совпадало с репозиторием
    @Query("SELECT * FROM tracks ORDER BY title")
    fun getAllTracks(): Flow<List<Track>>

    @Query("SELECT * FROM tracks WHERE id = :id")
    suspend fun getById(id: Long): Track?

    @Delete
    suspend fun delete(track: Track)
}

@Dao
interface PlaylistDao {
    @Insert
    suspend fun insert(playlist: Playlist): Long

    @Query("SELECT * FROM playlists ORDER BY name")
    fun observeAll(): Flow<List<Playlist>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addTrack(link: PlaylistTrack)

    @Query("""
        SELECT tracks.* FROM tracks
        INNER JOIN playlist_tracks ON tracks.id = playlist_tracks.trackId
        WHERE playlist_tracks.playlistId = :playlistId
        ORDER BY playlist_tracks.position
    """)
    fun observeTracks(playlistId: Long): Flow<List<Track>>
}

@Dao
interface HistoryDao {
    @Insert
    suspend fun insert(entry: PlayHistory)

    @Query("SELECT * FROM play_history ORDER BY playedAt DESC LIMIT :limit")
    fun observeRecent(limit: Int = 50): Flow<List<PlayHistory>>
}

@Dao
interface FavoriteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(favorite: Favorite)

    @Query("DELETE FROM favorites WHERE trackId = :trackId")
    suspend fun remove(trackId: Long)

    @Query("SELECT * FROM favorites")
    fun observeAll(): Flow<List<Favorite>>
}

@Dao
interface ModDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(mod: ModEntity)

    @Query("SELECT * FROM mods")
    fun observeAll(): Flow<List<ModEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSetting(setting: ModSetting)

    @Query("SELECT * FROM mod_settings WHERE modId = :modId")
    suspend fun getSettings(modId: String): List<ModSetting>
}