package com.ianm1647.ancientreforging.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.shadowsoffire.apotheosis.adventure.Adventure.Items;
import dev.shadowsoffire.apotheosis.adventure.loot.LootController;
import dev.shadowsoffire.apotheosis.adventure.loot.LootRarity;
import dev.shadowsoffire.apotheosis.util.DrawsOnLeft;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.EnchantingPhrases;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;

public class AncientReforgingScreen extends HandledScreen<AncientReforgingMenu> implements DrawsOnLeft {
    public static final Identifier TEXTURE = new Identifier("zenith", "textures/gui/reforge.png");
    protected ItemStack[] choices = new ItemStack[3];
    protected ItemStack lastInput;
    protected LootRarity lastRarity;
    protected Text title;

    public AncientReforgingScreen(AncientReforgingMenu menu, PlayerInventory inv, Text title) {
        super(menu, inv, title);
        this.lastInput = ItemStack.EMPTY;
        this.lastRarity = null;
        this.titleY = 5;
        Arrays.fill(this.choices, ItemStack.EMPTY);
        this.title = Text.translatable("container.zenith.reforge");
    }

    public boolean shouldRecompute() {
        ItemStack input = ((AncientReforgingMenu)this.handler).getSlot(0).getStack();
        LootRarity rarity = ((AncientReforgingMenu)this.getScreenHandler()).getRarity();
        return !ItemStack.canCombine(input, this.lastInput) || this.lastRarity != rarity;
    }

    public void recomputeChoices() {
        ItemStack input = ((AncientReforgingMenu)this.handler).getSlot(0).getStack();
        LootRarity rarity = ((AncientReforgingMenu)this.getScreenHandler()).getRarity();
        if (!input.isEmpty() && rarity != null) {
            Random rand = ((AncientReforgingMenu)this.handler).random;

            for(int i = 0; i < 3; ++i) {
                rand.setSeed((long)(((AncientReforgingMenu)this.handler).getSeed() ^ Registries.ITEM.getId(input.getItem()).hashCode() + i));
                this.choices[i] = LootController.createLootItem(input.copy(), rarity, rand);
            }
        } else {
            Arrays.fill(this.choices, ItemStack.EMPTY);
        }

        this.lastInput = input.copy();
        this.lastRarity = rarity;
    }

    public void render(DrawContext gfx, int x, int y, float pPartialTick) {
        if (this.shouldRecompute()) {
            this.recomputeChoices();
        }

        this.renderBackground(gfx);
        super.render(gfx, x, y, pPartialTick);
        RenderSystem.disableBlend();
        this.drawMouseoverTooltip(gfx, x, y);
        int xCenter = (this.width - this.backgroundWidth) / 2;
        int yCenter = (this.height - this.backgroundHeight) / 2;
        int dust = ((AncientReforgingMenu)this.handler).getDustCount();
        int mats = ((AncientReforgingMenu)this.handler).getMatCount();
        int levels = ((AncientReforgingMenu)this.handler).player.experienceLevel;

        for(int slot = 0; slot < 3; ++slot) {
            ItemStack choice = this.choices[slot];
            if (!choice.isEmpty() && !((AncientReforgingMenu)this.handler).needsReset()) {
                List<Text> tooltips = new ArrayList();
                int dustCost = ((AncientReforgingMenu)this.handler).getDustCost(slot);
                int matCost = ((AncientReforgingMenu)this.handler).getMatCost(slot);
                int levelCost = ((AncientReforgingMenu)this.handler).getLevelCost(slot);
                tooltips.add(Text.translatable("text.zenith.reforge_cost").formatted(new Formatting[]{Formatting.YELLOW, Formatting.UNDERLINE}));
                tooltips.add(ScreenTexts.EMPTY);
                if (dustCost > 0) {
                    tooltips.add(Text.translatable("%s %s", new Object[]{dustCost, Items.GEM_DUST.getName(ItemStack.EMPTY)}).formatted(dust < dustCost ? Formatting.RED : Formatting.GRAY));
                }

                if (matCost > 0) {
                    tooltips.add(Text.translatable("%s %s", new Object[]{matCost, ((AncientReforgingMenu)this.handler).getSlot(1).getStack().getName()}).formatted(mats < matCost ? Formatting.RED : Formatting.GRAY));
                }

                String key = levels >= levelCost ? (levelCost == 1 ? "container.enchant.level.one" : "container.enchant.level.many") : "container.enchant.level.requirement";
                tooltips.add(Text.translatable(key, new Object[]{levelCost}).formatted(levels < levelCost ? Formatting.RED : Formatting.GRAY));
                int k2 = x - (xCenter + 60);
                int l2 = y - (yCenter + 14 + 19 * slot);
                if (k2 >= 0 && l2 >= 0 && k2 < 108 && l2 < 19) {
                    gfx.drawItemTooltip(this.textRenderer, choice, x, y);
                    this.drawOnLeft(gfx, tooltips, this.y + 29);
                }
            }
        }

    }

