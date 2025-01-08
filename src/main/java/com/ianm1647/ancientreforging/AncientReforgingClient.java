package com.ianm1647.ancientreforging;

import com.ianm1647.ancientreforging.block.AncientReforgingTableTileRenderer;
import com.ianm1647.ancientreforging.screen.AncientReforgingScreen;
import dev.shadowsoffire.apotheosis.Apotheosis;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, modid = Apotheosis.MODID, value = Dist.CLIENT)
public class AncientReforgingClient {

    @SubscribeEvent
    public static void setup(FMLClientSetupEvent e) {
        e.enqueueWork(() -> {
            BlockEntityRenderers.register(AncientReforgingRegistry.BlockEntities.ANCIENT_REFORGING_TABLE, k -> new AncientReforgingTableTileRenderer());
        });
    }

    @SubscribeEvent
    public static void screens(RegisterMenuScreensEvent e) {
        e.register(AncientReforgingRegistry.Menus.ANCIENT_REFORGING, AncientReforgingScreen::new);
    }

    @SubscribeEvent
    public static void models(ModelEvent.RegisterAdditional e) {
        e.register(AncientReforgingTableTileRenderer.HAMMER);
    }
}
