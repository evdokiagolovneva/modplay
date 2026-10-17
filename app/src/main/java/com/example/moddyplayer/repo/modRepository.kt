package com.example.moddyplayer.repo

import com.example.moddyplayer.data.db.ModDao
import com.example.moddyplayer.data.db.ModEntity
import com.example.moddyplayer.data.db.ModSetting
import kotlinx.coroutines.flow.Flow

class ModRepository(private val dao: ModDao) {
    suspend fun upsertMod(mod: ModEntity) = dao.upsert(mod)

    fun observeAllMods(): Flow<List<ModEntity>> = dao.observeAll()

    suspend fun saveSetting(setting: ModSetting) = dao.upsertSetting(setting)

    suspend fun getModSettings(modId: String): List<ModSetting> = dao.getSettings(modId)
}