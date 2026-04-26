package ianm1647.ancientreforging;

import dev.shadowsoffire.apotheosis.Apotheosis;
import ianm1647.ancientreforging.block.AncientReforgingTableTileRenderer;
import ianm1647.ancientreforging.screen.AncientReforgingScreen;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.model.standalone.SimpleUnbakedStandaloneModel;
import net.neoforged.neoforge.client.model.standalone.StandaloneModelKey;

@EventBusSubscriber(modid = AncientReforging.MODID, value = Dist.CLIENT)
public class AncientReforgingClient {
    public static final StandaloneModelKey<BlockStateModel> HAMMER_MODEL = new StandaloneModelKey<>(() -> "apotheosis:hammer");

    @SubscribeEvent
    public static void setup(FMLClientSetupEvent e) {
        e.enqueueWork(() -> {
            BlockEntityRenderers.register(Reforge.BlockEntities.ANCIENT_REFORGING_TABLE, AncientReforgingTableTileRenderer::new);
        });
    }

    @SubscribeEvent
    public static void screens(RegisterMenuScreensEvent e) {
        e.register(Reforge.Menus.ANCIENT_REFORGING, AncientReforgingScreen::new);
    }

    @SubscribeEvent
    public static void registerStandaloneModels(ModelEvent.RegisterStandalone e) {
        e.register(HAMMER_MODEL, SimpleUnbakedStandaloneModel.blockStateModel(Apotheosis.loc("item/hammer")));
    }
}
