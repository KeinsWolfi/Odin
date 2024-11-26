package me.odinmain.features.impl.floor7.p3.termsim

import me.odinmain.events.impl.GuiEvent
import me.odinmain.features.impl.floor7.p3.TerminalSounds
import me.odinmain.features.impl.floor7.p3.TerminalSounds.clickSounds
import me.odinmain.utils.postAndCatch
import net.minecraft.inventory.ContainerChest
import net.minecraft.inventory.Slot
import net.minecraft.item.ItemStack
import kotlin.math.floor

object CorrectPanes : TermSimGui(
    "Correct all the panes!", 45
) {
    private val greenPane get() = ItemStack(pane, 1, 5 ).apply { setStackDisplayName("") }
    private val redPane   get() = ItemStack(pane, 1, 14).apply { setStackDisplayName("") }

    override fun create() {
        this.inventorySlots.inventorySlots.subList(0, 45).forEachIndexed { index, it ->
            if (floor(index / 9.0) in 1.0..3.0 && index % 9 in 2..6) it.putStack(if (Math.random() > 0.75) greenPane else redPane)
            else it.putStack(blackPane)
        }
    }

    override fun slotClick(slot: Slot, button: Int) {
        if (slot.stack?.metadata == 14) slot.putStack(greenPane) else slot.putStack(redPane)
        if (!TerminalSounds.enabled || !clickSounds) mc.thePlayer.playSound("random.orb", 1f, 1f)
        GuiEvent.Loaded(name, inventorySlots as ContainerChest).postAndCatch()
        if (inventorySlots.inventorySlots.subList(0, 45).none { it?.stack?.metadata == 14 })
            solved(this.name, 0)
    }
}