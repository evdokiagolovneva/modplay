package com.example.moddyplayer.data.db.repo

import com.example.moddyplayer.data.db.dataa.Track
import com.example.moddyplayer.data.db.dataa.TrackDao
import com.example.moddyplayer.data.db.dataa.MusicScanner
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class TrackRepository(
    private val trackDao: TrackDao,
    private val musicScanner: MusicScanner // инжектим наш сканер
) {
    // UI слушает этот поток из базы данных в реальном времени
    val allTracks: Flow<List<Track>> = trackDao.getAllTracks()

    /**
     * Сканирует устройство и сохраняет найденные треки в базу данных Room.
     */
    suspend fun scanAndSyncMusic() {
        val deviceTracks = musicScanner.scanTracks()
        for (track in deviceTracks) {
            trackDao.insert(track)
        }
    }

    suspend fun getAllTracksOnce(): List<Track> {
        return allTracks.first()
    }
}