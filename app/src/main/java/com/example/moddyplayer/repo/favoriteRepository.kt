package com.example.moddyplayer.repo

import com.example.moddyplayer.data.db.Favorite
import com.example.moddyplayer.data.db.FavoriteDao
import kotlinx.coroutines.flow.Flow

class FavoriteRepository(private val dao: FavoriteDao) {
    suspend fun addFavorite(favorite: Favorite) = dao.add(favorite)

    suspend fun removeFavorite(trackId: Long) = dao.remove(trackId)

    fun observeAll(): Flow<List<Favorite>> = dao.observeAll()
}