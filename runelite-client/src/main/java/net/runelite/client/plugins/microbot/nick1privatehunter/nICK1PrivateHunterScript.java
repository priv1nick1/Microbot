package net.runelite.client.plugins.microbot.nick1privatehunter;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.dialogues.Rs2Dialogue;
import net.runelite.client.plugins.microbot.util.equipment.Rs2Equipment;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;

import java.util.concurrent.TimeUnit;

@Slf4j
public class nICK1PrivateHunterScript extends Script {
    public static double version = 1.0;
    
    // Hunter level threshold
    private static final int HUNTER_LEVEL_THRESHOLD = 65;
    
    // Locations - Ruby Harvest in West Aldarin
    private static final WorldArea RUBY_HARVEST_AREA = new WorldArea(1337, 2930, 9, 10, 0);
    
    // Sunlight Moth location - North of Hunter Guild
    private static final WorldArea SUNLIGHT_MOTH_AREA = new WorldArea(1554, 3084, 7, 6, 0);
    
    // Stats tracking (public static so overlay can access)
    public static long startTime;
    public static int startExp;
    public static int currentExp;
    public static String currentStatus = "Starting...";
    public static boolean hasStartedCatching = false; // Track if we've caught our first butterfly
    public static long firstCatchTime; // Time when we first started catching
    public static int firstCatchExp; // Exp when we first started catching
    
    // State tracking
    private enum State {
        CHECKING_EQUIPMENT,
        BANKING,
        TRAVELING,
        CATCHING
    }
    
    private State currentState = State.CHECKING_EQUIPMENT;
    private int bankingFailCount = 0;
    private static final int MAX_BANKING_FAILS = 5;

