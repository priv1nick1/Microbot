package net.runelite.client.plugins.microbot.bondmuler;

import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import java.awt.*;

public class BondReceiverOverlay extends OverlayPanel {
    
    @Inject
    BondReceiverOverlay() {
        super();
        setPosition(OverlayPosition.TOP_LEFT);
        setPreferredSize(new Dimension(220, 120));
    }
    
    @Override
    public Dimension render(Graphics2D graphics) {
        panelComponent.setPreferredSize(new Dimension(220, 120));
        panelComponent.getChildren().clear();
        
        // Title with version
        panelComponent.getChildren().add(TitleComponent.builder()
            .text("Bond Receiver v0.05")
            .color(Color.CYAN)
            .build());
        
        // Bond received status
        String bondStatus = BondReceiverScript.bondReceived ? "Yes" : "No";
        Color bondColor = BondReceiverScript.bondReceived ? Color.GREEN : Color.RED;
        
        panelComponent.getChildren().add(LineComponent.builder()
            .left("Bond Received:")
            .right(bondStatus)
            .rightColor(bondColor)
            .build());
        
        // Membership applied status
        String membershipStatus = BondReceiverScript.membershipApplied ? "Yes" : "No";
        Color membershipColor = BondReceiverScript.membershipApplied ? Color.GREEN : Color.RED;
        
        panelComponent.getChildren().add(LineComponent.builder()
            .left("Membership:")
            .right(membershipStatus)
            .rightColor(membershipColor)
            .build());
        
        // Status
        panelComponent.getChildren().add(LineComponent.builder()
            .left("Status:")
            .right(BondReceiverScript.currentStatus)
            .rightColor(Color.WHITE)
            .build());
        
        return super.render(graphics);
    }
}


