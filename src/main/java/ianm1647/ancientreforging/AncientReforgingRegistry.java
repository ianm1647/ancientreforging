package ianm1647.ancientreforging;

import ianm1647.ancientreforging.block.AncientReforgingTableBlock;
import ianm1647.ancientreforging.block.AncientReforgingTableTile;
import ianm1647.ancientreforging.screen.AncientReforgingMenu;
import dev.shadowsoffire.apotheosis.affix.salvaging.SalvageItem;
import dev.shadowsoffire.apotheosis.loot.RarityRegistry;
import dev.shadowsoffire.placebo.block_entity.TickingBlockEntityType;
import dev.shadowsoffire.placebo.color.GradientColor;
import dev.shadowsoffire.placebo.registry.DeferredHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;

public class AncientReforgingRegistry {

    private static final DeferredHelper R = DeferredHelper.create(AncientReforging.MODID);

    public static class Blocks {
        public static final Holder<Block> ANCIENT_REFORGING_TABLE = R.block("ancient_reforging_table", AncientReforgingTableBlock::new, p -> p.requiresCorrectToolForDrops().strength(4, 1000F));

        private static void bootstrap() {}
    }

    public static class Items {

        public static final Holder<Item> ANCIENT_MATERIAL = rarityMat("ancient", new Item.Properties().component(DataComponents.ITEM_NAME, Component.translatable("item.apotheosis.ancient_material").withStyle(ChatFormatting.OBFUSCATED).withStyle(s -> s.withColor(GradientColor.RAINBOW))));

        public static final Holder<Item> ANCIENT_REFORGING_TABLE = R.blockItem("ancient_reforging_table", Blocks.ANCIENT_REFORGING_TABLE, p -> p.component(DataComponents.ITEM_NAME, Component.translatable("block.ancientreforging.ancient_reforging_table").withStyle(s -> s.withColor(GradientColor.RAINBOW))));

        private static Holder<Item> rarityMat(String id, Item.Properties properties) {
            return R.item(id + "_material", () -> new SalvageItem(RarityRegistry.INSTANCE.holder(AncientReforging.loc(id)), properties));
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
