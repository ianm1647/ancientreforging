package ianm1647.ancientreforging.data;

import dev.shadowsoffire.apotheosis.Apotheosis;
import ianm1647.ancientreforging.AncientReforging;
import ianm1647.ancientreforging.Reforge;
import dev.shadowsoffire.apotheosis.Apoth;
import dev.shadowsoffire.apotheosis.affix.AffixType;
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
import net.minecraft.util.Unit;
import net.minecraft.world.item.Item;

import java.util.concurrent.CompletableFuture;
import java.util.function.UnaryOperator;

public class ARRarityProvider extends DynamicRegistryProvider<LootRarity> {

    public ARRarityProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, RarityRegistry.INSTANCE);
    }

    @Override
    public String getName() {
        return "Ancient Rarities";
    }

    @Override
    public void generate() {
        this.addAncient("ancient", GradientColor.RAINBOW, Reforge.Items.ANCIENT_MATERIAL, b -> b
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
                                .set(DataComponents.UNBREAKABLE, Unit.INSTANCE)
                                .remove(Apoth.Components.DURABILITY_BONUS)
                                .build())))
                .invaderSound(Apoth.Sounds.INVADER_MYTHIC)
                .renderData(c -> c
                        .beamHeight(5f)
                        .beamRadius(0.05f)
                        .glowRadius(0.1f)
                        .shadow(d -> d
                                .texture(Apotheosis.loc("textures/rarity/shadow_t4.png"))
                                .frames(7)
                                .size(0.6F)
                                .frameTime(1.5F))
                        .particle(true))
        );
    }

    static <T> LootRule componentRule(DataComponentType<T> type, T value) {
        return new LootRule.ComponentLootRule(DataComponentPatch.builder().set(type, value).build());
    }

    void addAncient(String id, TextColor color, Holder<Item> material, UnaryOperator<LootRarity.Builder> config) {
        this.add(AncientReforging.loc(id), config.apply(builder(color, material)).build());
    }

    public static LootRarity.Builder builder(TextColor color, Holder<Item> material) {
        return new LootRarity.Builder(color, material);
    }

}
