package net.superscary.scrollabletooltips;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.superscary.scrollabletooltips.forge.config.ForgeTooltipScrollConfig;
import net.superscary.scrollabletooltips.forge.keybind.ForgeScrollKeyHandler;
import net.superscary.scrollabletooltips.tooltip.TooltipScrollManager;

@Mod(Constants.MOD_ID)
public class Scrollabletooltips {

    public Scrollabletooltips() {
        CommonClass.init();

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            ForgeTooltipScrollConfig config = ForgeTooltipScrollConfig.register();
            TooltipScrollManager.initialize(config);

            ForgeScrollKeyHandler.createKeyMapping();
            TooltipScrollManager.setKeyHandler(ForgeScrollKeyHandler.getInstance());

            FMLJavaModLoadingContext.get().getModEventBus().addListener(ForgeScrollKeyHandler::registerKeyMapping);
            
            Constants.LOG.info("Scrollable Tooltips initialized on Forge");
        });
    }
}
