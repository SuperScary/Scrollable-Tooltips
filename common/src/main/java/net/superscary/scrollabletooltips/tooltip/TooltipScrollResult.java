package net.superscary.scrollabletooltips.tooltip;

import lombok.Builder;
import lombok.Getter;

/**
 * Result of tooltip scroll computation, containing all information needed
 * to render a scrollable tooltip.
 */
@Getter
@Builder
public class TooltipScrollResult {
    
    /**
     * Whether scrollable tooltip rendering is active for this frame
     */
    private final boolean active;
    
    /**
     * The vertical scroll offset in pixels (how much to translate the content up)
     */
    private final int scrollOffsetPx;
    
    /**
     * The clipping rectangle for the tooltip content area
     */
    private final ClipRect clipRect;
    
    /**
     * Whether to show the top indicator (content exists above)
     */
    private final boolean showTopIndicator;
    
    /**
     * Whether to show the bottom indicator (content exists below)
     */
    private final boolean showBottomIndicator;
    
    /**
     * Creates an inactive result (no scrolling needed)
     */
    public static TooltipScrollResult inactive() {
        return TooltipScrollResult.builder()
                .active(false)
                .scrollOffsetPx(0)
                .clipRect(null)
                .showTopIndicator(false)
                .showBottomIndicator(false)
                .build();
    }
}
