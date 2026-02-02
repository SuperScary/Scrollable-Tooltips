package net.superscary.scrollabletooltips.neoforge.keybind;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.superscary.scrollabletooltips.Constants;
import net.superscary.scrollabletooltips.keybind.ScrollKeyHandler;
import org.lwjgl.glfw.GLFW;

/**
 * NeoForge implementation of the scroll keybind handler.
 */
public class NeoForgeScrollKeyHandler implements ScrollKeyHandler {
    
    public static final String CATEGORY = "key.categories." + Constants.MOD_ID;
    public static final String KEY_SCROLL = "key." + Constants.MOD_ID + ".scroll";
    
    private static KeyMapping scrollKey;
    private static NeoForgeScrollKeyHandler instance;
    
    public NeoForgeScrollKeyHandler() {
        instance = this;
    }
    
    /**
     * Creates the keymapping. Should be called during mod construction.
     */
    public static void createKeyMapping() {
        scrollKey = new KeyMapping(KEY_SCROLL, KeyConflictContext.GUI, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_LEFT_CONTROL, CATEGORY);
    }
    
    /**
     * Registers the keymapping. Call this from RegisterKeyMappingsEvent.
     */
    public static void registerKeyMapping(RegisterKeyMappingsEvent event) {
        if (scrollKey != null) {
            event.register(scrollKey);
        }
    }
    
    /**
     * Gets the singleton instance.
     */
    public static NeoForgeScrollKeyHandler getInstance() {
        if (instance == null) {
            instance = new NeoForgeScrollKeyHandler();
        }
        return instance;
    }
    
    @Override
    public boolean isScrollKeyHeld() {
        if (scrollKey == null) {
            return true;
        }

        long windowHandle = Minecraft.getInstance().getWindow().getWindow();
        InputConstants.Key key = scrollKey.getKey();
        
        if (key.getType() == InputConstants.Type.KEYSYM) {
            return InputConstants.isKeyDown(windowHandle, key.getValue());
        } else if (key.getType() == InputConstants.Type.MOUSE) {
            return GLFW.glfwGetMouseButton(windowHandle, key.getValue()) == GLFW.GLFW_PRESS;
        }
        
        return scrollKey.isDown();
    }
}
