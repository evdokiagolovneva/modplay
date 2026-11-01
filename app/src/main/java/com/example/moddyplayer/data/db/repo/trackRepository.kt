package com.example.moddyplayer.data.db.repo

import com.example.moddyplayer.data.db.dataa.Track
import com.example.moddyplayer.data.db.dataa.TrackDao
import com.example.moddyplayer.data.db.dataa.MusicScanner
import kotlinx.coroutines.flow.Flow

class TrackRepository(
    private val trackDao: TrackDao,
    private val musicScanner: MusicScanner // Инжектим наш сканер
) {
    // UI слушает этот поток из базы данных в реальном времени
    val allTracks: Flow<List<Track>> = trackDao.getAllTracks()

    /**
     * Сканирует устройство и сохраняет найденные треки в базу данных Room.
     * После этого UI автоматически обновится благодаря Flow из DAO.
     */
    suspend fun scanAndSyncMusic() {
        val deviceTracks = musicScanner.scanTracks()
        for (track in deviceTracks) {
            // Сохраняем по одному через имеющийся insert (или можно добавить insertAll в DAO)
            trackDao.insert(track)
        }
    }
}