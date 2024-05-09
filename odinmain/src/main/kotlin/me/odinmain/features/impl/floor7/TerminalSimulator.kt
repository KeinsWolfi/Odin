package me.odinmain.features.impl.floor7

import me.odinmain.features.Category
import me.odinmain.features.Module
import me.odinmain.features.settings.AlwaysActive
import me.odinmain.features.settings.impl.NumberSetting
import me.odinmain.features.settings.impl.StringSetting
import me.odinmain.utils.skyblock.sendCommand

@AlwaysActive
object TerminalSimulator : Module(
    name = "Terminal Simulator",
    description = "Simulates a floor 7 terminal from phase 3.",
    category = Category.FLOOR7
) {
    private val ping: String by StringSetting("Ping", "0", description = "Ping of the terminal.")
    private val repetitiveTerminals: Int by NumberSetting("Random Terminals", 1, 1, 100, description = "Amount of random terminals.")

    override fun onKeybind() {
        sendCommand(if (repetitiveTerminals == 1) "termsim $ping" else "termsim $ping $repetitiveTerminals", clientSide = true)
    }

    override fun onEnable() {
        sendCommand(if (repetitiveTerminals == 1) "termsim $ping" else "termsim $ping $repetitiveTerminals", clientSide = true)
    }
}