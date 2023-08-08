package me.odinclient.mixin;

import me.odinclient.events.impl.DrawSlotEvent;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = GuiContainer.class, priority = 999)
public class MixinGuiContainer {

    private final GuiContainer gui = (GuiContainer) (Object) this;

    @Shadow
    public Container inventorySlots;

    @Inject(method = "drawSlot", at = @At("HEAD"))
    private void onDrawSlot(Slot slotIn, CallbackInfo ci) {
        MinecraftForge.EVENT_BUS.post(new DrawSlotEvent(inventorySlots, gui, slotIn));
    }
}