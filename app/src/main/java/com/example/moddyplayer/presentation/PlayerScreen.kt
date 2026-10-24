package com.example.moddyplayer.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.moddyplayer.player.PlaybackController
import org.koin.compose.koinInject

@Composable
fun PlayerScreen(
    // Если навигация передает пустой список, мы можем не использовать его напрямую,
    // а управлять воспроизведением через PlaybackController
    tracks: List<String> = emptyList()
) {
    // Внедряем наш контроллер плеера через Koin
    val playbackController: PlaybackController = koinInject()
    val state by playbackController.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Название текущего трека
        Text(
            text = state.currentTrack?.title ?: "Трек не выбран",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Исполнитель
        Text(
            text = state.currentTrack?.artist ?: "Неизвестный исполнитель",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.secondary
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Прогресс-бар воспроизведения
        Slider(
            value = if (state.durationMs > 0) state.positionMs.toFloat() / state.durationMs.toFloat() else 0f,
            onValueChange = { newValue ->
                val newPosition = (newValue * state.durationMs).toLong()
                playbackController.seekTo(newPosition)
            },
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = formatTime(state.positionMs))
            Text(text = formatTime(state.durationMs))
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Кнопки управления (Назад, Play/Pause, Вперед)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = { playbackController.skipToPrevious() }) {
                Text("Назад")
            }

            Button(
                onClick = {
                    if (state.isPlaying) {
                        playbackController.pause()
                    } else {
                        playbackController.resume()
                    }
                }
            ) {
                Text(if (state.isPlaying) "Пауза" else "Играть")
            }

            Button(onClick = { playbackController.skipToNext() }) {
                Text("Вперед")
            }
        }
    }
}

// Вспомогательная функция для форматирования миллисекунд в формат ММ:СС
private fun formatTime(ms: Long): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d", minutes, seconds)
}