package net.superscary.scrollabletooltips.tooltip;

import lombok.Getter;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;

/**
 * Manages the scroll state for tooltips.
 * Tracks scroll position and resets when context changes.
 */
public class TooltipScrollState {
    
    private static final long TOOLTIP_TIMEOUT_MS = 500;
    
    @Getter
    private int scrollPx = 0;
    
    private String lastItemSignature = "";
    private String lastScreenClass = "";
    private int lastTooltipHeight = 0;
    private int lastMaxHeight = 0;
    private long lastTooltipShownTime = 0;
    
    /**
     * Resets the scroll state if the context has changed.
     * Context changes include: different item, different screen, tooltip timeout, or tooltip size change.
     *
     * @param screen The current screen
     * @param hoveredStack The currently hovered item stack
     * @param tooltipHeightPx The total height of the tooltip in pixels
     * @param maxHeightPx The maximum visible height in pixels
     * @param nowMs The current time in milliseconds
     */
    public void resetIfContextChanged(Screen screen, ItemStack hoveredStack, int tooltipHeightPx, int maxHeightPx, long nowMs) {
        String currentItemSignature = computeItemSignature(hoveredStack);
        String currentScreenClass = screen != null ? screen.getClass().getName() : "";
        
        boolean itemChanged = !currentItemSignature.equals(lastItemSignature);
        boolean screenChanged = !currentScreenClass.equals(lastScreenClass);
        boolean timedOut = (nowMs - lastTooltipShownTime) > TOOLTIP_TIMEOUT_MS;
        boolean sizeChanged = tooltipHeightPx != lastTooltipHeight || maxHeightPx != lastMaxHeight;
        
        if (itemChanged || screenChanged || timedOut) {
            scrollPx = 0;
        } else if (sizeChanged) {
            scrollPx = Math.max(0, Math.min(scrollPx, maxScrollPx(tooltipHeightPx, maxHeightPx)));
        }
        
        lastItemSignature = currentItemSignature;
        lastScreenClass = currentScreenClass;
        lastTooltipHeight = tooltipHeightPx;
        lastMaxHeight = maxHeightPx;
        lastTooltipShownTime = nowMs;
    }
    
    /**
     * Applies mouse wheel delta to update the scroll position.
     *
     * @param wheelDelta The mouse wheel delta (positive = scroll up, negative = scroll down)
     * @param tooltipHeightPx The total height of the tooltip in pixels
     * @param maxHeightPx The maximum visible height in pixels
     * @param scrollSpeedPx The scroll speed in pixels per wheel notch
     */
    public void applyWheelDelta(double wheelDelta, int tooltipHeightPx, int maxHeightPx, int scrollSpeedPx) {
        int delta = (int) (-wheelDelta * scrollSpeedPx);
        scrollPx = Math.max(0, Math.min(scrollPx + delta, maxScrollPx(tooltipHeightPx, maxHeightPx)));
    }
    
    /**
     * Calculates the maximum scroll position.
     *
     * @param tooltipHeightPx The total height of the tooltip in pixels
     * @param maxHeightPx The maximum visible height in pixels
     * @return The maximum scroll offset in pixels
     */
    public int maxScrollPx(int tooltipHeightPx, int maxHeightPx) {
        return Math.max(0, tooltipHeightPx - maxHeightPx);
    }
    
    /**
     * Checks if the tooltip is overflowed (needs scrolling).
     *
     * @param tooltipHeightPx The total height of the tooltip in pixels
     * @param maxHeightPx The maximum visible height in pixels
     * @return true if the tooltip height exceeds the maximum height
     */
    public boolean overflowed(int tooltipHeightPx, int maxHeightPx) {
        return tooltipHeightPx > maxHeightPx;
    }
    
    /**
     * Creates a unique signature for an item stack including its components/NBT.
     *
     * @param stack The item stack to create a signature for
     * @return A string signature unique to this item and its data
     */
    private String computeItemSignature(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return "";
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append(BuiltInRegistries.ITEM.getKey(stack.getItem()));
        sb.append(":");
        sb.append(stack.getCount());

        DataComponentPatch patch = stack.getComponentsPatch();
        if (!patch.isEmpty()) {
            sb.append(":");
            sb.append(patch.hashCode());
        }
        
        return sb.toString();
    }
    
    /**
     * Resets the scroll state completely.
     */
    public void reset() {
        scrollPx = 0;
        lastItemSignature = "";
        lastScreenClass = "";
        lastTooltipHeight = 0;
        lastMaxHeight = 0;
        lastTooltipShownTime = 0;
    }
}
