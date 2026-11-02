package com.example.moddyplayer.presentation

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.moddyplayer.data.db.dataa.Track
import com.example.moddyplayer.player.PlaybackController
import com.example.moddyplayer.worker.TrackDownloadWorker
import org.koin.compose.koinInject

@Composable
fun LibraryScreen(
    tracks: List<Track>,
    onTrackClick: (Track) -> Unit
) {

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (tracks.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Музыка не найдена. Добавьте треки на устройство")
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(tracks) { track ->
                    TrackItem(
                        track = track,
                        onClick = {
                            onTrackClick(track) // Вызываем колбэк при нажатии на трек
                        },
                        onDownloadClick = {
                            startTrackDownload(
                                context = context,
                                trackId = track.id.toString(),
                                trackUrl = ""
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun TrackItem(
    track: Track,
    onClick: () -> Unit,
    onDownloadClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = track.title,
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = track.artist ?: "Неизвестный исполнитель",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            // кнопка скачивания
            Button(
                onClick = onDownloadClick,
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(text = "Скачать")
            }
        }
    }
}

private fun startTrackDownload(context: Context, trackId: String, trackUrl: String) {
    val data = Data.Builder()
        .putString("TRACK_ID", trackId)
        .putString("TRACK_URL", trackUrl)
        .build()

    val downloadRequest = OneTimeWorkRequestBuilder<TrackDownloadWorker>()
        .setInputData(data)
        .build()

    WorkManager.getInstance(context).enqueue(downloadRequest)
}