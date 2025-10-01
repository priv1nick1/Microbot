package net.runelite.client.plugins.microbot.bondmuler;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("BondReceiver")
public interface BondReceiverConfig extends Config {
    
    @ConfigItem(
        keyName = "guide",
        name = "How to use",
        description = "Instructions for bond receiver",
        position = 0
    )
    default String guide() {
        return "1. Add this account to ironman-accounts.txt\n" +
               "2. Start Bond Master on main account\n" +
               "3. Start this plugin on ironman\n" +
               "4. Script will auto-receive bond and apply membership";
    }
}



