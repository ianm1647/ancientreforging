package com.ianm1647.ancientreforging.data;

import java.util.concurrent.CompletableFuture;
import java.util.function.UnaryOperator;

import com.ianm1647.ancientreforging.AncientReforging;
import org.spongepowered.include.com.google.common.base.Preconditions;

import dev.shadowsoffire.apotheosis.Apoth;
import dev.shadowsoffire.apotheosis.loot.LootRarity;
import dev.shadowsoffire.apotheosis.loot.RarityRegistry;
import dev.shadowsoffire.apotheosis.mobs.registries.InvaderRegistry;
import dev.shadowsoffire.apotheosis.mobs.types.Invader;
import dev.shadowsoffire.apotheosis.mobs.util.BasicBossData;
import dev.shadowsoffire.apotheosis.tiers.Constraints;
import dev.shadowsoffire.apotheosis.tiers.TieredWeights;
import dev.shadowsoffire.apotheosis.tiers.WorldTier;
import dev.shadowsoffire.apothic_attributes.api.ALObjects;
import dev.shadowsoffire.placebo.util.StepFunction;
import dev.shadowsoffire.placebo.util.data.DynamicRegistryProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.HolderLookup.RegistryLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;

public class ARInvaderProvider extends DynamicRegistryProvider<Invader> {

    public static final int DEFAULT_WEIGHT = 100;
    public static final int DEFAULT_QUALITY = 0;

    public ARInvaderProvider(PackOutput output, CompletableFuture<Provider> registries) {
        super(output, registries, InvaderRegistry.INSTANCE);
    }

    @Override
    public String getName() {
        return "Apothic Invaders";
    }

    @Override
    public void generate() {
        HolderLookup.Provider registries = this.lookupProvider.join();
        RegistryLookup<Biome> biomes = registries.lookup(Registries.BIOME).get();

        LootRarity ancient = rarity("ancient");

        addBoss("the_end/enderman", b -> basicMeleeStats(b)
                .entity(EntityType.ENDERMAN)
                .size(0.75, 3.7)
                .basicData(c -> meleeGear(c)
                        .name(Component.literal(BasicBossData.NAME_GEN))
                        .weights(TieredWeights.forAllTiers(DEFAULT_WEIGHT, DEFAULT_QUALITY))
                        .constraints(Constraints.forDimension(Level.END))
                        .bonusLoot(Apoth.LootTables.BONUS_BOSS_DROPS)));

        addBoss("the_end/shulker", b -> basicRangedStats(b)
                .entity(EntityType.SHULKER)
                .size(1.25, 1.25)
                .basicData(c -> rangedGear(c)
                        .name(Component.literal(BasicBossData.NAME_GEN))
                        .weights(TieredWeights.forAllTiers(20, 2))
                        .constraints(Constraints.forDimension(Level.END))
                        .bonusLoot(Apoth.LootTables.BONUS_BOSS_DROPS)));

        addBoss("the_end/evoker", b -> basicRangedStats(b)
                .entity(EntityType.EVOKER)
                .size(0.75, 2.45)
                .basicData(c -> rangedGear(c)
                        .name(Component.literal(BasicBossData.NAME_GEN))
                        .weights(TieredWeights.forTiersAbove(WorldTier.SUMMIT, 150, 5))
                        .constraints(Constraints.forDimension(Level.END))
                        .bonusLoot(Apoth.LootTables.BONUS_BOSS_DROPS, Apoth.LootTables.BONUS_RARE_BOSS_DROPS)));

    }

    private Invader.Builder basicMeleeStats(Invader.Builder builder) {
        LootRarity ancient = rarity("ancient");

        return builder
                .stats(ancient, c -> c
                        .enchantChance(0.95F)
                        .enchLevels(40, 30)
                        .effect(1, MobEffects.FIRE_RESISTANCE)
                        .modifier(Attributes.MAX_HEALTH, Operation.ADD_VALUE, 220, 450)
                        .modifier(Attributes.MOVEMENT_SPEED, Operation.ADD_MULTIPLIED_BASE, 0.5F, 0.8F)
                        .modifier(Attributes.ATTACK_DAMAGE, Operation.ADD_MULTIPLIED_BASE, 0.8F, 1.5F)
                        .modifier(Attributes.KNOCKBACK_RESISTANCE, Operation.ADD_VALUE, StepFunction.constant(1.2F))
                        .modifier(Attributes.ARMOR, Operation.ADD_VALUE, StepFunction.constant(20F))
                        .modifier(Attributes.ARMOR_TOUGHNESS, Operation.ADD_VALUE, StepFunction.constant(25F))
                        .modifier(Attributes.SCALE, Operation.ADD_MULTIPLIED_TOTAL, -0.15F, 0.25F));
    }

    private Invader.Builder basicRangedStats(Invader.Builder builder) {
        LootRarity ancient = rarity("ancient");

        return builder
                .stats(ancient, c -> c
                        .enchantChance(0.95F)
                        .enchLevels(40, 30)
                        .effect(1, MobEffects.FIRE_RESISTANCE)
                        .modifier(Attributes.MAX_HEALTH, Operation.ADD_VALUE, 220, 450)
                        .modifier(Attributes.MOVEMENT_SPEED, Operation.ADD_MULTIPLIED_BASE, 0.5F, 0.8F)
                        .modifier(ALObjects.Attributes.PROJECTILE_DAMAGE, Operation.ADD_MULTIPLIED_BASE, 0.9F, 1.8F)
                        .modifier(Attributes.KNOCKBACK_RESISTANCE, Operation.ADD_VALUE, StepFunction.constant(1F))
                        .modifier(Attributes.ARMOR, Operation.ADD_VALUE, StepFunction.constant(20F))
                        .modifier(Attributes.ARMOR_TOUGHNESS, Operation.ADD_VALUE, StepFunction.constant(25F))
                        .modifier(Attributes.SCALE, Operation.ADD_MULTIPLIED_TOTAL, -0.15F, 0.25F));
    }

    public static BasicBossData.Builder meleeGear(BasicBossData.Builder builder) {
        builder.gearSets(WorldTier.HAVEN, "#haven_melee");
        builder.gearSets(WorldTier.FRONTIER, "#frontier_melee");
        builder.gearSets(WorldTier.ASCENT, "#ascent_melee");
        builder.gearSets(WorldTier.SUMMIT, "#summit_melee");
        builder.gearSets(WorldTier.PINNACLE, "#apotheosis_melee");
        return builder;
    }

    public static BasicBossData.Builder rangedGear(BasicBossData.Builder builder) {
        builder.gearSets(WorldTier.HAVEN, "#haven_ranged");
        builder.gearSets(WorldTier.FRONTIER, "#frontier_ranged");
        builder.gearSets(WorldTier.ASCENT, "#ascent_ranged");
        builder.gearSets(WorldTier.SUMMIT, "#summit_ranged");
        builder.gearSets(WorldTier.PINNACLE, "#apotheosis_ranged");
        return builder;
    }

    private void addBoss(String name, UnaryOperator<Invader.Builder> builder) {
        this.add(AncientReforging.loc(name), builder.apply(Invader.builder()).build());
    }

    private static LootRarity rarity(String path) {
        return Preconditions.checkNotNull(RarityRegistry.INSTANCE.getValue(AncientReforging.loc(path)));
    }

}
