package ianm1647.ancientreforging;

import ianm1647.ancientreforging.data.ARAffixProvider;
import ianm1647.ancientreforging.data.ARInvaderProvider;
import ianm1647.ancientreforging.data.ARRarityProvider;
import com.mojang.logging.LogUtils;
import dev.shadowsoffire.apotheosis.Apoth;
import dev.shadowsoffire.placebo.datagen.DataGenBuilder;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.data.event.GatherDataEvent;
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
        AncientReforgingRegistry.bootstrap(bus);
        bus.addListener(this::commonSetup);

        NeoForge.EVENT_BUS.register(this);
        bus.addListener(this::addCreative);
        bus.addListener(this::data);

    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
    }

    public void data(GatherDataEvent e) {
        DataGenBuilder.create(MODID)
                .provider(ARRarityProvider::new)
                .provider(ARAffixProvider::new)
                .provider(ARInvaderProvider::new)
                .build(e);
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
        LOGGER.info("Ancient Reforging is starting...");
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == Apoth.Tabs.ADVENTURE.getKey()) {
            event.accept(AncientReforgingRegistry.Items.ANCIENT_MATERIAL.value());
            event.accept(AncientReforgingRegistry.Items.ANCIENT_REFORGING_TABLE.value());
        }
    }

    public static ResourceLocation loc(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }
}