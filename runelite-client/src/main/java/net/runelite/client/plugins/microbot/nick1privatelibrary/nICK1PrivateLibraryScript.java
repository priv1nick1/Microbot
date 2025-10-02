package net.runelite.client.plugins.microbot.nick1privatelibrary;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.dialogues.Rs2Dialogue;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
// import net.runelite.client.plugins.kourendlibrary.Library;

import java.util.concurrent.TimeUnit;

@Slf4j
public class nICK1PrivateLibraryScript extends Script {
    public static double version = 1.0;
    
    // Library locations - Corrected coordinates
    private static final WorldArea LIBRARY_AREA = new WorldArea(1622, 3798, 21, 20, 0);
    private static final WorldPoint LIBRARY_ENTRANCE = new WorldPoint(1632, 3808, 0); // Center of the area
    private static final WorldPoint GROUND_FLOOR_CENTER = new WorldPoint(1632, 3808, 0);
    private static final WorldPoint FIRST_FLOOR_CENTER = new WorldPoint(1632, 3808, 1);
    private static final WorldPoint SECOND_FLOOR_CENTER = new WorldPoint(1632, 3808, 2);
    
    // Stats tracking (public static so overlay can access)
    public static long startTime;
    public static int startExp;
    public static int currentExp;
    public static String currentStatus = "Starting...";
    public static int booksCollected = 0;
    public static int totalBooksCollected = 0;
    
    // State tracking
    private enum State {
        TALKING_TO_NPC,
        SEARCHING_BOOKCASES,
        COLLECTING_BOOK,
        RETURNING_TO_NPC,
        DELIVERING_BOOK,
        BANKING,
        WALKING_TO_LIBRARY
    }
    
    private State currentState = State.WALKING_TO_LIBRARY;
    // private Library library;
    private int currentFloor = 0; // 0=ground, 1=first, 2=second
    private int bankingFailCount = 0;
    private static final int MAX_BANKING_FAILS = 5;
    
    // Library mechanics tracking
    private String requestedBook = null;
    private String currentNPC = null; // Sam, Professor Gracklebone, or Villia
    private WorldPoint npcLocation = null;
    private int bookcaseSearchCount = 0;
    private static final int MAX_BOOKCASE_SEARCHES = 50; // Prevent infinite searching
    private boolean hasSpokenToBiblia = false; // Track if we've gotten location hints
    private boolean isHelpingSomeoneElse = false; // Track if we're already helping another NPC
    
    // Bookcase tracking
    private java.util.Map<WorldPoint, String> bookcaseMemory = new java.util.HashMap<>(); // Remember what book is in each bookcase
    private java.util.List<WorldPoint> searchedBookcases = new java.util.ArrayList<>(); // Track which bookcases we've searched
    private WorldPoint currentBookcaseTarget = null; // Current bookcase we're walking to

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
                        
                    case TALKING_TO_NPC:
                        handleTalkingToNPC();
                        break;
                        
                    case SEARCHING_BOOKCASES:
                        handleSearchingBookcases();
                        break;
                        
                    case COLLECTING_BOOK:
                        handleCollectingBook();
                        break;
                        
                    case RETURNING_TO_NPC:
                        handleReturningToNPC();
                        break;
                        
