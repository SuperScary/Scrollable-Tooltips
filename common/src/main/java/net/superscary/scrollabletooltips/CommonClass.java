package net.superscary.scrollabletooltips;

import net.superscary.scrollabletooltips.platform.Services;

/**
 * Common initialization class shared between all supported loaders.
 * Code written here can only import and access the vanilla codebase and common libraries.
 */
public class CommonClass {

    /**
     * Common initialization called by each loader.
     * Loader-specific config and client initialization happens in the platform modules.
     */
    public static void init() {
        Constants.LOG.info("Scrollable Tooltips initializing on {}!", Services.PLATFORM.getPlatformName());
    }
}
