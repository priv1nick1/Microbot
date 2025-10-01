package net.runelite.client.plugins.microbot.nick1privatelibrary;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.awt.*;

@PluginDescriptor(
        name = PluginDescriptor.Default + "nICK1 Private Library",
        description = "Private Arc Library book collector for nICK1",
        tags = {"library", "books", "magic", "microbot", "nick1"},
        enabledByDefault = false
)
@Slf4j
public class nICK1PrivateLibraryPlugin extends Plugin {
    @Inject
    nICK1PrivateLibraryScript libraryScript;
    
    @Inject
    private nICK1PrivateLibraryConfig config;
    
    @Inject
    private OverlayManager overlayManager;
    
    @Inject
    private nICK1PrivateLibraryOverlay libraryOverlay;

    @Provides
    nICK1PrivateLibraryConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(nICK1PrivateLibraryConfig.class);
    }

    @Override
    protected void startUp() throws AWTException {
        if (overlayManager != null) {
            overlayManager.add(libraryOverlay);
        }
        libraryScript.run(config);
        log.info("nICK1 Private Library plugin started!");
    }

    protected void shutDown() {
        libraryScript.shutdown();
        overlayManager.remove(libraryOverlay);
        log.info("nICK1 Private Library plugin stopped!");
    }
}
