package ianm1647.ancientreforging.data;

import ianm1647.ancientreforging.Reforge;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class ARLootProvider extends LootTableProvider {

    public ARLootProvider(PackOutput output, Set<ResourceKey<LootTable>> requiredTables, List<SubProviderEntry> subProviders, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, requiredTables, subProviders, registries);
    }

    public static ARLootProvider create(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        return new ARLootProvider(output, Set.of(), List.of(
                new LootTableProvider.SubProviderEntry(BlockLoot::new, LootContextParamSets.BLOCK)
        ), registries);
    }

    public static class BlockLoot extends BlockLootSubProvider {
        private final Set<Block> generatedLootTables = new HashSet();

        public BlockLoot(HolderLookup.Provider registries) {
            super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
        }

        @Override
        protected void generate () {
            this.dropSelf(Reforge.Blocks.ANCIENT_REFORGING_TABLE.value());
        }

        protected void add (Block block, LootTable.Builder builder){
            this.generatedLootTables.add(block);
            this.map.put(block.getLootTable(), builder);
        }

        protected Iterable<Block> getKnownBlocks () {
            return this.generatedLootTables;
        }
    }
}
