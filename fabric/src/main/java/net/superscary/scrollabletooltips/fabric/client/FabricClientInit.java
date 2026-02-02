package net.superscary.scrollabletooltips.fabric.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.superscary.scrollabletooltips.Constants;
import net.superscary.scrollabletooltips.fabric.config.FabricTooltipScrollConfig;
import net.superscary.scrollabletooltips.fabric.keybind.FabricScrollKeyHandler;
import net.superscary.scrollabletooltips.tooltip.TooltipScrollManager;

/**
 * Client-side initialization for Fabric.
 * Registers client events and initializes the config.
 */
@Environment(EnvType.CLIENT)
public class FabricClientInit implements ClientModInitializer {
    
    @Override
    public void onInitializeClient() {
        Constants.LOG.info("Initializing Scrollable Tooltips client on Fabric");

        FabricTooltipScrollConfig config = FabricTooltipScrollConfig.load();

        TooltipScrollManager.initialize(config);

        FabricScrollKeyHandler.register();
        TooltipScrollManager.setKeyHandler(FabricScrollKeyHandler.getInstance());
        
        Constants.LOG.info("Scrollable Tooltips client initialized");
    }
}
