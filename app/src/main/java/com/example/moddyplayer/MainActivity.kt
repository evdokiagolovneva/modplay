package com.example.moddyplayer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.moddyplayer.ui.theme.MOddyPlayerTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.unit.sp
import com.example.moddyplayer.navigation.AppNavHost
import com.example.moddyplayer.data.db.repo.TrackRepository
import org.koin.android.ext.android.inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope // или lifecycleScope
import kotlinx.coroutines.launch
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject


class MainActivity : ComponentActivity() {

    private val trackRepository: TrackRepository by inject()

    // Создаем лаунчер для запроса разрешений
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Если пользователь дал разрешение — запускаем сканирование музыки
            startMusicScan()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Проверяем и запрашиваем разрешение динамически
        checkAndRequestAudioPermission()

        setContent {

            AppNavHost()    // Главный экран / граф навигации вашего приложения

        }
    }

    private fun checkAndRequestAudioPermission() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_AUDIO
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        when {
            ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED -> {
                // Разрешение уже есть — сканируем
                startMusicScan()
            }
            else -> {
                // Запрашиваем у пользователя
                requestPermissionLauncher.launch(permission)
            }
        }
    }

    private fun startMusicScan() {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                trackRepository.scanAndSyncMusic()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}