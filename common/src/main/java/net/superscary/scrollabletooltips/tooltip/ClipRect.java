package net.superscary.scrollabletooltips.tooltip;

/**
 * Represents a clipping/scissor rectangle for tooltip rendering.
 */
public record ClipRect(int x, int y, int width, int height) {
    
    /**
     * Creates a ClipRect with the given parameters.
     * @param x The x coordinate of the clip region
     * @param y The y coordinate of the clip region
     * @param width The width of the clip region
     * @param height The height of the clip region
     * @return A new ClipRect instance
     */
    public static ClipRect of(int x, int y, int width, int height) {
        return new ClipRect(x, y, width, height);
    }
    
    /**
     * @return The right edge of the clip region (x + width)
     */
    public int right() {
        return x + width;
    }
    
    /**
     * @return The bottom edge of the clip region (y + height)
     */
    public int bottom() {
        return y + height;
    }
    
    /**
     * Checks if a point is within this clip region.
     * @param px The x coordinate to check
     * @param py The y coordinate to check
     * @return true if the point is within the clip region
     */
    public boolean contains(int px, int py) {
        return px >= x && px < right() && py >= y && py < bottom();
    }
}
