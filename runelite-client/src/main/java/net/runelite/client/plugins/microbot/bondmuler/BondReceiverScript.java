package net.runelite.client.plugins.microbot.bondmuler;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Point;
import net.runelite.api.widgets.Widget;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;
import net.runelite.client.plugins.microbot.util.dialogues.Rs2Dialogue;
import net.runelite.client.plugins.microbot.util.keyboard.Rs2Keyboard;

import java.awt.event.KeyEvent;

import java.util.concurrent.TimeUnit;

@Slf4j
public class BondReceiverScript extends Script {
    public static double version = 1.0;
    
    // Stats tracking
    public static String currentStatus = "Initializing";
    public static boolean bondReceived = false;
    public static boolean membershipApplied = false;
    
    private enum State {
        WAITING_FOR_TURN,
        WAITING_FOR_BOND_TRADE,
        ACCEPTING_BOND,
        USING_BOND,
        APPLYING_MEMBERSHIP,
        LOGGING_OUT,
        COMPLETE
    }
    
    private State currentState = State.WAITING_FOR_TURN;
    private long stateStartTime = 0;
    private static final int TIMEOUT_MS = 120000; // 2 minute timeout
    private String myCharacterName = "";

    public boolean run(BondReceiverConfig config) {
        Microbot.enableAutoRunOn = false;
        
        // Initialize bond queue system
        BondQueue.initialize();
        
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                // ABSOLUTE PRIORITY: Handle continue dialogues BEFORE ANYTHING ELSE
                // This MUST run before super.run() or isLoggedIn() checks!
                if (Rs2Dialogue.hasContinue() || Rs2Widget.hasWidget("Click here to continue") || Rs2Widget.hasWidget("continue")) {
                    log.info("CONTINUE DIALOGUE DETECTED - SPAMMING SPACE!");
                    
                    // Just spam SPACE - the most reliable method
                    Rs2Keyboard.keyPress(java.awt.event.KeyEvent.VK_SPACE);
                    sleep(100);
                    Rs2Keyboard.keyPress(java.awt.event.KeyEvent.VK_SPACE);
                    sleep(100);
                    Rs2Keyboard.keyPress(java.awt.event.KeyEvent.VK_SPACE);
                    sleep(100);
                    
                    // Also try clicking
                    Rs2Dialogue.clickContinue();
                    sleep(100);
                    
                    log.info("Spammed space for continue!");
                    return; // Exit immediately and retry next tick
                }
                
                if (!super.run()) return;
                
                if (!Microbot.isLoggedIn()) {
                    currentStatus = "Not logged in";
                    return;
                }
                
                // Get character name
                if (myCharacterName.isEmpty() && Rs2Player.getLocalPlayer() != null) {
                    myCharacterName = Rs2Player.getLocalPlayer().getName();
                    log.info("Character name detected: {}", myCharacterName);
                }
                
                // State machine
                switch (currentState) {
                    case WAITING_FOR_TURN:
                        handleWaitingForTurn();
                        break;
                    case WAITING_FOR_BOND_TRADE:
                        handleWaitingForBondTrade();
                        break;
                    case ACCEPTING_BOND:
                        handleAcceptingBond();
                        break;
                    case USING_BOND:
                        handleUsingBond();
                        break;
                    case APPLYING_MEMBERSHIP:
                        handleApplyingMembership();
                        break;
                    case LOGGING_OUT:
                        handleLoggingOut();
                        break;
                    case COMPLETE:
                        handleComplete();
                        break;
                }
                
                // Check for timeout
                if (System.currentTimeMillis() - stateStartTime > TIMEOUT_MS && 
                    currentState != State.COMPLETE) {
                    log.warn("State timeout! Resetting to waiting.");
                    transitionTo(State.WAITING_FOR_TURN);
                }
                
            } catch (Exception ex) {
                log.error("Error in bond receiver script: {}", ex.getMessage(), ex);
                currentStatus = "Error: " + ex.getMessage();
            }
        }, 0, 1000, TimeUnit.MILLISECONDS);
        
        return true;
    }
    
    private void handleWaitingForTurn() {
        currentStatus = "Waiting for turn...";
        
        // Announce our character name to the master
        if (myCharacterName != null && !myCharacterName.isEmpty()) {
            String currentAccount = BondQueue.getCurrentAccount();
            
            // If master is waiting and we haven't announced yet, announce ourselves
            if ((currentAccount == null || currentAccount.isEmpty() || !currentAccount.equals(myCharacterName))) {
                BondQueue.setCurrentAccount(myCharacterName);
                log.info("Announced character name to master: {}", myCharacterName);
            }
        }
        
        // Check if master is ready for us
        String status = BondQueue.getStatus();
        if (status.equals("WAITING_FOR_IRONMAN")) {
            log.info("Master is ready! Moving to trade phase.");
            transitionTo(State.WAITING_FOR_BOND_TRADE);
        }
    }
    
    private void handleWaitingForBondTrade() {
        currentStatus = "Waiting for bond trade...";
        
        // Check if bond appeared in inventory (already accepted)
        if (Rs2Inventory.hasItem("Old school bond")) {
            log.info("Bond received in inventory!");
            bondReceived = true;
            BondQueue.setStatus("BOND_RECEIVED");
            transitionTo(State.USING_BOND);
            return;
        }
        
        // BELT-AND-SUSPENDERS: If we see "Accept" in dialogue, do EVERYTHING
        if (Rs2Dialogue.hasSelectAnOption() && Rs2Dialogue.hasDialogueOption("Accept")) {
            log.info("BOND OFFER DETECTED! Attempting to accept...");
            currentStatus = "Accepting bond offer...";
            
            // Try METHOD 1: Click the widget directly
            Widget acceptOption = Rs2Dialogue.getDialogueOption("Accept");
            if (acceptOption != null) {
                log.info("Method 1: Clicking Accept widget...");
                Rs2Widget.clickWidget(acceptOption);
                sleep(300);
            }
            
            // Try METHOD 2: Press keyboard 1
            log.info("Method 2: Pressing keyboard 1...");
            Rs2Keyboard.keyPress(KeyEvent.VK_1);
            sleep(300);
            
            // Try METHOD 3: Press numpad 1
            log.info("Method 3: Pressing numpad 1...");
            Rs2Keyboard.keyPress(KeyEvent.VK_NUMPAD1);
            sleep(300);
            
            // Try METHOD 4: Type the character '1'
            log.info("Method 4: Typing character 1...");
            Rs2Keyboard.typeString("1");
            sleep(1500);
            
            log.info("All accept methods attempted! Waiting for bond...");
            return;
        }
        
        // Check for trade screen (widget 334 is trade screen)
        Widget tradeWidget = Rs2Widget.getWidget(334, 0);
        if (tradeWidget != null && !tradeWidget.isHidden()) {
            log.info("Trade screen detected!");
            transitionTo(State.ACCEPTING_BOND);
            return;
        }
        
        // Check queue status
        String status = BondQueue.getStatus();
        if (status.equals("BOND_OFFERED")) {
            currentStatus = "Looking for bond offer...";
        }
    }
    
    private void handleAcceptingBond() {
        currentStatus = "Accepting bond trade...";
        
        // Accept the trade (click accept buttons)
        // First accept button: widget 334, 30
        Widget firstAccept = Rs2Widget.getWidget(334, 30);
        if (firstAccept != null && !firstAccept.isHidden()) {
            Microbot.getMouse().click(firstAccept.getBounds());
            sleep(1200);
            return;
        }
        
        // Second accept button: widget 334, 19 (confirmation screen)
        Widget secondAccept = Rs2Widget.getWidget(334, 19);
        if (secondAccept != null && !secondAccept.isHidden()) {
            Microbot.getMouse().click(secondAccept.getBounds());
            sleep(1200);
            
            // Trade should complete
            transitionTo(State.WAITING_FOR_BOND_TRADE);
            return;
        }
    }
    
    private void handleUsingBond() {
        currentStatus = "Using bond...";
        
        if (!Rs2Inventory.hasItem("Old school bond")) {
            log.warn("Bond disappeared from inventory!");
            transitionTo(State.WAITING_FOR_TURN);
            return;
        }
        
        // Right-click bond and select "Redeem"
        if (Rs2Inventory.interact("Old school bond", "Redeem")) {
            log.info("Clicked Redeem on bond");
            sleep(1200);
            transitionTo(State.APPLYING_MEMBERSHIP);
        }
    }
    
    private void handleApplyingMembership() {
        currentStatus = "Applying membership...";
        
        // If bond is gone from inventory, membership was applied
        if (!Rs2Inventory.hasItem("Old school bond")) {
            log.info("Bond consumed - membership applied!");
            membershipApplied = true;
            BondQueue.setStatus("COMPLETE");
            transitionTo(State.LOGGING_OUT);
            return;
        }
        
        // Step 1: Click "14 days" option if present
        if (Rs2Widget.hasWidget("14 days")) {
            log.info("Clicking 14 days membership option...");
            Rs2Widget.clickWidget("14 days");
            sleep(1500);
        }
        
        // Step 2: Click "Accept" using the SAME method as "14 days"!
        boolean acceptClicked = false;
        if (Rs2Widget.hasWidget("Accept")) {
            log.info("Clicking Accept button (same method as 14 days)...");
            Rs2Widget.clickWidget("Accept");
            acceptClicked = true;
            sleep(2000); // Wait for animation
        }
        
        // Check if bond is gone OR interface closed (success!)
        if (!Rs2Inventory.hasItem("Old school bond") || 
            (acceptClicked && !Rs2Widget.hasWidget("Accept") && !Rs2Widget.hasWidget("14 days"))) {
            log.info("SUCCESS! Bond consumed and membership applied!");
            membershipApplied = true;
            BondQueue.setStatus("COMPLETE");
            transitionTo(State.LOGGING_OUT);
            return;
        }
        
        log.debug("Still waiting for bond to be consumed...");
        
        log.debug("Waiting for membership interface...");
    }
    
    private void handleLoggingOut() {
        currentStatus = "Logging out...";
        
        // Log out
        Microbot.pauseAllScripts.set(true);
        sleep(2000);
        
        // Click logout button
        Rs2Player.logout();
        sleep(3000);
        
        transitionTo(State.COMPLETE);
    }
    
    private void handleComplete() {
        currentStatus = "Complete!";
        log.info("Bond receiver complete. Membership applied: {}", membershipApplied);
    }
    
    private void transitionTo(State newState) {
        log.debug("State transition: {} -> {}", currentState, newState);
        currentState = newState;
        stateStartTime = System.currentTimeMillis();
    }

    @Override
    public void shutdown() {
        super.shutdown();
        log.info("Bond Receiver stopped.");
    }
}

