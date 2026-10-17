package com.example.moddyplayer.player

import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import org.koin.android.ext.android.inject

class PlaybackService : MediaSessionService() {

    private var mediaSession: MediaSession? = null
    private val playerCore: PlayerCore by inject()

    override fun onCreate() {
        super.onCreate()
        val exoPlayer = (playerCore as? PlaybackController)?.exoPlayer

        if (exoPlayer != null) {
            mediaSession = MediaSession.Builder(this, exoPlayer).build()
        }
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        return mediaSession
    }

    override fun onDestroy() {
        mediaSession?.run {
            player.release()
            release()
            mediaSession = null
        }


        super.onDestroy()
    }
}