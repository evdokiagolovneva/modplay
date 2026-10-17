package com.example.moddyplayer.data.db

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object CoverArtLoader {

    suspend fun loadAlbumArt(context: Context, trackUri: String): Bitmap? = withContext(Dispatchers.IO) {
        val retriever = MediaMetadataRetriever()
        try {
            retriever.setDataSource(context, Uri.parse(trackUri))
            val artBytes = retriever.embeddedPicture
            if (artBytes != null) {
                android.graphics.BitmapFactory.decodeByteArray(artBytes, 0, artBytes.size)
            } else {
                null
            }
        } catch (e: Exception) {
            null
        } finally {
            retriever.release()
        }
    }
}