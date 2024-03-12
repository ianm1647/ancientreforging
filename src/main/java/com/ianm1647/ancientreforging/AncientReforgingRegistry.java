package com.ianm1647.ancientreforging;

import com.ianm1647.ancientreforging.block.AncientReforgingTableBlock;
import com.ianm1647.ancientreforging.block.AncientReforgingTableTile;
import com.ianm1647.ancientreforging.screen.AncientReforgingMenu;
import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.adventure.affix.salvaging.SalvageItem;
import dev.shadowsoffire.apotheosis.adventure.loot.RarityRegistry;
import dev.shadowsoffire.placebo.menu.MenuUtil;
import dev.shadowsoffire.placebo.registry.DeferredHelper;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class AncientReforgingRegistry {

    private static final DeferredHelper R = DeferredHelper.create(AncientReforging.MODID);

    private static class Items {

        private static RegistryObject<Item> rarityMat(String id) {
            return R.item(id + "_material", () -> new SalvageItem(RarityRegistry.INSTANCE.holder(Apotheosis.loc(id)), new Item.Properties()));
        }

        private static void bootstrap() {}
    }

    public static class Blocks {
        public static final RegistryObject<AncientReforgingTableBlock> ANCIENT_REFORGING_TABLE = registerBlock("ancient_reforging_table",
                () -> new AncientReforgingTableBlock(BlockBehaviour.Properties.of().requiresCorrectToolForDrops().strength(4, 1000F), 5));

        private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
            RegistryObject<T> toReturn = R.block(name, block);
            registerBlockItem(name, toReturn);
            return toReturn;
        }

        private static <T extends Block>RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block) {
            return R.item(name, () -> new BlockItem(block.get(), new Item.Properties()));
        }

        private static void bootstrap() {}
    }

    public static class BlockEntities {
        public static final RegistryObject<BlockEntityType<AncientReforgingTableTile>> ANCIENT_REFORGING_TABLE = R.blockEntity("ancient_reforging_table", () ->
                BlockEntityType.Builder.of(AncientReforgingTableTile::new, Blocks.ANCIENT_REFORGING_TABLE.get()).build(null));

        private static void bootstrap() {}
    }

    public static class Menus {
        public static final RegistryObject<MenuType<AncientReforgingMenu>> ANCIENT_REFORGING = R.menu("ancient_reforging", () -> MenuUtil.posType(AncientReforgingMenu::new));

        private static void bootstrap() {}
    }

    public static void bootstrap() {
        Items.bootstrap();
        Blocks.bootstrap();
        BlockEntities.bootstrap();
        Menus.bootstrap();
    }
}
