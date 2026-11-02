package com.example.moddyplayer.player

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.audio.AudioProcessor
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.example.moddyplayer.data.db.dataa.Track
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.SimpleCache
import com.example.moddyplayer.data.db.repo.TrackRepository
import java.io.File

@OptIn(UnstableApi::class)
class PlaybackController(
    private val context: Context,
    private val simpleCache: SimpleCache,
    private val trackRepository: TrackRepository
) : PlayerCore {

    // ИСПРАВЛЕНИЕ: Используем универсальный DefaultDataSource.Factory вместо DefaultHttpDataSource.Factory
    private val dataSourceFactory = CacheDataSource.Factory()
        .setCache(simpleCache)
        .setUpstreamDataSourceFactory(androidx.media3.datasource.DefaultDataSource.Factory(context))
        .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)

    private val exoPlayer: ExoPlayer = ExoPlayer.Builder(context)
        .setMediaSourceFactory(androidx.media3.exoplayer.source.DefaultMediaSourceFactory(context).setDataSourceFactory(dataSourceFactory))
        .build()

    private val _state = MutableStateFlow(PlayerState())
    override val state: StateFlow<PlayerState> = _state.asStateFlow()

    private val coroutineScope = CoroutineScope(Dispatchers.Main + Job())
    private var progressJob: Job? = null

    init {
        // слушаем изменения состояния стандартного ExoPlayer
        exoPlayer.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _state.update { it.copy(isPlaying = isPlaying) }
                if (isPlaying) {
                    startProgressTracking()
                } else {
                    stopProgressTracking()
                }
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_READY) {
                    val duration = exoPlayer.duration.coerceAtLeast(0L)
                    _state.update { it.copy(durationMs = duration) }
                }
            }
        })
    }

    fun setPlaylist(tracks: List<Track>, startIndex: Int = 0) {
        if (tracks.isEmpty()) return
        val safeIndex = startIndex.coerceIn(0, tracks.size - 1)

        _state.update { it.copy(playlist = tracks) }
        play(tracks[safeIndex])
    }

    override fun play(track: Track) {
        // Если очередь пуста, автоматически подтягиваем все треки из базы данных
        if (_state.value.playlist.isEmpty()) {
            coroutineScope.launch(Dispatchers.IO) {
                val allTracks = trackRepository.getAllTracksOnce() 
                // обновляем плейлист в стейте
                _state.update { it.copy(playlist = allTracks) }
            }
        }

        val downloadedFile = File(context.filesDir, "downloads/${track.id}.mp3")
        val uriToPlay = if (downloadedFile.exists()) {
            android.net.Uri.fromFile(downloadedFile)
        } else {
            android.net.Uri.parse(track.uri)
        }

        val mediaItem = MediaItem.fromUri(uriToPlay)
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        exoPlayer.play()

        val currentPlaylist = _state.value.playlist
        val index = currentPlaylist.indexOfFirst { it.id == track.id }

        _state.update {
            it.copy(
                currentTrack = track,
                durationMs = track.durationMs,
                currentIndex = if (index != -1) index else 0
            )
        }
    }

    fun skipToNext() {
        val state = _state.value
        if (state.playlist.isEmpty()) return

        // Вычисляем индекс следующего трека (с кольцевым переходом в начало)
        val nextIndex = if (state.currentIndex < state.playlist.size - 1) {
            state.currentIndex + 1
        } else {
            0 // Возвращаемся к первому треку
        }

        play(state.playlist[nextIndex])
    }

    fun skipToPrevious() {
        val state = _state.value
        if (state.playlist.isEmpty()) return

        // Вычисляем индекс предыдущего трека (с кольцевым переходом в конец)
        val prevIndex = if (state.currentIndex > 0) {
            state.currentIndex - 1
        } else {
            state.playlist.size - 1 // Переходим на последний трек
        }

        play(state.playlist[prevIndex])
    }

    override fun pause() {
        exoPlayer.pause()
    }

    override fun seekTo(ms: Long) {
        exoPlayer.seekTo(ms)
        _state.update { it.copy(positionMs = ms) }
    }

    override fun registerAudioProcessor(processor: AudioProcessor) {
        // позднее здесь можно будет настраивать аудиопроцессоры
    }

    // отслеживание текущей позиции трека
    private fun startProgressTracking() {
        progressJob?.cancel()
        progressJob = coroutineScope.launch {
            while (isActive) {
                val currentPosition = exoPlayer.currentPosition.coerceAtLeast(0L)
                _state.update { it.copy(positionMs = currentPosition) }
                delay(500L) // обновление каждые полсекунды
            }
        }
    }

    private fun stopProgressTracking() {
        progressJob?.cancel()
        progressJob = null
    }

    fun release() {
        stopProgressTracking()
        exoPlayer.release()
    }

    fun resume() {
        exoPlayer.play()
    }


}