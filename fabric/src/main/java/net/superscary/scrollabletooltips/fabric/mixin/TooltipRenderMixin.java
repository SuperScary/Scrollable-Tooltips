package net.superscary.scrollabletooltips.fabric.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.client.gui.screens.inventory.tooltip.TooltipRenderUtil;
import net.minecraft.world.item.ItemStack;
import net.superscary.scrollabletooltips.config.TooltipScrollConfig;
import net.superscary.scrollabletooltips.tooltip.ClipRect;
import net.superscary.scrollabletooltips.tooltip.TooltipScrollManager;
import net.superscary.scrollabletooltips.tooltip.TooltipScrollResult;
import net.superscary.scrollabletooltips.tooltip.TooltipScrollService;
import org.joml.Vector2ic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

/**
 * Mixin to handle tooltip rendering with scrolling support for Fabric.
 * Redirects background rendering to use clamped height for proper rounded corners.
 */
@Mixin(GuiGraphics.class)
public abstract class TooltipRenderMixin {
    
    @Shadow
    private ItemStack tooltipStack;
    
    @Unique
    private TooltipScrollResult scrollabletooltips$currentResult = null;
    
    @Unique
    private boolean scrollabletooltips$poseWasPushed = false;
    
    @Unique
    private boolean scrollabletooltips$scissorEnabled = false;
    
    // Store tooltip geometry for use in redirect
    @Unique
    private int scrollabletooltips$tooltipX = 0;
    @Unique
    private int scrollabletooltips$tooltipY = 0;
    @Unique
    private int scrollabletooltips$tooltipWidth = 0;
    @Unique
    private int scrollabletooltips$clampedContentHeight = 0;
    
    /**
     * Inject at the start of tooltip rendering to compute scroll state.
     */
    @Inject(
            method = "renderTooltipInternal",
            at = @At("HEAD")
    )
    private void scrollabletooltips$onTooltipRenderStart(Font font, List<ClientTooltipComponent> components, int mouseX, int mouseY, ClientTooltipPositioner positioner, CallbackInfo ci) {
        scrollabletooltips$poseWasPushed = false;
        scrollabletooltips$scissorEnabled = false;
        scrollabletooltips$tooltipX = 0;
        scrollabletooltips$tooltipY = 0;
        scrollabletooltips$tooltipWidth = 0;
        scrollabletooltips$clampedContentHeight = 0;
        
        if (!TooltipScrollManager.isEnabled() || components == null || components.isEmpty()) {
            scrollabletooltips$currentResult = null;
            return;
        }

        int tooltipWidth = 0;
        int tooltipHeight = components.size() == 1 ? -2 : 0;
        
        for (ClientTooltipComponent component : components) {
            int componentWidth = component.getWidth(font);
            if (componentWidth > tooltipWidth) {
                tooltipWidth = componentWidth;
            }
            tooltipHeight += component.getHeight();
        }
        
        Minecraft mc = Minecraft.getInstance();
        Screen screen = mc.screen;
        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();

        Vector2ic position = positioner.positionTooltip(screenWidth, screenHeight, mouseX, mouseY, tooltipWidth, tooltipHeight);
        int tooltipX = position.x();
        int tooltipY = position.y();

        int totalTooltipHeight = tooltipHeight + 8;
        int totalTooltipWidth = tooltipWidth + 8;
        
        ItemStack hoveredStack = this.tooltipStack != null ? this.tooltipStack : ItemStack.EMPTY;

        TooltipScrollService service = TooltipScrollManager.getService();
        service.onTooltipRenderStart(screen, hoveredStack, tooltipX, tooltipY, totalTooltipWidth, totalTooltipHeight);
        
        scrollabletooltips$currentResult = service.compute(screen, hoveredStack, tooltipX, tooltipY, totalTooltipWidth, totalTooltipHeight, screenWidth, screenHeight);

        if (scrollabletooltips$currentResult != null && scrollabletooltips$currentResult.isActive()) {
            TooltipScrollConfig config = service.getConfig();
            int maxHeight = config.getMaxTooltipHeightPx();
            
            scrollabletooltips$tooltipX = tooltipX;
            scrollabletooltips$tooltipY = tooltipY;
            scrollabletooltips$tooltipWidth = tooltipWidth;
            // Clamp content height (without padding)
            scrollabletooltips$clampedContentHeight = Math.min(tooltipHeight, maxHeight - 8);
        }
    }
    
