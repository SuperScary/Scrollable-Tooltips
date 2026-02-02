package net.superscary.scrollabletooltips.fabric.mixin;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import net.superscary.scrollabletooltips.tooltip.TooltipScrollManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin to capture the hovered slot in container screens.
 * This allows the scroll service to track which item's tooltip is being shown.
 */
@Mixin(AbstractContainerScreen.class)
public abstract class ContainerScreenMixin {
    
    @Shadow
    protected Slot hoveredSlot;
    
    /**
     * Before rendering the tooltip for an item in a container screen,
     * notify the scroll service about the hovered item.
     */
    @Inject(
            method = "renderTooltip",
            at = @At("HEAD")
    )
    private void scrollabletooltips$onRenderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY, CallbackInfo ci) {
        if (TooltipScrollManager.isEnabled() && hoveredSlot != null && hoveredSlot.hasItem()) {
            // The tooltip service will be updated with this stack when the tooltip actually renders
            // This is handled by the GuiGraphics mixin
        }
    }
}