    public boolean run(nICK1PrivateHunterConfig config) {
        Microbot.enableAutoRunOn = true;
        
        // Initialize stats tracking
        startTime = System.currentTimeMillis();
        startExp = Microbot.getClient().getSkillExperience(Skill.HUNTER);
        currentExp = startExp;
        currentStatus = "Starting...";
        bankingFailCount = 0;
        hasStartedCatching = false; // Reset catching flag
        
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            if (!super.run()) return;
            
            try {
                // Safety check: Wait for player to be fully loaded
                if (!Microbot.isLoggedIn()) {
                    currentStatus = "Not logged in";
                    return;
                }
                
                if (Rs2Player.getLocalPlayer() == null) {
                    currentStatus = "Loading player data...";
                    return;
                }
                
                // Update current exp (with null check)
                try {
                    currentExp = Microbot.getClient().getSkillExperience(Skill.HUNTER);
                } catch (Exception e) {
                    currentStatus = "Loading game data...";
                    return;
                }
                
                // Handle dialogues first (like sailing confirmation)
                handleDialogues();
                
                // Main script logic
                switch (currentState) {
                    case CHECKING_EQUIPMENT:
                        handleEquipmentCheck();
                        break;
                        
                    case BANKING:
                        handleBanking();
                        break;
                        
                    case TRAVELING:
                        handleTraveling();
                        break;
                        
                    case CATCHING:
                        handleCatching();
                        break;
                }
                
            } catch (Exception ex) {
                log.error("Error in script: {}", ex.getMessage(), ex);
                currentStatus = "Error: " + (ex.getMessage() != null ? ex.getMessage() : "Unknown");
            }
        }, 0, 1000, TimeUnit.MILLISECONDS);
        
        return true;
    }
    
    private void handleEquipmentCheck() {
        currentStatus = "Checking equipment...";
        log.info(currentStatus);
        
        WorldPoint playerLocation = Rs2Player.getWorldLocation();
        int hunterLevel = Rs2Player.getRealSkillLevel(Skill.HUNTER);
        
        // Determine which area we should be in based on level
        WorldArea targetArea = hunterLevel < HUNTER_LEVEL_THRESHOLD ? RUBY_HARVEST_AREA : SUNLIGHT_MOTH_AREA;
        
        // Check if we have the minimum required item (butterfly net)
        boolean hasButterflyNet = Rs2Equipment.isWearing("Butterfly net");
        
        // SMART CHECK: If we're already at the butterfly location with a net, skip to catching!
        if (hasButterflyNet && targetArea.contains(playerLocation)) {
            log.info("Already at butterfly location with net equipped! Starting to catch.");
            currentState = State.CATCHING;
            return;
        }
        
        // Check if we have all required equipment for proper setup
        boolean hasGracefulHood = Rs2Equipment.isWearing("Graceful hood");
        boolean hasGracefulTop = Rs2Equipment.isWearing("Graceful top");
        boolean hasGracefulLegs = Rs2Equipment.isWearing("Graceful legs");
        boolean hasGracefulGloves = Rs2Equipment.isWearing("Graceful gloves");
        boolean hasGracefulBoots = Rs2Equipment.isWearing("Graceful boots");
        boolean hasGracefulCape = Rs2Equipment.isWearing("Graceful cape");
        boolean hasCoins = Rs2Inventory.hasItem("Coins");
        
        if (hasGracefulHood && hasGracefulTop && hasGracefulLegs && 
            hasGracefulGloves && hasGracefulBoots && hasGracefulCape && 
            hasButterflyNet && hasCoins) {
            
            log.info("All equipment ready! Proceeding to travel.");
            currentState = State.TRAVELING;
        } else {
            log.info("Missing equipment. Going to bank.");
            currentState = State.BANKING;
        }
    }
    
    private void handleBanking() {
        currentStatus = "Banking for equipment...";
        log.info(currentStatus);
        
        // FALLBACK CHECK: If we've failed too many times, check if we can just skip banking
        if (bankingFailCount >= MAX_BANKING_FAILS) {
            log.warn("Banking failed {} times. Checking if we can skip banking...", bankingFailCount);
            
            WorldPoint playerLocation = Rs2Player.getWorldLocation();
            int hunterLevel = Rs2Player.getRealSkillLevel(Skill.HUNTER);
            WorldArea targetArea = hunterLevel < HUNTER_LEVEL_THRESHOLD ? RUBY_HARVEST_AREA : SUNLIGHT_MOTH_AREA;
            boolean hasButterflyNet = Rs2Equipment.isWearing("Butterfly net");
            
            // If we have a net and are at/near the location, just start catching
            if (hasButterflyNet && targetArea.contains(playerLocation)) {
                log.info("Have butterfly net and at location - skipping bank and starting to catch!");
                currentState = State.CATCHING;
                bankingFailCount = 0;
                return;
            }
            
            // If we have a net but not at location, try traveling
            if (hasButterflyNet) {
                log.info("Have butterfly net but not at location - traveling there");
                currentState = State.TRAVELING;
                bankingFailCount = 0;
                return;
            }
            
            // Otherwise we're truly stuck - reset and try equipment check again
            log.error("Stuck in banking loop with no butterfly net. Resetting to equipment check.");
            currentState = State.CHECKING_EQUIPMENT;
            bankingFailCount = 0;
            return;
        }
        
        // Open nearest bank or bank chest
        if (!Rs2Bank.isOpen()) {
            boolean bankOpened = Rs2Bank.openBank();
            if (!bankOpened) {
                bankingFailCount++;
                log.warn("Failed to open bank (attempt {}/{}), retrying...", bankingFailCount, MAX_BANKING_FAILS);
                return;
            }
            sleepUntil(Rs2Bank::isOpen, 5000);
        }
        
        if (Rs2Bank.isOpen()) {
            // Reset fail counter since we successfully opened bank
            bankingFailCount = 0;
            
            // Deposit all items first
            Rs2Bank.depositAll();
            sleep(600);
            
            // Withdraw graceful gear and equip
            Rs2Bank.withdrawAndEquip("Graceful hood");
            sleep(300);
            Rs2Bank.withdrawAndEquip("Graceful top");
            sleep(300);
            Rs2Bank.withdrawAndEquip("Graceful legs");
            sleep(300);
            Rs2Bank.withdrawAndEquip("Graceful gloves");
            sleep(300);
            Rs2Bank.withdrawAndEquip("Graceful boots");
            sleep(300);
            Rs2Bank.withdrawAndEquip("Graceful cape");
            sleep(300);
            Rs2Bank.withdrawAndEquip("Butterfly net");
            sleep(300);
            
            // Withdraw all coins
            Rs2Bank.withdrawAll("Coins");
            sleep(600);
            
            // Close bank
            Rs2Bank.closeBank();
            sleep(600);
            
            log.info("Equipment obtained! Ready to travel.");
            currentState = State.TRAVELING;
        }
    }
    
    private void handleTraveling() {
        int hunterLevel = Rs2Player.getRealSkillLevel(Skill.HUNTER);
        WorldArea targetArea;
        String targetName;
        String butterflyName;
        
        // Determine where to go based on hunter level
        if (hunterLevel < HUNTER_LEVEL_THRESHOLD) {
            targetArea = RUBY_HARVEST_AREA;
            targetName = "Ruby Harvest area (West Aldarin)";
            butterflyName = "Ruby harvest";
        } else {
            targetArea = SUNLIGHT_MOTH_AREA;
            targetName = "Sunlight Moth area (North of Hunter Guild)";
            butterflyName = "Sunlight moth";
        }
        
        log.info("Hunter level: {}. Traveling to {}", hunterLevel, targetName);
        
        WorldPoint playerLocation = Rs2Player.getWorldLocation();
        
        // Check if we're already in the target area
        if (targetArea.contains(playerLocation)) {
            // SMART CHECK: Can we see the butterflies? If yes, start catching immediately
            if (Rs2Npc.getNpc(butterflyName) != null) {
                log.info("Already at destination and can see {}! Starting to catch.", butterflyName);
                currentState = State.CATCHING;
                return;
            } else {
                log.info("At destination but no butterflies visible. Moving to center of area.");
                // Walk to the center of the area to find butterflies
                WorldPoint centerPoint = new WorldPoint(
                    targetArea.getX() + targetArea.getWidth() / 2,
                    targetArea.getY() + targetArea.getHeight() / 2,
                    targetArea.getPlane()
                );
                Rs2Walker.walkTo(centerPoint, 5);
                currentStatus = "Looking for " + butterflyName;
                Microbot.status = currentStatus;
            }
        } else {
            // Walk to the center of the area
            WorldPoint centerPoint = new WorldPoint(
                targetArea.getX() + targetArea.getWidth() / 2,
                targetArea.getY() + targetArea.getHeight() / 2,
                targetArea.getPlane()
            );
            Rs2Walker.walkTo(centerPoint, 5);
            currentStatus = "Walking to " + targetName;
            Microbot.status = currentStatus;
        }
    }
    
    private void handleCatching() {
        int hunterLevel = Rs2Player.getRealSkillLevel(Skill.HUNTER);
        String butterflyName;
        
        // Determine which butterfly to catch
        if (hunterLevel < HUNTER_LEVEL_THRESHOLD) {
            butterflyName = "Ruby harvest";
        } else {
            butterflyName = "Sunlight moth";
        }
        
        // Don't do anything if player is already catching
        if (Rs2Player.isAnimating() || Rs2Player.isMoving()) {
            return;
        }
        
        currentStatus = "Catching " + butterflyName;
        log.info(currentStatus);
        Microbot.status = currentStatus;
        
        // Try to catch the butterfly
        boolean caught = Rs2Npc.interact(butterflyName, "Catch");
        
        if (caught) {
            // Mark that we've started catching for XP calculation
            if (!hasStartedCatching) {
                hasStartedCatching = true;
                firstCatchTime = System.currentTimeMillis();
                firstCatchExp = currentExp;
                log.info("First catch detected! Starting XP tracking from now.");
            }
            
            // Wait a bit for the action to complete
            sleep(1200, 1800);
        } else {
            log.warn("Could not find {} nearby", butterflyName);
        }
    }

    private void handleDialogues() {
        // Handle sailing dialogue - click "Okay, don't ask again"
        if (Rs2Dialogue.isInDialogue()) {
            String dialogueText = Rs2Dialogue.getDialogueText();
            
            // Check if it's the sailing cost dialogue
            if (dialogueText != null && dialogueText.contains("Sailing to")) {
                log.info("Handling sailing dialogue...");
                // Click option 3: "Okay, don't ask again."
                Rs2Dialogue.clickOption("Okay, don't ask again");
                sleep(600, 1200);
            }
        }
    }

    @Override
    public void shutdown() {
        super.shutdown();
        log.info("nICK1 Private Hunter stopped!");
    }
}

