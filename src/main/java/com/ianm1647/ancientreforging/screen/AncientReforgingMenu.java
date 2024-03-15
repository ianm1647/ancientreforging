package com.ianm1647.ancientreforging.screen;

import blue.endless.jankson.annotation.Nullable;
import com.ianm1647.ancientreforging.AncientReforgingRegistry;
import com.ianm1647.ancientreforging.block.AncientReforgingTableTile;
import dev.shadowsoffire.apotheosis.adventure.Adventure.Items;
import dev.shadowsoffire.apotheosis.adventure.affix.reforging.ReforgingRecipe;
import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;
import dev.shadowsoffire.apotheosis.adventure.loot.LootController;
import dev.shadowsoffire.apotheosis.adventure.loot.LootRarity;
import dev.shadowsoffire.apotheosis.adventure.loot.RarityRegistry;
import dev.shadowsoffire.apotheosis.cca.IntComponent;
import dev.shadowsoffire.apotheosis.cca.ZenithComponents;
import dev.shadowsoffire.apotheosis.util.ApothMiscUtil;
import dev.shadowsoffire.placebo.menu.MenuUtil;
import dev.shadowsoffire.placebo.menu.PlaceboContainerMenu;
import dev.shadowsoffire.placebo.util.EnchantmentUtils;
import java.util.Objects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.screen.Property;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.math.random.Xoroshiro128PlusPlusRandom;

public class AncientReforgingMenu extends PlaceboContainerMenu {
    public static final String REFORGE_SEED = "apoth_reforge_seed";
    protected final BlockPos pos;
    protected final AncientReforgingTableTile tile;
    protected final PlayerEntity player;
    protected SimpleInventory itemInventory;
    protected final Random random;
    protected final int[] seed;
    protected final int[] costs;
    protected Property needsReset;

    public AncientReforgingMenu(int id, PlayerInventory inv, PacketByteBuf buf) {
        this(id, inv, buf.readBlockPos());
    }