                    case DELIVERING_BOOK:
                        handleDeliveringBook();
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
            log.info("Already in library! Starting to talk to NPC.");
            currentState = State.TALKING_TO_NPC;
        } else {
            // Walk to library entrance
            Rs2Walker.walkTo(LIBRARY_ENTRANCE, 5);
            Microbot.status = currentStatus;
        }
    }
    
    private void handleTalkingToNPC() {
        currentStatus = "Talking to NPC...";
        log.info(currentStatus);
        
        // Try to find one of the three NPCs: Sam, Professor Gracklebone, or Villia
        String[] npcNames = {"Sam", "Professor Gracklebone", "Villia"};
        String targetNPC = null;
        
        // Try to find an available NPC
        for (String npcName : npcNames) {
            if (Rs2Npc.getNpc(npcName) != null) {
                targetNPC = npcName;
                currentNPC = npcName;
                break;
            }
        }
        
        if (targetNPC == null) {
            log.warn("No available NPCs found, waiting...");
            sleep(3000);
            return;
        }
        
        // Try to interact with the found NPC
        boolean talked = Rs2Npc.interact(targetNPC, "Talk-to");
        
        if (talked) {
            log.info("Talking to {}...", targetNPC);
            sleep(2000, 3000); // Wait for dialogue
            
            // Check if we got a book request from dialogue
            if (Rs2Dialogue.isInDialogue()) {
                String dialogueText = Rs2Dialogue.getDialogueText();
                if (dialogueText != null) {
                    // Check if we're already helping someone else
                    if (dialogueText.contains("I'll grab you later when you're not busy helping someone else") ||
                        dialogueText.contains("helping someone else") ||
                        dialogueText.contains("not busy")) {
                        log.info("Already helping someone else! Need to complete current task first.");
                        isHelpingSomeoneElse = true;
                        currentStatus = "Already helping someone else - completing current task...";
                        currentState = State.SEARCHING_BOOKCASES; // Try to find and complete current book
                        return;
                    }
                    
                    // Check if we got a book request
                    if (dialogueText.contains("book")) {
                        // Extract book name from dialogue (simplified)
                        requestedBook = "Book"; // This would need proper parsing
                        log.info("Got book request from {}: {}", targetNPC, requestedBook);
                        
                        // After getting book request, talk to Biblia for location hints
                        if (!hasSpokenToBiblia) {
                            currentState = State.SEARCHING_BOOKCASES; // Skip Biblia for now, implement later
                        } else {
                            currentState = State.SEARCHING_BOOKCASES;
                        }
                    }
                }
            }
        } else {
            log.warn("Could not find {}, retrying...", targetNPC);
            sleep(2000);
        }
    }
    
    private void handleSearchingBookcases() {
        currentStatus = "Systematically searching bookcases for " + (requestedBook != null ? requestedBook : "book") + "...";
        log.info(currentStatus);
        
        // Check if we've already found the requested book in our memory
        if (requestedBook != null && bookcaseMemory.containsValue(requestedBook)) {
            log.info("Found {} in our memory! Going to collect it.", requestedBook);
            // Find the bookcase with the requested book
            for (java.util.Map.Entry<WorldPoint, String> entry : bookcaseMemory.entrySet()) {
                if (entry.getValue().equals(requestedBook)) {
                    currentBookcaseTarget = entry.getKey();
                    Rs2Walker.walkTo(currentBookcaseTarget, 3);
                    currentState = State.COLLECTING_BOOK;
                    return;
                }
            }
        }
        
        // Check if we've searched too many bookcases
        if (bookcaseSearchCount >= MAX_BOOKCASE_SEARCHES) {
            log.warn("Searched too many bookcases, trying different approach...");
            bookcaseSearchCount = 0;
            currentState = State.TALKING_TO_NPC; // Reset and try again
            return;
        }
        
        // Find the next unsearched bookcase
        WorldPoint nextBookcase = findNextUnsearchedBookcase();
        
        if (nextBookcase != null) {
            currentBookcaseTarget = nextBookcase;
            log.info("Walking to bookcase at: {}", nextBookcase);
            Rs2Walker.walkTo(nextBookcase, 3);
            
            // Check if we're close enough to search
            WorldPoint playerLocation = Rs2Player.getWorldLocation();
            if (playerLocation.distanceTo(nextBookcase) <= 3) {
                // Try to search the bookcase
                boolean searched = Rs2GameObject.interact("Bookcase", "Search");
                
                if (searched) {
                    bookcaseSearchCount++;
                    searchedBookcases.add(nextBookcase);
                    log.info("Searching bookcase {}/{} at {}", bookcaseSearchCount, MAX_BOOKCASE_SEARCHES, nextBookcase);
                    sleep(2000, 3000); // Wait for search to complete
                    currentState = State.COLLECTING_BOOK;
                } else {
                    log.warn("Could not find bookcase at {}, looking for another one.", nextBookcase);
                    sleep(1000);
                }
            }
        } else {
            log.warn("No more bookcases to search, trying different approach...");
            currentState = State.TALKING_TO_NPC; // Reset and try again
        }
    }
    
    private void handleCollectingBook() {
        currentStatus = "Collecting book...";
        log.info(currentStatus);
        
        // Check if we got a book (inventory changed)
        if (Rs2Inventory.hasItem("Book")) {
            // Try to identify what book we got (this would need proper book name detection)
            String foundBook = "Book"; // Simplified - would need to detect actual book name
            
            // Remember what book is in this bookcase
            if (currentBookcaseTarget != null) {
                bookcaseMemory.put(currentBookcaseTarget, foundBook);
                log.info("Found {} in bookcase at {} - added to memory", foundBook, currentBookcaseTarget);
            }
            
            booksCollected++;
            totalBooksCollected++;
            log.info("Collected {}! Total books: {}", foundBook, totalBooksCollected);
            
            // Check if this is the book we were looking for
            if (requestedBook != null && foundBook.equals(requestedBook)) {
                log.info("Found the requested book {}! Returning to NPC.", requestedBook);
                currentState = State.RETURNING_TO_NPC;
            } else {
                log.info("Found {} but looking for {}. Continuing search...", foundBook, requestedBook);
                currentState = State.SEARCHING_BOOKCASES;
            }
        } else {
            // No book found in this bookcase - remember that too
            if (currentBookcaseTarget != null) {
                bookcaseMemory.put(currentBookcaseTarget, "Empty");
                log.info("No book found in bookcase at {} - marked as empty", currentBookcaseTarget);
            }
            log.warn("No book found in bookcase, continuing search...");
            currentState = State.SEARCHING_BOOKCASES;
        }
    }
    
    private void handleReturningToNPC() {
        if (isHelpingSomeoneElse && currentNPC == null) {
            // We don't know which NPC we're helping, try to find any available NPC
            currentStatus = "Looking for NPC to deliver book...";
            log.info(currentStatus);
            
            String[] npcNames = {"Sam", "Professor Gracklebone", "Villia"};
            for (String npcName : npcNames) {
                if (Rs2Npc.getNpc(npcName) != null) {
                    currentNPC = npcName;
                    log.info("Found {} to deliver book to", npcName);
                    break;
                }
            }
            
            if (currentNPC == null) {
                log.warn("No NPCs found, waiting...");
                sleep(3000);
                return;
            }
        }
        
        currentStatus = "Returning to " + (currentNPC != null ? currentNPC : "NPC") + "...";
        log.info(currentStatus);
        
        // Find the NPC we originally talked to
        if (currentNPC != null && Rs2Npc.getNpc(currentNPC) != null) {
            // NPC is still available, walk to them
            Rs2Walker.walkTo(Rs2Npc.getNpc(currentNPC).getWorldLocation(), 3);
            
            // Check if we're close enough to NPC
            WorldPoint playerLocation = Rs2Player.getWorldLocation();
            WorldPoint npcLocation = Rs2Npc.getNpc(currentNPC).getWorldLocation();
            if (playerLocation.distanceTo(npcLocation) <= 3) {
                currentState = State.DELIVERING_BOOK;
            }
        } else {
            log.warn("Original NPC {} not found, looking for any available NPC...", currentNPC);
            currentState = State.TALKING_TO_NPC; // Find a new NPC
        }
    }
    
    private void handleDeliveringBook() {
        currentStatus = "Delivering book to " + (currentNPC != null ? currentNPC : "NPC") + "...";
        log.info(currentStatus);
        
        // Try to interact with the original NPC to deliver book
        if (currentNPC != null) {
            boolean talked = Rs2Npc.interact(currentNPC, "Talk-to");
            
            if (talked) {
                log.info("Delivering book to {}...", currentNPC);
                sleep(2000, 3000); // Wait for dialogue
                
                // Check if book was delivered (inventory should be empty of books)
                if (!Rs2Inventory.hasItem("Book")) {
                    log.info("Book delivered successfully to {}!", currentNPC);
                    requestedBook = null; // Reset for next book
                    bookcaseSearchCount = 0; // Reset search counter
                    isHelpingSomeoneElse = false; // Reset helping flag
                    currentNPC = null; // Reset NPC
                    currentState = State.TALKING_TO_NPC; // Get next book request
                }
            } else {
                log.warn("Could not find {} to deliver book, retrying...", currentNPC);
                sleep(2000);
            }
        } else {
            log.warn("No NPC to deliver to, finding new NPC...");
            currentState = State.TALKING_TO_NPC;
        }
    }
    
    private void handleBanking() {
        // Books cannot be banked according to the wiki
        // This state should not be reached in normal operation
        log.warn("Banking state reached - books cannot be banked! Returning to NPC.");
        currentState = State.TALKING_TO_NPC;
    }
    
    private boolean isInLibrary(WorldPoint location) {
        // Check if we're in the library area using the correct WorldArea
        return LIBRARY_AREA.contains(location);
    }
    
    private WorldPoint findNextUnsearchedBookcase() {
        // This is a simplified version - in reality you'd use the Library.java helper
        // to find actual bookcase locations and check if they've been searched
        
        WorldPoint playerLocation = Rs2Player.getWorldLocation();
        
        // Generate some bookcase locations around the library (simplified)
        java.util.List<WorldPoint> potentialBookcases = new java.util.ArrayList<>();
        
        // Add some bookcase locations around the current position
        for (int x = -10; x <= 10; x += 5) {
            for (int y = -10; y <= 10; y += 5) {
                WorldPoint bookcaseLocation = new WorldPoint(
                    playerLocation.getX() + x,
                    playerLocation.getY() + y,
                    currentFloor
                );
                
                // Only add if it's within the library area and not already searched
                if (LIBRARY_AREA.contains(bookcaseLocation) && !searchedBookcases.contains(bookcaseLocation)) {
                    potentialBookcases.add(bookcaseLocation);
                }
            }
        }
        
        // Return the closest unsearched bookcase
        if (!potentialBookcases.isEmpty()) {
            return potentialBookcases.get(0); // Simplified - would find closest one
        }
        
        return null; // No more bookcases to search
    }

    @Override
    public void shutdown() {
        super.shutdown();
        log.info("nICK1 Private Library stopped! Total books collected: {}", totalBooksCollected);
    }
}
