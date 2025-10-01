package net.runelite.client.plugins.microbot.playerhoplog;

import lombok.extern.slf4j.Slf4j;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.security.Login;

import java.util.concurrent.TimeUnit;

@Slf4j
public class PlayerHopLogScript extends Script {
    public static double version = 1.0;
    
    // Stats tracking (public static so overlay can access)
    public static int totalHops = 0;
    public static int nearbyPlayers = 0;
    public static String currentStatus = "Monitoring...";
    public static long lastHopTime = 0;

    public boolean run(PlayerHopLogConfig config) {
        Microbot.enableAutoRunOn = false;
        
        // Reset stats
        totalHops = 0;
        lastHopTime = 0;
        
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            if (!super.run()) return;
            
            try {
                if (!Microbot.isLoggedIn()) {
                    currentStatus = "Logged out";
                    return;
                }
                
                // Count nearby players (excluding yourself)
                nearbyPlayers = (int) Rs2Player.getPlayers(player -> true).count();
                
                // Check if we should hop
                if (nearbyPlayers > 0) {
                    currentStatus = "Hopping...";
                    log.warn("Detected {} player(s) nearby! Hopping to random P2P world...", nearbyPlayers);
                    
                    // Get random P2P world
                    int randomWorld = Login.getRandomWorld(true); // true = members only
                    
                    if (randomWorld > 0) {
                        log.info("Hopping to world {}", randomWorld);
                        boolean hopped = Microbot.hopToWorld(randomWorld);
                        
                        if (hopped) {
                            totalHops++;
                            lastHopTime = System.currentTimeMillis();
                            currentStatus = "Hopped!";
                            log.info("Successfully hopped to world {}. Total hops: {}", randomWorld, totalHops);
                            
                            // Wait for hop to complete
                            sleepUntil(() -> !Microbot.isHopping(), 10000);
                            sleep(2000, 3000); // Extra delay after hop
                        } else {
                            currentStatus = "Hop failed";
                            log.warn("Failed to hop to world {}", randomWorld);
                        }
                    } else {
                        currentStatus = "No world";
                        log.error("Could not find a random P2P world to hop to");
                    }
                } else {
                    currentStatus = "All clear";
                }
                
            } catch (Exception ex) {
                log.error("Error in player hop log script: {}", ex.getMessage());
                currentStatus = "Error: " + ex.getMessage();
            }
        }, 0, 600, TimeUnit.MILLISECONDS); // Check every 600ms (faster detection)
        
        return true;
    }

    @Override
    public void shutdown() {
        super.shutdown();
        log.info("Player Hop Log stopped. Total hops: {}", totalHops);
    }
}

