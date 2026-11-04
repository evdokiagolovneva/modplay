package com.example.moddyplayer

import Mod.engine.ModEngine
import android.app.Application
import com.example.moddyplayer.data.db.repo.ModRepository
import com.example.moddyplayer.di.appModule
import com.example.moddyplayer.di.modEngineModule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import java.io.File

class ModdyPlayerApp : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@ModdyPlayerApp)
            modules(appModule, modEngineModule)
        }

        scanAssetsForMods()
    }
    private fun scanAssetsForMods()
    {
        try{
            val modEngine: ModEngine = get()
            val modRepository: ModRepository = get()

            val folderName = "mods"
            val assetFiles = assets.list("mods") ?: return

            for (fileName in assetFiles) {
                if (fileName.endsWith(".apk")) {
                    val fullAssetPath = "$folderName/$fileName"

                    modEngine.loadMod(this, fullAssetPath)

                    val pureFileName = fileName.substringAfterLast('/')
                    val targetFile = java.io.File(File(filesDir, "mods"), pureFileName)
                    val manifest = modEngine.readManifest(targetFile.absolutePath)

                    GlobalScope.launch(Dispatchers.IO) {
                        // Проверяем, есть ли уже такой мод в БД, чтобы не перезаписать текущий флаг 'enabled'
                        // Если вашей сущности Mod нужен маппинг, соберите её из полей manifest
                        // Ниже примерный код (подставьте конструктор вашей сущности Mod):
                        val existingMods =
                            modRepository.observeAllMods().firstOrNull() ?: emptyList()
                        val alreadyInDb = existingMods.any { it.id == manifest.id }

                        if (!alreadyInDb) {
                            val newMod =
                                com.example.moddyplayer.data.db.dataa.ModEntity(
                                    id = manifest.id,
                                    name = manifest.name,
                                    version = manifest.version,
                                    author = manifest.authorName,
                                    enabled = false
                                )
                            modRepository.upsertMod(newMod)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}



