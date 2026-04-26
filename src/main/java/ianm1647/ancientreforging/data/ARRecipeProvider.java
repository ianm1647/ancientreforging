package ianm1647.ancientreforging.data;

import dev.shadowsoffire.apotheosis.Apoth;
import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.affix.reforging.ReforgingRecipe;
import dev.shadowsoffire.apotheosis.affix.salvaging.SalvagingRecipe;
import dev.shadowsoffire.apotheosis.loot.LootRarity;
import dev.shadowsoffire.apotheosis.loot.RarityRegistry;
import dev.shadowsoffire.apotheosis.socket.AddSocketsRecipe;
import dev.shadowsoffire.apotheosis.socket.gem.Purity;
import dev.shadowsoffire.apotheosis.socket.gem.cutting.PurityUpgradeRecipe;
import dev.shadowsoffire.apotheosis.util.AffixItemIngredient;
import dev.shadowsoffire.apotheosis.util.GemIngredient;
import dev.shadowsoffire.placebo.datagen.LegacyRecipeProvider;
import dev.shadowsoffire.placebo.reload.DynamicHolder;
import ianm1647.ancientreforging.AncientReforging;
import ianm1647.ancientreforging.Reforge;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.crafting.SizedIngredient;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

public class ARRecipeProvider extends LegacyRecipeProvider {
    public ARRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, AncientReforging.MODID);
    }

    @Override
    public String getName() {
        return "Ancient Reforging Recipes";
    }

    private static ResourceKey<Recipe<?>> key(Identifier id) {
        return ResourceKey.create(Registries.RECIPE, id);
    }

    @Override
    protected void genRecipes(RecipeOutput recipeOutput, HolderLookup.Provider registries) {
        addShaped(Reforge.Blocks.ANCIENT_REFORGING_TABLE, 3, 3, null, Reforge.Items.ANCIENT_MATERIAL, null, Apoth.Items.MYTHIC_MATERIAL, Apoth.Items.REFORGING_TABLE, Apoth.Items.MYTHIC_MATERIAL, Items.END_STONE_BRICKS, Items.END_STONE_BRICKS, Items.END_STONE_BRICKS);

        addAffixSalvaging("ancient", Reforge.Items.ANCIENT_MATERIAL);

        addReforging("common", 1, 0, 2, Reforge.Blocks.ANCIENT_REFORGING_TABLE);
        addReforging("uncommon", 2, 1, 5, Reforge.Blocks.ANCIENT_REFORGING_TABLE);
        addReforging("rare", 2, 2, 15, Reforge.Blocks.ANCIENT_REFORGING_TABLE);
        addReforging("epic", 2, 4, 30, Reforge.Blocks.ANCIENT_REFORGING_TABLE);
        addReforging("mythic", 3, 5, 50, Reforge.Blocks.ANCIENT_REFORGING_TABLE);
        addAncientReforging("ancient", 3, 10, 125, Reforge.Blocks.ANCIENT_REFORGING_TABLE);

        addPurityUpgrade(Purity.PERFECT, 9, List.of(Reforge.Items.ANCIENT_MATERIAL), 1);
    }

    private void addPurityUpgrade(Purity purity, int gemDust, List<Holder<Item>> materials, int zerothMatCost) {
        SizedIngredient dustIng = SizedIngredient.of(Apoth.Items.GEM_DUST.value(), gemDust);
        List<SizedIngredient> materialIngs = new ArrayList<>();
        int matAmount = zerothMatCost;
        for (Holder<Item> mat : materials) {
            materialIngs.add(SizedIngredient.of(mat.value(), matAmount));
            matAmount /= 3;
        }
        var recipe = new PurityUpgradeRecipe(purity, List.of(dustIng), materialIngs);
        this.recipeOutput.accept(key(AncientReforging.loc("gem_cutting/" + purity.name().toLowerCase(Locale.ROOT))), recipe, null);
    }

    @SafeVarargs
    private void addReforging(String rarity, int mats, int sigils, int levels, Holder<Block>... tables) {
        DynamicHolder<LootRarity> lRarity = RarityRegistry.INSTANCE.holder(Apotheosis.loc(rarity));
        this.recipeOutput.accept(key(AncientReforging.loc("reforging/" + rarity)), new ReforgingRecipe(lRarity, mats, sigils, levels, HolderSet.direct(tables)), null);
    }

    @SafeVarargs
    private void addAncientReforging(String rarity, int mats, int sigils, int levels, Holder<Block>... tables) {
        DynamicHolder<LootRarity> lRarity = RarityRegistry.INSTANCE.holder(AncientReforging.loc(rarity));
        this.recipeOutput.accept(key(AncientReforging.loc("reforging/" + rarity)), new ReforgingRecipe(lRarity, mats, sigils, levels, HolderSet.direct(tables)), null);
    }

    private void addGemSalvaging(Purity purity, int min, int max) {
        Ingredient input = new Ingredient(new GemIngredient(purity));
        SalvagingRecipe.OutputData output = new SalvagingRecipe.OutputData(Apoth.Items.GEM_DUST.value(), min, max);
        this.addSalvaging("gem/" + purity.getSerializedName(), input, output);
    }

    private void addAffixSalvaging(String rarity, Holder<Item> material) {
        Ingredient input = new Ingredient(new AffixItemIngredient(RarityRegistry.INSTANCE.holder(AncientReforging.loc(rarity))));
        SalvagingRecipe.OutputData output = new SalvagingRecipe.OutputData(material.value(), 1, 4);
        this.addSalvaging("affix_item/" + rarity, input, output);
    }

    private void addOtherSalvaging(String path, Ingredient input, SalvagingRecipe.OutputData output) {
        addSalvaging("salvaging/other/" + path, input, List.of(output));
    }

    private void addSalvaging(String path, Ingredient input, SalvagingRecipe.OutputData output) {
        addSalvaging("salvaging/" + path, input, List.of(output));
    }

    private void addSalvaging(String path, Ingredient input, List<SalvagingRecipe.OutputData> outputs) {
        this.recipeOutput.accept(key(AncientReforging.loc(path)), new SalvagingRecipe(input, outputs), null);
    }

    private void addSockets(String path, Ingredient input, int maxSockets) {
        this.recipeOutput.accept(key(AncientReforging.loc(path)), new AddSocketsRecipe(input, maxSockets), null);
    }
}
