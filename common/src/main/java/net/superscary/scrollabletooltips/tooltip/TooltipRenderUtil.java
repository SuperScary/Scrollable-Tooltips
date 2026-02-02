package net.superscary.scrollabletooltips.tooltip;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import org.joml.Matrix4f;

/**
 * Utility class for tooltip rendering operations including
 * scissoring and indicator drawing.
 */
public class TooltipRenderUtil {
    
    /**
     * Height of the fade indicators in pixels
     */
    public static final int INDICATOR_HEIGHT = 8;
    
    /**
     * Alpha value for indicator gradients (0-255)
     */
    private static final int INDICATOR_ALPHA = 180;
    
    /**
     * Indicator color (dark gray/black for visibility)
     */
    private static final int INDICATOR_COLOR = 0x101010;
    
    /**
     * Draws the top fade indicator showing content exists above.
     *
     * @param guiGraphics The GUI graphics context
     * @param x The x coordinate of the indicator
     * @param y The y coordinate of the indicator (top of visible area)
     * @param width The width of the indicator
     */
    public static void drawTopIndicator(GuiGraphics guiGraphics, int x, int y, int width) {
        if (width <= 0) return;
        
        // Draw gradient from opaque at top to transparent at bottom
        fillGradient(guiGraphics, 
                x, y, 
                x + width, y + INDICATOR_HEIGHT,
                (INDICATOR_ALPHA << 24) | INDICATOR_COLOR,  // Top: more opaque
                (0 << 24) | INDICATOR_COLOR);               // Bottom: transparent
        
        // Draw small arrow indicator
        drawUpArrow(guiGraphics, x + width / 2, y + 2);
    }
    
    /**
     * Draws the bottom fade indicator showing content exists below.
     *
     * @param guiGraphics The GUI graphics context
     * @param x The x coordinate of the indicator
     * @param y The y coordinate of the indicator bottom (bottom of visible area)
     * @param width The width of the indicator
     */
    public static void drawBottomIndicator(GuiGraphics guiGraphics, int x, int y, int width) {
        if (width <= 0) return;
        
        int indicatorY = y - INDICATOR_HEIGHT;

        fillGradient(guiGraphics,
                x, indicatorY,
                x + width, y,
                (0 << 24) | INDICATOR_COLOR,                // Top: transparent
                (INDICATOR_ALPHA << 24) | INDICATOR_COLOR); // Bottom: more opaque
        
        // Draw small arrow indicator
        drawDownArrow(guiGraphics, x + width / 2, y - 4);
    }
    
    /**
     * Draws a small upward-pointing arrow.
     */
    private static void drawUpArrow(GuiGraphics guiGraphics, int centerX, int topY) {
        int color = 0xFFCCCCCC;

        guiGraphics.fill(centerX, topY, centerX + 1, topY + 1, color);
        guiGraphics.fill(centerX - 1, topY + 1, centerX + 2, topY + 2, color);
        guiGraphics.fill(centerX - 2, topY + 2, centerX + 3, topY + 3, color);
    }
    
    /**
     * Draws a small downward-pointing arrow.
     */
    private static void drawDownArrow(GuiGraphics guiGraphics, int centerX, int bottomY) {
        int color = 0xFFCCCCCC;

        guiGraphics.fill(centerX - 2, bottomY - 2, centerX + 3, bottomY - 1, color);
        guiGraphics.fill(centerX - 1, bottomY - 1, centerX + 2, bottomY, color);
        guiGraphics.fill(centerX, bottomY, centerX + 1, bottomY + 1, color);
    }
    
    /**
     * Fills a rectangle with a vertical gradient.
     *
     * @param guiGraphics The GUI graphics context
     * @param x1 Left edge
     * @param y1 Top edge
     * @param x2 Right edge
     * @param y2 Bottom edge
     * @param colorTop Top color (ARGB)
     * @param colorBottom Bottom color (ARGB)
     */
    private static void fillGradient(GuiGraphics guiGraphics, int x1, int y1, int x2, int y2, int colorTop, int colorBottom) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        
        BufferBuilder bufferBuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        Matrix4f matrix = guiGraphics.pose().last().pose();
        
