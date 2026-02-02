package net.superscary.scrollabletooltips.fabric.mixin;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.superscary.scrollabletooltips.tooltip.TooltipScrollManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin to track tooltip state on screens.
 * This helps the scroll service know when tooltips are visible.
 */
@Mixin(Screen.class)
public abstract class ScreenTooltipMixin {
    
    /**
     * Reset tooltip active state at the start of each render.
     */
    @Inject(
            method = "render",
            at = @At("HEAD")
    )
    private void scrollabletooltips$onRenderStart(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        TooltipScrollManager.getService().resetTooltipActive();
    }
}
