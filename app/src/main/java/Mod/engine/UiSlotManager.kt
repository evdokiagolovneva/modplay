package Mod.engine
import androidx.compose.runtime.Composable

class UiSlotManager {
    private val slots = mutableMapOf<String, @Composable () -> Unit>()

    fun injectModUi(slotId: String, content: @Composable () -> Unit) {
        slots[slotId] = content
    }

    fun getSlot(slotId: String): (@Composable () -> Unit)? {
        return slots[slotId]
    }
}