    public AncientReforgingMenu(int id, PlayerInventory inv, BlockPos pos) {
        super(AncientReforgingRegistry.Menus.ANCIENT_REFORGING, id, inv);
        this.itemInventory = new SimpleInventory(1);
        this.random = new Xoroshiro128PlusPlusRandom(0L);
        this.seed = new int[2];
        this.costs = new int[3];
        this.needsReset = Property.create();
        this.player = inv.player;
        this.pos = pos;
        this.tile = (AncientReforgingTableTile)this.level.getBlockEntity(pos);
        this.addSlot(new PlaceboContainerMenu.UpdatingSlot(this.itemInventory, 0, 25, 24, (stack) -> !LootCategory.forItem(stack).isNone()) {
            public int getMaxItemCount() {
                return 1;
            }

            public int getMaxItemCount(ItemStack stack) {
                return 1;
            }
        });
        this.addSlot(new Slot(this.tile.inventory, 0, 15, 45) {
            public boolean canInsert(ItemStack stack) {
                return AncientReforgingMenu.this.tile.isValidRarityMat(stack);
            }
        });
        this.addSlot(new Slot(this.tile.inventory, 1, 35, 45) {
            public boolean canInsert(ItemStack stack) {
                return stack.isOf(Items.GEM_DUST);
            }
        });
        SimpleInventory var10004 = this.tile.inventory;
        AncientReforgingTableTile var10008 = this.tile;
        Objects.requireNonNull(var10008);
        this.addSlot(new UpdatingSlot(var10004, 0, 15, 45, var10008::isValidRarityMat));
        this.addSlot(new UpdatingSlot(var10004, 1, 35, 45, (ItemStack stack) -> stack.isOf(Items.GEM_DUST)));
        this.addPlayerSlots(inv, 8, 84);
        this.mover.registerRule((stack, slot) -> {
            return slot >= this.playerInvStart && !LootCategory.forItem(stack).isNone();
        }, 0, 1);
        this.mover.registerRule((stack, slot) -> {
            return slot >= this.playerInvStart && this.tile.isValidRarityMat(stack);
        }, 1, 2);
        this.mover.registerRule((stack, slot) -> {
            return slot >= this.playerInvStart && stack.getItem() == Items.GEM_DUST;
        }, 2, 3);
        this.mover.registerRule((stack, slot) -> {
            return slot < this.playerInvStart;
        }, this.playerInvStart, this.hotbarStart + 9);
        this.registerInvShuffleRules();
        this.updateSeed();
        this.addProperty(this.needsReset);
        this.addProperty(Property.create(this.seed, 0));
        this.addProperty(Property.create(this.seed, 1));
        this.addProperty(Property.create(this.costs, 0));
        this.addProperty(Property.create(this.costs, 1));
        this.addProperty(Property.create(this.costs, 2));
    }

    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        this.dropInventory(player, this.itemInventory);
    }

    protected void updateSeed() {
        if (this.player.getCustomData().contains("apoth_reforge_seed")) {
            this.player.getCustomData().remove("apoth_reforge_seed");
        }

        int seed = ((IntComponent)ZenithComponents.REFORGING_SEED.get(this.player)).getValue();
        if (seed == 0) {
            seed = this.player.getRandom().nextInt();
            ((IntComponent)ZenithComponents.REFORGING_SEED.get(this.player)).setValue(seed);
        }

        this.seed[0] = MenuUtil.split(seed, false);
        this.seed[1] = MenuUtil.split(seed, true);
    }

    public int getSeed() {
        return MenuUtil.merge(this.seed[0], this.seed[1], true);
    }

    public boolean onButtonClick(PlayerEntity player, int slot) {
        if (slot >= 0 && slot < 3) {
            ItemStack input = this.getSlot(0).getStack();
            LootRarity rarity = this.getRarity();
            ReforgingRecipe recipe = this.tile.getRecipeFor(rarity);
            if (recipe != null && !input.isEmpty() && !this.needsReset()) {
                int dust = this.getDustCount();
                int dustCost = this.getDustCost(slot);
                int mats = this.getMatCount();
                int matCost = this.getMatCost(slot);
                int levels = this.player.experienceLevel;
                int levelCost = this.getLevelCost(slot);
                if ((dust < dustCost || mats < matCost || levels < levelCost) && !player.isCreative()) {
                    return false;
                } else {
                    if (!player.getWorld().isClient) {
                        Random rand = this.random;
                        rand.setSeed((long)(this.getSeed() ^ Registries.ITEM.getId(input.getItem()).hashCode() + slot));
                        ItemStack output = LootController.createLootItem(input.copy(), rarity, rand);
                        this.getSlot(0).setStackNoCallbacks(output);
                        if (!player.isCreative()) {
                            this.getSlot(1).getStack().decrement(matCost);
                            this.getSlot(2).getStack().decrement(dustCost);
                        }

                        EnchantmentUtils.chargeExperience(player, ApothMiscUtil.getExpCostForSlot(levelCost, slot));
                        ((IntComponent)ZenithComponents.REFORGING_SEED.get(this.player)).setValue(player.getRandom().nextInt());
                        this.updateSeed();
                        this.needsReset.set(1);
                    }

                    player.playSound(SoundEvents.ENTITY_EVOKER_CAST_SPELL, 0.99F, this.level.random.nextFloat() * 0.25F + 1.0F);
                    player.playSound(SoundEvents.BLOCK_AMETHYST_CLUSTER_STEP, 0.34F, this.level.random.nextFloat() * 0.2F + 0.8F);
                    player.playSound(SoundEvents.BLOCK_SMITHING_TABLE_USE, 0.45F, this.level.random.nextFloat() * 0.5F + 0.75F);
                    return true;
                }
            } else {
                return false;
            }
        } else {
            return super.onButtonClick(player, slot);
        }
    }

    public int getMatCount() {
        return this.getSlot(1).getStack().getCount();
    }

    public int getDustCount() {
        return this.getSlot(2).getStack().getCount();
    }

    @Nullable
    public LootRarity getRarity() {
        ItemStack s = this.getSlot(1).getStack();
        return s.isEmpty() ? null : (LootRarity)RarityRegistry.getMaterialRarity(s.getItem()).getOptional().orElse((LootRarity) null);
    }

    public int getDustCost(int slot) {
        int var10000 = this.costs[0];
        ++slot;
        return var10000 * slot;
    }

    public int getMatCost(int slot) {
        int var10000 = this.costs[1];
        ++slot;
        return var10000 * slot;
    }

    public int getLevelCost(int slot) {
        int var10000 = this.costs[2];
        ++slot;
        return var10000 * slot;
    }

    public boolean needsReset() {
        return this.needsReset.get() != 0;
    }

    public void onContentChanged(Inventory pContainer) {
        LootRarity rarity = this.getRarity();
        if (rarity != null) {
            ReforgingRecipe recipe = this.tile.getRecipeFor(rarity);
            if (recipe != null) {
                this.costs[0] = recipe.dustCost();
                this.costs[1] = recipe.matCost();
                this.costs[2] = recipe.levelCost();
            }
        }

        if (this.needsReset()) {
            this.needsReset.set(0);
        }

        super.onContentChanged(pContainer);
        this.tile.markDirty();
    }

    public boolean canUse(PlayerEntity player) {
        if (this.level.isClient) {
            return true;
        } else {
            return this.level.getBlockState(this.pos).getBlock() == AncientReforgingRegistry.Blocks.ANCIENT_REFORGING_TABLE;
        }
    }
}
