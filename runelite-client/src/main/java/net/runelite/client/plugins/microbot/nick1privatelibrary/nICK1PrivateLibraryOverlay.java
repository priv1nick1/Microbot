package net.runelite.client.plugins.microbot.nick1privatelibrary;

import net.runelite.api.Skill;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import java.awt.*;
import java.text.DecimalFormat;

public class nICK1PrivateLibraryOverlay extends OverlayPanel {
    private static final DecimalFormat NUMBER_FORMAT = new DecimalFormat("#,###");
    
    @Inject
    nICK1PrivateLibraryOverlay(nICK1PrivateLibraryPlugin plugin) {
        super(plugin);
        setPosition(OverlayPosition.TOP_LEFT);
        setNaughty();
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        try {
            panelComponent.setPreferredSize(new Dimension(220, 190));
            
            // Title
            panelComponent.getChildren().add(TitleComponent.builder()
                    .text("nICK1 Private Library")
                    .color(new Color(0, 150, 255)) // Blue color
                    .build());

            panelComponent.getChildren().add(LineComponent.builder().build());

            // Calculate stats
            long runtime = System.currentTimeMillis() - nICK1PrivateLibraryScript.startTime;
            int expGained = nICK1PrivateLibraryScript.currentExp - nICK1PrivateLibraryScript.startExp;
            
            // Calculate exp per hour - only start calculating after first book collection
            int expPerHour = 0;
            if (nICK1PrivateLibraryScript.totalBooksCollected > 0) {
                double hours = runtime / 3600000.0;
                expPerHour = hours > 0 ? (int)(expGained / hours) : 0;
            }
            
            // Current level
            int currentLevel = Rs2Player.getRealSkillLevel(Skill.MAGIC);
            
            // Dynamic target level - 65 if below, 99 if at or above
            int targetLevel = currentLevel < 65 ? 65 : 99;
            
            // Calculate time to dynamic target level
            String timeToLevel = calculateTimeToLevel(currentLevel, expPerHour, targetLevel);
            
            // Display stats
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Exp/H:")
                    .right(NUMBER_FORMAT.format(expPerHour))
                    .rightColor(Color.GREEN)
                    .build());
            
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Current level:")
                    .right(String.valueOf(currentLevel))
                    .rightColor(Color.YELLOW)
                    .build());
            
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Time to level " + targetLevel + ":")
                    .right(timeToLevel)
                    .rightColor(Color.CYAN)
                    .build());
            
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Status:")
                    .right(nICK1PrivateLibraryScript.currentStatus)
                    .rightColor(Color.WHITE)
                    .build());

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return super.render(graphics);
    }
    
    private String formatRuntime(long millis) {
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        
        seconds = seconds % 60;
        minutes = minutes % 60;
        
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
    
    private String calculateTimeToLevel(int currentLevel, int expPerHour, int targetLevel) {
        if (currentLevel >= targetLevel) {
            return "Level reached!";
        }
        
        if (expPerHour <= 0) {
            return "Calculating...";
        }
        
        int currentExp = nICK1PrivateLibraryScript.currentExp;
        int targetExp = net.runelite.api.Experience.getXpForLevel(targetLevel);
        int expNeeded = targetExp - currentExp;
        
        if (expNeeded <= 0) {
            return "Level reached!";
        }
        
        double hoursNeeded = (double) expNeeded / expPerHour;
        
        int hours = (int) hoursNeeded;
        int minutes = (int) ((hoursNeeded - hours) * 60);
        
        if (hours > 0) {
            return String.format("%dh %dm", hours, minutes);
        } else {
            return String.format("%dm", minutes);
        }
    }
}
