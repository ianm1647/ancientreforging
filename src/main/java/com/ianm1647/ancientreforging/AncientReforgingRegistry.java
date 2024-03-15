package com.ianm1647.ancientreforging;

import com.google.common.collect.ImmutableSet;
import com.ianm1647.ancientreforging.block.AncientReforgingTableBlock;
import com.ianm1647.ancientreforging.block.AncientReforgingTableTile;
import com.ianm1647.ancientreforging.screen.AncientReforgingMenu;
import dev.shadowsoffire.apotheosis.adventure.Adventure;
import dev.shadowsoffire.placebo.block_entity.TickingBlockEntityType;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public class AncientReforgingRegistry {

    public static void bootstrap() {
        Blocks.bootstrap();
        Tiles.bootstrap();
        Menus.bootstrap();
    }

    public static class Blocks {
        public static final Block ANCIENT_REFORGING_TABLE = block("ancient_reforging_table", new AncientReforgingTableBlock(AbstractBlock.Settings.create().requiresTool().strength(4.0F, 1000.0F), 5));

        private static Block block(String name, Block block) {
            blockItem(name, block);
            return Registry.register(Registries.BLOCK, new Identifier(AncientReforging.MODID, name), block);
        }

        private static Item blockItem(String name, Block block) {
            Item item = Registry.register(Registries.ITEM, new Identifier(AncientReforging.MODID, name),
                    new BlockItem(block, new FabricItemSettings()));
            ItemGroupEvents.modifyEntriesEvent(Adventure.Tabs.ADVENTURE).register(entries -> entries.add(item));
            return item;
        }
        private static void bootstrap() {
        }
    }

    public static class Tiles {
        public static final BlockEntityType<AncientReforgingTableTile> ANCIENT_REFORGING_TABLE = blockEntity("ancient_reforging_table", new TickingBlockEntityType<>(AncientReforgingTableTile::new, ImmutableSet.of(Blocks.ANCIENT_REFORGING_TABLE), true, false));
        ;

        private static void bootstrap() {
            ItemStorage.SIDED.registerForBlockEntity((blockEntity, direction) -> blockEntity.storage, ANCIENT_REFORGING_TABLE);
        }

        public static <T extends BlockEntity> BlockEntityType<T> blockEntity(String id, BlockEntityType<T> be) {
            return Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(AncientReforging.MODID, id), be);
        }
    }

    public static class Menus {
        public static final ScreenHandlerType<AncientReforgingMenu> ANCIENT_REFORGING = (ScreenHandlerType<AncientReforgingMenu>) menu("ancient_reforging", new ExtendedScreenHandlerType<>(AncientReforgingMenu::new));

        private static ScreenHandlerType<?> menu(String name, ScreenHandlerType<?> type) {
            return Registry.register(Registries.SCREEN_HANDLER, new Identifier(AncientReforging.MODID, name), type);
        }
        private static void bootstrap() {
        }
    }

}
