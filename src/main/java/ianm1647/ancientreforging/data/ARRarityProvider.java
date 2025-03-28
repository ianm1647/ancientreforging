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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.Unbreakable;

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

        /*
        this.addRarity("common", TextColor.fromRgb(0x808080), Apoth.Items.COMMON_MATERIAL, b -> b
                .sortIndex(300)
                .weights(TieredWeights.builder()
                        .with(WorldTier.HAVEN, 600, 0)
                        .with(WorldTier.FRONTIER, 290, 0)
                        .with(WorldTier.ASCENT, 100, 0)
                        .with(WorldTier.SUMMIT, 0, 0)
                        .with(WorldTier.PINNACLE, 0, 0))
                .rule(new LootRule.AffixLootRule(AffixType.STAT))
                .rule(new LootRule.ChancedLootRule(0.25F, new LootRule.AffixLootRule(AffixType.STAT))));

        // Uncommon, reasonable rarity until the final tier.
        // Two stats, with a chance for a basic effect. Opportunity for a socket.
        this.addRarity("uncommon", TextColor.fromRgb(0x33FF33), Apoth.Items.UNCOMMON_MATERIAL, b -> b
                .sortIndex(400)
                .weights(TieredWeights.builder()
                        .with(WorldTier.HAVEN, 360, 2.5F)
                        .with(WorldTier.FRONTIER, 600, 0)
                        .with(WorldTier.ASCENT, 300, 0)
                        .with(WorldTier.SUMMIT, 120, 0)
                        .with(WorldTier.PINNACLE, 0, 0))
                .rule(new LootRule.AffixLootRule(AffixType.STAT))
                .rule(new LootRule.AffixLootRule(AffixType.STAT))
                .rule(new LootRule.ChancedLootRule(0.5F, new LootRule.AffixLootRule(AffixType.BASIC_EFFECT)))
                .rule(new LootRule.SocketLootRule(0, 1)));

        // Rare - always available, but starts out with very low frequency.
        // Two stats, one basic effect, with a chance for another.
        // Chance for two sockets, and can receive durability bonuses.
        this.addRarity("rare", TextColor.fromRgb(0x5555FF), Apoth.Items.RARE_MATERIAL, b -> b
                .sortIndex(500)
                .weights(TieredWeights.builder()
                        .with(WorldTier.HAVEN, 40, 5)
                        .with(WorldTier.FRONTIER, 100, 5)
                        .with(WorldTier.ASCENT, 500, 2.5F)
                        .with(WorldTier.SUMMIT, 290, 2.5F)
                        .with(WorldTier.PINNACLE, 100, 0))
                .rule(new LootRule.AffixLootRule(AffixType.STAT))
                .rule(new LootRule.AffixLootRule(AffixType.STAT))
                .rule(new LootRule.AffixLootRule(AffixType.BASIC_EFFECT))
                .rule(new LootRule.ChancedLootRule(0.35F, new LootRule.AffixLootRule(AffixType.BASIC_EFFECT)))
                .rule(new LootRule.SocketLootRule(0, 2))
                .rule(new LootRule.DurabilityLootRule(0.1F, 0.25F)));

        // Epic, not available in Haven, and exceptionally low chance on Frontier+Ascent.
        // Starts to really appear in Summit, and is the most common rarity in Apotheosis.
        // Three stats, a basic effect, an ability, and a chance for a second basic effect.
        // Guaranteed to receive a socket, and receives a good durability bonus.
        this.addRarity("epic", TextColor.fromRgb(0xBB00BB), Apoth.Items.EPIC_MATERIAL, b -> b
                .sortIndex(600)
                .weights(TieredWeights.builder()
                        .with(WorldTier.HAVEN, 0, 0)
                        .with(WorldTier.FRONTIER, 10, 0)
                        .with(WorldTier.ASCENT, 100, 5)
                        .with(WorldTier.SUMMIT, 540, 5)
                        .with(WorldTier.PINNACLE, 650, 2.5F))
                .rule(new LootRule.AffixLootRule(AffixType.STAT))
                .rule(new LootRule.AffixLootRule(AffixType.STAT))
                .rule(new LootRule.AffixLootRule(AffixType.STAT))
                .rule(new LootRule.AffixLootRule(AffixType.BASIC_EFFECT))
                .rule(new LootRule.ChancedLootRule(0.25F, new LootRule.AffixLootRule(AffixType.BASIC_EFFECT)))
                .rule(new LootRule.AffixLootRule(AffixType.ABILITY))
                .rule(new LootRule.SocketLootRule(1, 3))
                .rule(new LootRule.DurabilityLootRule(0.25F, 0.55F)));

        // Mythic, the pinnacle rarity. Does not appear until summit, and has very low rarity until Apotheosis.
        // Four stats, two basic effects, one ability, guaranteed sockets and high durability bonuses.
        // Has a very low chance to receive 4 sockets, and be Unbreakable.
        this.addRarity("mythic", TextColor.fromRgb(0xED7014), Apoth.Items.MYTHIC_MATERIAL, b -> b
                .sortIndex(700)
                .weights(TieredWeights.builder()
                        .with(WorldTier.HAVEN, 0, 0)
                        .with(WorldTier.FRONTIER, 0, 0)
                        .with(WorldTier.ASCENT, 0, 0)
                        .with(WorldTier.SUMMIT, 50, 5)
                        .with(WorldTier.PINNACLE, 250, 10))
                .rule(new LootRule.AffixLootRule(AffixType.STAT))
                .rule(new LootRule.AffixLootRule(AffixType.STAT))
                .rule(new LootRule.AffixLootRule(AffixType.STAT))
                .rule(new LootRule.AffixLootRule(AffixType.STAT))
                .rule(new LootRule.AffixLootRule(AffixType.BASIC_EFFECT))
                .rule(new LootRule.AffixLootRule(AffixType.BASIC_EFFECT))
                .rule(new LootRule.AffixLootRule(AffixType.ABILITY))
                .rule(new LootRule.SelectLootRule(0.95F, // 95% chance for 1-3 sockets, 5% chance for guaranteed 4 sockets.
                        new LootRule.SocketLootRule(1, 3),
                        new LootRule.SocketLootRule(4, 4)))
                .rule(new LootRule.SelectLootRule(0.99F, // 99% chance to roll a durability bonus, 1% to be unbreakable.
                        new LootRule.DurabilityLootRule(0.45F, 0.75F),
                        new LootRule.ComponentLootRule(DataComponentPatch.builder()
                                .set(DataComponents.UNBREAKABLE, new Unbreakable(true))
                                .remove(Apoth.Components.DURABILITY_BONUS)
                                .build()))));

         */

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
                                .set(DataComponents.UNBREAKABLE, new Unbreakable(true))
                                .set(Apoth.Components.DURABILITY_BONUS, 0F)
                                .build()))));
    }

    static <T> LootRule componentRule(DataComponentType<T> type, T value) {
        return new LootRule.ComponentLootRule(DataComponentPatch.builder().set(type, value).build());
    }

    void addRarity(String id, TextColor color, Holder<Item> material, UnaryOperator<LootRarity.Builder> config) {
        this.add(Apotheosis.loc(id), config.apply(builder(color, material)).build());
    }

    void addAncient(String id, TextColor color, Holder<Item> material, UnaryOperator<LootRarity.Builder> config) {
        this.add(AncientReforging.loc(id), config.apply(builder(color, material)).build());
    }

    public static LootRarity.Builder builder(TextColor color, Holder<Item> material) {
        return new LootRarity.Builder(color, material);
    }

}
