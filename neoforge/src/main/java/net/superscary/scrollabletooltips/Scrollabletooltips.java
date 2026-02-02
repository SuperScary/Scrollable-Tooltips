package net.superscary.scrollabletooltips;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.superscary.scrollabletooltips.neoforge.config.NeoForgeTooltipScrollConfig;
import net.superscary.scrollabletooltips.neoforge.keybind.NeoForgeScrollKeyHandler;
import net.superscary.scrollabletooltips.tooltip.TooltipScrollManager;

@Mod(Constants.MOD_ID)
public class Scrollabletooltips {

    public Scrollabletooltips(IEventBus eventBus, ModContainer modContainer) {
        CommonClass.init();

        if (FMLEnvironment.dist == Dist.CLIENT) {
            NeoForgeTooltipScrollConfig config = NeoForgeTooltipScrollConfig.register(modContainer);
            TooltipScrollManager.initialize(config);

            NeoForgeScrollKeyHandler.createKeyMapping();
            TooltipScrollManager.setKeyHandler(NeoForgeScrollKeyHandler.getInstance());

            eventBus.addListener(NeoForgeScrollKeyHandler::registerKeyMapping);
            
            Constants.LOG.info("Scrollable Tooltips initialized on NeoForge");
        }
    }
}
