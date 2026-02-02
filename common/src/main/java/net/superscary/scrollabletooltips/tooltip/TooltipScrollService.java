package net.superscary.scrollabletooltips.tooltip;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.item.ItemStack;
import net.superscary.scrollabletooltips.config.TooltipScrollConfig;

/**
 * Main service that orchestrates tooltip scrolling functionality.
 * This class manages state and computes scroll results for each frame.
 */
public class TooltipScrollService {
    
    @Getter
    private final TooltipScrollState state;

    /**
     * -- SETTER --
     *  Sets the config instance. Should be called once during mod initialization.
     * <p>
     * -- GETTER --
     *  Gets the current config instance.
     *
     */
    @Getter
    @Setter
    private TooltipScrollConfig config;

    /**
     * -- GETTER --
     *  Checks if a tooltip is currently active.
     *
     */
    @Getter
    private boolean tooltipActive = false;
    private long lastTooltipRenderTime = 0;
    private static final long TOOLTIP_ACTIVE_TIMEOUT_MS = 100; // Consider tooltip active for 100ms after render
    private int currentTooltipX = 0;
    private int currentTooltipY = 0;
    private int currentTooltipWidth = 0;
    private int currentTooltipHeight = 0;

    /**
     * -- GETTER --
     *  Gets the current hovered item stack.
     *
     */
    @Getter
    private ItemStack currentHoveredStack = ItemStack.EMPTY;

    /**
     * -- GETTER --
     *  Gets the last computed scroll result.
     *
     */
    @Getter
    private TooltipScrollResult lastResult = TooltipScrollResult.inactive();
    
    public TooltipScrollService() {
        this.state = new TooltipScrollState();
    }

    /**
     * Notifies the service that a tooltip is about to be rendered.
     * Call this before tooltip rendering begins.
     *
     * @param screen The current screen
     * @param hoveredStack The item stack being hovered (may be empty for non-item tooltips)
     * @param tooltipX The x position of the tooltip
     * @param tooltipY The y position of the tooltip
     * @param tooltipWidth The width of the tooltip
     * @param tooltipHeight The height of the tooltip
     */
    public void onTooltipRenderStart(Screen screen, ItemStack hoveredStack, 
                                      int tooltipX, int tooltipY, 
                                      int tooltipWidth, int tooltipHeight) {
        if (!isEnabled()) {
            tooltipActive = false;
            return;
        }
        
        tooltipActive = true;
        lastTooltipRenderTime = System.currentTimeMillis();
        currentTooltipX = tooltipX;
        currentTooltipY = tooltipY;
        currentTooltipWidth = tooltipWidth;
        currentTooltipHeight = tooltipHeight;
        currentHoveredStack = hoveredStack != null ? hoveredStack : ItemStack.EMPTY;
        
        // Update state
        int maxHeight = config.getMaxTooltipHeightPx();
        state.resetIfContextChanged(screen, currentHoveredStack, tooltipHeight, maxHeight, System.currentTimeMillis());
    }
    
    /**
     * Notifies the service that tooltip rendering has ended.
     */
    public void onTooltipRenderEnd() {
    }
    
    /**
     * Processes a mouse scroll event.
     *
     * @param delta The scroll wheel delta
     * @return true if the scroll was consumed (tooltip scrolled), false otherwise
     */
    public boolean onMouseScroll(double delta) {
        if (!isEnabled()) {
            return false;
        }

        if (config.isRequireKeyHold() && !TooltipScrollManager.isScrollKeyHeld()) {
            return false;
        }

        long now = System.currentTimeMillis();
        boolean wasRecentlyActive = tooltipActive || (now - lastTooltipRenderTime) < TOOLTIP_ACTIVE_TIMEOUT_MS;
        
        if (!wasRecentlyActive || currentTooltipHeight == 0) {
            return false;
        }
        
        int maxHeight = config.getMaxTooltipHeightPx();
        boolean isOverflowed = state.overflowed(currentTooltipHeight, maxHeight);

        if (config.isOnlyWhenOverflowed() && !isOverflowed) {
            return false;
        }
        
        if (!isOverflowed) {
            return false;
        }
        
        state.applyWheelDelta(delta, currentTooltipHeight, maxHeight, config.getScrollSpeedPx());
        return true;
    }
    
    /**
     * Computes the scroll result for the current tooltip.
     * Call this when rendering the tooltip to get scroll offset and clipping info.
     *
     * @param screen The current screen
     * @param hoveredStack The item stack being hovered
     * @param tooltipX The x position of the tooltip
     * @param tooltipY The y position of the tooltip
     * @param tooltipWidth The width of the tooltip
     * @param tooltipHeight The height of the tooltip
     * @param screenWidth The screen width
     * @param screenHeight The screen height
     * @return The computed scroll result
     */
    public TooltipScrollResult compute(Screen screen, ItemStack hoveredStack,
                                       int tooltipX, int tooltipY,
                                       int tooltipWidth, int tooltipHeight,
                                       int screenWidth, int screenHeight) {
        if (!isEnabled()) {
            lastResult = TooltipScrollResult.inactive();
            return lastResult;
        }
        
        int maxHeight = config.getMaxTooltipHeightPx();

        int availableHeight = screenHeight - tooltipY - 4; // 4px margin
        if (availableHeight < maxHeight) {
            maxHeight = Math.max(40, availableHeight); // Minimum 40px
        }
        
        boolean isOverflowed = state.overflowed(tooltipHeight, maxHeight);
        
        if (!isOverflowed) {
            lastResult = TooltipScrollResult.inactive();
            return lastResult;
        }

        state.resetIfContextChanged(screen, hoveredStack, tooltipHeight, maxHeight, System.currentTimeMillis());
        
        int scrollPx = state.getScrollPx();
        int maxScrollPx = state.maxScrollPx(tooltipHeight, maxHeight);

        ClipRect clipRect = ClipRect.of(
                tooltipX - 1,
                tooltipY - 1,
                tooltipWidth + 2,
                maxHeight + 2
        );

        boolean showTop = config.isShowIndicators() && scrollPx > 0;
        boolean showBottom = config.isShowIndicators() && scrollPx < maxScrollPx;
        
        lastResult = TooltipScrollResult.builder()
                .active(true)
                .scrollOffsetPx(scrollPx)
                .clipRect(clipRect)
                .showTopIndicator(showTop)
                .showBottomIndicator(showBottom)
                .build();
        
        return lastResult;
    }

    /**
     * Checks if the service is enabled via config.
     *
     * @return true if enabled, false otherwise
     */
    public boolean isEnabled() {
        return config != null && config.isEnabled();
    }

    /**
     * Resets the tooltip active state.
     * Should be called at the start of each frame.
     */
    public void resetTooltipActive() {
        tooltipActive = false;
    }

    /**
     * Gets the current tooltip bounds.
     *
     * @return Array of [x, y, width, height] or null if no tooltip active
     */
    public int[] getCurrentTooltipBounds() {
        if (!tooltipActive) return null;
        return new int[]{currentTooltipX, currentTooltipY, currentTooltipWidth, currentTooltipHeight};
    }
}