    protected void drawBackground(DrawContext gfx, float partials, int x, int y) {
        int xCenter = (this.width - this.backgroundWidth) / 2;
        int slotsX = xCenter + 60;
        int yCenter = (this.height - this.backgroundHeight) / 2;
        gfx.drawTexture(TEXTURE, xCenter, yCenter, 0, 0, this.backgroundWidth, this.backgroundHeight);
        LootRarity rarity = ((AncientReforgingMenu)this.handler).getRarity();
        int dust;
        if (!((AncientReforgingMenu)this.handler).getSlot(0).getStack().isEmpty() && rarity != null && !((AncientReforgingMenu)this.handler).needsReset()) {
            dust = ((AncientReforgingMenu)this.handler).getDustCount();
            int mats = ((AncientReforgingMenu)this.handler).getMatCount();
            int levels = ((AncientReforgingMenu)this.handler).player.experienceLevel;
            EnchantingPhrases.getInstance().setSeed((long)((AncientReforgingMenu)this.handler).getSeed());

            for(int slot = 0; slot < 3; ++slot) {
                RenderSystem.setShader(GameRenderer::getPositionTexProgram);
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                RenderSystem.setShaderTexture(0, TEXTURE);
                int dustCost = ((AncientReforgingMenu)this.handler).getDustCost(slot);
                int matCost = ((AncientReforgingMenu)this.handler).getMatCost(slot);
                int levelCost = ((AncientReforgingMenu)this.handler).getLevelCost(slot);
                int maxCost = ((AncientReforgingMenu)this.handler).getMatCost(2);
                String levelStr = "" + levelCost;
                String costStr = "" + matCost;
                int width = 86 - this.textRenderer.getWidth(levelStr + maxCost);
                int randTextX = slotsX + 15 + this.textRenderer.getWidth("" + maxCost);
                StringVisitable randText = EnchantingPhrases.getInstance().generatePhrase(this.textRenderer, width);
                int color = 5329233;
                if ((dust < dustCost || levels < levelCost || mats < matCost) && !this.client.player.getAbilities().creativeMode) {
                    gfx.drawTexture(TEXTURE, slotsX, yCenter + 14 + 19 * slot, 0, 185, 108, 19);
                    gfx.drawTexture(TEXTURE, slotsX + 1, yCenter + 15 + 19 * slot, 16 * slot, 239, 16, 16);
                    gfx.drawTextWrapped(this.textRenderer, randText, randTextX, yCenter + 16 + 19 * slot, width, color);
                    color = this.darken(rarity.getColor().getRgb(), 2);
                } else {
                    int k2 = x - (xCenter + 60);
                    int l2 = y - (yCenter + 14 + 19 * slot);
                    if (k2 >= 0 && l2 >= 0 && k2 < 108 && l2 < 19) {
                        gfx.drawTexture(TEXTURE, slotsX, yCenter + 14 + 19 * slot, 0, 204, 108, 19);
                        color = 16777088;
                    } else {
                        gfx.drawTexture(TEXTURE, slotsX, yCenter + 14 + 19 * slot, 0, 166, 108, 19);
                        color = 13487565;
                    }

                    gfx.drawTexture(TEXTURE, slotsX + 1, yCenter + 15 + 19 * slot, 16 * slot, 223, 16, 16);
                    gfx.drawTextWrapped(this.textRenderer, randText, randTextX, yCenter + 16 + 19 * slot, width, color);
                    color = rarity.getColor().getRgb();
                }

                this.drawBorderedString(gfx, costStr, slotsX + 10, yCenter + 21 + 19 * slot, color, this.darken(color, 4));
                this.drawBorderedString(gfx, levelStr, slotsX + 106 - this.textRenderer.getWidth(levelStr), yCenter + 16 + 19 * slot + 7, color, this.darken(color, 4));
            }

        } else {
            for(dust = 0; dust < 3; ++dust) {
                gfx.drawTexture(TEXTURE, slotsX, yCenter + 14 + 19 * dust, 0, 185, 108, 19);
            }

        }
    }

    protected int darken(int rColor, int factor) {
        int r = rColor >> 16 & 255;
        int g = rColor >> 8 & 255;
        int b = rColor & 255;
        r /= factor;
        g /= factor;
        b /= factor;
        return r << 16 | g << 8 | b;
    }

    protected void drawBorderedString(DrawContext gfx, String str, int x, int y, int color, int shadowColor) {
        Text comp = Text.literal(str);
        gfx.drawText(this.textRenderer, comp, x, y - 1, shadowColor, false);
        gfx.drawText(this.textRenderer, comp, x - 1, y, shadowColor, false);
        gfx.drawText(this.textRenderer, comp, x, y + 1, shadowColor, false);
        gfx.drawText(this.textRenderer, comp, x + 1, y, shadowColor, false);
        gfx.drawText(this.textRenderer, comp, x, y, color, false);
    }

    protected void drawForeground(DrawContext gfx, int x, int y) {
        gfx.drawText(this.textRenderer, this.title, this.titleX, this.titleY, 4210752, false);
        gfx.drawText(this.textRenderer, this.playerInventoryTitle, this.playerInventoryTitleX, this.playerInventoryTitleY, 4210752, false);
    }

    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        int i = (this.width - this.backgroundWidth) / 2;
        int j = (this.height - this.backgroundHeight) / 2;

        for(int k = 0; k < 3; ++k) {
            double d0 = pMouseX - (double)(i + 60);
            double d1 = pMouseY - (double)(j + 14 + 19 * k);
            if (d0 >= 0.0 && d1 >= 0.0 && d0 < 108.0 && d1 < 19.0 && ((AncientReforgingMenu)this.handler).onButtonClick(this.client.player, k)) {
                this.client.interactionManager.clickButton(((AncientReforgingMenu)this.handler).syncId, k);
                return true;
            }
        }

        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }
}
