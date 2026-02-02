package net.superscary.scrollabletooltips.tooltip;

import lombok.Getter;
import lombok.Setter;
import net.superscary.scrollabletooltips.config.TooltipScrollConfig;
import net.superscary.scrollabletooltips.keybind.ScrollKeyHandler;

/**
 * Singleton manager for the tooltip scroll service.
 * Provides global access to the scroll service instance.
 */
public class TooltipScrollManager {
    
    private static final TooltipScrollService SERVICE = new TooltipScrollService();
    /**
     * -- SETTER --
     *  Sets the keybind handler for checking scroll key state.
     * <p>
     * -- GETTER --
     *  Gets the current keybind handler.
     *
     */
    @Getter
    @Setter
    private static ScrollKeyHandler keyHandler = null;
    
    /**
     * Gets the global tooltip scroll service instance.
     *
     * @return The tooltip scroll service
     */
    public static TooltipScrollService getService() {
        return SERVICE;
    }
    
    /**
     * Initializes the manager with the platform-specific config.
     *
     * @param config The config instance
     */
    public static void initialize(TooltipScrollConfig config) {
        SERVICE.setConfig(config);
    }

    /**
     * Checks if the scroll key is currently held.
     *
     * @return true if the key is held or if no handler is set
     */
    public static boolean isScrollKeyHeld() {
        return keyHandler == null || keyHandler.isScrollKeyHeld();
    }
    
    /**
     * Convenience method to process a mouse scroll event.
     *
     * @param delta The scroll wheel delta
     * @return true if the scroll was consumed
     */
    public static boolean onMouseScroll(double delta) {
        return SERVICE.onMouseScroll(delta);
    }
    
    /**
     * Convenience method to check if scrolling is enabled.
     *
     * @return true if enabled
     */
    public static boolean isEnabled() {
        return SERVICE.isEnabled();
    }
}
