package net.runelite.client.plugins.microbot.bondmuler;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Shared queue system for bond muling.
 * Master and receiver plugins communicate through files in .microbot/bonding/
 */
@Slf4j
public class BondQueue {
    
    private static final String BOND_DIR = System.getProperty("user.home") + "/.microbot/bonding/";
    private static final String QUEUE_FILE = BOND_DIR + "queue.txt";
    private static final String STATUS_FILE = BOND_DIR + "status.txt";
    private static final String CURRENT_FILE = BOND_DIR + "current.txt";
    
    private static final ReentrantLock lock = new ReentrantLock();
    
    /**
     * Initialize bonding directory
     */
    public static void initialize() {
        try {
            Files.createDirectories(Paths.get(BOND_DIR));
            
            // Create files if they don't exist
            if (!Files.exists(Paths.get(QUEUE_FILE))) {
                Files.write(Paths.get(QUEUE_FILE), new ArrayList<>());
            }
            if (!Files.exists(Paths.get(STATUS_FILE))) {
                Files.write(Paths.get(STATUS_FILE), Collections.singletonList("IDLE"));
            }
            if (!Files.exists(Paths.get(CURRENT_FILE))) {
                Files.write(Paths.get(CURRENT_FILE), Collections.singletonList(""));
            }
            
            log.info("Bond queue system initialized at: {}", BOND_DIR);
        } catch (IOException e) {
            log.error("Failed to initialize bond queue system", e);
        }
    }
    
    /**
     * Load queue from ironman-accounts.txt
     */
    public static List<IronmanAccount> loadIronmanAccounts(String filePath) {
        List<IronmanAccount> accounts = new ArrayList<>();
        
        try {
            if (!Files.exists(Paths.get(filePath))) {
                log.warn("Ironman accounts file not found: {}", filePath);
                return accounts;
            }
            
            List<String> lines = Files.readAllLines(Paths.get(filePath));
            
            for (String line : lines) {
                line = line.trim();
                
                // Skip comments and empty lines
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                
                // Parse: email:password (character name detected on login)
                String[] parts = line.split(":");
                if (parts.length >= 2) {
                    String email = parts[0].trim();
                    String password = parts[1].trim();
                    
                    accounts.add(new IronmanAccount(email, password, null));
                } else {
                    log.warn("Invalid line format (expected email:password): {}", line);
                }
            }
            
            log.info("Loaded {} ironman accounts", accounts.size());
            
        } catch (IOException e) {
            log.error("Failed to load ironman accounts", e);
        }
        
        return accounts;
    }
    
    /**
     * Get current status
     */
    public static String getStatus() {
        lock.lock();
        try {
            List<String> lines = Files.readAllLines(Paths.get(STATUS_FILE));
            return lines.isEmpty() ? "IDLE" : lines.get(0);
        } catch (IOException e) {
            return "ERROR";
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * Set current status
     */
    public static void setStatus(String status) {
        lock.lock();
        try {
            Files.write(Paths.get(STATUS_FILE), Collections.singletonList(status));
            log.info("Status updated: {}", status);
        } catch (IOException e) {
            log.error("Failed to update status", e);
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * Get current account being processed
     */
    public static String getCurrentAccount() {
        lock.lock();
        try {
            List<String> lines = Files.readAllLines(Paths.get(CURRENT_FILE));
            return lines.isEmpty() ? "" : lines.get(0);
        } catch (IOException e) {
            return "";
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * Set current account being processed
     */
    public static void setCurrentAccount(String characterName) {
        lock.lock();
        try {
            Files.write(Paths.get(CURRENT_FILE), Collections.singletonList(characterName));
            log.info("Current account set to: {}", characterName);
        } catch (IOException e) {
            log.error("Failed to set current account", e);
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * Clear current account
     */
    public static void clearCurrentAccount() {
        setCurrentAccount("");
    }
    
    /**
     * Ironman account data class
     */
    public static class IronmanAccount {
        public final String email;
        public final String password;
        public String characterName; // Detected on login
        
        public IronmanAccount(String email, String password, String characterName) {
            this.email = email;
            this.password = password;
            this.characterName = characterName;
        }
        
        @Override
        public String toString() {
            return (characterName != null ? characterName : email) + " (" + email + ")";
        }
    }
}

