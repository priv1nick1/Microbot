package net.runelite.client.plugins.microbot.util.security;

import lombok.extern.slf4j.Slf4j;

/**
 * External Login Handler for EternalFarm and other client managers.
 * 
 * This class allows external tools to inject login credentials via:
 * 1. System Properties (Java -D flags)
 * 2. Environment Variables
 * 3. Direct method calls
 * 
 * Usage with EternalFarm:
 * Add these JVM arguments when launching Microbot:
 * -Dmicrobot.username=your_email@example.com
 * -Dmicrobot.password=your_password
 * -Dmicrobot.proxy=ip:port:user:pass (optional)
 * -Dmicrobot.world=360 (optional)
 * -Dmicrobot.members=true (optional)
 */
@Slf4j
public class ExternalLogin {
    
    private static final String PROP_USERNAME = "microbot.username";
    private static final String PROP_PASSWORD = "microbot.password";
    private static final String PROP_PROXY = "microbot.proxy";
    private static final String PROP_WORLD = "microbot.world";
    private static final String PROP_MEMBERS = "microbot.members";
    
    private static final String ENV_USERNAME = "MICROBOT_USERNAME";
    private static final String ENV_PASSWORD = "MICROBOT_PASSWORD";
    private static final String ENV_PROXY = "MICROBOT_PROXY";
    private static final String ENV_WORLD = "MICROBOT_WORLD";
    private static final String ENV_MEMBERS = "MICROBOT_MEMBERS";
    
    // Stored credentials for EternalFarm integration
    private static String externalUsername = null;
    private static String externalPassword = null;
    private static String externalProxy = null;
    private static Integer externalWorld = null;
    private static Boolean externalMembers = null;
    
    /**
     * Initialize external login from system properties or environment variables.
     * Call this during Microbot startup.
     */
    public static void initialize() {
        // Try system properties first (highest priority)
        externalUsername = System.getProperty(PROP_USERNAME);
        externalPassword = System.getProperty(PROP_PASSWORD);
        externalProxy = System.getProperty(PROP_PROXY);
        String worldStr = System.getProperty(PROP_WORLD);
        String membersStr = System.getProperty(PROP_MEMBERS);
        
        // Fall back to environment variables if properties not set
        if (externalUsername == null) {
            externalUsername = System.getenv(ENV_USERNAME);
        }
        if (externalPassword == null) {
            externalPassword = System.getenv(ENV_PASSWORD);
        }
        if (externalProxy == null) {
            externalProxy = System.getenv(ENV_PROXY);
        }
        if (worldStr == null) {
            worldStr = System.getenv(ENV_WORLD);
        }
        if (membersStr == null) {
            membersStr = System.getenv(ENV_MEMBERS);
        }
        
        // Parse world and members
        if (worldStr != null && !worldStr.isEmpty()) {
            try {
                externalWorld = Integer.parseInt(worldStr);
            } catch (NumberFormatException e) {
                log.warn("Invalid world number: {}", worldStr);
            }
        }
        
        if (membersStr != null && !membersStr.isEmpty()) {
            externalMembers = Boolean.parseBoolean(membersStr);
        }
        
        // Log what we found (without showing password)
        if (hasExternalCredentials()) {
            log.info("External credentials detected:");
            log.info("  Username: {}", externalUsername);
            log.info("  Password: {}", externalPassword != null ? "***SET***" : "NOT SET");
            log.info("  Proxy: {}", externalProxy != null ? externalProxy : "NOT SET");
            log.info("  World: {}", externalWorld != null ? externalWorld : "DEFAULT");
            log.info("  Members: {}", externalMembers != null ? externalMembers : "AUTO-DETECT");
        } else {
            log.info("No external credentials detected, using RuneLite profile system");
        }
    }
    
    /**
     * Check if external credentials are available.
     */
    public static boolean hasExternalCredentials() {
        return externalUsername != null && externalPassword != null;
    }
    
    /**
     * Get external username.
     */
    public static String getUsername() {
        return externalUsername;
    }
    
    /**
     * Get external password (encrypted if needed).
     */
    public static String getPassword() {
        return externalPassword;
    }
    
    /**
     * Get external proxy string (format: ip:port:user:pass).
     */
    public static String getProxy() {
        return externalProxy;
    }
    
    /**
     * Get external world number.
     */
    public static Integer getWorld() {
        return externalWorld;
    }
    
    /**
     * Get external members status.
     */
    public static Boolean isMembers() {
        return externalMembers;
    }
    
    /**
     * Manually set credentials (for programmatic use).
     */
    public static void setCredentials(String username, String password) {
        setCredentials(username, password, null, null, null);
    }
    
    /**
     * Manually set credentials with all options.
     */
    public static void setCredentials(String username, String password, String proxy, Integer world, Boolean members) {
        externalUsername = username;
        externalPassword = password;
        externalProxy = proxy;
        externalWorld = world;
        externalMembers = members;
        
        log.info("External credentials set programmatically");
    }
    
    /**
     * Clear external credentials.
     */
    public static void clear() {
        externalUsername = null;
        externalPassword = null;
        externalProxy = null;
        externalWorld = null;
        externalMembers = null;
        
        log.info("External credentials cleared");
    }
    
    /**
     * Get encrypted password for use with Login class.
     * Returns encrypted password if possible, otherwise returns as-is.
     */
    public static String getEncryptedPassword() {
        if (externalPassword == null) {
            return null;
        }
        
        try {
            return Encryption.encrypt(externalPassword);
        } catch (Exception e) {
            log.warn("Could not encrypt external password, using as-is");
            return externalPassword;
        }
    }
}

