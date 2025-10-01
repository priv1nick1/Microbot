package net.runelite.client.plugins.microbot.playerhoplog;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.awt.*;

@PluginDescriptor(
        name = PluginDescriptor.Default + "Player Hop Log",
        description = "Automatically hops to a random P2P world when another player is nearby",
        tags = {"hop", "player", "detection", "microbot", "anti-pk"},
        enabledByDefault = false
)
@Slf4j
public class PlayerHopLogPlugin extends Plugin {
    @Inject
    PlayerHopLogScript playerHopLogScript;
    
    @Inject
    private PlayerHopLogConfig config;
    
    @Inject
    private OverlayManager overlayManager;
    
    @Inject
    private PlayerHopLogOverlay playerHopLogOverlay;

    @Provides
    PlayerHopLogConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(PlayerHopLogConfig.class);
    }

    @Override
    protected void startUp() throws AWTException {
        if (overlayManager != null) {
            overlayManager.add(playerHopLogOverlay);
        }
        playerHopLogScript.run(config);
        log.info("Player Hop Log plugin started!");
    }

    protected void shutDown() {
        playerHopLogScript.shutdown();
        overlayManager.remove(playerHopLogOverlay);
        log.info("Player Hop Log plugin stopped!");
    }
}


