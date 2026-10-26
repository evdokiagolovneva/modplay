package Mod.api

import androidx.compose.runtime.Composable

interface UiSlot {
    val targetSlotId: String
    val content: @Composable () -> Unit
}
