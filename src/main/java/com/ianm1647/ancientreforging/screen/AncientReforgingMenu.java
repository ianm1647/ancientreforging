package com.ianm1647.ancientreforging.screen;

import com.ianm1647.ancientreforging.AncientReforgingRegistry;
import com.ianm1647.ancientreforging.block.AncientReforgingTableTile;
import dev.shadowsoffire.apotheosis.adventure.Adventure.Items;
import dev.shadowsoffire.apotheosis.adventure.affix.reforging.ReforgingRecipe;
import dev.shadowsoffire.apotheosis.adventure.affix.reforging.ReforgingTableTile;
import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;
import dev.shadowsoffire.apotheosis.adventure.loot.LootController;
import dev.shadowsoffire.apotheosis.adventure.loot.LootRarity;
import dev.shadowsoffire.apotheosis.adventure.loot.RarityRegistry;
import dev.shadowsoffire.apotheosis.util.ApothMiscUtil;
import dev.shadowsoffire.placebo.cap.InternalItemHandler;
import dev.shadowsoffire.placebo.menu.BlockEntityMenu;
import dev.shadowsoffire.placebo.util.EnchantmentUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Objects;

public class AncientReforgingMenu extends BlockEntityMenu<AncientReforgingTableTile> {
    public static final String REFORGE_SEED = "apoth_reforge_seed";
    protected final Player player;
    protected InternalItemHandler itemInv = new InternalItemHandler(1);
    protected InternalItemHandler choicesInv = new InternalItemHandler(3);
    protected final RandomSource random = new XoroshiroRandomSource(0L);
    protected final int[] costs = new int[3];
    protected int seed = -1;

    public AncientReforgingMenu(int id, Inventory inv, BlockPos pos) {
        super(AncientReforgingRegistry.Menus.ANCIENT_REFORGING.get(), id, inv, pos);
        this.player = inv.player;
        this.addSlot(new UpdatingSlot(this.itemInv, 0, 81, 62, (stack) -> !LootCategory.forItem(stack).isNone()) {
            public int getMaxStackSize() {
                return 1;
            }

            public int getMaxStackSize(ItemStack pStack) {
                return 1;
            }
        });
        InternalItemHandler var10004 = this.tile.inv;
        AncientReforgingTableTile var10008 = this.tile;
        Objects.requireNonNull(var10008);
        this.addSlot(new UpdatingSlot(var10004, 0, 39, 40, var10008::isValidRarityMat));
        this.addSlot(new UpdatingSlot(this.tile.inv, 1, 123, 86,
                (stack) -> stack.getItem() == Items.SIGIL_OF_REBIRTH.get()));
        this.addSlot(new ReforgingResultSlot(this.choicesInv, 0, 27, 135));
        this.addSlot(new ReforgingResultSlot(this.choicesInv, 1, 81, 135));
        this.addSlot(new ReforgingResultSlot(this.choicesInv, 2, 135, 135));
        this.addPlayerSlots(inv, 8, 184);
        this.mover.registerRule((stack, slot) -> slot >= this.playerInvStart && !LootCategory.forItem(stack).isNone(), 0, 1);
        this.mover.registerRule((stack, slot) -> slot >= this.playerInvStart && this.tile.isValidRarityMat(stack), 1, 2);
        this.mover.registerRule((stack, slot) -> slot >= this.playerInvStart && stack.getItem() == Items.SIGIL_OF_REBIRTH.get(), 2, 3);
        this.mover.registerRule((stack, slot) -> slot < this.playerInvStart, this.playerInvStart, this.hotbarStart + 9);
        this.registerInvShuffleRules();
        this.updateSeed();
        this.addDataSlot(DataSlot.shared(this.costs, 0));
        this.addDataSlot(DataSlot.shared(this.costs, 1));
        this.addDataSlot(DataSlot.shared(this.costs, 2));
    }

    public void removed(Player pPlayer) {
        super.removed(pPlayer);
        this.clearContainer(pPlayer, new RecipeWrapper(this.itemInv));
    }

    protected void updateSeed() {
        int seed = this.player.getPersistentData().getInt("apoth_reforge_seed");
        if (seed == 0) {
            seed = this.player.getRandom().nextInt();
            this.player.getPersistentData().putInt("apoth_reforge_seed", seed);
        }

        this.seed = seed;
    }

