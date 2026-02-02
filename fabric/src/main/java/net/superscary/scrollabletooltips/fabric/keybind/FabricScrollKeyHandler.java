package net.superscary.scrollabletooltips.fabric.keybind;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.superscary.scrollabletooltips.Constants;
import net.superscary.scrollabletooltips.keybind.ScrollKeyHandler;
import org.lwjgl.glfw.GLFW;

/**
 * Fabric implementation of the scroll keybind handler.
 */
public class FabricScrollKeyHandler implements ScrollKeyHandler {
    
    public static final String CATEGORY = "key.categories." + Constants.MOD_ID;
    public static final String KEY_SCROLL = "key." + Constants.MOD_ID + ".scroll";
    
    private static KeyMapping scrollKey;
    private static FabricScrollKeyHandler instance;
    
    public FabricScrollKeyHandler() {
        instance = this;
    }
    
    /**
     * Registers the keymapping with Fabric.
     */
    public static void register() {
        scrollKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(KEY_SCROLL, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_LEFT_CONTROL, CATEGORY));
    }
    
    /**
     * Gets the singleton instance.
     */
    public static FabricScrollKeyHandler getInstance() {
        if (instance == null) {
            instance = new FabricScrollKeyHandler();
        }
        return instance;
    }
    
    @Override
    public boolean isScrollKeyHeld() {
        if (scrollKey == null) {
            return true;
        }

        long windowHandle = Minecraft.getInstance().getWindow().getWindow();
        InputConstants.Key key = KeyBindingHelper.getBoundKeyOf(scrollKey);
        
        if (key.getType() == InputConstants.Type.KEYSYM) {
            return InputConstants.isKeyDown(windowHandle, key.getValue());
        } else if (key.getType() == InputConstants.Type.MOUSE) {
            return GLFW.glfwGetMouseButton(windowHandle, key.getValue()) == GLFW.GLFW_PRESS;
        }
        
        return scrollKey.isDown();
    }
}
