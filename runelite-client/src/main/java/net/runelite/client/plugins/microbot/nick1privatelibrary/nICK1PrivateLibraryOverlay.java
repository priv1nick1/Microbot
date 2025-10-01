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
            panelComponent.setPreferredSize(new Dimension(220, 200));
            
            // Title
            panelComponent.getChildren().add(TitleComponent.builder()
                    .text("nICK1 Private Library")
                    .color(new Color(0, 150, 255)) // Blue color
                    .build());

            panelComponent.getChildren().add(LineComponent.builder().build());

            // Calculate stats
            long runtime = System.currentTimeMillis() - nICK1PrivateLibraryScript.startTime;
            int expGained = nICK1PrivateLibraryScript.currentExp - nICK1PrivateLibraryScript.startExp;
            
            // Calculate exp per hour
            double hours = runtime / 3600000.0;
            int expPerHour = hours > 0 ? (int)(expGained / hours) : 0;
            
            // Current level
            int currentLevel = Rs2Player.getRealSkillLevel(Skill.MAGIC);
            
            // Calculate time to next level
            String timeToLevel = calculateTimeToLevel(currentLevel, expPerHour);
            
            // Display stats
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Exp/H:")
                    .right(NUMBER_FORMAT.format(expPerHour))
                    .rightColor(Color.GREEN)
                    .build());
            
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Exp Gained:")
                    .right(NUMBER_FORMAT.format(expGained))
                    .rightColor(Color.LIGHT_GRAY)
                    .build());
            
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Current level:")
                    .right(String.valueOf(currentLevel))
                    .rightColor(Color.YELLOW)
                    .build());
            
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Time to next level:")
                    .right(timeToLevel)
                    .rightColor(Color.CYAN)
                    .build());
            
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Books collected:")
                    .right(String.valueOf(nICK1PrivateLibraryScript.totalBooksCollected))
                    .rightColor(Color.ORANGE)
                    .build());
            
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Status:")
                    .right(nICK1PrivateLibraryScript.currentStatus)
                    .rightColor(Color.WHITE)
                    .build());
            
            // Calculate and display runtime
            long runtimeMillis = System.currentTimeMillis() - nICK1PrivateLibraryScript.startTime;
            String runtimeFormatted = formatRuntime(runtimeMillis);
            
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Time Running:")
                    .right(runtimeFormatted)
                    .rightColor(Color.MAGENTA)
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
    
    private String calculateTimeToLevel(int currentLevel, int expPerHour) {
        if (currentLevel >= 99) {
            return "Max level!";
        }
        
        if (expPerHour <= 0) {
            return "Calculating...";
        }
        
        int currentExp = nICK1PrivateLibraryScript.currentExp;
        int nextLevelExp = net.runelite.api.Experience.getXpForLevel(currentLevel + 1);
        int expNeeded = nextLevelExp - currentExp;
        
        if (expNeeded <= 0) {
            return "Level up!";
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
