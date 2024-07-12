package com.ianm1647.ancientreforging.block;

import com.ianm1647.ancientreforging.screen.AncientReforgingMenu;
import dev.shadowsoffire.apotheosis.adventure.loot.LootRarity;
import dev.shadowsoffire.apotheosis.adventure.loot.RarityRegistry;
import dev.shadowsoffire.placebo.block_entity.TickingEntityBlock;
import dev.shadowsoffire.placebo.menu.MenuUtil;
import dev.shadowsoffire.placebo.menu.SimplerMenuProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;

public class AncientReforgingTableBlock extends Block implements TickingEntityBlock {
    public static final Component TITLE = Component.translatable("container.apotheosis.reforge");
    public static final VoxelShape SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 12.0, 16.0);
    protected final int maxRarity;

    public AncientReforgingTableBlock(BlockBehaviour.Properties properties, int maxRarity) {
        super(properties);
        this.maxRarity = maxRarity;
    }

    public LootRarity getMaxRarity() {
        return (LootRarity)RarityRegistry.byOrdinal(this.maxRarity).get();
    }

    public boolean useShapeForLightOcclusion(BlockState pState) {
        return true;
    }

    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }

    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        return MenuUtil.openGui(player, pos, AncientReforgingMenu::new);
    }

    public MenuProvider getMenuProvider(BlockState state, Level world, BlockPos pos) {
        return new SimplerMenuProvider(world, pos, AncientReforgingMenu::new);
    }

    @Override
    public void appendHoverText(ItemStack pStack, BlockGetter pLevel, List<Component> list, TooltipFlag pFlag) {
        list.add(Component.translatable("block.ancientreforging.ancient_reforging_table.desc").withStyle(ChatFormatting.GRAY));
        list.add(Component.translatable("block.ancientreforging.ancient_reforging_table.desc2", this.getMaxRarity().toComponent()).withStyle(ChatFormatting.GRAY));
    }

    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new AncientReforgingTableTile(pPos, pState);
    }

    /** @deprecated */
    @Deprecated
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != this || newState.getBlock() != this) {
            BlockEntity te = world.getBlockEntity(pos);
            if (te instanceof AncientReforgingTableTile) {
                AncientReforgingTableTile ref = (AncientReforgingTableTile)te;

                for(int i = 0; i < ref.inv.getSlots(); ++i) {
                    popResource(world, pos, ref.inv.getStackInSlot(i));
                }
            }

            super.onRemove(state, world, pos, newState, isMoving);
        }
    }
}
