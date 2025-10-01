package net.runelite.client.plugins.microbot.playerhoplog;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup("playerhoplog")
public interface PlayerHopLogConfig extends Config {
    
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
        return "This plugin automatically hops to a random P2P world when ANY player appears nearby (even at 50+ tile distance)!";
    }
}



