package com.example.moddyplayer.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.navigation.compose.NavHost
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.moddyplayer.presentation.LibraryScreen
import com.example.moddyplayer.presentation.ModsScreen
import com.example.moddyplayer.presentation.PlayerScreen
import com.example.moddyplayer.presentation.SettingsScreen
import kotlin.collections.emptyList
@Preview
@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
)
{
    Column(Modifier.fillMaxSize())
    {
        NavHost(
            navController = navController,
            startDestination = Settings
        )
        {
            composable<Player>
            {
                //           backStackEntry -> val playerData = backStackEntry.toRoute<Player>()
                PlayerScreen(emptyList())
            }
            composable<Library>()
            {
                //           backStackEntry -> val libraryData = backStackEntry.toRoute<Library>()
                LibraryScreen(emptyList()/*libraryData.nashadbMB*/)

            }
            composable<Mods>()
            {
                //          backStackEntry -> val modsData = backStackEntry.toRoute<Mods>()
                ModsScreen(emptyList()/*modsData.nashiModiMB*/)
            }
            composable<Settings>()
            {
                SettingsScreen()
            }
        }
        Row(Modifier
            .fillMaxWidth()
            .padding(12.dp))
        {
            Button({ navController.navigate(Player) }, Modifier.weight(1f))
            { Text("Player") }
            Button({ navController.navigate(Library) }, Modifier.weight(1f))
            { Text("Library") }
            Button({ navController.navigate(Mods) }, Modifier.weight(1f))
             { Text("Mods") }
            Button({ navController.navigate(Settings) }, Modifier.weight(1f))
            { Text("Settings") }

        }
    }
}

