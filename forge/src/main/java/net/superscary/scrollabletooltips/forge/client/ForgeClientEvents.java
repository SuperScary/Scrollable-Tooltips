package net.superscary.scrollabletooltips.forge.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.superscary.scrollabletooltips.Constants;
import net.superscary.scrollabletooltips.tooltip.TooltipScrollManager;

/**
 * Forge client event handlers for tooltip scrolling.
 */
@Mod.EventBusSubscriber(modid = Constants.MOD_ID, value = Dist.CLIENT)
public class ForgeClientEvents {
    
    /**
     * Handle mouse scroll events on screens.
     * If a scrollable tooltip is active, consume the scroll for tooltip scrolling.
     */
    @SubscribeEvent
    public static void onScreenMouseScrolled(ScreenEvent.MouseScrolled.Pre event) {
        if (!TooltipScrollManager.isEnabled()) {
            return;
        }
        
        double delta = event.getDeltaY();
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
