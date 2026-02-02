package net.superscary.scrollabletooltips.config;

/**
 * Common interface for tooltip scroll configuration.
 * Each loader implements this with their native config system.
 */
public interface TooltipScrollConfig {
    
    /**
     * @return Whether the scrollable tooltips feature is enabled
     */
    boolean isEnabled();
    
    /**
     * @return Maximum tooltip height in pixels before scrolling becomes available
     */
    int getMaxTooltipHeightPx();
    
    /**
     * @return Number of pixels to scroll per mouse wheel notch
     */
    int getScrollSpeedPx();
    
    /**
     * @return Whether scrolling should only be enabled when the tooltip is overflowed
     */
    boolean isOnlyWhenOverflowed();
    
    /**
     * @return Whether to show visual indicators when content exists above/below the visible area
     */
    boolean isShowIndicators();
    
    /**
     * @return Whether a key must be held to enable tooltip scrolling
     */
    boolean isRequireKeyHold();
}
