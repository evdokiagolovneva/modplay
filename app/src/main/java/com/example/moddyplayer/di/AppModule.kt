
@file:OptIn(androidx.media3.common.util.UnstableApi::class)
package com.example.moddyplayer.di

import androidx.room.Room
import com.example.moddyplayer.data.db.dataa.AppDatabase
import com.example.moddyplayer.player.PlaybackController
import com.example.moddyplayer.player.PlayerCore
import com.example.moddyplayer.data.db.repo.FavoriteRepository
import com.example.moddyplayer.data.db.repo.HistoryRepository
import com.example.moddyplayer.data.db.repo.ModRepository
import com.example.moddyplayer.data.db.repo.PlaylistRepository
import com.example.moddyplayer.data.db.repo.TrackRepository
import org.koin.dsl.module
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.moddyplayer.data.db.network.MusicApi
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import java.io.File

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
    single { TrackRepository(get(), get()) }
    single { PlaylistRepository(get()) }
    single { HistoryRepository(get()) }
    single { FavoriteRepository(get()) }
    single { ModRepository(get()) }

    // плеер (ExoPlayer / PlayerCore)
    single<PlayerCore> { PlaybackController(get(), get()) }
    single { PlaybackController(get(), get()) }

    // сеть логгер для запросов
    single {
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    // сеть OkHttpClient
    single {
        OkHttpClient.Builder()
            .addInterceptor(get<HttpLoggingInterceptor>())
            .build()
    }

    // сеть Retrofit
    single {
        Retrofit.Builder()
            .baseUrl("https://api.example.com/") // Здесь позже укажешь реальный URL (например, Jamendo API)
            .client(get())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // сеть API
    single<MusicApi> {
        get<Retrofit>().create(MusicApi::class.java)
    }


    single {
        val cacheDirectory = File(get<android.content.Context>().cacheDir, "media_cache")
        val cacheEvictor = LeastRecentlyUsedCacheEvictor(100 * 1024 * 1024) // 100 МБ
        val databaseProvider = StandaloneDatabaseProvider(get())
        SimpleCache(cacheDirectory, cacheEvictor, databaseProvider)
    }

}