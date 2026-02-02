package net.superscary.scrollabletooltips.fabric.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.superscary.scrollabletooltips.tooltip.TooltipScrollManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin to intercept mouse scroll events for tooltip scrolling.
 */
@Mixin(MouseHandler.class)
public class MouseScrollMixin {
    
    @Shadow
    @Final
    private Minecraft minecraft;
    
    /**
     * Intercepts mouse scroll events to handle tooltip scrolling.
     * If a scrollable tooltip is active and consumes the scroll, we prevent further processing.
     */
    @Inject(
            method = "onScroll",
            at = @At("HEAD"),
            cancellable = true
    )
    private void scrollabletooltips$onScroll(long windowPointer, double xOffset, double yOffset, CallbackInfo ci) {
        if (windowPointer != minecraft.getWindow().getWindow()) {
            return;
        }

        if (minecraft.screen == null) {
            return;
        }

        if (TooltipScrollManager.isEnabled()) {
            if (TooltipScrollManager.onMouseScroll(yOffset)) {
                ci.cancel();
            }
        }
    }
}
