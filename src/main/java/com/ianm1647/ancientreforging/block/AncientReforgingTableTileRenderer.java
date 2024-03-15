package com.ianm1647.ancientreforging.block;

import dev.shadowsoffire.apotheosis.mixin.accessors.ItemRendererAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

public class AncientReforgingTableTileRenderer implements BlockEntityRenderer<AncientReforgingTableTile> {
    private static final Identifier HAMMER = new Identifier("zenith", "item/hammer");

    public AncientReforgingTableTileRenderer() {
    }

    public void render(AncientReforgingTableTile tile, float partials, MatrixStack matrix, VertexConsumerProvider pBufferSource, int light, int overlay) {
        ItemRenderer irenderer = MinecraftClient.getInstance().getItemRenderer();
        BakedModel base = irenderer.getModels().getModelManager().getModel(HAMMER);
        matrix.push();
        double px = 0.0625;
        matrix.scale(1.25F, 1.25F, 1.25F);
        matrix.translate(8.5 * px / 1.25, 16.0 * px / 1.25 - 0.015, 7.0 * px / 1.25);
        matrix.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(45.0F));
        matrix.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90.0F));
        float factor;
        float sin;
        float sinSq;
        if (tile.step1) {
            factor = (float)(tile.time % 60) + partials;
            sin = MathHelper.sin(factor * 3.1415927F / 120.0F);
            sinSq = sin * sin;
            matrix.translate(0.125 * (double)sinSq, 0.0, -0.15 * (double)sinSq);
            matrix.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(45.0F * sinSq));
        } else {
            factor = (float)(tile.time % 5) + partials;
            sin = MathHelper.sin(1.5707964F + factor * 3.1415927F / 10.0F);
            sinSq = sin * sin;
            matrix.translate(0.125 * (double)sinSq, 0.0, -0.15 * (double)sinSq);
            matrix.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(45.0F * sinSq));
        }

        VertexConsumerProvider.Immediate src = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
        ((ItemRendererAccessor)irenderer).callRenderModelLists(base, ItemStack.EMPTY, light, overlay, matrix, ItemRenderer.getDirectItemGlintConsumer(src, RenderLayers.getEntityBlockLayer(tile.getCachedState(), true), true, false));
        src.draw();
        matrix.pop();
    }
}
