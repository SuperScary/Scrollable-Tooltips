package net.superscary.scrollabletooltips.forge.keybind;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.superscary.scrollabletooltips.Constants;
import net.superscary.scrollabletooltips.keybind.ScrollKeyHandler;
import org.lwjgl.glfw.GLFW;

/**
 * Forge implementation of the scroll keybind handler.
 */
public class ForgeScrollKeyHandler implements ScrollKeyHandler {
    
    public static final String CATEGORY = "key.categories." + Constants.MOD_ID;
    public static final String KEY_SCROLL = "key." + Constants.MOD_ID + ".scroll";
    
    private static KeyMapping scrollKey;
    private static ForgeScrollKeyHandler instance;
    
    public ForgeScrollKeyHandler() {
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
    public static ForgeScrollKeyHandler getInstance() {
        if (instance == null) {
            instance = new ForgeScrollKeyHandler();
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
