package ianm1647.ancientreforging.item;

import dev.shadowsoffire.apotheosis.affix.salvaging.SalvageItem;
import dev.shadowsoffire.apotheosis.loot.LootRarity;
import dev.shadowsoffire.placebo.dynreg.DynamicHolder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.ItemStack;

public class AncientSalvageItem extends SalvageItem {
    public AncientSalvageItem(DynamicHolder<LootRarity> rarity, Properties pProperties) {
        super(rarity, pProperties);
    }

    @Override
    public Component getName(ItemStack pStack) {
        if (!this.rarity.isBound()) {
            return super.getName(pStack);
        }
        return Component.translatable(this.getDescriptionId()).withStyle(Style.EMPTY.withObfuscated(true).withColor(this.rarity.get().color()));
    }
}
