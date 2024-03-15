package com.ianm1647.ancientreforging;

import com.ianm1647.ancientreforging.block.AncientReforgingTableTileRenderer;
import com.ianm1647.ancientreforging.screen.AncientReforgingScreen;
import dev.shadowsoffire.apotheosis.Apotheosis;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.block.Block;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;

public class AncientReforgingClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		if (Apotheosis.enableAdventure) {
			registerRenderLayer();
			HandledScreens.register(AncientReforgingRegistry.Menus.ANCIENT_REFORGING, AncientReforgingScreen::new);
			BlockEntityRendererFactories.register(AncientReforgingRegistry.Tiles.ANCIENT_REFORGING_TABLE, (context) -> new AncientReforgingTableTileRenderer());
		}
	}

	public static void registerRenderLayer() {
		renderLayer(AncientReforgingRegistry.Blocks.ANCIENT_REFORGING_TABLE, RenderLayer.getCutout());
	}
	private static void renderLayer(Block block, RenderLayer layer) {
		BlockRenderLayerMap.INSTANCE.putBlock(block, layer);
	}
}