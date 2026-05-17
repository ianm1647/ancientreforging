package ianm1647.ancientreforging.block;

import java.util.Comparator;
import java.util.function.Consumer;

import dev.shadowsoffire.apotheosis.affix.reforging.ReforgingRecipe;
import dev.shadowsoffire.apotheosis.affix.reforging.ReforgingRecipeCache;
import dev.shadowsoffire.apotheosis.affix.reforging.ReforgingTableBlockItem;
import dev.shadowsoffire.apotheosis.loot.LootRarity;
import dev.shadowsoffire.apotheosis.loot.RarityRegistry;
import dev.shadowsoffire.placebo.color.GradientColor;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.Block;

public class AncientReforgingTableBlockItem extends ReforgingTableBlockItem {

    public AncientReforgingTableBlockItem(Block block, Item.Properties properties) {
        super(block, properties);
    }

    @Override
    public Component getName(ItemStack pStack) {
        return Component.translatable(this.getDescriptionId()).withStyle(Style.EMPTY.withColor(GradientColor.RAINBOW.getValue()));
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag tooltipFlag) {
        tooltip.accept(Component.translatable("block.ancientreforging.ancient_reforging_table.desc").withStyle(ChatFormatting.GRAY));

        LootRarity max = this.computeMaxRarity();
        if (max == null) {
            return;
        }

        tooltip.accept(Component.translatable("block.ancientreforging.ancient_reforging_table.desc2", max.toComponent()).withStyle(ChatFormatting.GRAY));

//        LootRarity globalMax = RarityRegistry.getSortedRarities().stream()
//                .max(Comparator.comparingInt(LootRarity::sortIndex))
//                .orElse(null);
//
//        if (globalMax != null && max.sortIndex() < globalMax.sortIndex()) {
//        }
    }

    @SuppressWarnings("deprecation")
    private LootRarity computeMaxRarity() {
        LootRarity best = null;
        for (RecipeHolder<ReforgingRecipe> holder : ReforgingRecipeCache.all()) {
            ReforgingRecipe recipe = holder.value();
            if (!recipe.tables().contains(this.getBlock().builtInRegistryHolder()) || !recipe.rarity().isBound()) {
                continue;
            }
            LootRarity r = recipe.rarity().get();
            if (best == null || r.sortIndex() > best.sortIndex()) {
                best = r;
            }
        }
        return best;
    }
}
