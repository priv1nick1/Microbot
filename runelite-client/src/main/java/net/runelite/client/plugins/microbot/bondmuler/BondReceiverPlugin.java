package net.runelite.client.plugins.microbot.bondmuler;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.awt.*;

@PluginDescriptor(
    name = "nICK Bond Receiver",
    description = "Receives bonds from master account automatically",
    tags = {"bond", "muling", "ironman", "nick"},
    enabledByDefault = false
)
@Slf4j
public class BondReceiverPlugin extends Plugin {
    
    @Inject
    private BondReceiverConfig config;
    
    @Inject
    private OverlayManager overlayManager;
    
    @Inject
    private BondReceiverOverlay overlay;
    
    @Provides
    BondReceiverConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(BondReceiverConfig.class);
    }
    
    private BondReceiverScript script;
    
    @Override
    protected void startUp() throws AWTException {
        if (overlayManager != null) {
            overlayManager.add(overlay);
        }
        
        script = new BondReceiverScript();
        script.run(config);
        
        log.info("Bond Receiver plugin started!");
    }
    
    @Override
    protected void shutDown() {
        if (overlayManager != null) {
            overlayManager.remove(overlay);
        }
        
        if (script != null) {
            script.shutdown();
        }
        
        log.info("Bond Receiver plugin stopped!");
    }
}

