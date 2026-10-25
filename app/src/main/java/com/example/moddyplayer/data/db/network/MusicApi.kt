package com.example.moddyplayer.data.db.network

import retrofit2.http.GET

interface MusicApi {
    @GET("tracks") // потом на реальный путь API
    suspend fun getTracks(): List<NetworkTrackDto>
}