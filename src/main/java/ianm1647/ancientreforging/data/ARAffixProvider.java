package ianm1647.ancientreforging.data;

import dev.shadowsoffire.apotheosis.loot.LootCategory;
import ianm1647.ancientreforging.AncientReforging;
import dev.shadowsoffire.apotheosis.affix.*;
import dev.shadowsoffire.apotheosis.affix.effect.*;
import dev.shadowsoffire.apotheosis.Apoth;
import dev.shadowsoffire.apotheosis.loot.LootRarity;
import dev.shadowsoffire.apotheosis.loot.RarityRegistry;
import dev.shadowsoffire.apotheosis.tiers.TieredWeights;
import dev.shadowsoffire.apotheosis.tiers.WorldTier;
import dev.shadowsoffire.apotheosis.util.ApothMiscUtil;
import dev.shadowsoffire.apothic_attributes.api.ALObjects;
import dev.shadowsoffire.placebo.reload.DynamicHolder;
import dev.shadowsoffire.placebo.util.StepFunction;
import dev.shadowsoffire.placebo.util.data.DynamicRegistryProvider;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.NeoForgeMod;
import org.spongepowered.include.com.google.common.base.Preconditions;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.UnaryOperator;

public class ARAffixProvider extends DynamicRegistryProvider<Affix> {

    public static final int DEFAULT_WEIGHT = 25;
    public static final int DEFAULT_QUALITY = 0;

    public static final LootCategory[] ARMOR = { Apoth.LootCategories.HELMET, Apoth.LootCategories.CHESTPLATE, Apoth.LootCategories.LEGGINGS, Apoth.LootCategories.BOOTS };

