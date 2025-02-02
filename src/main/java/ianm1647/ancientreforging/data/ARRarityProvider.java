package ianm1647.ancientreforging.data;

import ianm1647.ancientreforging.AncientReforging;
import ianm1647.ancientreforging.AncientReforgingRegistry;
import dev.shadowsoffire.apotheosis.Apoth;
import dev.shadowsoffire.apotheosis.affix.AffixType;
import dev.shadowsoffire.apotheosis.loot.LootCategory;
import dev.shadowsoffire.apotheosis.loot.LootRarity;
import dev.shadowsoffire.apotheosis.loot.LootRule;
import dev.shadowsoffire.apotheosis.loot.RarityRegistry;
import dev.shadowsoffire.apotheosis.tiers.TieredWeights;
import dev.shadowsoffire.apotheosis.tiers.WorldTier;
import dev.shadowsoffire.placebo.color.GradientColor;
import dev.shadowsoffire.placebo.util.data.DynamicRegistryProvider;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.Unbreakable;
import org.spongepowered.include.com.google.common.base.Preconditions;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.UnaryOperator;

public class ARRarityProvider extends DynamicRegistryProvider<LootRarity> {

    public ARRarityProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, RarityRegistry.INSTANCE);
    }

    @Override
    public String getName() {
        return "Rarities";
    }

    @Override
    public void generate() {
        this.addRarity("ancient", GradientColor.RAINBOW, AncientReforgingRegistry.Items.ANCIENT_MATERIAL, b -> b
                .sortIndex(800)
                .weights(TieredWeights.builder()
                        .with(WorldTier.HAVEN, 0, 0)
                        .with(WorldTier.FRONTIER, 0, 0)
                        .with(WorldTier.ASCENT, 0, 0)
                        .with(WorldTier.SUMMIT, 0, 0)
                        .with(WorldTier.PINNACLE, 10, 2))
                .rule(new LootRule.AffixLootRule(AffixType.STAT))
                .rule(new LootRule.AffixLootRule(AffixType.STAT))
                .rule(new LootRule.AffixLootRule(AffixType.STAT))
                .rule(new LootRule.AffixLootRule(AffixType.STAT))
                .rule(new LootRule.AffixLootRule(AffixType.STAT))
                .rule(new LootRule.AffixLootRule(AffixType.BASIC_EFFECT))
                .rule(new LootRule.AffixLootRule(AffixType.BASIC_EFFECT))
                .rule(new LootRule.AffixLootRule(AffixType.BASIC_EFFECT))
                .rule(new LootRule.AffixLootRule(AffixType.ABILITY))
                .rule(new LootRule.AffixLootRule(AffixType.ABILITY))
                .rule(new LootRule.SelectLootRule(0.85F, // 95% chance for 3-4 sockets, 5% chance for guaranteed 5 sockets.
                        new LootRule.SocketLootRule(3, 4),
                        new LootRule.SocketLootRule(5, 5)))
                .rule(new LootRule.SelectLootRule(0.95F, // 99% chance to roll a durability bonus, 1% to be unbreakable.
                        new LootRule.DurabilityLootRule(0.6F, 0.9F),
                        new LootRule.ComponentLootRule(DataComponentPatch.builder()
                                .set(DataComponents.UNBREAKABLE, new Unbreakable(true))
                                .set(Apoth.Components.DURABILITY_BONUS, 0F)
                                .build()))));
    }

    static <T> LootRule componentRule(DataComponentType<T> type, T value) {
        return new LootRule.ComponentLootRule(DataComponentPatch.builder().set(type, value).build());
    }

    void addRarity(String id, TextColor color, Holder<Item> material, UnaryOperator<ARRarityProvider.RarityBuilder> config) {
        this.add(AncientReforging.loc(id), config.apply(builder(color, material)).build());
    }

    public static RarityBuilder builder(TextColor color, Holder<Item> material) {
        return new RarityBuilder(color, material);
    }

    public static class RarityBuilder {

        private final TextColor color;
        private final Holder<Item> material;
        private TieredWeights weights;
        private final List<LootRule> rules = new ArrayList<>();
        private final Map<LootCategory, List<LootRule>> overrides = new IdentityHashMap<>();
        private int index = 1000;

        public RarityBuilder(TextColor color, Holder<Item> material) {
            this.color = color;
            this.material = material;
        }

        public RarityBuilder weights(TieredWeights.Builder builder) {
            this.weights = builder.build();
            return this;
        }

        public RarityBuilder rule(LootRule rule) {
            this.rules.add(rule);
            return this;
        }

        public RarityBuilder override(LootCategory category, UnaryOperator<ARRarityProvider.RuleListBuilder> config) {
            List<LootRule> list = new ArrayList<>();
            config.apply(new ARRarityProvider.RuleListBuilder(){

                @Override
                public ARRarityProvider.RuleListBuilder rule(LootRule rule) {
                    list.add(rule);
                    return this;
                }

            });
            this.overrides.put(category, list);
            return this;
        }

        public RarityBuilder sortIndex(int index) {
            this.index = index;
            return this;
        }

        public LootRarity build() {
            Preconditions.checkNotNull(this.weights);
            Preconditions.checkArgument(this.rules.size() > 0);
            return new LootRarity(this.color, this.material, this.weights, this.rules, this.overrides, this.index);
        }

    }

    public static interface RuleListBuilder {
        RuleListBuilder rule(LootRule rule);
    }

}
