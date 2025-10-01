package net.runelite.client.plugins.microbot.bondmuler;

import lombok.extern.slf4j.Slf4j;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
public class BondMasterScript extends Script {
    public static double version = 1.0;
    
    // Stats tracking
    public static int bondsGiven = 0;
    public static int currentAccountIndex = 0;
    public static String currentStatus = "Idle";
    public static List<BondQueue.IronmanAccount> accounts = null;
    
    private enum State {
        LOADING_ACCOUNTS,
        WAITING_FOR_IRONMAN,
        USING_BOND,
        WAITING_FOR_ACCEPTANCE,
        WAITING_FOR_LOGOUT,
        COMPLETE,
        ERROR
    }
    
    private State currentState = State.LOADING_ACCOUNTS;
    private long stateStartTime = 0;
    private static final int TIMEOUT_MS = 60000; // 1 minute timeout per state

    public boolean run(BondMasterConfig config) {
        Microbot.enableAutoRunOn = false;
        
        // Initialize bond queue system
        BondQueue.initialize();
        
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            if (!super.run()) return;
            
            try {
                if (!Microbot.isLoggedIn()) {
                    currentStatus = "Not logged in";
                    return;
                }
                
                // State machine
                switch (currentState) {
                    case LOADING_ACCOUNTS:
                        handleLoadingAccounts(config);
                        break;
                    case WAITING_FOR_IRONMAN:
                        handleWaitingForIronman();
                        break;
                    case USING_BOND:
                        handleUsingBond();
                        break;
                    case WAITING_FOR_ACCEPTANCE:
                        handleWaitingForAcceptance();
                        break;
                    case WAITING_FOR_LOGOUT:
                        handleWaitingForLogout();
                        break;
                    case COMPLETE:
                        handleComplete();
                        break;
                    case ERROR:
                        handleError();
                        break;
                }
                
                // Check for timeout
                if (System.currentTimeMillis() - stateStartTime > TIMEOUT_MS && 
                    currentState != State.COMPLETE && currentState != State.ERROR) {
                    log.warn("State timeout! Moving to next account.");
                    moveToNextAccount();
                }
                
            } catch (Exception ex) {
                log.error("Error in bond master script: {}", ex.getMessage(), ex);
                currentStatus = "Error: " + ex.getMessage();
            }
        }, 0, 1000, TimeUnit.MILLISECONDS);
        
        return true;
    }
    
    private void handleLoadingAccounts(BondMasterConfig config) {
        currentStatus = "Loading accounts...";
        
        String accountsFile = config.ironmanAccountsFile();
        accounts = BondQueue.loadIronmanAccounts(accountsFile);
        
        if (accounts.isEmpty()) {
            currentStatus = "ERROR: No accounts found!";
            log.error("No ironman accounts loaded from: {}", accountsFile);
            transitionTo(State.ERROR);
            return;
        }
        
        log.info("Loaded {} ironman accounts for bonding", accounts.size());
        currentAccountIndex = 0;
        bondsGiven = 0;
        
        transitionTo(State.WAITING_FOR_IRONMAN);
    }
    
    private void handleWaitingForIronman() {
        if (currentAccountIndex >= accounts.size()) {
            currentStatus = "All accounts processed!";
            log.info("Completed bonding all {} accounts!", accounts.size());
            transitionTo(State.COMPLETE);
            return;
        }
        
        BondQueue.IronmanAccount currentAccount = accounts.get(currentAccountIndex);
        currentStatus = "Waiting for: " + currentAccount.characterName;
        
        // Update queue status
        BondQueue.setCurrentAccount(currentAccount.characterName);
        BondQueue.setStatus("WAITING_FOR_IRONMAN");
        
        // Check if ironman is logged in (nearby)
        boolean ironmanNearby = Rs2Player.getPlayers(p -> 
            p.getName() != null && 
            p.getName().equalsIgnoreCase(currentAccount.characterName)
        ).findFirst().isPresent();
        
        if (ironmanNearby) {
            log.info("Ironman detected: {}", currentAccount.characterName);
            transitionTo(State.USING_BOND);
        }
    }
    
    private void handleUsingBond() {
        BondQueue.IronmanAccount currentAccount = accounts.get(currentAccountIndex);
        currentStatus = "Using bond on: " + currentAccount.characterName;
        
        // Check if we have a bond
        if (!Rs2Inventory.hasItem("Old school bond")) {
            log.error("No bond in inventory! Please add bonds.");
            currentStatus = "ERROR: No bonds!";
            transitionTo(State.ERROR);
            return;
        }
        
        // Use bond on player
        log.info("Using bond on player: {}", currentAccount.characterName);
        
        // Get the player
        net.runelite.client.plugins.microbot.util.player.Rs2PlayerModel player = 
            Rs2Player.getPlayer(currentAccount.characterName, true);
        
        if (player == null) {
            log.warn("Player not found: {}", currentAccount.characterName);
            return;
        }
        
        // First, select the bond
        if (Rs2Inventory.use("Old school bond")) {
            sleep(600);
            
            // Then click on the player (using menu invoke)
            Microbot.status = "Use bond on " + currentAccount.characterName;
            Microbot.doInvoke(
                new net.runelite.client.plugins.microbot.util.menu.NewMenuEntry(
                    0, 
                    0, 
                    net.runelite.api.MenuAction.WIDGET_TARGET_ON_PLAYER.getId(), 
                    player.getId(), 
                    -1, 
                    currentAccount.characterName, 
                    player
                ),
                net.runelite.client.plugins.microbot.util.misc.Rs2UiHelper.getActorClickbox(player)
            );
            
            log.info("Bond used on {}. Waiting for acceptance...", currentAccount.characterName);
            BondQueue.setStatus("BOND_OFFERED");
            sleep(1000);
            transitionTo(State.WAITING_FOR_ACCEPTANCE);
        } else {
            log.warn("Failed to select bond from inventory");
        }
    }
    
    private void handleWaitingForAcceptance() {
        BondQueue.IronmanAccount currentAccount = accounts.get(currentAccountIndex);
        currentStatus = "Waiting for acceptance: " + currentAccount.characterName;
        
        // Check queue status - receiver will update when they get the bond
        String status = BondQueue.getStatus();
        
        if (status.equals("BOND_RECEIVED")) {
            log.info("Bond accepted by: {}", currentAccount.characterName);
            transitionTo(State.WAITING_FOR_LOGOUT);
        }
    }
    
    private void handleWaitingForLogout() {
        BondQueue.IronmanAccount currentAccount = accounts.get(currentAccountIndex);
        currentStatus = "Waiting for logout: " + currentAccount.characterName;
        
        String status = BondQueue.getStatus();
        
        if (status.equals("COMPLETE")) {
            log.info("Account {} completed! Moving to next.", currentAccount.characterName);
            bondsGiven++;
            moveToNextAccount();
        }
    }
    
    private void handleComplete() {
        currentStatus = String.format("Complete! %d/%d bonded", bondsGiven, accounts.size());
        BondQueue.setStatus("ALL_COMPLETE");
        BondQueue.clearCurrentAccount();
    }
    
    private void handleError() {
        currentStatus = "Error - check logs";
        log.error("Bond master in error state");
    }
    
    private void moveToNextAccount() {
        currentAccountIndex++;
        BondQueue.clearCurrentAccount();
        
        if (currentAccountIndex >= accounts.size()) {
            transitionTo(State.COMPLETE);
        } else {
            transitionTo(State.WAITING_FOR_IRONMAN);
        }
    }
    
    private void transitionTo(State newState) {
        log.debug("State transition: {} -> {}", currentState, newState);
        currentState = newState;
        stateStartTime = System.currentTimeMillis();
    }

    @Override
    public void shutdown() {
        super.shutdown();
        BondQueue.setStatus("MASTER_STOPPED");
        log.info("Bond Master stopped. Bonds given: {}", bondsGiven);
    }
}

