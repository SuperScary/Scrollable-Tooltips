package net.superscary.scrollabletooltips.fabric.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import lombok.Setter;
import net.fabricmc.loader.api.FabricLoader;
import net.superscary.scrollabletooltips.Constants;
import net.superscary.scrollabletooltips.config.TooltipScrollConfig;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Fabric implementation of tooltip scroll config using a JSON file.
 * Stored in config/scrollabletooltips.json
 */
public class FabricTooltipScrollConfig implements TooltipScrollConfig {
    
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String CONFIG_FILE = "scrollabletooltips.json";
    
    @Getter
    private static FabricTooltipScrollConfig instance;
    
    private ConfigData data;
    
    private FabricTooltipScrollConfig() {
        this.data = new ConfigData();
    }
    
    /**
     * Loads or creates the config file.
     *
     * @return The loaded config instance
     */
    public static FabricTooltipScrollConfig load() {
        instance = new FabricTooltipScrollConfig();
        
        Path configPath = FabricLoader.getInstance().getConfigDir().resolve(CONFIG_FILE);
        
        if (Files.exists(configPath)) {
            try (Reader reader = Files.newBufferedReader(configPath)) {
                instance.data = GSON.fromJson(reader, ConfigData.class);
                if (instance.data == null) {
                    instance.data = new ConfigData();
                }
                Constants.LOG.info("Loaded config from {}", configPath);
            } catch (IOException e) {
                Constants.LOG.error("Failed to load config, using defaults", e);
                instance.data = new ConfigData();
            }
        } else {
            // Create default config
            instance.save();
            Constants.LOG.info("Created default config at {}", configPath);
        }
        
        return instance;
    }
    
    /**
     * Saves the config to file.
     */
    public void save() {
        Path configPath = FabricLoader.getInstance().getConfigDir().resolve(CONFIG_FILE);
        
        try (Writer writer = Files.newBufferedWriter(configPath)) {
            GSON.toJson(data, writer);
        } catch (IOException e) {
            Constants.LOG.error("Failed to save config", e);
        }
    }
    
    @Override
    public boolean isEnabled() {
        return data.enabled;
    }
    
    @Override
    public int getMaxTooltipHeightPx() {
        return data.maxTooltipHeightPx;
    }
    
    @Override
    public int getScrollSpeedPx() {
        return data.scrollSpeedPx;
    }
    
    @Override
    public boolean isOnlyWhenOverflowed() {
        return data.onlyWhenOverflowed;
    }
    
    @Override
    public boolean isShowIndicators() {
        return data.showIndicators;
    }
    
    @Override
    public boolean isRequireKeyHold() {
        return data.requireKeyHold;
    }
    
    /**
     * Internal config data class for JSON serialization.
     */
    @Getter
    @Setter
    public static class ConfigData {
        private boolean enabled = true;
        private int maxTooltipHeightPx = 180;
        private int scrollSpeedPx = 10;
        private boolean onlyWhenOverflowed = true;
        private boolean showIndicators = true;
        private boolean requireKeyHold = true;
    }
}
