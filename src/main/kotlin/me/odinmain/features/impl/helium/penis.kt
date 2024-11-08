package me.odinmain.features.impl.helium

import me.odinmain.features.Category
import me.odinmain.features.Module
import me.odinmain.utils.skyblock.modMessage
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

object penis : Module(
    name = "W",
    description = "W.",
    category = Category.HELIUM
) {
    override fun onEnable() {
        modMessage("Real?")
        super.onEnable()
    }
}