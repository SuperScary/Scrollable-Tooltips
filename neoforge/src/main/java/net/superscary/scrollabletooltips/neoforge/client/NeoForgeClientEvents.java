package net.superscary.scrollabletooltips.neoforge.client;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.superscary.scrollabletooltips.Constants;
import net.superscary.scrollabletooltips.tooltip.TooltipScrollManager;

/**
 * NeoForge client event handlers for tooltip scrolling.
 */
@EventBusSubscriber(modid = Constants.MOD_ID, value = Dist.CLIENT)
public class NeoForgeClientEvents {
    
    /**
     * Handle mouse scroll events on screens.
     * If a scrollable tooltip is active, consume the scroll for tooltip scrolling.
     */
    @SubscribeEvent
    public static void onScreenMouseScrolled(ScreenEvent.MouseScrolled.Pre event) {
        if (!TooltipScrollManager.isEnabled()) {
            return;
        }
        
        double delta = event.getScrollDeltaY();
        if (TooltipScrollManager.onMouseScroll(delta)) {
            event.setCanceled(true);
        }
    }
    
    /**
     * Reset tooltip state at the start of each screen render.
     */
    @SubscribeEvent
    public static void onScreenRenderPre(ScreenEvent.Render.Pre event) {
        TooltipScrollManager.getService().resetTooltipActive();
    }
}
