package com.example.moddyplayer.navigation

import kotlinx.serialization.Serializable
import Mod.manifest.ModManifest
import com.example.moddyplayer.data.db.Track


// ваще тут надо не строчку а трек
@Serializable
object Player

// тут надо не стринг а другой объекть
@Serializable
object Library

// тут надо моды отобразить не знаю какая структура за них отвечает наобум написал
@Serializable
object Mods

// тут объект. Не знаю надо ли туда что-то передавать если честно, потому объекть
@Serializable
object Settings
