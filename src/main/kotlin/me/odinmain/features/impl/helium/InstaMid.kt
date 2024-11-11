package me.odinmain.features.impl.helium

import me.odinmain.events.impl.PacketReceivedEvent
import me.odinmain.events.impl.PacketSentEvent
import me.odinmain.features.Category
import me.odinmain.features.Module
import me.odinmain.utils.PacketUtils
import me.odinmain.utils.skyblock.modMessage
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.network.play.client.C0CPacketInput
import net.minecraft.network.play.server.S1BPacketEntityAttach
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import kotlin.math.abs
import kotlin.math.pow

object InstaMid: Module(
    name = "InstaMid",
    description = "Instantly mid to Necrons platform",
    category = Category.HELIUM
) {
    private var prep = true
    private var reg = false

    @SubscribeEvent
    fun onPacketSend(p: PacketSentEvent) {
        if(!reg) return
        if(p.packet is C03PacketPlayer || p.packet is C0CPacketInput) {
            p.isCanceled = true
            val riding = mc.thePlayer.isRiding
            if(riding) prep = false
            if(!riding && !prep) {
                reg = false
                prep = true
                PacketUtils.sendPacket(C03PacketPlayer.C06PacketPlayerPosLook(54.0, 65.0, 76.0, 0f, 0f, false))
            }
        }
    }

    @SubscribeEvent
    fun onPacketReceive(p: PacketReceivedEvent) {
        if(p.packet !is S1BPacketEntityAttach) return
        if(!isOnPlatform()) return
        if(p.packet.entityId != mc.thePlayer.entityId) return
        if(p.packet.vehicleEntityId < 0) return
        prep = true
        reg = true
        modMessage("Trying to InstaMid")
    }

    private fun isOnPlatform(): Boolean {
        if (mc.thePlayer.posY > 100) return false;
        if (mc.thePlayer.posY < 64) return false;
        return (abs(mc.thePlayer.posX - 54.5).pow(2.0) + abs(mc.thePlayer.posZ - 76.5).pow(2.0) < 56.25)
    }
}