package com.example.moddyplayer.data.db.repo

import com.example.moddyplayer.data.db.dataa.ModDao
import com.example.moddyplayer.data.db.dataa.ModEntity
import com.example.moddyplayer.data.db.dataa.ModSetting
import kotlinx.coroutines.flow.Flow

class ModRepository(private val dao: ModDao) {
    suspend fun upsertMod(mod: ModEntity) = dao.upsert(mod)

    fun observeAllMods(): Flow<List<ModEntity>> = dao.observeAll()

    suspend fun saveSetting(setting: ModSetting) = dao.upsertSetting(setting)

    suspend fun getModSettings(modId: String): List<ModSetting> = dao.getSettings(modId)
}