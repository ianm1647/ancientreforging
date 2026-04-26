package ianm1647.ancientreforging;

import dev.shadowsoffire.apotheosis.data.RarityProvider;
import dev.shadowsoffire.placebo.datagen.DataGenBuilder;
import dev.shadowsoffire.placebo.tabs.TabFillingRegistry;
import dev.shadowsoffire.placebo.util.data.DynamicRegistryProvider;
import ianm1647.ancientreforging.data.*;
import com.mojang.logging.LogUtils;
import dev.shadowsoffire.apotheosis.Apoth;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.data.DataProvider;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.slf4j.Logger;

@Mod(AncientReforging.MODID)
public class AncientReforging
{
    public static final String MODID = "ancientreforging";
    private static final Logger LOGGER = LogUtils.getLogger();

    public AncientReforging(IEventBus bus) {
        Reforge.bootstrap(bus);
        bus.register(this);
    }

    @SubscribeEvent
    public void setup(FMLCommonSetupEvent e) {
        e.enqueueWork(() -> {
            TabFillingRegistry.register(Apoth.Tabs.ADVENTURE.getKey(),
                    Reforge.Items.ANCIENT_MATERIAL,
                    Reforge.Items.ANCIENT_REFORGING_TABLE
            );
        });
    }

    @SubscribeEvent
    public void data(GatherDataEvent.Client e) {
        DataGenBuilder.create(MODID)
                .provider(DynamicRegistryProvider.runSilently((DataGenBuilder.DataProviderFactory<RarityProvider>) RarityProvider::new))
                .provider(ARLootProvider::create)
                .provider(ARRecipeProvider::new)
                .provider(ARRarityProvider::new)
                .provider(ARAffixProvider::new)
                .provider(ARInvaderProvider::new)
                .build(e);

        Object2IntOpenHashMap<String> map = (Object2IntOpenHashMap<String>) DataProvider.FIXED_ORDER_FIELDS;
        map.put("ancientreforging:ancient", 6);
    }

    public static Identifier loc(String path) {
        return Identifier.fromNamespaceAndPath(MODID, path);
    }
}