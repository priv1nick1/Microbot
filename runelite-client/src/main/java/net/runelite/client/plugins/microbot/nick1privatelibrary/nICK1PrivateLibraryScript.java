package net.runelite.client.plugins.microbot.nick1privatelibrary;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
// import net.runelite.client.plugins.kourendlibrary.Library;

import java.util.concurrent.TimeUnit;

@Slf4j
public class nICK1PrivateLibraryScript extends Script {
    public static double version = 1.0;
    
    // Library locations
    private static final WorldPoint LIBRARY_ENTRANCE = new WorldPoint(1639, 3632, 0);
    private static final WorldPoint GROUND_FLOOR_CENTER = new WorldPoint(1639, 3632, 0);
    private static final WorldPoint FIRST_FLOOR_CENTER = new WorldPoint(1639, 3632, 1);
    private static final WorldPoint SECOND_FLOOR_CENTER = new WorldPoint(1639, 3632, 2);
    
    // Stats tracking (public static so overlay can access)
    public static long startTime;
    public static int startExp;
    public static int currentExp;
    public static String currentStatus = "Starting...";
    public static int booksCollected = 0;
    public static int totalBooksCollected = 0;
    
    // State tracking
    private enum State {
        CHECKING_INVENTORY,
        WALKING_TO_BOOKCASE,
        SEARCHING_BOOKCASE,
        COLLECTING_BOOK,
        BANKING,
        WALKING_TO_LIBRARY
    }
    
    private State currentState = State.WALKING_TO_LIBRARY;
    // private Library library;
    private int currentFloor = 0; // 0=ground, 1=first, 2=second
    private int bankingFailCount = 0;
    private static final int MAX_BANKING_FAILS = 5;

