package com.example.moddyplayer.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.moddyplayer.player.PlaybackController
import com.example.moddyplayer.presentation.LibraryScreen
import com.example.moddyplayer.presentation.ModsScreen
import com.example.moddyplayer.presentation.PlayerScreen
import com.example.moddyplayer.presentation.SettingsScreen
import com.example.moddyplayer.repo.TrackRepository
import org.koin.compose.koinInject

@Preview
@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
) {
    // внедряем репозиторий и контроллер плеера через Koin
    val trackRepository: TrackRepository = koinInject()
    val playbackController: PlaybackController = koinInject()

    //список треков из базы данных Room в реальном времени
    val tracks by trackRepository.observeAll().collectAsState(initial = emptyList())

    Column(Modifier.fillMaxSize()) {
        NavHost(
            navController = navController,
            modifier = Modifier.weight(1f),
            //startDestination = Settings
            startDestination = Library

        ) {
            composable<Player> {
                PlayerScreen(emptyList())
            }
            composable<Library> {
                // передаем настоящий список треков из Room
                LibraryScreen(tracks = tracks)
                navController.navigate(Player)
            }
            composable<Mods> {
                ModsScreen(emptyList())
            }
            composable<Settings> {
                SettingsScreen()
            }
        }

        // нижняя навигационная панель
        Row(
            Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Button({ navController.navigate(Player) }, Modifier.weight(1f)) {
                Text("Player")
            }
            Button({ navController.navigate(Library) }, Modifier.weight(1f)) {
                Text("Library")
            }
            Button({ navController.navigate(Mods) }, Modifier.weight(1f)) {
                Text("Mods")
            }
            Button({ navController.navigate(Settings) }, Modifier.weight(1f)) {
                Text("Settings")
            }
        }
    }
}