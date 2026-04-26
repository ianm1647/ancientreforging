package ianm1647.ancientreforging;

import ianm1647.ancientreforging.block.AncientReforgingTableBlock;
import ianm1647.ancientreforging.block.AncientReforgingTableBlockItem;
import ianm1647.ancientreforging.block.AncientReforgingTableTile;
import ianm1647.ancientreforging.item.AncientSalvageItem;
import ianm1647.ancientreforging.screen.AncientReforgingMenu;
import dev.shadowsoffire.apotheosis.loot.RarityRegistry;
import dev.shadowsoffire.placebo.block_entity.TickingBlockEntityType;
import dev.shadowsoffire.placebo.registry.DeferredHelper;
import net.minecraft.core.Holder;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;

import java.util.function.UnaryOperator;

public class Reforge {

    private static final DeferredHelper R = DeferredHelper.create(AncientReforging.MODID);

    public static class Blocks {
        public static final Holder<Block> ANCIENT_REFORGING_TABLE = R.block("ancient_reforging_table", AncientReforgingTableBlock::new, p -> p.requiresCorrectToolForDrops().strength(4, 1000F));

        private static void bootstrap() {}
    }

    public static class Items {

        public static final Holder<Item> ANCIENT_MATERIAL = ancientMat("ancient");

        public static final Holder<Item> ANCIENT_REFORGING_TABLE = R.blockItem("ancient_reforging_table", Blocks.ANCIENT_REFORGING_TABLE, AncientReforgingTableBlockItem::new, UnaryOperator.identity());

        private static Holder<Item> ancientMat(String id) {
            return R.item(id + "_material", p -> new AncientSalvageItem(RarityRegistry.INSTANCE.holder(AncientReforging.loc(id)), p));
        }

        private static void bootstrap() {}
    }

    public static class BlockEntities {
        public static final BlockEntityType<AncientReforgingTableTile> ANCIENT_REFORGING_TABLE = R.tickingBlockEntity("ancient_reforging_table", AncientReforgingTableTile::new, TickingBlockEntityType.TickSide.CLIENT, Blocks.ANCIENT_REFORGING_TABLE);
        private static void bootstrap() {}
    }

    public static class Menus {
        public static final MenuType<AncientReforgingMenu> ANCIENT_REFORGING = R.menuWithPos("ancient_reforging", AncientReforgingMenu::new);

        private static void bootstrap() {}
    }

    public static void bootstrap(IEventBus bus) {
        bus.register(R);

        Blocks.bootstrap();
        Items.bootstrap();
        BlockEntities.bootstrap();
        Menus.bootstrap();
    }
}
