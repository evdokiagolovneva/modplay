package com.example.moddyplayer.data.db.repo

import com.example.moddyplayer.data.db.dataa.Track
import com.example.moddyplayer.data.db.dataa.TrackDao
import com.example.moddyplayer.data.db.network.MusicApi
import kotlinx.coroutines.flow.Flow

class TrackRepository(
    private val trackDao: TrackDao,
    private val musicApi: MusicApi // Добавляем сетевой источник
) {
    // получение треков
    val allTracks = trackDao.getAllTracks()

    suspend fun refreshTracks() {
        try {
            //делаем запрос в сеть
            val networkTracks = musicApi.getTracks()
            // превращаем DTO в локальные сущности базы данных
            // при необходимости сохраняем в trackDao.insertAll(...)
        } catch (e: Exception) {
            e.printStackTrace()
            // обработка сетевой ошибки (оставляем локальные данные)
        }
    }
}