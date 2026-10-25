package com.example.moddyplayer.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.net.URL

class TrackDownloadWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            // получаем данные из входных параметров (передадим при запуске)
            val trackId = inputData.getLong("TRACK_ID", -1L)
            val trackUrl = inputData.getString("TRACK_URL") ?: return@withContext Result.failure()
            val trackTitle = inputData.getString("TRACK_TITLE") ?: "track"

            // создаем файл в директории приложения для офлайнпрослушивания
            val downloadsDir = File(applicationContext.filesDir, "downloads")
            if (!downloadsDir.exists()) {
                downloadsDir.mkdirs()
            }

            val file = File(downloadsDir, "$trackId.mp3")

            // Скачиваем файл из сети
            URL(trackUrl).openStream().use { input ->
                file.outputStream().use { output ->
                    input.copyTo(output)
                }
            }

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure()
        }
    }
}