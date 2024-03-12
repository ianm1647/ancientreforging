package com.ianm1647.ancientreforging;

import com.ianm1647.ancientreforging.block.AncientReforgingTableTileRenderer;
import com.ianm1647.ancientreforging.screen.AncientReforgingScreen;
import com.mojang.logging.LogUtils;
import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.adventure.Adventure;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(AncientReforging.MODID)
public class AncientReforging
{
    public static final String MODID = "ancientreforging";
    private static final Logger LOGGER = LogUtils.getLogger();

    public AncientReforging()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        AncientReforgingRegistry.bootstrap();

        modEventBus.addListener(this::commonSetup);

        MinecraftForge.EVENT_BUS.register(this);
        modEventBus.addListener(this::addCreative);

    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {

    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if(Apotheosis.enableAdventure) {
            if(event.getTabKey() == Adventure.Tabs.ADVENTURE.getKey()) {
                event.accept(Adventure.Items.ANCIENT_MATERIAL.get());
                event.accept(AncientReforgingRegistry.Blocks.ANCIENT_REFORGING_TABLE);
            }
        }
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {

    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            MenuScreens.register(AncientReforgingRegistry.Menus.ANCIENT_REFORGING.get(), AncientReforgingScreen::new);
            BlockEntityRenderers.register(AncientReforgingRegistry.BlockEntities.ANCIENT_REFORGING_TABLE.get(), k -> new AncientReforgingTableTileRenderer());
        }
    }

    @Mod.EventBusSubscriber(modid = Apotheosis.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModBusSub {
        @SubscribeEvent
        public static void models(ModelEvent.RegisterAdditional e) {
            e.register(new ResourceLocation(Apotheosis.MODID, "item/hammer"));
        }
    }
}

