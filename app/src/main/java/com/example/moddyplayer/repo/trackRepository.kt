package com.example.moddyplayer.repo
import com.example.moddyplayer.data.db.Track
import com.example.moddyplayer.data.db.TrackDao
import kotlinx.coroutines.flow.Flow

class TrackRepository(private val dao: TrackDao) {
    fun observeAll(): Flow<List<Track>> = dao.observeAll()
    suspend fun add(track: Track): Long = dao.insert(track)
    suspend fun getById(id: Long): Track? = dao.getById(id)
}