    /**
     * Redirect drawManaged to render background at clamped height with proper rounded corners.
     */
    @Redirect(method = "renderTooltipInternal", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;drawManaged(Ljava/lang/Runnable;)V"))
    private void scrollabletooltips$redirectDrawManaged(GuiGraphics instance, Runnable originalRunnable) {
        if (scrollabletooltips$currentResult != null && scrollabletooltips$currentResult.isActive() && scrollabletooltips$clampedContentHeight > 0) {
            instance.drawManaged(() -> TooltipRenderUtil.renderTooltipBackground(instance, scrollabletooltips$tooltipX, scrollabletooltips$tooltipY, scrollabletooltips$tooltipWidth, scrollabletooltips$clampedContentHeight, 400));

            Minecraft mc = Minecraft.getInstance();
            instance.flush();

            ClipRect clipRect = ClipRect.of(scrollabletooltips$tooltipX, scrollabletooltips$tooltipY, scrollabletooltips$tooltipWidth, scrollabletooltips$clampedContentHeight + 2);
            
            double guiScale = mc.getWindow().getGuiScale();
            int windowHeight = mc.getWindow().getHeight();
            net.superscary.scrollabletooltips.tooltip.TooltipRenderUtil.enableScissorDirect(clipRect, guiScale, windowHeight);
            scrollabletooltips$scissorEnabled = true;
        } else {
            instance.drawManaged(originalRunnable);
        }
    }
    
    /**
     * Inject after drawManaged to apply translation for text content scrolling.
     */
    @Inject(method = "renderTooltipInternal", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;drawManaged(Ljava/lang/Runnable;)V", shift = At.Shift.AFTER))
    private void scrollabletooltips$afterBackgroundRender(Font font, List<ClientTooltipComponent> components, int mouseX, int mouseY, ClientTooltipPositioner positioner, CallbackInfo ci) {
        if (scrollabletooltips$currentResult != null && scrollabletooltips$currentResult.isActive()) {
            GuiGraphics guiGraphics = (GuiGraphics) (Object) this;

            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(0, -scrollabletooltips$currentResult.getScrollOffsetPx(), 0);
            scrollabletooltips$poseWasPushed = true;
        }
    }
    
    /**
     * Inject at the end to clean up and draw indicators.
     */
    @Inject(method = "renderTooltipInternal", at = @At("RETURN"))
    private void scrollabletooltips$onTooltipRenderEnd(Font font, List<ClientTooltipComponent> components, int mouseX, int mouseY, ClientTooltipPositioner positioner, CallbackInfo ci) {
        if (scrollabletooltips$currentResult != null && scrollabletooltips$currentResult.isActive()) {
            GuiGraphics guiGraphics = (GuiGraphics) (Object) this;

            guiGraphics.flush();
            
            if (scrollabletooltips$poseWasPushed) {
                guiGraphics.pose().popPose();
                scrollabletooltips$poseWasPushed = false;
            }

            if (scrollabletooltips$scissorEnabled) {
                net.superscary.scrollabletooltips.tooltip.TooltipRenderUtil.disableScissorDirect();
                scrollabletooltips$scissorEnabled = false;
            }

            if (scrollabletooltips$currentResult.isShowTopIndicator()) {
                net.superscary.scrollabletooltips.tooltip.TooltipRenderUtil.drawTopIndicator(
                        guiGraphics, 
                        scrollabletooltips$tooltipX, 
                        scrollabletooltips$tooltipY, 
                        scrollabletooltips$tooltipWidth
                );
            }
            if (scrollabletooltips$currentResult.isShowBottomIndicator()) {
                net.superscary.scrollabletooltips.tooltip.TooltipRenderUtil.drawBottomIndicator(
                        guiGraphics, 
                        scrollabletooltips$tooltipX, 
                        scrollabletooltips$tooltipY + scrollabletooltips$clampedContentHeight + 2,
                        scrollabletooltips$tooltipWidth
                );
            }
            
            TooltipScrollManager.getService().onTooltipRenderEnd();
        }
        
        scrollabletooltips$currentResult = null;
    }
}
