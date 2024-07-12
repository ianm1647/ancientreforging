package com.ianm1647.ancientreforging.block;

import com.ianm1647.ancientreforging.AncientReforgingRegistry;
import dev.shadowsoffire.apotheosis.Apoth.RecipeTypes;
import dev.shadowsoffire.apotheosis.adventure.Adventure.Items;
import dev.shadowsoffire.apotheosis.adventure.affix.reforging.ReforgingRecipe;
import dev.shadowsoffire.apotheosis.adventure.loot.LootRarity;
import dev.shadowsoffire.apotheosis.adventure.loot.RarityRegistry;
import dev.shadowsoffire.placebo.block_entity.TickingBlockEntity;
import dev.shadowsoffire.placebo.cap.InternalItemHandler;
import dev.shadowsoffire.placebo.reload.DynamicHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

public class AncientReforgingTableTile extends BlockEntity implements TickingBlockEntity {
    public int time = 0;
    public boolean step1 = true;
    public InternalItemHandler inv = new InternalItemHandler(2) {
        public boolean isItemValid(int slot, ItemStack stack) {
            return slot == 0 ? AncientReforgingTableTile.this.isValidRarityMat(stack) : stack.is((Item)Items.SIGIL_OF_REBIRTH.get());
        }

        protected void onContentsChanged(int slot) {
            AncientReforgingTableTile.this.setChanged();
        }
    };
    LazyOptional<IItemHandler> invCap = LazyOptional.of(() -> this.inv);

    public AncientReforgingTableTile(BlockPos pWorldPosition, BlockState pBlockState) {
        super(AncientReforgingRegistry.BlockEntities.ANCIENT_REFORGING_TABLE.get(), pWorldPosition, pBlockState);
    }

    public LootRarity getMaxRarity() {
        return ((AncientReforgingTableBlock)this.getBlockState().getBlock()).getMaxRarity();
    }

    public boolean isValidRarityMat(ItemStack stack) {
        DynamicHolder<LootRarity> rarity = RarityRegistry.getMaterialRarity(stack.getItem());
        return rarity.isBound() && this.getMaxRarity().isAtLeast((LootRarity)rarity.get()) && this.getRecipeFor((LootRarity)rarity.get()) != null;
    }

    public @Nullable ReforgingRecipe getRecipeFor(LootRarity rarity) {
        return this.level.getRecipeManager().getAllRecipesFor(RecipeTypes.REFORGING).stream().filter((r) -> r.rarity().get() == rarity).findFirst().orElse(null);
    }

    public void clientTick(Level pLevel, BlockPos pPos, BlockState pState) {
        Player player = pLevel.getNearestPlayer((double)pPos.getX() + 0.5, (double)pPos.getY() + 0.5, (double)pPos.getZ() + 0.5, 4.0, false);
        if (player != null) {
            ++this.time;
        } else {
            if (this.time == 0 && this.step1) {
                return;
            }

            ++this.time;
        }

        if (this.step1 && this.time == 59) {
            this.step1 = false;
            this.time = 0;
        } else if (this.time == 4 && !this.step1) {
            RandomSource rand = pLevel.random;

            for(int i = 0; i < 6; ++i) {
                pLevel.addParticle(ParticleTypes.CRIT, (double)pPos.getX() + 0.5 - 0.1 * rand.nextDouble(), (double)pPos.getY() + 0.8125, (double)pPos.getZ() + 0.5 + 0.1 * rand.nextDouble(), 0.0, 0.0, 0.0);
            }

            pLevel.playLocalSound(pPos.getX(), pPos.getY(), pPos.getZ(), SoundEvents.ANVIL_PLACE, SoundSource.BLOCKS, 0.03F, 1.7F + rand.nextFloat() * 0.2F, true);
            this.step1 = true;
            this.time = 0;
        }

    }

    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("inventory", this.inv.serializeNBT());
    }

    public void load(CompoundTag tag) {
        super.load(tag);
        this.inv.deserializeNBT(tag.getCompound("inventory"));
    }

    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        return cap == ForgeCapabilities.ITEM_HANDLER ? this.invCap.cast() : super.getCapability(cap, side);
    }

    public void invalidateCaps() {
        super.invalidateCaps();
        this.invCap.invalidate();
    }

    public void reviveCaps() {
        super.reviveCaps();
        this.invCap = LazyOptional.of(() -> this.inv);
    }
}
