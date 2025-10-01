package net.runelite.client.plugins.microbot.playerhoplog;

import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import java.awt.*;

public class PlayerHopLogOverlay extends OverlayPanel {
    
    @Inject
    PlayerHopLogOverlay(PlayerHopLogPlugin plugin) {
        super(plugin);
        setPosition(OverlayPosition.TOP_RIGHT);
        setNaughty();
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        try {
            panelComponent.setPreferredSize(new Dimension(160, 120));
            
            // Title
            panelComponent.getChildren().add(TitleComponent.builder()
                    .text("Player Hop Log")
                    .color(Color.RED)
                    .build());

            panelComponent.getChildren().add(LineComponent.builder().build());

            // Display stats
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Players:")
                    .right(String.valueOf(PlayerHopLogScript.nearbyPlayers))
                    .rightColor(PlayerHopLogScript.nearbyPlayers > 0 ? Color.RED : Color.GREEN)
                    .build());
            
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Hops:")
                    .right(String.valueOf(PlayerHopLogScript.totalHops))
                    .rightColor(Color.CYAN)
                    .build());
            
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Status:")
                    .right(PlayerHopLogScript.currentStatus)
                    .rightColor(Color.WHITE)
                    .build());

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return super.render(graphics);
    }
}