    public boolean run(nICK1PrivateLibraryConfig config) {
        Microbot.enableAutoRunOn = true;
        
        // Initialize stats tracking
        startTime = System.currentTimeMillis();
        startExp = Microbot.getClient().getSkillExperience(Skill.MAGIC);
        currentExp = startExp;
        currentStatus = "Starting...";
        booksCollected = 0;
        totalBooksCollected = 0;
        bankingFailCount = 0;
        
        // Initialize library helper
        // library = new Library();
        
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
                
                // Update current exp
                try {
                    currentExp = Microbot.getClient().getSkillExperience(Skill.MAGIC);
                } catch (Exception e) {
                    currentStatus = "Loading game data...";
                    return;
                }
                
                // Main script logic
                switch (currentState) {
                    case WALKING_TO_LIBRARY:
                        handleWalkingToLibrary();
                        break;
                        
                    case CHECKING_INVENTORY:
                        handleInventoryCheck();
                        break;
                        
                    case WALKING_TO_BOOKCASE:
                        handleWalkingToBookcase();
                        break;
                        
                    case SEARCHING_BOOKCASE:
                        handleSearchingBookcase();
                        break;
                        
                    case COLLECTING_BOOK:
                        handleCollectingBook();
                        break;
                        
                    case BANKING:
                        handleBanking();
                        break;
                }
                
            } catch (Exception ex) {
                log.error("Error in library script: {}", ex.getMessage(), ex);
                currentStatus = "Error: " + (ex.getMessage() != null ? ex.getMessage() : "Unknown");
            }
        }, 0, 1000, TimeUnit.MILLISECONDS);
        
        return true;
    }
    
    private void handleWalkingToLibrary() {
        currentStatus = "Walking to library...";
        log.info(currentStatus);
        
        WorldPoint playerLocation = Rs2Player.getWorldLocation();
        
        // Check if we're already in the library
        if (isInLibrary(playerLocation)) {
            log.info("Already in library! Starting book collection.");
            currentState = State.CHECKING_INVENTORY;
        } else {
            // Walk to library entrance
            Rs2Walker.walkTo(LIBRARY_ENTRANCE, 5);
            Microbot.status = currentStatus;
        }
    }
    
    private void handleInventoryCheck() {
        currentStatus = "Checking inventory...";
        log.info(currentStatus);
        
        // Check if inventory is full
        if (Rs2Inventory.isFull()) {
            log.info("Inventory full, going to bank.");
            currentState = State.BANKING;
        } else {
            log.info("Inventory has space, looking for bookcases.");
            currentState = State.WALKING_TO_BOOKCASE;
        }
    }
    
    private void handleWalkingToBookcase() {
        currentStatus = "Looking for bookcases...";
        log.info(currentStatus);
        
        // Find the nearest available bookcase
        WorldPoint nearestBookcase = findNearestBookcase();
        
        if (nearestBookcase != null) {
            log.info("Found bookcase at: {}", nearestBookcase);
            Rs2Walker.walkTo(nearestBookcase, 3);
            currentState = State.SEARCHING_BOOKCASE;
        } else {
            log.warn("No bookcases found, checking other floors...");
            // Try different floor
            if (currentFloor < 2) {
                currentFloor++;
                log.info("Trying floor {}", currentFloor);
            } else {
                log.warn("No bookcases found on any floor, waiting...");
                sleep(5000);
            }
        }
    }
    
    private void handleSearchingBookcase() {
        currentStatus = "Searching bookcase...";
        log.info(currentStatus);
        
        // Try to interact with bookcase
        boolean searched = Rs2GameObject.interact("Bookcase", "Search");
        
        if (searched) {
            log.info("Searching bookcase...");
            sleep(2000, 3000); // Wait for search to complete
            currentState = State.COLLECTING_BOOK;
        } else {
            log.warn("Could not find bookcase nearby, looking for another one.");
            currentState = State.WALKING_TO_BOOKCASE;
        }
    }
    
    private void handleCollectingBook() {
        currentStatus = "Collecting book...";
        log.info(currentStatus);
        
        // Check if we got a book (inventory changed)
        if (Rs2Inventory.hasItem("Book")) {
            booksCollected++;
            totalBooksCollected++;
            log.info("Collected book! Total books: {}", totalBooksCollected);
            currentState = State.CHECKING_INVENTORY;
        } else {
            log.warn("No book found in bookcase, looking for another one.");
            currentState = State.WALKING_TO_BOOKCASE;
        }
    }
    
    private void handleBanking() {
        currentStatus = "Banking books...";
        log.info(currentStatus);
        
        // FALLBACK CHECK: If we've failed too many times, try to continue anyway
        if (bankingFailCount >= MAX_BANKING_FAILS) {
            log.warn("Banking failed {} times. Continuing with collection...", bankingFailCount);
            currentState = State.CHECKING_INVENTORY;
            bankingFailCount = 0;
            return;
        }
        
        // Open nearest bank
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
            
            // Deposit all books
            Rs2Bank.depositAll("Book");
            sleep(600);
            
            // Close bank
            Rs2Bank.closeBank();
            sleep(600);
            
            log.info("Books deposited! Continuing collection.");
            currentState = State.CHECKING_INVENTORY;
        }
    }
    
    private boolean isInLibrary(WorldPoint location) {
        // Check if we're in the library area (rough bounds)
        return location.getX() >= 1600 && location.getX() <= 1680 &&
               location.getY() >= 3600 && location.getY() <= 3660;
    }
    
    private WorldPoint findNearestBookcase() {
        // This is a simplified version - in reality you'd use the Library.java helper
        // to find actual bookcase locations and check if they have books
        
        WorldPoint playerLocation = Rs2Player.getWorldLocation();
        
        // For now, just return a nearby location to test
        // In a real implementation, you'd query the Library class for bookcase locations
        return new WorldPoint(
            playerLocation.getX() + (int)(Math.random() * 10 - 5),
            playerLocation.getY() + (int)(Math.random() * 10 - 5),
            currentFloor
        );
    }

    @Override
    public void shutdown() {
        super.shutdown();
        log.info("nICK1 Private Library stopped! Total books collected: {}", totalBooksCollected);
    }
}
