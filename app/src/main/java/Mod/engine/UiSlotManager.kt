package Mod.engine

import com.example.player_api.api.UiSlot
import Mod.manifest.ModManifest
import androidx.compose.runtime.Composable

class UiSlotManager {
    private val activeSlots: MutableMap<String, MutableList<UiSlot>> = mutableMapOf() // id,UiSlot

    fun registerSlot(slot: UiSlot)
    {
        val list = activeSlots.getOrPut(slot.targetSlotId) { mutableListOf() }
        list.add(slot)
    }

    fun unregisterSlotsForMod(modId: String) {
        for ((_, list) in activeSlots) {
            list.removeAll { slot -> slot.modId == modId }
        }
    }


    fun unregisterSingleSlot(slot: UiSlot)
    {
        val list = activeSlots[slot.targetSlotId] ?: return

        list.remove(slot)

        if(list.isEmpty())
            activeSlots.remove(slot.targetSlotId)
    }
    @Composable
    fun Render(slotId: String) {
        val slots = activeSlots[slotId] ?: return
        for (slot in slots) {
            slot.content()
        }
    }



}