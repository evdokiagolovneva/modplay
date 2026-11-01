package com.example.moddyplayer.data.db.dataa

import android.content.ContentUris
import android.content.Context
import android.media.MediaScannerConnection
import android.provider.MediaStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class MusicScanner(private val context: Context) {

    suspend fun scanTracks(): List<Track> = withContext(Dispatchers.IO) {
        // 1. Сначала принудительно просим систему просканировать папку «Загрузки» или всю внешнюю память,
        // чтобы файлы, перетащенные с ПК, появились в MediaStore.
        triggerMediaScan()

        val tracks = mutableListOf<Track>()
        val collection = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATA
        )

        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"

        context.contentResolver.query(
            collection,
            projection,
            selection,
            null,
            "${MediaStore.Audio.Media.TITLE} ASC"
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
            val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)

            while (cursor.moveToNext()) {
                val title = cursor.getString(titleColumn) ?: "Без названия"
                val artist = cursor.getString(artistColumn) ?: "Неизвестный исполнитель"
                val album = cursor.getString(albumColumn) ?: "Неизвестный альбом"
                val duration = cursor.getLong(durationColumn)
                val filePath = cursor.getString(dataColumn) ?: ""

                val trackUri = if (filePath.isNotEmpty()) "file://$filePath" else continue

                tracks.add(
                    Track(
                        title = title,
                        artist = artist,
                        album = album,
                        durationMs = duration,
                        uri = trackUri
                    )
                )
            }
        }
        return@withContext tracks
    }

    // Вспомогательная функция для синхронного вызова системного сканера
    private suspend fun triggerMediaScan() = suspendCoroutine { continuation ->
        // Указываем путь к папке загрузок или всей внешней памяти эмулятора
        val downloadDir = android.os.Environment.getExternalStoragePublicDirectory(
            android.os.Environment.DIRECTORY_DOWNLOADS
        )

        if (downloadDir.exists()) {
            MediaScannerConnection.scanFile(
                context,
                arrayOf(downloadDir.absolutePath),
                null
            ) { _, _ ->
                // Как только система закончит сканирование папки, продолжаем выполнение
                continuation.resume(Unit)
            }
        } else {
            continuation.resume(Unit)
        }
    }
}