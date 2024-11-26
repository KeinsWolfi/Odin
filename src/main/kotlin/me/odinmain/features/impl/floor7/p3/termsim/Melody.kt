package me.odinmain.features.impl.floor7.p3.termsim

import me.odinmain.events.impl.GuiEvent
import me.odinmain.features.impl.floor7.p3.TerminalSounds
import me.odinmain.features.impl.floor7.p3.TerminalSounds.clickSounds
import me.odinmain.utils.postAndCatch
import net.minecraft.inventory.ContainerChest
import net.minecraft.inventory.Slot
import net.minecraft.item.Item
import net.minecraft.item.ItemStack

object Melody : TermSimGui(
    "Click the button on time!",
    54
) {
    private val magentaPane get() = ItemStack(pane, 1, 2 ).apply { setStackDisplayName("") }
    private val greenPane   get() = ItemStack(pane, 1, 5 ).apply { setStackDisplayName("") }
    private val redPane     get() = ItemStack(pane, 1, 14).apply { setStackDisplayName("") }
    private val whitePane   get() = ItemStack(pane, 1, 0 ).apply { setStackDisplayName("") }
    private val redClay     get() = ItemStack(Item.getItemById(159), 1, 14).apply { setStackDisplayName("") }
    private val greenClay   get() = ItemStack(Item.getItemById(159), 1, 5 ).apply { setStackDisplayName("") }

    var magentaColumn = 1
    var limeColumn = 2
    var currentRow = 1
    var limeDirection = 1

    override fun create() {
        currentRow = 1
        magentaColumn = (1..5).random()
        limeColumn = 1
        limeDirection = 1
        updateGui()
    }

    private var counter = 0

    override fun updateScreen() {
        if (counter++ % 10 != 0) return
        limeColumn += limeDirection
        if (limeColumn == 1 || limeColumn == 5) limeDirection *= -1
        updateGui()
    }

    fun updateGui() {
        this.inventorySlots.inventorySlots.subList(0, 54).forEachIndexed { index, it ->
            when {
                index % 9 == magentaColumn && index / 9 !in 1..4 -> it.putStack(magentaPane)
                index % 9 == limeColumn && index / 9 == currentRow -> it.putStack(greenPane)
                index % 9 in 1..5 && index / 9 == currentRow -> it.putStack(redPane)
                index % 9 == 7 && index / 9 == currentRow -> it.putStack(greenClay)
                index % 9 == 7 && index / 9 in 1..4 -> it.putStack(redClay)
                index % 9 in 1..5 && index / 9 in 1..4 -> it.putStack(whitePane)
                else -> it.putStack(blackPane)
            }
        }

        GuiEvent.Loaded(name, inventorySlots as ContainerChest).postAndCatch()
    }

    override fun slotClick(slot: Slot, button: Int) {
        if (slot.slotIndex % 9 != 7 || limeColumn != magentaColumn || slot.slotIndex / 9 != currentRow) return

        currentRow++
        magentaColumn = (1 until 5).random()
        updateGui()

        if (!TerminalSounds.enabled || !clickSounds) mc.thePlayer.playSound("random.orb", 1f, 1f)
        if (currentRow >= 5) solved(this.name, 5)
    }
}


