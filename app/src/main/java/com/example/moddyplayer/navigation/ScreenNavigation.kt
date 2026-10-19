package com.example.moddyplayer.navigation

import Mod.manifest.ModManifest
import androidx.navigation.NavController

fun NavController.toPlayer(tracks: List<String>)
{
    this.navigate(com.example.moddyplayer.navigation.Player)
}

fun NavController.toSettings()
{
    this.navigate(Settings)
}

fun NavController.toLibrary(dbMB: List<String>)
{
    this.navigate(com.example.moddyplayer.navigation.Library)
}

fun NavController.toMods(modsManifests: List<ModManifest>)
{
    this.navigate(com.example.moddyplayer.navigation.Mods)
}