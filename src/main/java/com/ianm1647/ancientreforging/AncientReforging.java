package com.ianm1647.ancientreforging;

import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.adventure.Adventure;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AncientReforging implements ModInitializer {

	public static final String MODID = "ancientreforging";
    public static final Logger LOGGER = LoggerFactory.getLogger("ancientreforging");

	@Override
	public void onInitialize() {
		if (Apotheosis.enableAdventure) {
			AncientReforgingRegistry.bootstrap();
			ItemGroupEvents.modifyEntriesEvent(Adventure.Tabs.ADVENTURE).register(entries -> entries.add(Adventure.Items.ANCIENT_MATERIAL));
		}
	}
}