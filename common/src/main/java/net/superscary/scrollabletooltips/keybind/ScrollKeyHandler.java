package net.superscary.scrollabletooltips.keybind;

/**
 * Common interface for checking if the scroll keybind is pressed.
 * Each loader implements this with their native keybind system.
 */
public interface ScrollKeyHandler {
    
    /**
     * @return true if the scroll key is currently held down
     */
    boolean isScrollKeyHeld();
}
