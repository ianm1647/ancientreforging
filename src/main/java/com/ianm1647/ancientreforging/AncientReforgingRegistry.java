package com.ianm1647.ancientreforging;

import com.ianm1647.ancientreforging.block.AncientReforgingTableBlock;
import com.ianm1647.ancientreforging.block.AncientReforgingTableTile;
import com.ianm1647.ancientreforging.screen.AncientReforgingMenu;
import dev.shadowsoffire.apotheosis.affix.salvaging.SalvageItem;
import dev.shadowsoffire.apotheosis.loot.RarityRegistry;
import dev.shadowsoffire.placebo.block_entity.TickingBlockEntityType;
import dev.shadowsoffire.placebo.registry.DeferredHelper;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class AncientReforgingRegistry {

    private static final DeferredHelper R = DeferredHelper.create(AncientReforging.MODID);

    public static class Blocks {
        public static final Holder<Block> ANCIENT_REFORGING_TABLE = R.block("ancient_reforging_table", AncientReforgingTableBlock::new, p -> p.requiresCorrectToolForDrops().strength(4, 1000F));

        private static void bootstrap() {}
    }

    public static class Items {

        public static final Holder<Item> ANCIENT_MATERIAL = rarityMat("ancient");

        public static final Holder<Item> ANCIENT_REFORGING_TABLE = R.blockItem("ancient_reforging_table", Blocks.ANCIENT_REFORGING_TABLE, p -> p.rarity(Rarity.EPIC));

        private static Holder<Item> rarityMat(String id) {
            return R.item(id + "_material", () -> new SalvageItem(RarityRegistry.INSTANCE.holder(ResourceLocation.fromNamespaceAndPath(AncientReforging.MODID, id)), new Item.Properties()));
        }

        private static void bootstrap() {}
    }

    public static class BlockEntities {
        public static final BlockEntityType<AncientReforgingTableTile> ANCIENT_REFORGING_TABLE = R.tickingBlockEntity("ancient_reforging_table", AncientReforgingTableTile::new,
                TickingBlockEntityType.TickSide.CLIENT, Blocks.ANCIENT_REFORGING_TABLE);
        private static void bootstrap() {}
    }

    public static class Menus {
        public static final MenuType<AncientReforgingMenu> ANCIENT_REFORGING = R.menuWithPos("ancient_reforging", AncientReforgingMenu::new);

        private static void bootstrap() {}
    }

    public static void bootstrap() {
        Blocks.bootstrap();
        Items.bootstrap();
        BlockEntities.bootstrap();
        Menus.bootstrap();
    }
}
