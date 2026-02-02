package net.superscary.scrollabletooltips.neoforge.config;

import lombok.Getter;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.superscary.scrollabletooltips.config.TooltipScrollConfig;

/**
 * NeoForge implementation of tooltip scroll config using NeoForge's config system.
 */
public class NeoForgeTooltipScrollConfig implements TooltipScrollConfig {

    /**
     * -- GETTER --
     *  Gets the singleton instance.
     */
    @Getter
    private static NeoForgeTooltipScrollConfig instance;
    
    private final ModConfigSpec.BooleanValue enabled;
    private final ModConfigSpec.IntValue maxTooltipHeightPx;
    private final ModConfigSpec.IntValue scrollSpeedPx;
    private final ModConfigSpec.BooleanValue onlyWhenOverflowed;
    private final ModConfigSpec.BooleanValue showIndicators;
    private final ModConfigSpec.BooleanValue requireKeyHold;
    
    private final ModConfigSpec spec;
    
    private NeoForgeTooltipScrollConfig() {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        
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
     * Registers the config with NeoForge.
     *
     * @param modContainer The mod container
     */
    public static NeoForgeTooltipScrollConfig register(ModContainer modContainer) {
        instance = new NeoForgeTooltipScrollConfig();
        modContainer.registerConfig(ModConfig.Type.CLIENT, instance.spec);
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
