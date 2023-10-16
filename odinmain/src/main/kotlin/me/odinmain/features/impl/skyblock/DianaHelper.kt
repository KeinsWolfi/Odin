package me.odinmain.features.impl.skyblock

import me.odinmain.events.impl.ChatPacketEvent
import me.odinmain.events.impl.PacketSentEvent
import me.odinmain.features.Category
import me.odinmain.features.Module
import me.odinmain.features.settings.Setting.Companion.withDependency
import me.odinmain.features.settings.impl.BooleanSetting
import me.odinmain.features.settings.impl.ColorSetting
import me.odinmain.utils.VecUtils.addVec
import me.odinmain.utils.VecUtils.toVec3i
import me.odinmain.utils.clock.Clock
import me.odinmain.utils.floor
import me.odinmain.utils.render.Color
import me.odinmain.utils.render.world.RenderUtils
import me.odinmain.utils.skyblock.ChatUtils
import me.odinmain.utils.skyblock.DianaBurrowEstimate
import me.odinmain.utils.skyblock.PlayerUtils
import net.minecraft.network.play.client.C07PacketPlayerDigging
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement
import net.minecraft.network.play.server.S29PacketSoundEffect
import net.minecraft.network.play.server.S2APacketParticles
import net.minecraft.util.Vec3
import net.minecraft.util.Vec3i
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent


object DianaHelper : Module(
    name = "Diana Helper",
    description = "Helps with Diana's event.",
    category = Category.SKYBLOCK,
    tag = TagType.NEW
) {
    private val guessColor: Color by ColorSetting("Guess Color", default = Color.WHITE)
    private val tracer: Boolean by BooleanSetting("Tracer", default = false)
    private val sendInqMsg: Boolean by BooleanSetting("Send Inq Msg", default = true)
    private val showWarpSettings: Boolean by BooleanSetting("Show Warp Settings", default = true)
    private val castle: Boolean by BooleanSetting("Castle Warp").withDependency { showWarpSettings }
    private val crypt: Boolean by BooleanSetting("Crypt Warp").withDependency { showWarpSettings }
    private val darkAuction: Boolean by BooleanSetting("DA Warp").withDependency { showWarpSettings }
    private val museum: Boolean by BooleanSetting("Museum Warp").withDependency { showWarpSettings }
    private val wizard: Boolean by BooleanSetting("Wizard Warp").withDependency { showWarpSettings }
    private var warpLocation: WarpPoint? = null

    private val cmdCooldown = Clock(3_000)
    var renderPos: Vec3? = null
    val burrowsRender = mutableMapOf<Vec3i, BurrowType>()

    enum class BurrowType(val text: String, val color: Color) {
        START("§aStart", Color.GREEN),
        MOB("§cMob", Color.RED),
        TREASURE("§6Treasure", Color.GOLD),
        UNKNOWN("§fUnknown?!", Color.WHITE),
    }


    init {
        onMessage(Regex("Woah! You dug out a Minos Inquisitor!"), { sendInqMsg && enabled }) {
            ChatUtils.partyMessage("x: ${PlayerUtils.posX.floor()}, y: ${PlayerUtils.posY.floor()}, z: ${PlayerUtils.posZ.floor()}")
            PlayerUtils.alert("§a§lInquisitor!")
        }

        onPacket(S29PacketSoundEffect::class.java) { DianaBurrowEstimate.handleSoundPacket(it) }

        onPacket(S2APacketParticles::class.java) { DianaBurrowEstimate.handleParticlePacket(it) }

        onPacket(S2APacketParticles::class.java) { DianaBurrowEstimate.handleBurrow(it) }

        onWorldLoad {
            DianaBurrowEstimate.reset()
            burrowsRender.clear()
            renderPos = null
            DianaBurrowEstimate.recentBurrows.clear()
        }
    }

    @SubscribeEvent
    fun onChat(event: ChatPacketEvent) { DianaBurrowEstimate.chat(event) }

    @SubscribeEvent
    fun onInteract(event: PacketSentEvent) {
        if (event.packet is C08PacketPlayerBlockPlacement)
            DianaBurrowEstimate.blockEvent(event.packet.position.toVec3i())
        else if (event.packet is C07PacketPlayerDigging)
            DianaBurrowEstimate.blockEvent(event.packet.position.toVec3i())
    }

    @SubscribeEvent
    fun onRenderWorld(event: RenderWorldLastEvent) {
        renderPos?.let { guess ->
            warpLocation = WarpPoint.entries.filter { it.unlocked() }.minBy { warp ->
                warp.location.distanceTo(guess)
            }.takeIf { it.location.distanceTo(guess) + 20 < mc.thePlayer.positionVector.distanceTo(guess) }
            RenderUtils.renderCustomBeacon("§6Guess${warpLocation?.displayName ?: ""}§r", guess, guessColor, event.partialTicks)
            if (tracer)
                RenderUtils.draw3DLine(mc.thePlayer.positionVector.addVec(y = mc.thePlayer.eyeHeight.toDouble()), guess, guessColor, 5, true, event.partialTicks)
        }

        burrowsRender.forEach { (location, type) ->
            RenderUtils.renderCustomBeacon(type.text, Vec3(location), type.color, event.partialTicks)
        }
    }

    override fun onKeybind() {
        if (!cmdCooldown.hasTimePassed()) return
        warpLocation?.let { ChatUtils.sendCommand("warp ${it.name}") }
    }

    private enum class WarpPoint(
        val displayName: String,
        val location: Vec3,
        var unlocked: () -> Boolean
    ) {
        HUB     (displayName = " §8(Hub)",          Vec3(-3.0, 70.0, -70.0),   { true }),
        CASTLE  (displayName = " §8(Castle)",       Vec3(-250.0, 130.0, 45.0), { castle }),
        CRYPT   (displayName = " §8(Crypt)",        Vec3(-190.0, 74.0, -88.0), { crypt }),
        DA      (displayName = " §8(Dark Auction)", Vec3(91.0, 74.0, 17.03),   { darkAuction }),
        MUSEUM  (displayName = " §8(Museum)",       Vec3(-75.0, 76.0, 81.0),   { museum }),
        WIZARD  (displayName = " §8(Wizard)",       Vec3(42.5, 122.0, 69.0),   { wizard })
    }
}