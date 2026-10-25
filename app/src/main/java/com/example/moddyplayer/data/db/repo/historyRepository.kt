package com.example.moddyplayer.data.db.repo

import com.example.moddyplayer.data.db.dataa.HistoryDao
import com.example.moddyplayer.data.db.dataa.PlayHistory
import kotlinx.coroutines.flow.Flow

class HistoryRepository(private val dao: HistoryDao) {
    suspend fun addEntry(entry: PlayHistory) = dao.insert(entry)

    fun observeRecent(limit: Int = 50): Flow<List<PlayHistory>> = dao.observeRecent(limit)
}