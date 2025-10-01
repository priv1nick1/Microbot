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
    name = "nICK Bond Master",
    description = "Gives bonds to ironman accounts automatically",
    tags = {"bond", "muling", "ironman", "nick"},
    enabledByDefault = false
)
@Slf4j
public class BondMasterPlugin extends Plugin {
    
    @Inject
    private BondMasterConfig config;
    
    @Inject
    private OverlayManager overlayManager;
    
    @Inject
    private BondMasterOverlay overlay;
    
    @Provides
    BondMasterConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(BondMasterConfig.class);
    }
    
    private BondMasterScript script;
    
    @Override
    protected void startUp() throws AWTException {
        if (overlayManager != null) {
            overlayManager.add(overlay);
        }
        
        script = new BondMasterScript();
        script.run(config);
        
        log.info("Bond Master plugin started!");
    }
    
    @Override
    protected void shutDown() {
        if (overlayManager != null) {
            overlayManager.remove(overlay);
        }
        
        if (script != null) {
            script.shutdown();
        }
        
        log.info("Bond Master plugin stopped!");
    }
}

