package net.superscary.scrollabletooltips.forge.config;

import lombok.Getter;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.superscary.scrollabletooltips.config.TooltipScrollConfig;

/**
 * Forge implementation of tooltip scroll config using Forge's config system.
 */
public class ForgeTooltipScrollConfig implements TooltipScrollConfig {

    /**
     * -- GETTER --
     *  Gets the singleton instance.
     */
    @Getter
    private static ForgeTooltipScrollConfig instance;
    
    private final ForgeConfigSpec.BooleanValue enabled;
    private final ForgeConfigSpec.IntValue maxTooltipHeightPx;
    private final ForgeConfigSpec.IntValue scrollSpeedPx;
    private final ForgeConfigSpec.BooleanValue onlyWhenOverflowed;
    private final ForgeConfigSpec.BooleanValue showIndicators;
    private final ForgeConfigSpec.BooleanValue requireKeyHold;
    
    private final ForgeConfigSpec spec;
    
    private ForgeTooltipScrollConfig() {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        
        builder.comment("Scrollable Tooltips Configuration")
                .push("general");
        
        enabled = builder
                .comment("Enable scrollable tooltips")
                .define("enabled", true);
        
        maxTooltipHeightPx = builder
                .comment("Maximum tooltip height in pixels before scrolling becomes available")
                .defineInRange("maxTooltipHeightPx", 180, 40, 600);
        
        scrollSpeedPx = builder
                .comment("Number of pixels to scroll per mouse wheel notch")
                .defineInRange("scrollSpeedPx", 10, 1, 100);
        
        onlyWhenOverflowed = builder
                .comment("Only enable scrolling when the tooltip exceeds the maximum height")
                .define("onlyWhenOverflowed", true);
        
        showIndicators = builder
                .comment("Show visual indicators when content exists above/below the visible area")
                .define("showIndicators", true);
        
        requireKeyHold = builder
                .comment("Require holding a key (default: Left Ctrl) to enable tooltip scrolling. Key can be changed in Controls settings.")
                .define("requireKeyHold", true);
        
        builder.pop();
        
        spec = builder.build();
    }
    
    /**
     * Registers the config with Forge.
     */
    public static ForgeTooltipScrollConfig register() {
        instance = new ForgeTooltipScrollConfig();
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, instance.spec);
        return instance;
    }

    @Override
    public boolean isEnabled() {
        return enabled.get();
    }
    
    @Override
    public int getMaxTooltipHeightPx() {
        return maxTooltipHeightPx.get();
    }
    
    @Override
    public int getScrollSpeedPx() {
        return scrollSpeedPx.get();
    }
    
    @Override
    public boolean isOnlyWhenOverflowed() {
        return onlyWhenOverflowed.get();
    }
    
    @Override
    public boolean isShowIndicators() {
        return showIndicators.get();
    }
    
    @Override
    public boolean isRequireKeyHold() {
        return requireKeyHold.get();
    }
}
