package com.ianm1647.ancientreforging.block;

import com.ianm1647.ancientreforging.AncientReforgingRegistry;
import dev.shadowsoffire.apotheosis.adventure.Adventure.Items;
import dev.shadowsoffire.apotheosis.adventure.Adventure.RecipeTypes;
import dev.shadowsoffire.apotheosis.adventure.affix.reforging.ReforgingRecipe;
import dev.shadowsoffire.apotheosis.adventure.loot.LootRarity;
import dev.shadowsoffire.apotheosis.adventure.loot.RarityRegistry;
import dev.shadowsoffire.placebo.block_entity.TickingBlockEntity;
import dev.shadowsoffire.placebo.reload.DynamicHolder;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class AncientReforgingTableTile extends BlockEntity implements ExtendedScreenHandlerFactory, TickingBlockEntity {
    public int time = 0;
    public boolean step1 = true;
    protected final BlockPos pos;
    public SimpleInventory inventory = new SimpleInventory(2) {
        public boolean isValid(int slot, ItemStack stack) {
            return slot == 0 ? AncientReforgingTableTile.this.isValidRarityMat(stack) : stack.isOf(Items.GEM_DUST);
        }

        public void markDirty() {
            AncientReforgingTableTile.this.markDirty();
        }
    };
    public InventoryStorage storage;

    public AncientReforgingTableTile(BlockPos pPos, BlockState pBlockState) {
        super(AncientReforgingRegistry.Tiles.ANCIENT_REFORGING_TABLE, pPos, pBlockState);
        this.storage = InventoryStorage.of(this.inventory, null);
        this.pos = pPos;
    }

    public LootRarity getMaxRarity() {
        return ((AncientReforgingTableBlock)this.getCachedState().getBlock()).getMaxRarity();
    }

    public boolean isValidRarityMat(ItemStack stack) {
        DynamicHolder<LootRarity> rarity = RarityRegistry.getMaterialRarity(stack.getItem());
        return rarity.isBound() && this.getMaxRarity().isAtLeast(rarity.get()) && this.getRecipeFor(rarity.get()) != null;
    }

    public @Nullable ReforgingRecipe getRecipeFor(LootRarity rarity) {
        return this.world.getRecipeManager().listAllOfType(RecipeTypes.REFORGING).stream().filter((r) -> r.rarity().get() == rarity).findFirst().orElse(null);
    }

    public void clientTick(World pLevel, BlockPos pPos, BlockState pState) {
        PlayerEntity player = pLevel.getClosestPlayer((double)pPos.getX() + 0.5, (double)pPos.getY() + 0.5, (double)pPos.getZ() + 0.5, 4.0, false);
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
            Random rand = pLevel.random;

            for(int i = 0; i < 6; ++i) {
                pLevel.addParticle(ParticleTypes.CRIT, (double)pPos.getX() + 0.5 - 0.1 * rand.nextDouble(), (double)pPos.getY() + 0.8125, (double)pPos.getZ() + 0.5 + 0.1 * rand.nextDouble(), 0.0, 0.0, 0.0);
            }

            pLevel.playSound((double)pPos.getX(), (double)pPos.getY(), (double)pPos.getZ(), SoundEvents.BLOCK_ANVIL_PLACE, SoundCategory.BLOCKS, 0.03F, 1.7F + rand.nextFloat() * 0.2F, true);
            this.step1 = true;
            this.time = 0;
        }

    }

    public void writeNbt(NbtCompound tag) {
        super.writeNbt(tag);
        Inventories.writeNbt(tag, this.inventory.stacks);
    }

    public void readNbt(NbtCompound tag) {
        super.readNbt(tag);
        Inventories.readNbt(tag, this.inventory.stacks);
    }

    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(this.pos);
    }

    public Text getDisplayName() {
        return Text.translatable("block.zenith.reforging_table");
    }

    public @Nullable ScreenHandler createMenu(int i, PlayerInventory inventory, PlayerEntity player) {
        return null;
    }
}
