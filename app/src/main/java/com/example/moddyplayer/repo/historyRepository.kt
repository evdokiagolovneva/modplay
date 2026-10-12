package com.example.moddyplayer.repo

import com.example.moddyplayer.data.db.HistoryDao
import com.example.moddyplayer.data.db.PlayHistory
import kotlinx.coroutines.flow.Flow

class HistoryRepository(private val dao: HistoryDao) {
    suspend fun addEntry(entry: PlayHistory) = dao.insert(entry)

    fun observeRecent(limit: Int = 50): Flow<List<PlayHistory>> = dao.observeRecent(limit)
}