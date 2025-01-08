package com.ianm1647.ancientreforging;

import com.mojang.logging.LogUtils;
import dev.shadowsoffire.apotheosis.Apoth;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import org.slf4j.Logger;

@Mod(AncientReforging.MODID)
public class AncientReforging
{
    public static final String MODID = "ancientreforging";
    private static final Logger LOGGER = LogUtils.getLogger();

    public AncientReforging(IEventBus bus)
    {
        AncientReforgingRegistry.bootstrap();
        bus.addListener(this::commonSetup);

        NeoForge.EVENT_BUS.register(this);
        bus.addListener(this::addCreative);

    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {

    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if(event.getTabKey() == Apoth.Tabs.ADVENTURE.getKey()) {
            event.accept(AncientReforgingRegistry.Items.ANCIENT_MATERIAL.value());
            event.accept(AncientReforgingRegistry.Items.ANCIENT_REFORGING_TABLE.value());
        }
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
        LOGGER.info("Ancient Reforging is starting...");
    }
}