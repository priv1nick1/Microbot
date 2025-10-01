package net.runelite.client.plugins.microbot.bondmuler;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("BondMaster")
public interface BondMasterConfig extends Config {
    
    @ConfigItem(
        keyName = "guide",
        name = "How to use",
        description = "Instructions for bond master",
        position = 0
    )
    default String guide() {
        return "1. Put bonds in inventory\n" +
               "2. Create ironman-accounts.txt\n" +
               "3. Start this plugin on main\n" +
               "4. Start receiver on ironmen";
    }
    
    @ConfigItem(
        keyName = "ironmanAccountsFile",
        name = "Ironman accounts file",
        description = "Path to ironman-accounts.txt",
        position = 1
    )
    default String ironmanAccountsFile() {
        return "ironman-accounts.txt";
    }
}


