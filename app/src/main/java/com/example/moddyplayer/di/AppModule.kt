package com.example.moddyplayer.di

import androidx.room.Room
import com.example.moddyplayer.data.db.AppDatabase
import com.example.moddyplayer.player.PlaybackController
import com.example.moddyplayer.player.PlayerCore
import com.example.moddyplayer.repo.FavoriteRepository
import com.example.moddyplayer.repo.HistoryRepository
import com.example.moddyplayer.repo.ModRepository
import com.example.moddyplayer.repo.PlaylistRepository
import com.example.moddyplayer.repo.TrackRepository
import org.koin.dsl.module

val appModule = module {
    // База данных Room
    single {
        Room.databaseBuilder(
            get(),
            AppDatabase::class.java,
            "moddy_player_db"
        ).build()
    }

    // DAO
    single { get<AppDatabase>().trackDao() }
    single { get<AppDatabase>().playlistDao() }
    single { get<AppDatabase>().historyDao() }
    single { get<AppDatabase>().favoriteDao() }
    single { get<AppDatabase>().modDao() }

    // репозитории
    single { TrackRepository(get()) }
    single { PlaylistRepository(get()) }
    single { HistoryRepository(get()) }
    single { FavoriteRepository(get()) }
    single { ModRepository(get()) }

    // плеер (ExoPlayer / PlayerCore)
    single<PlayerCore> { PlaybackController(get()) }
}