    public int getMatCount() {
        return this.getSlot(1).getItem().getCount();
    }

    public int getSigilCount() {
        return this.getSlot(2).getItem().getCount();
    }

    @Nullable
    public LootRarity getRarity() {
        ItemStack s = this.getSlot(1).getItem();
        return s.isEmpty() ? null : RarityRegistry.getMaterialRarity(s.getItem()).getOptional().orElse(null);
    }

    public int getSigilCost(int slot) {
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

    public void slotsChanged(Container pContainer) {
        LootRarity rarity = this.getRarity();
        if (rarity != null) {
            ReforgingRecipe recipe = this.tile.getRecipeFor(rarity);
            if (recipe != null) {
                this.costs[0] = recipe.sigilCost();
                this.costs[1] = recipe.matCost();
                this.costs[2] = recipe.levelCost();
            }
        }

        ItemStack input = this.getSlot(0).getItem();

        for(int slot = 0; slot < 3; ++slot) {
            if (!input.isEmpty() && rarity != null) {
                RandomSource rand = this.random;
                rand.setSeed((long)(this.seed ^ ForgeRegistries.ITEMS.getKey(input.getItem()).hashCode() + slot));
                ItemStack output = LootController.createLootItem(input.copy(), rarity, rand);
                this.choicesInv.setStackInSlot(slot, output);
            } else {
                this.choicesInv.setStackInSlot(slot, ItemStack.EMPTY);
            }
        }

        super.slotsChanged(pContainer);
        this.tile.setChanged();
    }

    public class ReforgingResultSlot extends SlotItemHandler {
        public ReforgingResultSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
            super(itemHandler, index, xPosition, yPosition);
        }

        public boolean mayPlace(@NotNull ItemStack stack) {
            return false;
        }

        public boolean mayPickup(Player playerIn) {
            ItemStack input = AncientReforgingMenu.this.getSlot(0).getItem();
            LootRarity rarity = AncientReforgingMenu.this.getRarity();
            ReforgingRecipe recipe = AncientReforgingMenu.this.tile.getRecipeFor(rarity);
            if (recipe != null && !input.isEmpty()) {
                int sigils = AncientReforgingMenu.this.getSigilCount();
                int sigilCost = AncientReforgingMenu.this.getSigilCost(this.getSlotIndex());
                int mats = AncientReforgingMenu.this.getMatCount();
                int matCost = AncientReforgingMenu.this.getMatCost(this.getSlotIndex());
                int levels = AncientReforgingMenu.this.player.experienceLevel;
                int levelCost = AncientReforgingMenu.this.getLevelCost(this.getSlotIndex());
                return ((sigils >= sigilCost && mats >= matCost && levels >= levelCost) || AncientReforgingMenu.this.player.isCreative()) && super.mayPickup(playerIn);
            } else {
                return false;
            }
        }

        public void onTake(Player player, ItemStack stack) {
            if (!player.level().isClientSide) {
                AncientReforgingMenu.this.getSlot(0).set(ItemStack.EMPTY);
                if (!player.isCreative()) {
                    int sigilCost = AncientReforgingMenu.this.getSigilCost(this.getSlotIndex());
                    int matCost = AncientReforgingMenu.this.getMatCost(this.getSlotIndex());
                    int levelCost = AncientReforgingMenu.this.getLevelCost(this.getSlotIndex());
                    AncientReforgingMenu.this.getSlot(1).getItem().shrink(matCost);
                    AncientReforgingMenu.this.getSlot(2).getItem().shrink(sigilCost);
                    EnchantmentUtils.chargeExperience(player, ApothMiscUtil.getExpCostForSlot(levelCost, this.getSlotIndex()));
                }

                player.getPersistentData().putInt("apoth_reforge_seed", player.getRandom().nextInt());
                AncientReforgingMenu.this.updateSeed();
            }

            player.playSound(SoundEvents.EVOKER_CAST_SPELL, 0.99F, player.level().random.nextFloat() * 0.25F + 1.0F);
            player.playSound(SoundEvents.AMETHYST_CLUSTER_STEP, 0.34F, player.level().random.nextFloat() * 0.2F + 0.8F);
            player.playSound(SoundEvents.SMITHING_TABLE_USE, 0.45F, player.level().random.nextFloat() * 0.5F + 0.75F);
        }
    }
}


