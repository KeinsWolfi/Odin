package me.odinmain.utils

import net.minecraft.client.Minecraft
import net.minecraft.network.Packet
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent

object PacketUtils {

    private val packets = ArrayList<Packet<*>>()

    fun sendPacket(packet: Packet<*>) {
        Minecraft.getMinecraft().netHandler?.addToSendQueue(packet)
    }

}