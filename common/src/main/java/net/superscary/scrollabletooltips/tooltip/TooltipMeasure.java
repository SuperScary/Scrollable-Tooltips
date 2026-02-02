package net.superscary.scrollabletooltips.tooltip;

import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * Utilities for measuring tooltip dimensions.
 */
public class TooltipMeasure {
    
    /**
     * Standard line height in Minecraft tooltips
     */
    public static final int LINE_HEIGHT = 10;
    
    /**
     * First line has additional height for spacing
     */
    public static final int FIRST_LINE_EXTRA = 2;
    
    /**
     * Vertical padding at top and bottom of tooltip
     */
    public static final int VERTICAL_PADDING = 3;
    
    /**
     * Horizontal padding on each side of tooltip
     */
    public static final int HORIZONTAL_PADDING = 3;
    
    /**
     * Additional border size for tooltip frame
     */
    public static final int BORDER_SIZE = 1;
    
    /**
     * Calculates the total height of a tooltip in pixels.
     *
     * @param textLines The text components in the tooltip
     * @param font The font used for rendering (used for potential future enhancements)
     * @return The total height in pixels including padding
     */
    public static int calculateHeight(List<Component> textLines, Font font) {
        if (textLines == null || textLines.isEmpty()) {
            return 0;
        }
        
        int lineCount = textLines.size();
        // Height = (lines * lineHeight) + first line extra + vertical padding
        int contentHeight = (lineCount * LINE_HEIGHT) + FIRST_LINE_EXTRA;
        
        // Total includes top and bottom padding
        return contentHeight + (VERTICAL_PADDING * 2) + (BORDER_SIZE * 2);
    }
    
    /**
     * Calculates the total height of a tooltip including image components.
     *
     * @param textLines The text components in the tooltip
     * @param imageComponents Optional image/special components that may add extra height
     * @param font The font used for rendering
     * @return The total height in pixels including padding
     */
    public static int calculateHeight(List<Component> textLines, List<TooltipComponent> imageComponents, Font font) {
        int baseHeight = calculateHeight(textLines, font);
        
        // Add height for image components if present
        if (imageComponents != null && !imageComponents.isEmpty()) {
            // Each image component typically adds its own height
            // This is a simplified calculation - actual height depends on component type
            for (TooltipComponent component : imageComponents) {
                // Components like BundleTooltip add additional height
                // We estimate based on common component sizes
                baseHeight += estimateComponentHeight(component);
            }
        }
        
        return baseHeight;
    }
    
    /**
     * Estimates the height of a tooltip component.
     *
     * @param component The tooltip component
     * @return Estimated height in pixels
     */
    private static int estimateComponentHeight(TooltipComponent component) {
        // Default estimation for unknown components
        // Specific components could be handled individually if needed
        return 20; // Conservative estimate
    }
    
    /**
     * Calculates the width of a tooltip based on its text content.
     *
     * @param textLines The text components in the tooltip
     * @param font The font used for rendering
     * @return The total width in pixels including padding
     */
    public static int calculateWidth(List<Component> textLines, Font font) {
        if (textLines == null || textLines.isEmpty() || font == null) {
            return 0;
        }
        
        int maxWidth = 0;
        for (Component line : textLines) {
            int lineWidth = font.width(line);
            if (lineWidth > maxWidth) {
                maxWidth = lineWidth;
            }
        }
        
        // Add horizontal padding and border
        return maxWidth + (HORIZONTAL_PADDING * 2) + (BORDER_SIZE * 2);
    }
    
    /**
     * Calculates the content height (without padding) for a tooltip.
     *
     * @param lineCount The number of lines in the tooltip
     * @return The content height in pixels
     */
    public static int calculateContentHeight(int lineCount) {
        if (lineCount <= 0) {
            return 0;
        }
        return (lineCount * LINE_HEIGHT) + FIRST_LINE_EXTRA;
    }
    
    /**
     * Gets the Y offset where tooltip content starts (accounting for padding).
     *
     * @return The Y offset in pixels
     */
    public static int getContentStartY() {
        return VERTICAL_PADDING + BORDER_SIZE;
    }
}
