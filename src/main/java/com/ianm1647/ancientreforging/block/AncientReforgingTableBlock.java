package com.ianm1647.ancientreforging.block;

import com.ianm1647.ancientreforging.screen.AncientReforgingMenu;
import dev.shadowsoffire.apotheosis.adventure.Adventure;
import dev.shadowsoffire.apotheosis.adventure.loot.LootRarity;
import dev.shadowsoffire.apotheosis.adventure.loot.RarityRegistry;
import dev.shadowsoffire.placebo.block_entity.TickingEntityBlock;
import dev.shadowsoffire.placebo.menu.SimplerMenuProvider;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.List;

public class AncientReforgingTableBlock extends Block implements TickingEntityBlock {
    public static final Text TITLE = Text.translatable("container.zenith.reforge");
    public static final VoxelShape SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 12.0, 16.0);
    protected final int maxRarity;

    public AncientReforgingTableBlock(AbstractBlock.Settings properties, int maxRarity) {
        super(properties);
        this.maxRarity = maxRarity;
    }

    public LootRarity getMaxRarity() {
        return (LootRarity) RarityRegistry.byOrdinal(this.maxRarity).get();
    }

    public boolean hasSidedTransparency(BlockState pState) {
        return true;
    }

    public VoxelShape getOutlineShape(BlockState pState, BlockView pLevel, BlockPos pPos, ShapeContext pContext) {
        return SHAPE;
    }

    public ActionResult onUse(BlockState state, World level, final BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!level.isClient) {
            player.openHandledScreen(new ExtendedScreenHandlerFactory() {
                public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
                    buf.writeBlockPos(pos);
                }

                public Text getDisplayName() {
                    return Text.translatable("block.zenith.salvaging_table");
                }

                public ScreenHandler createMenu(int i, PlayerInventory inventory, PlayerEntity player) {
                    return new AncientReforgingMenu(i, inventory, pos);
                }
            });
            return ActionResult.SUCCESS;
        } else {
            return ActionResult.CONSUME;
        }
    }

    public NamedScreenHandlerFactory createScreenHandlerFactory(BlockState state, World world, BlockPos pos) {
        return new SimplerMenuProvider<>(world, pos, AncientReforgingMenu::new);
    }

    @Environment(EnvType.CLIENT)
    public void appendTooltip(ItemStack pStack, BlockView pLevel, List<Text> list, TooltipContext pFlag) {
        list.add(Text.translatable(Adventure.Blocks.REFORGING_TABLE.getTranslationKey() + ".desc").formatted(Formatting.GRAY));
        list.add(Text.translatable(Adventure.Blocks.REFORGING_TABLE.getTranslationKey() + ".desc2", this.getMaxRarity().toComponent()).formatted(Formatting.GRAY));

    }

    public BlockEntity createBlockEntity(BlockPos pPos, BlockState pState) {
        return new AncientReforgingTableTile(pPos, pState);
    }

    /** @deprecated */
    @Deprecated
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != this || newState.getBlock() != this) {
            BlockEntity te = world.getBlockEntity(pos);
            if (te instanceof AncientReforgingTableTile) {
                AncientReforgingTableTile ref = (AncientReforgingTableTile)te;

                for(int i = 0; i < ref.inventory.stacks.size(); ++i) {
                    dropStack(world, pos, ref.inventory.getStack(i));
                }
            }

            super.onStateReplaced(state, world, pos, newState, isMoving);
        }
    }
}
