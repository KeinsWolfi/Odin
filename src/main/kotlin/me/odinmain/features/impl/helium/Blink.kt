package me.odinmain.features.impl.helium

import me.odinmain.events.impl.PacketSentEvent
import me.odinmain.features.Category
import me.odinmain.features.Module
import me.odinmain.features.settings.Setting.Companion.withDependency
import me.odinmain.features.settings.impl.*
import me.odinmain.utils.PacketUtils
import me.odinmain.utils.render.Color
import me.odinmain.utils.render.getMCTextWidth
import me.odinmain.utils.render.mcText
import me.odinmain.utils.skyblock.modMessage
import net.minecraft.network.Packet
import net.minecraft.network.play.client.*
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent

object Blink: Module(
    name = "Blink",
    description = "Blink",
    category = Category.HELIUM
) {

    private val autoSend by BooleanSetting("AutoSend", true, description = "Automatically send all packets after the given ticks.")
    private val sendThresh by NumberSetting<Int>("Send Threshold", 20, 1, 40, increment = 1, description = "The amount of ticks to send the packets after.")

    private val packetsToBlink by DropdownSetting("Packets to Blink")

    private val C03 by BooleanSetting("C03", true, description = "Movement packets.").withDependency { packetsToBlink }
    private val C02 by BooleanSetting("C02", false, description = "Entity use packets.").withDependency { packetsToBlink }
    private val C0B by BooleanSetting("C0B", false, description = "Entity action packets.").withDependency { packetsToBlink }
    private val BlockInteractions by BooleanSetting("Block Interactions", false, description = "Block interaction packets. (C07, C08)").withDependency { packetsToBlink }
    private val C09 by BooleanSetting("C09", false, description = "Held item change packets.").withDependency { packetsToBlink }

    var ticks: Int = sendThresh

    private val debug by BooleanSetting("Debug", false, description = "Debug messages.")

    private val ticksUntilSend by HudSetting("Display until Send", 10f, 10f, 2f, false) {
        if(it){
            mcText("Ticks until Send: 17", 1f, 1f, 1, ticksUntilSendColor, center = false)
        } else {
            mcText("Ticks until Send: $ticks", 1f, 1f, 1, ticksUntilSendColor, center = false)
        }
        getMCTextWidth("Ticks until Send: 17") + 2f to 10f
    }
    private val ticksUntilSendColor by ColorSetting("Display Color", Color.DARK_RED, description = "The color of the 'Ticks until Send' display")

    private var packetList = ArrayList<Packet<*>>()

    @SubscribeEvent
    fun onWorldChange(e: WorldEvent.Load){
        if(enabled){
            toggle()
        }
    }

    override fun onDisable() {
        super.onDisable()
        modMessage("Sending ${packetList.size} packets.")
        packetList.forEach {
            PacketUtils.sendPacket(it)
        }
        packetList.clear()
    }

    @SubscribeEvent
    fun onPacketSend(p: PacketSentEvent) {
        if(enabled){
            if (C03 && p.packet is C03PacketPlayer) {
                packetList.add(p.packet)
                p.isCanceled = true
            } else if (C02 && p.packet is C02PacketUseEntity) {
                packetList.add(p.packet)
                p.isCanceled = true
            } else if (C0B && p.packet is C0BPacketEntityAction) {
                packetList.add(p.packet)
                p.isCanceled = true
            } else if (BlockInteractions && (p.packet is C07PacketPlayerDigging || p.packet is C08PacketPlayerBlockPlacement)) {
                packetList.add(p.packet)
                p.isCanceled = true
            } else if (C09 && p.packet is C09PacketHeldItemChange) {
                packetList.add(p.packet)
                p.isCanceled = true
            }
        }
    }

    @SubscribeEvent
    fun onTick(e: TickEvent.ClientTickEvent) {
        if (ticks == 0) {
            if (autoSend) {
                packetList.forEach {
                    PacketUtils.sendPacket(it)
                }
                packetList.clear()
                ticks = sendThresh
            }
        } else {
            ticks--
        }
    }

}