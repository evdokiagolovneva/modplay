package com.example.moddyplayer.data.db

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import com.example.moddyplayer.data.db.Track
import com.example.moddyplayer.repo.TrackRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MusicScanner(
    private val context: Context,
    private val trackRepository: TrackRepository
) {
    suspend fun scanAudioFiles() = withContext(Dispatchers.IO) {
        val collection = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.IS_MUSIC
        )

        // ищем ТОЛЬКО музыкальные файлы
        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
        val sortOrder = "${MediaStore.Audio.Media.TITLE} ASC"

        context.contentResolver.query(
            collection,
            projection,
            selection,
            null,
            sortOrder
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
            val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val title = cursor.getString(titleColumn) ?: "Без названия"
                val artist = cursor.getString(artistColumn) ?: "Неизвестный исполнитель"
                val album = cursor.getString(albumColumn) ?: "Неизвестный альбом"
                val duration = cursor.getLong(durationColumn)

                // формируем URI файла для ExoPlayer
                val contentUri = ContentUris.withAppendedId(
                    collection,
                    id
                )

                val track = Track(
                    title = title,
                    artist = artist,
                    album = album,
                    durationMs = duration,
                    uri = contentUri.toString()
                )

                // сохраняем в базу данных через репозиторий
                trackRepository.add(track)
            }
        }
    }
}