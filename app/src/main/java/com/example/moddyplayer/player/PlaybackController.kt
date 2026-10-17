package com.example.moddyplayer.player

import android.content.Context
import android.media.audiofx.Equalizer
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.audio.AudioProcessor
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.example.moddyplayer.data.db.Track
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

@OptIn(UnstableApi::class)
class PlaybackController(
    private val context: Context
) : PlayerCore {

    internal val exoPlayer: ExoPlayer = ExoPlayer.Builder(context).build()

    private val _state = MutableStateFlow(PlayerState())
    override val state: StateFlow<PlayerState> = _state.asStateFlow()

    private val coroutineScope = CoroutineScope(Dispatchers.Main + Job())
    private var progressJob: Job? = null
    private var currentQueue: List<Track> = emptyList()

    private var equalizer: Equalizer? = null

    init {
        exoPlayer.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _state.update { it.copy(isPlaying = isPlaying) }
                if (isPlaying) {
                    startProgressTracking()
                    // Инициализируем эквалайзер по текущему audioSessionId плеера, когда пошла музыка
                    setupEqualizer()
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

            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                val index = exoPlayer.currentMediaItemIndex
                if (index in currentQueue.indices) {
                    val track = currentQueue[index]
                    _state.update {
                        it.copy(
                            currentTrack = track,
                            currentIndex = index,
                            durationMs = track.durationMs
                        )
                    }
                }
            }
        })
    }

    private fun setupEqualizer() {
        if (equalizer == null && exoPlayer.audioSessionId != android.media.audiofx.AudioEffect.ERROR_BAD_VALUE) {
            try {
                equalizer = Equalizer(0, exoPlayer.audioSessionId).apply {
                    enabled = true
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun setEqualizerBandLevel(band: Short, level: Short) {
        try {
            equalizer?.setBandLevel(band, level)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getNumberOfBands(): Short? {
        return equalizer?.numberOfBands
    }

    override fun play(track: Track) {
        currentQueue = listOf(track)
        val mediaItem = MediaItem.fromUri(track.uri)
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        exoPlayer.play()

        _state.update {
            it.copy(
                queue = currentQueue,
                currentIndex = 0,
                currentTrack = track,
                durationMs = track.durationMs
            )
        }
    }

    override fun playQueue(tracks: List<Track>, startIndex: Int) {
        if (tracks.isEmpty()) return
        currentQueue = tracks
        val mediaItems = tracks.map { MediaItem.fromUri(it.uri) }

        exoPlayer.setMediaItems(mediaItems, startIndex, 0L)
        exoPlayer.prepare()
        exoPlayer.play()

        _state.update {
            it.copy(
                queue = tracks,
                currentIndex = startIndex,
                currentTrack = tracks[startIndex],
                durationMs = tracks[startIndex].durationMs
            )
        }
    }

    override fun pause() {
        exoPlayer.pause()
    }

    override fun resume() {
        if (!exoPlayer.isPlaying && exoPlayer.mediaItemCount > 0) {
            exoPlayer.play()
        }
    }

    override fun skipToNext() {
        if (exoPlayer.hasNextMediaItem()) {
            exoPlayer.seekToNextMediaItem()
        }
    }

    override fun skipToPrevious() {
        if (exoPlayer.hasPreviousMediaItem()) {
            exoPlayer.seekToPreviousMediaItem()
        }
    }

    override fun seekTo(ms: Long) {
        exoPlayer.seekTo(ms)
        _state.update { it.copy(positionMs = ms) }
    }

    override fun setPlaybackSpeed(speed: Float) {
        exoPlayer.playbackParameters = exoPlayer.playbackParameters.withSpeed(speed)
        _state.update { it.copy(playbackSpeed = speed) }
    }

    override fun toggleShuffle(): Boolean {
        val newShuffleState = !exoPlayer.shuffleModeEnabled
        exoPlayer.shuffleModeEnabled = newShuffleState
        _state.update { it.copy(isShuffleEnabled = newShuffleState) }
        return newShuffleState
    }

    override fun toggleRepeatMode(): RepeatMode {
        val nextMode = when (exoPlayer.repeatMode) {
            Player.REPEAT_MODE_OFF -> {
                exoPlayer.repeatMode = Player.REPEAT_MODE_ALL
                RepeatMode.ALL
            }
            Player.REPEAT_MODE_ALL -> {
                exoPlayer.repeatMode = Player.REPEAT_MODE_ONE
                RepeatMode.ONE
            }
            else -> {
                exoPlayer.repeatMode = Player.REPEAT_MODE_OFF
                RepeatMode.OFF
            }
        }
        _state.update { it.copy(repeatMode = nextMode) }
        return nextMode
    }

    override fun registerAudioProcessor(processor: AudioProcessor) {
        // зарезервировано для  модов
    }

    private fun startProgressTracking() {
        progressJob?.cancel()
        progressJob = coroutineScope.launch {
            while (isActive) {
                val currentPosition = exoPlayer.currentPosition.coerceAtLeast(0L)
                _state.update { it.copy(positionMs = currentPosition) }
                delay(500L)
            }
        }
    }

    private fun stopProgressTracking() {
        progressJob?.cancel()
        progressJob = null
    }

    fun release() {
        stopProgressTracking()
        equalizer?.release()
        equalizer = null
        exoPlayer.release()
    }
}