package ianm1647.ancientreforging;

import dev.shadowsoffire.apotheosis.data.RarityProvider;
import dev.shadowsoffire.placebo.datagen.DataGenBuilder;
import dev.shadowsoffire.placebo.util.data.DynamicRegistryProvider;
import ianm1647.ancientreforging.data.*;
import com.mojang.logging.LogUtils;
import dev.shadowsoffire.apotheosis.Apoth;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.data.DataProvider;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
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
        NeoForge.EVENT_BUS.register(this);
        Reforge.bootstrap(bus);
        bus.addListener(this::addCreative);
        bus.addListener(this::data);
    }

    public void data(GatherDataEvent e) {
        DataGenBuilder.create(MODID)
                .provider(DynamicRegistryProvider.runSilently(RarityProvider::new))
                .provider(ARLootProvider::create)
                .provider(ARRecipeProvider::new)
                .provider(ARRarityProvider::new)
                .provider(ARAffixProvider::new)
                .provider(ARInvaderProvider::new)
                .build(e);

        Object2IntOpenHashMap<String> map = (Object2IntOpenHashMap<String>) DataProvider.FIXED_ORDER_FIELDS;
        map.put("ancientreforging:ancient", 6);
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
        LOGGER.info("Ancient Reforging is starting...");
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == Apoth.Tabs.ADVENTURE.getKey()) {
            event.accept(Reforge.Items.ANCIENT_MATERIAL.value());
            event.accept(Reforge.Items.ANCIENT_REFORGING_TABLE.value());
        }
    }

    public static ResourceLocation loc(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }
}