        float alphaTop = (float)(colorTop >> 24 & 255) / 255.0F;
        float redTop = (float)(colorTop >> 16 & 255) / 255.0F;
        float greenTop = (float)(colorTop >> 8 & 255) / 255.0F;
        float blueTop = (float)(colorTop & 255) / 255.0F;
        
        float alphaBottom = (float)(colorBottom >> 24 & 255) / 255.0F;
        float redBottom = (float)(colorBottom >> 16 & 255) / 255.0F;
        float greenBottom = (float)(colorBottom >> 8 & 255) / 255.0F;
        float blueBottom = (float)(colorBottom & 255) / 255.0F;
        
        bufferBuilder.addVertex(matrix, x2, y1, 0).setColor(redTop, greenTop, blueTop, alphaTop);
        bufferBuilder.addVertex(matrix, x1, y1, 0).setColor(redTop, greenTop, blueTop, alphaTop);
        bufferBuilder.addVertex(matrix, x1, y2, 0).setColor(redBottom, greenBottom, blueBottom, alphaBottom);
        bufferBuilder.addVertex(matrix, x2, y2, 0).setColor(redBottom, greenBottom, blueBottom, alphaBottom);
        
        BufferUploader.drawWithShader(bufferBuilder.buildOrThrow());
        RenderSystem.disableBlend();
    }
    
    /**
     * Enables scissor testing for the given clip rectangle.
     * Converts from GUI coordinates to OpenGL window coordinates.
     *
     * @param clipRect The clip rectangle in GUI coordinates
     * @param guiScale The current GUI scale factor
     * @param windowHeight The window height in pixels
     */
    public static void enableScissor(ClipRect clipRect, double guiScale, int windowHeight) {
        if (clipRect == null) return;
        
        int x = (int) (clipRect.x() * guiScale);
        int width = (int) (clipRect.width() * guiScale);
        int height = (int) (clipRect.height() * guiScale);
        // OpenGL Y is from bottom, GUI Y is from top - flip it
        int y = windowHeight - (int) ((clipRect.y() + clipRect.height()) * guiScale);
        
        RenderSystem.enableScissor(x, y, width, height);
    }
    
    /**
     * Disables scissor testing.
     */
    public static void disableScissor() {
        RenderSystem.disableScissor();
    }
    
    /**
     * Enables scissor testing using GuiGraphics built-in method.
     * This handles coordinate conversion automatically.
     *
     * @param guiGraphics The GUI graphics context
     * @param clipRect The clip rectangle
     */
    public static void enableScissorGuiGraphics(GuiGraphics guiGraphics, ClipRect clipRect) {
        if (clipRect == null) return;
        guiGraphics.enableScissor(clipRect.x(), clipRect.y(), clipRect.right(), clipRect.bottom());
    }
    
    /**
     * Disables scissor testing using GuiGraphics built-in method.
     *
     * @param guiGraphics The GUI graphics context
     */
    public static void disableScissorGuiGraphics(GuiGraphics guiGraphics) {
        guiGraphics.disableScissor();
    }
    
    /**
     * Enables scissor using RenderSystem directly.
     * This is more reliable for tooltip rendering.
     *
     * @param clipRect The clip rectangle in GUI coordinates
     * @param guiScale The GUI scale
     * @param windowHeight The actual window height in pixels
     */
    public static void enableScissorDirect(ClipRect clipRect, double guiScale, int windowHeight) {
        if (clipRect == null) return;
        
        int scaledX = (int) (clipRect.x() * guiScale);
        int scaledWidth = (int) (clipRect.width() * guiScale);
        int scaledHeight = (int) (clipRect.height() * guiScale);

        int scaledY = windowHeight - (int) ((clipRect.y() + clipRect.height()) * guiScale);
        
        RenderSystem.enableScissor(scaledX, scaledY, scaledWidth, scaledHeight);
    }
    
    /**
     * Disables scissor using RenderSystem directly.
     */
    public static void disableScissorDirect() {
        RenderSystem.disableScissor();
    }
}
