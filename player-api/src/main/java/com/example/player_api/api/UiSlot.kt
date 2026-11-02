package com.example.player_api.api

import androidx.compose.runtime.Composable


class UiSlot(
    val modId: String,
    val targetSlotId: String,
    val content: @Composable () -> Unit
)
