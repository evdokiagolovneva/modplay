package com.example.moddyplayer.data.db.repo

import com.example.moddyplayer.data.db.dataa.Favorite
import com.example.moddyplayer.data.db.dataa.FavoriteDao
import kotlinx.coroutines.flow.Flow

class FavoriteRepository(private val dao: FavoriteDao) {
    suspend fun addFavorite(favorite: Favorite) = dao.add(favorite)

    suspend fun removeFavorite(trackId: Long) = dao.remove(trackId)

    fun observeAll(): Flow<List<Favorite>> = dao.observeAll()
}