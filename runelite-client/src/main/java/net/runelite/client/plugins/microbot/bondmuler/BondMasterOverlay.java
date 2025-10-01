package net.runelite.client.plugins.microbot.bondmuler;

import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import java.awt.*;

public class BondMasterOverlay extends OverlayPanel {
    
    @Inject
    BondMasterOverlay() {
        super();
        setPosition(OverlayPosition.TOP_LEFT);
        setPreferredSize(new Dimension(250, 150));
    }
    
    @Override
    public Dimension render(Graphics2D graphics) {
        panelComponent.setPreferredSize(new Dimension(250, 150));
        panelComponent.getChildren().clear();
        
        // Title with version
        panelComponent.getChildren().add(TitleComponent.builder()
            .text("Bond Master v0.03")
            .color(Color.ORANGE)
            .build());
        
        // Bonds given
        panelComponent.getChildren().add(LineComponent.builder()
            .left("Bonds Given:")
            .right(String.valueOf(BondMasterScript.bondsGiven))
            .rightColor(Color.GREEN)
            .build());
        
        // Total accounts
        int total = BondMasterScript.accounts != null ? BondMasterScript.accounts.size() : 0;
        panelComponent.getChildren().add(LineComponent.builder()
            .left("Total Accounts:")
            .right(String.valueOf(total))
            .rightColor(Color.CYAN)
            .build());
        
        // Current account
        String currentAccount = "None";
        if (BondMasterScript.accounts != null && 
            BondMasterScript.currentAccountIndex < BondMasterScript.accounts.size()) {
            currentAccount = BondMasterScript.accounts.get(BondMasterScript.currentAccountIndex).characterName;
        }
        
        panelComponent.getChildren().add(LineComponent.builder()
            .left("Current:")
            .right(currentAccount)
            .rightColor(Color.YELLOW)
            .build());
        
        // Status
        panelComponent.getChildren().add(LineComponent.builder()
            .left("Status:")
            .right(BondMasterScript.currentStatus)
            .rightColor(Color.WHITE)
            .build());
        
        return super.render(graphics);
    }
}


