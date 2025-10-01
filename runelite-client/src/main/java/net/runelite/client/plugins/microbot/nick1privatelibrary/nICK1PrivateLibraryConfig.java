package net.runelite.client.plugins.microbot.nick1privatelibrary;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup("nick1privatelibrary")
public interface nICK1PrivateLibraryConfig extends Config {
    
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
        return "Private Arc Library book collector for nICK1. Automatically collects magic exp books from the Kourend Library!";
    }
}
