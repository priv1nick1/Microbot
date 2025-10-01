package net.runelite.client.plugins.microbot.nick1privatehunter;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup("nick1privatehunter")
public interface nICK1PrivateHunterConfig extends Config {
    
    @ConfigSection(
            name = "General",
            description = "General settings",
            position = 0
    )
    String generalSection = "general";

    @ConfigItem(
            keyName = "guide",
            name = "How to use",
            description = "How to use this plugin",
            position = 0,
            section = generalSection
    )
    default String guide() {
        return "Private butterfly hunter for nICK1. Catches Ruby Harvest (<65) or Sunlight Moth (65+)!";
    }
}