    public ARAffixProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, AffixRegistry.INSTANCE);
    }

    @Override
    public String getName() {
        return "Affixes";
    }

    @Override
    public void generate() {
        LootRarity ancient = rarity("ancient");

        HolderLookup.RegistryLookup<Enchantment> enchants = this.lookupProvider.join().lookup(Registries.ENCHANTMENT).get();

        // Generic Attributes
        this.addAttribute("generic", "lucky", Attributes.LUCK, AttributeModifier.Operation.ADD_VALUE, b -> b
                .definition(AffixType.STAT, DEFAULT_WEIGHT, DEFAULT_QUALITY)
                .categories(Apoth.BuiltInRegs.LOOT_CATEGORY.stream().filter(c -> c != Apoth.LootCategories.NONE).toArray(LootCategory[]::new))
                .step(0.25F)
                .value(ancient, 6F, 12F));

        // Telepathic, which applies to a bunch of categories
        this.add(AncientReforging.loc("generic/telepathic"),
                new TelepathicAffix(
                        AffixDefinition.builder(AffixType.BASIC_EFFECT).weights(TieredWeights.forAllTiers(DEFAULT_WEIGHT, DEFAULT_QUALITY)).build(),
                        linkedSet(ancient)));

        // Armor Attributes
        this.addAttribute("armor", "aquatic", NeoForgeMod.SWIM_SPEED, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, b -> b
                .definition(AffixType.STAT, DEFAULT_WEIGHT, DEFAULT_QUALITY)
                .categories(Apoth.LootCategories.BOOTS)
                .value(ancient, 0.8F, 1.4F));

        this.addAttribute("armor", "blessed", Attributes.MAX_HEALTH, AttributeModifier.Operation.ADD_VALUE, b -> b
                .definition(AffixType.STAT, DEFAULT_WEIGHT, DEFAULT_QUALITY)
                .categories(ARMOR)
                .step(0.25F)
                .value(ancient, 10, 16));

        this.addAttribute("armor", "elastic", Attributes.STEP_HEIGHT, AttributeModifier.Operation.ADD_VALUE, b -> b
                .definition(AffixType.STAT, DEFAULT_WEIGHT, DEFAULT_QUALITY)
                .categories(Apoth.LootCategories.BOOTS)
                .step(0.25F)
                .value(ancient, 2, 4));

        this.addAttribute("armor", "fortunate", Attributes.LUCK, AttributeModifier.Operation.ADD_VALUE, b -> b
                .definition(AffixType.STAT, DEFAULT_WEIGHT, DEFAULT_QUALITY)
                .categories(ARMOR)
                .step(0.25F)
                .value(ancient, 6, 10));

        this.addAttribute("armor", "gravitational", Attributes.GRAVITY, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, b -> b
                .definition(AffixType.STAT, DEFAULT_WEIGHT, DEFAULT_QUALITY)
                .categories(Apoth.LootCategories.CHESTPLATE)
                .step(-0.01F)
                .value(ancient, -0.4F, -1F));

        this.addAttribute("armor", "ironforged", Attributes.ARMOR, AttributeModifier.Operation.ADD_VALUE, b -> b
                .definition(AffixType.STAT, DEFAULT_WEIGHT, DEFAULT_QUALITY)
                .categories(ARMOR)
                .step(0.25F)
                .value(ancient, 8, 16));

        this.addAttribute("armor", "adamantine", Attributes.ARMOR, AttributeModifier.Operation.ADD_MULTIPLIED_BASE, b -> b
                .definition(AffixType.STAT, DEFAULT_WEIGHT, DEFAULT_QUALITY)
                .categories(Apoth.LootCategories.CHESTPLATE, Apoth.LootCategories.LEGGINGS)
                .value(ancient, 0.5F, 0.9F));

        this.addAttribute("armor", "spiritual", ALObjects.Attributes.HEALING_RECEIVED, AttributeModifier.Operation.ADD_MULTIPLIED_BASE, b -> b
                .definition(AffixType.STAT, DEFAULT_WEIGHT, DEFAULT_QUALITY)
                .categories(Apoth.LootCategories.CHESTPLATE, Apoth.LootCategories.LEGGINGS)
                .value(ancient, 0.4F, 0.8F));

        this.addAttribute("armor", "stalwart", Attributes.KNOCKBACK_RESISTANCE, AttributeModifier.Operation.ADD_VALUE, b -> b
                .definition(AffixType.STAT, DEFAULT_WEIGHT, DEFAULT_QUALITY)
                .categories(ARMOR)
                .value(ancient, 0.5F, 0.8F));

        this.addAttribute("armor", "steel_touched", Attributes.ARMOR_TOUGHNESS, AttributeModifier.Operation.ADD_VALUE, b -> b
                .definition(AffixType.STAT, DEFAULT_WEIGHT, DEFAULT_QUALITY)
                .categories(ARMOR)
                .step(0.25F)
                .value(ancient, 4F, 12F));

        this.addAttribute("armor", "windswept", Attributes.MOVEMENT_SPEED, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, b -> b
                .definition(AffixType.STAT, DEFAULT_WEIGHT, DEFAULT_QUALITY)
                .categories(Apoth.LootCategories.LEGGINGS, Apoth.LootCategories.BOOTS)
                .value(ancient, 0.4F, 0.9F));

        this.addAttribute("armor", "winged", ALObjects.Attributes.ELYTRA_FLIGHT, AttributeModifier.Operation.ADD_VALUE, b -> b
                .definition(AffixType.STAT, DEFAULT_WEIGHT, DEFAULT_QUALITY)
                .categories(Apoth.LootCategories.CHESTPLATE)
                .value(ancient, 1));

        this.addAttribute("armor", "unbound", NeoForgeMod.CREATIVE_FLIGHT, AttributeModifier.Operation.ADD_VALUE, b -> b
                .definition(AffixType.STAT, d -> d
                        .weights(TieredWeights.onlyFor(WorldTier.PINNACLE, 20, 5))
                        .exclusiveWith(afx("armor/attribute/winged")))
                .categories(Apoth.LootCategories.CHESTPLATE)
                .value(ancient, 1));

        this.addAttribute("armor", "fireproof", Attributes.BURNING_TIME, AttributeModifier.Operation.ADD_MULTIPLIED_BASE, b -> b
                .definition(AffixType.STAT, DEFAULT_WEIGHT, DEFAULT_QUALITY)
                .categories(Apoth.LootCategories.HELMET)
                .step(-0.05F)
                .value(ancient, -0.9F, -1.5F));

        this.addAttribute("armor", "oxygenated", Attributes.OXYGEN_BONUS, AttributeModifier.Operation.ADD_VALUE, b -> b
                .definition(AffixType.STAT, DEFAULT_WEIGHT, DEFAULT_QUALITY)
                .categories(Apoth.LootCategories.HELMET)
                .value(ancient, 1.65F, 2.5F));

        // Breaker Attributes

        this.addAttribute("breaker", "destructive", ALObjects.Attributes.MINING_SPEED, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, b -> b
                .definition(AffixType.STAT, DEFAULT_WEIGHT, DEFAULT_QUALITY)
                .categories(Apoth.LootCategories.BREAKER, Apoth.LootCategories.SHEARS)
                .value(ancient, 1.15F, 1.6F));

        this.addAttribute("breaker", "experienced", ALObjects.Attributes.EXPERIENCE_GAINED, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, b -> b
                .definition(AffixType.STAT, DEFAULT_WEIGHT, DEFAULT_QUALITY)
                .categories(Apoth.LootCategories.BREAKER)
                .value(ancient, 1.15F, 1.5F));

        this.addAttribute("breaker", "lengthy", Attributes.BLOCK_INTERACTION_RANGE, AttributeModifier.Operation.ADD_VALUE, b -> b
                .definition(AffixType.STAT, DEFAULT_WEIGHT, DEFAULT_QUALITY)
                .categories(Apoth.LootCategories.BREAKER)
                .step(0.25F)
                .value(ancient, 3, 8));

        this.addAttribute("breaker", "submerged", Attributes.SUBMERGED_MINING_SPEED, AttributeModifier.Operation.ADD_VALUE, b -> b
                .definition(AffixType.STAT, DEFAULT_WEIGHT, DEFAULT_QUALITY)
                .categories(Apoth.LootCategories.BREAKER)
                .value(ancient, 1F, 1.8F));

        // Ranged Attributes

        this.addAttribute("ranged", "agile", ALObjects.Attributes.DRAW_SPEED, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, b -> b
                .definition(AffixType.STAT, DEFAULT_WEIGHT, DEFAULT_QUALITY)
                .categories(Apoth.LootCategories.BOW)
                .value(ancient, 1F, 1.5F));

        this.addAttribute("ranged", "elven", ALObjects.Attributes.ARROW_DAMAGE, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, b -> b
                .definition(AffixType.STAT, DEFAULT_WEIGHT, DEFAULT_QUALITY)
                .categories(Apoth.LootCategories.BOW, Apoth.LootCategories.TRIDENT)
                .value(ancient, 0.5F, 0.8F));

        this.addAttribute("ranged", "streamlined", ALObjects.Attributes.ARROW_VELOCITY, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, b -> b
                .definition(AffixType.STAT, DEFAULT_WEIGHT, DEFAULT_QUALITY)
                .categories(Apoth.LootCategories.BOW, Apoth.LootCategories.TRIDENT)
                .value(ancient, 0.3F, 0.7F));

        this.addAttribute("ranged", "windswept", Attributes.MOVEMENT_SPEED, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, b -> b
                .definition(AffixType.STAT, DEFAULT_WEIGHT, DEFAULT_QUALITY)
                .categories(Apoth.LootCategories.BOW, Apoth.LootCategories.TRIDENT)
                .value(ancient, 0.4F, 0.8F));

        // Shield Attributes

        this.addAttribute("shield", "ironforged", Attributes.ARMOR, AttributeModifier.Operation.ADD_MULTIPLIED_BASE, b -> b
                .definition(AffixType.STAT, DEFAULT_WEIGHT, DEFAULT_QUALITY)
                .categories(Apoth.LootCategories.SHIELD)
                .value(ancient, 0.4F, 0.6F));

        this.addAttribute("shield", "stalwart", Attributes.KNOCKBACK_RESISTANCE, AttributeModifier.Operation.ADD_VALUE, b -> b
                .definition(AffixType.STAT, DEFAULT_WEIGHT, DEFAULT_QUALITY)
                .categories(Apoth.LootCategories.SHIELD)
                .value(ancient, 0.5F, 0.7F));

        this.addAttribute("shield", "steel_touched", Attributes.ARMOR_TOUGHNESS, AttributeModifier.Operation.ADD_MULTIPLIED_BASE, b -> b
                .definition(AffixType.STAT, DEFAULT_WEIGHT, DEFAULT_QUALITY)
                .categories(Apoth.LootCategories.SHIELD)
                .value(ancient, 0.4F, 0.6F));

        // Melee Weapon Attributes

        this.addAttribute("melee", "vampiric", ALObjects.Attributes.LIFE_STEAL, AttributeModifier.Operation.ADD_VALUE, b -> b
                .definition(AffixType.STAT, d -> d
                        .weights(TieredWeights.forAllTiers(DEFAULT_WEIGHT, DEFAULT_QUALITY))
                        .exclusiveWith(afx("melee/attribute/berserking")))
                .categories(Apoth.LootCategories.MELEE_WEAPON, Apoth.LootCategories.TRIDENT)
                .value(ancient, 0.5F, 0.8F));

        this.addAttribute("melee", "murderous", Attributes.ATTACK_DAMAGE, AttributeModifier.Operation.ADD_MULTIPLIED_BASE, b -> b
                .definition(AffixType.STAT, DEFAULT_WEIGHT, DEFAULT_QUALITY)
                .categories(Apoth.LootCategories.MELEE_WEAPON, Apoth.LootCategories.TRIDENT)
                .value(ancient, 0.5F, 1.15F));

        this.addAttribute("melee", "violent", Attributes.ATTACK_DAMAGE, AttributeModifier.Operation.ADD_VALUE, b -> b
                .definition(AffixType.STAT, DEFAULT_WEIGHT, DEFAULT_QUALITY)
                .categories(Apoth.LootCategories.MELEE_WEAPON, Apoth.LootCategories.TRIDENT)
                .step(0.25F)
                .value(ancient, 10F, 16F));

        this.addAttribute("melee", "piercing", ALObjects.Attributes.ARMOR_PIERCE, AttributeModifier.Operation.ADD_VALUE, b -> b
                .definition(AffixType.STAT, DEFAULT_WEIGHT, DEFAULT_QUALITY)
                .categories(Apoth.LootCategories.MELEE_WEAPON, Apoth.LootCategories.TRIDENT)
                .step(0.25F)
                .value(ancient, 10F, 24F));

        this.addAttribute("melee", "lacerating", ALObjects.Attributes.CRIT_DAMAGE, AttributeModifier.Operation.ADD_VALUE, b -> b
                .definition(AffixType.STAT, DEFAULT_WEIGHT, DEFAULT_QUALITY)
                .categories(Apoth.LootCategories.MELEE_WEAPON, Apoth.LootCategories.TRIDENT)
                .value(ancient, 0.5F, 0.8F));

        this.addAttribute("melee", "intricate", ALObjects.Attributes.CRIT_CHANCE, AttributeModifier.Operation.ADD_VALUE, b -> b
                .definition(AffixType.STAT, DEFAULT_WEIGHT, DEFAULT_QUALITY)
                .categories(Apoth.LootCategories.MELEE_WEAPON, Apoth.LootCategories.TRIDENT)
                .value(ancient, 0.5F, 1.15F));

        this.addAttribute("melee", "infernal", ALObjects.Attributes.FIRE_DAMAGE, AttributeModifier.Operation.ADD_VALUE, b -> b
                .definition(AffixType.STAT, d -> d
                        .weights(TieredWeights.forAllTiers(DEFAULT_WEIGHT, DEFAULT_QUALITY))
                        .exclusiveWith(afx("melee/attribute/glacial")))
                .categories(Apoth.LootCategories.MELEE_WEAPON, Apoth.LootCategories.TRIDENT)
                .step(0.25F)
                .value(ancient, 8F, 20F));

        this.addAttribute("melee", "graceful", Attributes.ATTACK_SPEED, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, b -> b
                .definition(AffixType.STAT, DEFAULT_WEIGHT, DEFAULT_QUALITY)
                .categories(Apoth.LootCategories.MELEE_WEAPON, Apoth.LootCategories.TRIDENT)
                .value(ancient, 0.75F, 1.2F));

        this.addAttribute("melee", "glacial", ALObjects.Attributes.COLD_DAMAGE, AttributeModifier.Operation.ADD_VALUE, b -> b
                .definition(AffixType.STAT, d -> d
                        .weights(TieredWeights.forAllTiers(DEFAULT_WEIGHT, DEFAULT_QUALITY))
                        .exclusiveWith(afx("melee/attribute/infernal")))
                .categories(Apoth.LootCategories.MELEE_WEAPON, Apoth.LootCategories.TRIDENT)
                .step(0.25F)
                .value(ancient, 8F, 20F));

        this.addAttribute("melee", "lengthy", Attributes.ENTITY_INTERACTION_RANGE, AttributeModifier.Operation.ADD_VALUE, b -> b
                .definition(AffixType.STAT, DEFAULT_WEIGHT, DEFAULT_QUALITY)
                .categories(Apoth.LootCategories.MELEE_WEAPON, Apoth.LootCategories.TRIDENT)
                .step(0.25F)
                .value(ancient, 3, 6));

        this.addAttribute("melee", "forceful", Attributes.ATTACK_KNOCKBACK, AttributeModifier.Operation.ADD_VALUE, b -> b
                .definition(AffixType.STAT, DEFAULT_WEIGHT, DEFAULT_QUALITY)
                .categories(Apoth.LootCategories.MELEE_WEAPON)
                .step(0.25F)
                .value(ancient, 3, 6));

        this.addAttribute("melee", "berserking", ALObjects.Attributes.OVERHEAL, AttributeModifier.Operation.ADD_VALUE, b -> b
                .definition(AffixType.STAT, d -> d
                        .weights(TieredWeights.forAllTiers(DEFAULT_WEIGHT, DEFAULT_QUALITY))
                        .exclusiveWith(afx("melee/attribute/vampiric")))
                .categories(Apoth.LootCategories.MELEE_WEAPON)
                .value(ancient, 0.4F, 0.9F));

        this.addAttribute("melee", "giant_slaying", ALObjects.Attributes.CURRENT_HP_DAMAGE, AttributeModifier.Operation.ADD_VALUE, b -> b
                .definition(AffixType.STAT, d -> d
                        .weights(TieredWeights.onlyFor(WorldTier.PINNACLE, 20, 5)))
                .categories(Apoth.LootCategories.MELEE_WEAPON)
                .value(ancient, 0.3F, 0.7F));

        // Damage Reduction Affixes

        this.addDamageReduction("armor", "blockading", DamageReductionAffix.DamageType.PHYSICAL, b -> b
                .definition(AffixType.ABILITY, DEFAULT_WEIGHT, DEFAULT_QUALITY)
                .categories(Apoth.LootCategories.CHESTPLATE, Apoth.LootCategories.LEGGINGS)
                .value(ancient, 0.15F, 0.3F));

        this.addDamageReduction("armor", "runed", DamageReductionAffix.DamageType.MAGIC, b -> b
                .definition(AffixType.ABILITY, d -> d
                        .weights(TieredWeights.forAllTiers(DEFAULT_WEIGHT, DEFAULT_QUALITY))
                        .exclusiveWith(afx("armor/dmg_reduction/blockading")))
                .categories(Apoth.LootCategories.HELMET, Apoth.LootCategories.CHESTPLATE, Apoth.LootCategories.LEGGINGS, Apoth.LootCategories.BOOTS)
                .value(ancient, 0.15F, 0.3F));

        // Armor Basic Effects

        this.addDamageReduction("armor", "blast_forged", DamageReductionAffix.DamageType.EXPLOSION, b -> b
                .definition(AffixType.BASIC_EFFECT, DEFAULT_WEIGHT, DEFAULT_QUALITY)
                .categories(Apoth.LootCategories.CHESTPLATE, Apoth.LootCategories.LEGGINGS)
                .value(ancient, 0.3F, 0.75F));

        this.addDamageReduction("armor", "feathery", DamageReductionAffix.DamageType.FALL, b -> b
                .definition(AffixType.BASIC_EFFECT, DEFAULT_WEIGHT, DEFAULT_QUALITY)
                .categories(Apoth.LootCategories.BOOTS)
                .value(ancient, 0.3F, 0.8F));

        this.addDamageReduction("armor", "deflective", DamageReductionAffix.DamageType.PROJECTILE, b -> b
                .definition(AffixType.BASIC_EFFECT, DEFAULT_WEIGHT, DEFAULT_QUALITY)
                .categories(Apoth.LootCategories.HELMET, Apoth.LootCategories.CHESTPLATE)
                .value(ancient, 0.3F, 0.6F));

        this.addDamageReduction("armor", "grounded", DamageReductionAffix.DamageType.LIGHTNING, b -> b
                .definition(AffixType.BASIC_EFFECT, DEFAULT_WEIGHT, DEFAULT_QUALITY)
                .categories(Apoth.LootCategories.HELMET, Apoth.LootCategories.BOOTS)
                .value(ancient, 0.3F, 0.75F));

        this.addMobEffect("armor", "revitalizing", MobEffects.HEAL, MobEffectAffix.Target.HURT_SELF, b -> b
                .definition(AffixType.BASIC_EFFECT, DEFAULT_WEIGHT, DEFAULT_QUALITY)
                .categories(Apoth.LootCategories.CHESTPLATE, Apoth.LootCategories.LEGGINGS)
                .value(ancient, StepFunction.constant(2), StepFunction.fromBounds(0, 2F, 0.5F), 120));

        this.addMobEffect("armor", "nimble", MobEffects.MOVEMENT_SPEED, MobEffectAffix.Target.HURT_SELF, b -> b
                .definition(AffixType.BASIC_EFFECT, DEFAULT_WEIGHT, DEFAULT_QUALITY)
                .categories(Apoth.LootCategories.LEGGINGS, Apoth.LootCategories.BOOTS)
                .value(ancient, 400, 800, StepFunction.fromBounds(0, 3, 0.75F), 300));

        this.addMobEffect("armor", "bursting", ALObjects.MobEffects.VITALITY, MobEffectAffix.Target.HURT_SELF, b -> b
                .definition(AffixType.BASIC_EFFECT, d -> d
                        .weights(TieredWeights.forAllTiers(DEFAULT_WEIGHT, DEFAULT_QUALITY))
                        .exclusiveWith(afx("armor/mob_effect/revitalizing")))
                .categories(Apoth.LootCategories.CHESTPLATE, Apoth.LootCategories.LEGGINGS)
                .value(ancient, StepFunction.constant(100), StepFunction.fromBounds(0, 2F, 0.5F), 150));

        this.addMobEffect("armor", "bolstering", MobEffects.DAMAGE_RESISTANCE, MobEffectAffix.Target.HURT_SELF, b -> b
                .definition(AffixType.BASIC_EFFECT, DEFAULT_WEIGHT, DEFAULT_QUALITY)
                .categories(Apoth.LootCategories.CHESTPLATE, Apoth.LootCategories.LEGGINGS)
                .value(ancient, 160, 320, StepFunction.fromBounds(0, 2, 0.5F), 120));

        this.addMobEffect("armor", "blinding", MobEffects.BLINDNESS, MobEffectAffix.Target.HURT_ATTACKER, b -> b
                .definition(AffixType.BASIC_EFFECT, DEFAULT_WEIGHT, DEFAULT_QUALITY)
                .categories(Apoth.LootCategories.HELMET)
                .value(ancient, 120, 300, 0, 100));

        // Breaker Basic Effects

        this.addMobEffect("breaker", "swift", MobEffects.DIG_SPEED, MobEffectAffix.Target.BREAK_SELF, b -> b
                .definition(AffixType.BASIC_EFFECT, DEFAULT_WEIGHT, DEFAULT_QUALITY)
                .categories(Apoth.LootCategories.BREAKER)
                .value(ancient, 480, 800, StepFunction.fromBounds(0, 3, 0.5F), 300));

        this.addMobEffect("breaker", "spelunkers", MobEffects.MOVEMENT_SPEED, MobEffectAffix.Target.BREAK_SELF, b -> b
                .definition(AffixType.BASIC_EFFECT, DEFAULT_WEIGHT, DEFAULT_QUALITY)
                .categories(Apoth.LootCategories.BREAKER)
                .value(ancient, 680, 1200, StepFunction.fromBounds(0, 3, 0.5F), 300));

        Holder<Enchantment> fortune = enchants.getOrThrow(Enchantments.FORTUNE);
        this.addEnchantment("breaker", "prosperous", fortune, EnchantmentAffix.Mode.EXISTING, b -> b
                .definition(AffixType.BASIC_EFFECT, DEFAULT_WEIGHT, DEFAULT_QUALITY)
                .categories(Apoth.LootCategories.BREAKER)
                .step(0.5F)
                .value(ancient, 4, 8));

        this.add(AncientReforging.loc("breaker/effect/omnetic"),
                new OmneticAffix.Builder()
                        .definition(AffixType.BASIC_EFFECT, DEFAULT_WEIGHT, 5)
                        .value(ancient, "netherite", Items.NETHERITE_AXE, Items.NETHERITE_SHOVEL, Items.NETHERITE_PICKAXE, Items.NETHERITE_SWORD, Items.NETHERITE_HOE)
                        .build());

        this.add(AncientReforging.loc("breaker/effect/radial"),
                new RadialAffix.Builder()
                        .definition(AffixType.BASIC_EFFECT, DEFAULT_WEIGHT, 5)
                        .categories(Apoth.LootCategories.BREAKER, Apoth.LootCategories.SHEARS)
                        .value(ancient, c -> c
                                .radii(5, 5)
                                .radii(7, 5)
                                .radii(7, 7))
                        .build());

        // Ranged Basic Effects

        this.addMobEffect("ranged", "shulkers", MobEffects.LEVITATION, MobEffectAffix.Target.ARROW_TARGET, b -> b
                .definition(AffixType.BASIC_EFFECT, DEFAULT_WEIGHT, DEFAULT_QUALITY)
                .categories(Apoth.LootCategories.BOW)
                .value(ancient, 40, 200, StepFunction.fromBounds(0, 3, 0.5F), 70));

        this.addMobEffect("ranged", "acidic", ALObjects.MobEffects.SUNDERING, MobEffectAffix.Target.ARROW_TARGET, b -> b
                .definition(AffixType.BASIC_EFFECT, d -> d
                        .weights(TieredWeights.onlyFor(WorldTier.PINNACLE, 20, 5)))
                .categories(Apoth.LootCategories.BOW)
                .stacking()
                .limit(4)
                .value(ancient, 160, 320, 0, 30));

        this.addMobEffect("ranged", "ensnaring", MobEffects.MOVEMENT_SLOWDOWN, MobEffectAffix.Target.ARROW_TARGET, b -> b
                .definition(AffixType.BASIC_EFFECT, DEFAULT_WEIGHT, DEFAULT_QUALITY)
                .categories(Apoth.LootCategories.BOW, Apoth.LootCategories.TRIDENT)
                .value(ancient, 150, 350, StepFunction.fromBounds(0, 3, 0.25F), 80));

        this.addMobEffect("ranged", "fleeting", MobEffects.MOVEMENT_SPEED, MobEffectAffix.Target.ARROW_SELF, b -> b
                .definition(AffixType.BASIC_EFFECT, DEFAULT_WEIGHT, DEFAULT_QUALITY)
                .categories(Apoth.LootCategories.BOW, Apoth.LootCategories.TRIDENT)
                .value(ancient, 200, 500, StepFunction.fromBounds(0, 2, 0.25F), 0));

        this.addMobEffect("ranged", "grievous", ALObjects.MobEffects.GRIEVOUS, MobEffectAffix.Target.ARROW_TARGET, b -> b
                .definition(AffixType.BASIC_EFFECT, DEFAULT_WEIGHT, DEFAULT_QUALITY)
                .categories(Apoth.LootCategories.BOW, Apoth.LootCategories.TRIDENT)
                .value(ancient, 400, 600, StepFunction.fromBounds(0, 3, 0.5F), 200));

        this.addMobEffect("ranged", "ivy_laced", MobEffects.POISON, MobEffectAffix.Target.ARROW_TARGET, b -> b
                .definition(AffixType.BASIC_EFFECT, DEFAULT_WEIGHT, DEFAULT_QUALITY)
                .categories(Apoth.LootCategories.BOW, Apoth.LootCategories.TRIDENT)
                .stacking()
                .limit(5)
                .value(ancient, 200, 400, StepFunction.fromBounds(0, 3, 0.5F), 20));

        this.addMobEffect("ranged", "blighted", MobEffects.WITHER, MobEffectAffix.Target.ARROW_TARGET, b -> b
                .definition(AffixType.BASIC_EFFECT, DEFAULT_WEIGHT, DEFAULT_QUALITY)
                .categories(Apoth.LootCategories.BOW, Apoth.LootCategories.TRIDENT)
                .value(ancient, 320, 500, StepFunction.fromBounds(0, 4, 0.5F), 100));

        this.addMobEffect("ranged", "deathbound", MobEffects.WITHER, MobEffectAffix.Target.ARROW_TARGET, b -> b
                .definition(AffixType.BASIC_EFFECT, d -> d
                        .weights(TieredWeights.onlyFor(WorldTier.PINNACLE, 20, 5))
                        .exclusiveWith(afx("ranged/mob_effect/blighted")))
                .categories(Apoth.LootCategories.BOW, Apoth.LootCategories.TRIDENT)
                .stacking()
                .limit(4)
                .value(ancient, 200, 400, 2, 20));

        // Melee Basic Effects

        this.addMobEffect("melee", "bloodletting", ALObjects.MobEffects.BLEEDING, MobEffectAffix.Target.ATTACK_TARGET, b -> b
                .definition(AffixType.BASIC_EFFECT, DEFAULT_WEIGHT, DEFAULT_QUALITY)
                .categories(Apoth.LootCategories.MELEE_WEAPON, Apoth.LootCategories.TRIDENT)
                .stacking()
                .limit(3)
                .value(ancient, 200, 400, StepFunction.fromBounds(0, 2, 0.25F), 40));

        this.addMobEffect("melee", "caustic", ALObjects.MobEffects.SUNDERING, MobEffectAffix.Target.ATTACK_TARGET, b -> b
                .definition(AffixType.BASIC_EFFECT, DEFAULT_WEIGHT, DEFAULT_QUALITY)
                .categories(Apoth.LootCategories.MELEE_WEAPON, Apoth.LootCategories.TRIDENT)
                .stacking()
                .limit(3)
                .value(ancient, 400, 800, StepFunction.fromBounds(0, 2, 0.5F), 150));

        this.addMobEffect("melee", "sophisticated", ALObjects.MobEffects.KNOWLEDGE, MobEffectAffix.Target.ATTACK_SELF, b -> b
                .definition(AffixType.BASIC_EFFECT, DEFAULT_WEIGHT, DEFAULT_QUALITY)
                .categories(Apoth.LootCategories.MELEE_WEAPON, Apoth.LootCategories.TRIDENT)
                .stacking()
                .limit(3)
                .value(ancient, 600, 1500, StepFunction.fromBounds(0, 3, 0.5F), 600));

        this.addMobEffect("melee", "omniscient", ALObjects.MobEffects.KNOWLEDGE, MobEffectAffix.Target.ATTACK_SELF, b -> b
                .definition(AffixType.BASIC_EFFECT, d -> d
                        .weights(TieredWeights.onlyFor(WorldTier.PINNACLE, 20, 5))
                        .exclusiveWith(afx("melee/mob_effect/sophisticated")))
                .categories(Apoth.LootCategories.MELEE_WEAPON, Apoth.LootCategories.TRIDENT)
                .stacking()
                .limit(8)
                .value(ancient, 250, 450, StepFunction.fromBounds(0, 2, 0.25F), 40));

        this.addMobEffect("melee", "weakening", MobEffects.WEAKNESS, MobEffectAffix.Target.ATTACK_TARGET, b -> b
                .definition(AffixType.BASIC_EFFECT, DEFAULT_WEIGHT, DEFAULT_QUALITY)
                .categories(Apoth.LootCategories.MELEE_WEAPON, Apoth.LootCategories.TRIDENT)
                .value(ancient, 150, 450, StepFunction.fromBounds(0, 3, 0.25F), 150));

        this.addMobEffect("melee", "elusive", MobEffects.MOVEMENT_SPEED, MobEffectAffix.Target.ATTACK_SELF, b -> b
                .definition(AffixType.BASIC_EFFECT, DEFAULT_WEIGHT, DEFAULT_QUALITY)
                .categories(Apoth.LootCategories.MELEE_WEAPON, Apoth.LootCategories.TRIDENT)
                .stacking()
                .limit(3)
                .value(ancient, 400, 1200, StepFunction.fromBounds(0, 3, 0.5F), 300));

        // Shield basic effects

        this.addMobEffect("shield", "devilish", ALObjects.MobEffects.BLEEDING, MobEffectAffix.Target.BLOCK_ATTACKER, b -> b
                .definition(AffixType.BASIC_EFFECT, DEFAULT_WEIGHT, DEFAULT_QUALITY)
                .categories(Apoth.LootCategories.SHIELD)
                .stacking()
                .limit(4)
                .value(ancient, 200, 400, StepFunction.fromBounds(0, 1, 0.5F), 30));

        this.addMobEffect("shield", "venomous", MobEffects.POISON, MobEffectAffix.Target.BLOCK_ATTACKER, b -> b
                .definition(AffixType.BASIC_EFFECT, DEFAULT_WEIGHT, DEFAULT_QUALITY)
                .categories(Apoth.LootCategories.SHIELD)
                .stacking()
                .limit(4)
                .value(ancient, 300, 500, StepFunction.fromBounds(0, 1, 0.5F), 150));

        this.addMobEffect("shield", "withering", MobEffects.WITHER, MobEffectAffix.Target.BLOCK_ATTACKER, b -> b
                .definition(AffixType.BASIC_EFFECT, DEFAULT_WEIGHT, DEFAULT_QUALITY)
                .categories(Apoth.LootCategories.SHIELD)
                .value(ancient, 120, 320, StepFunction.fromBounds(0, 3, 0.5F), 0));

        this.addMobEffect("shield", "reinforcing", MobEffects.DAMAGE_RESISTANCE, MobEffectAffix.Target.BLOCK_SELF, b -> b
                .definition(AffixType.BASIC_EFFECT, DEFAULT_WEIGHT, DEFAULT_QUALITY)
                .categories(Apoth.LootCategories.SHIELD)
                .value(ancient, 300, 500, StepFunction.fromBounds(0, 1, 0.5F), 100));

        this.addMobEffect("shield", "galvanizing", MobEffects.DAMAGE_RESISTANCE, MobEffectAffix.Target.BLOCK_SELF, b -> b
                .definition(AffixType.BASIC_EFFECT, d -> d
                        .weights(TieredWeights.onlyFor(WorldTier.PINNACLE, 20, 5))
                        .exclusiveWith(afx("shield/mob_effect/reinforcing")))
                .categories(Apoth.LootCategories.SHIELD)
                .stacking()
                .limit(3)
                .value(ancient, 200, 360, StepFunction.fromBounds(0, 1, 0.25F), 40));

        // Breaker Abilities

        this.add(AncientReforging.loc("breaker/ability/enlightened"),
                AffixBuilder.simple(EnlightenedAffix::new)
                        .definition(AffixType.ABILITY, DEFAULT_WEIGHT, DEFAULT_QUALITY)
                        .step(-1)
                        .value(ancient, 2, 0)
                        .build());

        this.add(AncientReforging.loc("breaker/ability/supermassive"),
                new RadialAffix.Builder()
                        .definition(AffixType.ABILITY, c -> c
                                .weights(TieredWeights.onlyFor(WorldTier.PINNACLE, 20, 5))
                                .exclusiveWith(afx("breaker/effect/radial")))
                        .categories(Apoth.LootCategories.BREAKER)
                        .value(ancient, c -> c
                                .radii(9, 9))
                        .build());

        // Ranged Abilities

        this.add(AncientReforging.loc("ranged/magical"), new MagicalArrowAffix(
                AffixDefinition.builder(AffixType.ABILITY)
                        .weights(TieredWeights.forAllTiers(DEFAULT_WEIGHT, DEFAULT_QUALITY))
                        .build(),
                linkedSet(ancient)));

        this.add(AncientReforging.loc("ranged/spectral"),
                AffixBuilder.simple(SpectralShotAffix::new)
                        .definition(AffixType.ABILITY, DEFAULT_WEIGHT, DEFAULT_QUALITY)
                        .value(ancient, 0.5F, 0.9F)
                        .build());

        Holder<Enchantment> looting = enchants.getOrThrow(Enchantments.LOOTING);
        this.addEnchantment("ranged", "prosperous", looting, EnchantmentAffix.Mode.SINGLE, b -> b
                .definition(AffixType.ABILITY, DEFAULT_WEIGHT, DEFAULT_QUALITY)
                .categories(Apoth.LootCategories.BOW)
                .step(0.25F)
                .value(ancient, 10, 14));

        // Melee Abilities

        this.add(AncientReforging.loc("melee/festive"),
                FestiveAffix.builder()
                        .categories(Apoth.LootCategories.MELEE_WEAPON, Apoth.LootCategories.TRIDENT)
                        .definition(AffixType.BASIC_EFFECT, DEFAULT_WEIGHT, DEFAULT_QUALITY)
                        .value(ancient, StepFunction.fromBounds(0.05F, 0.12F, 0.005F), 20)
                        .build());

        this.add(AncientReforging.loc("melee/thunderstruck"),
                AffixBuilder.categorized(ThunderstruckAffix::new)
                        .definition(AffixType.ABILITY, DEFAULT_WEIGHT, DEFAULT_QUALITY)
                        .categories(Apoth.LootCategories.MELEE_WEAPON, Apoth.LootCategories.TRIDENT)
                        .step(1)
                        .value(ancient, 7, 11)
                        .build());

        this.add(AncientReforging.loc("melee/cleaving"),
                new CleavingAffix.Builder()
                        .definition(AffixType.ABILITY, DEFAULT_WEIGHT, DEFAULT_QUALITY)
                        .value(ancient, 0.8F, 1.0F, 3, 6)
                        .build());

        this.add(AncientReforging.loc("melee/executing"),
                AffixBuilder.categorized(ExecutingAffix::new)
                        .categories(Apoth.LootCategories.MELEE_WEAPON, Apoth.LootCategories.TRIDENT)
                        .definition(AffixType.ABILITY, DEFAULT_WEIGHT, DEFAULT_QUALITY)
                        .value(ancient, 0.35F, 0.6F)
                        .build());

        // Shield Abilities

        this.add(AncientReforging.loc("shield/retreating"), new RetreatingAffix(
                AffixDefinition.builder(AffixType.ABILITY).weights(TieredWeights.forAllTiers(DEFAULT_WEIGHT, DEFAULT_QUALITY)).build(),
                linkedSet(ancient)));

        this.add(AncientReforging.loc("shield/psychic"), AffixBuilder.simple(PsychicAffix::new)
                .definition(AffixType.ABILITY, DEFAULT_WEIGHT, DEFAULT_QUALITY)
                .value(ancient, 0.8F, 1.6F)
                .build());

        this.add(AncientReforging.loc("shield/catalyzing"), AffixBuilder.simple(CatalyzingAffix::new)
                .definition(AffixType.ABILITY, DEFAULT_WEIGHT, DEFAULT_QUALITY)
                .step(20)
                .value(ancient, 500, 700)
                .build());

        this.futures.add(CompletableFuture.runAsync(RarityRegistry.INSTANCE::validateExistingHolders));
        this.futures.add(CompletableFuture.runAsync(AffixRegistry.INSTANCE::validateExistingHolders));
    }

    private HolderSet<Block> blockSet(TagKey<Block> tag) {
        return BuiltInRegistries.BLOCK.getOrCreateTag(tag);
    }

    private void addEnchantment(String type, String name, Holder<Enchantment> enchantment, EnchantmentAffix.Mode mode, UnaryOperator<EnchantmentAffix.Builder> config) {
        var builder = new EnchantmentAffix.Builder(enchantment, mode);
        config.apply(builder);
        this.add(AncientReforging.loc(type + "/enchantment/" + name), builder.build());
    }

    private void addMobEffect(String type, String name, Holder<MobEffect> effect, MobEffectAffix.Target target, UnaryOperator<MobEffectAffix.Builder> config) {
        var builder = new MobEffectAffix.Builder(effect, target);
        config.apply(builder);
        this.add(AncientReforging.loc(type + "/mob_effect/" + name), builder.build());
    }

    private void addDamageReduction(String type, String name, DamageReductionAffix.DamageType dType, UnaryOperator<DamageReductionAffix.Builder> config) {
        var builder = new DamageReductionAffix.Builder(dType);
        config.apply(builder);
        this.add(AncientReforging.loc(type + "/dmg_reduction/" + name), builder.build());
    }

    private void addAttribute(String type, String name, Holder<Attribute> attribute, AttributeModifier.Operation op, UnaryOperator<AttributeAffix.Builder> config) {
        var builder = new AttributeAffix.Builder(attribute, op);
        config.apply(builder);
        this.add(AncientReforging.loc(type + "/attribute/" + name), builder.build());
    }

    private static LootRarity rarity(String path) {
        return Preconditions.checkNotNull(RarityRegistry.INSTANCE.getValue(AncientReforging.loc(path)));
    }

    private static DynamicHolder<Affix> afx(String path) {
        return AffixRegistry.INSTANCE.holder(AncientReforging.loc(path));
    }

    private static Set<LootRarity> linkedSet(LootRarity... rarities) {
        return ApothMiscUtil.linkedSet(rarities);
    }

}
