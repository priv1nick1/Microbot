package net.runelite.client.plugins.microbot.nick1privatehunter;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.awt.*;

@PluginDescriptor(
        name = PluginDescriptor.Default + "nICK1 Private Hunter",
        description = "Private butterfly hunting script for nICK1",
        tags = {"hunter", "butterflies", "microbot", "nick1"},
        enabledByDefault = false
)
@Slf4j
public class nICK1PrivateHunterPlugin extends Plugin {
    @Inject
    nICK1PrivateHunterScript hunterScript;
    
    @Inject
    private nICK1PrivateHunterConfig config;
    
    @Inject
    private OverlayManager overlayManager;
    
    @Inject
    private nICK1PrivateHunterOverlay hunterOverlay;

    @Provides
    nICK1PrivateHunterConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(nICK1PrivateHunterConfig.class);
    }

    @Override
    protected void startUp() throws AWTException {
        if (overlayManager != null) {
            overlayManager.add(hunterOverlay);
        }
        hunterScript.run(config);
    }

    protected void shutDown() {
        hunterScript.shutdown();
        overlayManager.remove(hunterOverlay);
    }
}

