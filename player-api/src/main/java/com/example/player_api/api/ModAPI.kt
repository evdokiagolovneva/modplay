package com.example.player_api.api
import androidx.media3.common.MediaMetadata
import androidx.media3.common.MediaItem


interface ModAPI {


    fun onEnable(playerHost: PlayerHostAPI)

    fun onDisable()


    // По мере необходимости интерфейс может понадобиться расширить
}