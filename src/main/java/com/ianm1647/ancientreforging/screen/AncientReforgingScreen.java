package com.ianm1647.ancientreforging.screen;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.shadowsoffire.apotheosis.adventure.Adventure;
import dev.shadowsoffire.apotheosis.adventure.Adventure.Items;
import dev.shadowsoffire.apotheosis.adventure.affix.salvaging.SalvagingScreen;
import dev.shadowsoffire.apotheosis.adventure.client.AdventureContainerScreen;
import dev.shadowsoffire.apotheosis.adventure.client.GhostVertexBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class AncientReforgingScreen extends AdventureContainerScreen<AncientReforgingMenu> {
    public static final ResourceLocation TEXTURE = new ResourceLocation("apotheosis", "textures/gui/reforge.png");
    public static final ResourceLocation ANIMATED_TEXTURE = new ResourceLocation("apotheosis", "textures/gui/reforge_animation.png");
    public static final int MAX_ANIMATION_TIME = 8;
    protected boolean hasMainItem = false;
    protected int animationTick = 0;
    protected int maxSlot = -1;
    protected int opacityTick = 0;
    protected int availableOpacity = 170;

    public AncientReforgingScreen(AncientReforgingMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageHeight = 266;
    }

    public void render(GuiGraphics gfx, int mouseX, int mouseY, float pPartialTick) {
        this.renderBackground(gfx);
        super.render(gfx, mouseX, mouseY, pPartialTick);
        RenderSystem.disableBlend();
        this.renderTooltip(gfx, mouseX, mouseY);
        int sigils = this.menu.getSigilCount();
        int mats = this.menu.getMatCount();
        int levels = this.menu.player.experienceLevel;

        for(int idx = 0; idx < 3; ++idx) {
            Slot slot = this.getMenu().getSlot(3 + idx);
            if (this.isHovering(slot.x, slot.y, 16, 16, (double)mouseX, (double)mouseY)) {
                ItemStack choice = slot.getItem();
                if (!choice.isEmpty()) {
                    List<Component> tooltips = new ArrayList();
                    int sigilCost = this.menu.getSigilCost(idx);
                    int matCost = this.menu.getMatCost(idx);
                    int levelCost = this.menu.getLevelCost(idx);
                    boolean creative = this.minecraft.player.isCreative();
                    tooltips.add(Component.translatable("text.apotheosis.reforge_cost").withStyle(ChatFormatting.YELLOW, ChatFormatting.UNDERLINE));
                    tooltips.add(CommonComponents.EMPTY);
                    if (sigilCost > 0) {
                        tooltips.add(Component.translatable("%s %s", sigilCost, Items.SIGIL_OF_REBIRTH.get().getName(ItemStack.EMPTY)).withStyle(!creative && sigils < sigilCost ? ChatFormatting.RED : ChatFormatting.GRAY));
                    }

                    if (matCost > 0) {
                        tooltips.add(Component.translatable("%s %s", matCost, this.menu.getSlot(1).getItem().getHoverName().getString()).withStyle(!creative && mats < matCost ? ChatFormatting.RED : ChatFormatting.GRAY));
                    }

                    String key = idx == 0 ? "container.enchant.level.one" : "container.enchant.level.many";
                    tooltips.add(Component.translatable(key, idx + 1).withStyle(!creative && levels < levelCost ? ChatFormatting.RED : ChatFormatting.GRAY));
                    tooltips.add(Component.literal(" "));
                    tooltips.add(Component.translatable("container.enchant.level.requirement", levelCost).withStyle(!creative && levels < levelCost ? ChatFormatting.RED : ChatFormatting.GRAY));
                    this.drawOnLeft(gfx, tooltips, this.getGuiTop() + 45);
                    break;
                }
            }
        }

    }

    protected void renderBg(GuiGraphics gfx, float partials, int x, int y) {
        int left = this.getGuiLeft();
        int top = this.getGuiTop();
        int xCenter = (this.width - this.imageWidth) / 2;
        int yCenter = (this.height - this.imageHeight) / 2;
        gfx.blit(TEXTURE, xCenter, yCenter, 0.0F, 0.0F, this.imageWidth, this.imageHeight, 256, 384);

        for(int idx = 0; idx < 3; ++idx) {
            if (this.maxSlot >= idx && this.animationTick == 0) {
                gfx.blit(TEXTURE, left + 20 + 46 * idx, top + 129, (float)(20 + 46 * idx), 273.0F, 46, 35, 256, 384);
            }
        }

        boolean hadItem = this.hasMainItem;
        this.hasMainItem = this.menu.getSlot(0).hasItem();
        if (!hadItem && this.hasMainItem) {
            this.animationTick = 8;
            this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI((SoundEvent) Adventure.Sounds.REFORGE.get(), 1.0F, 2.0F));
        }

        int mats;
        if (this.hasMainItem) {
            float delta = Mth.clamp(((float)(8 - this.animationTick) - partials) / 8.0F, 0.0F, 1.0F);
            mats = Mth.lerpInt(delta, 0, 20);
            gfx.blit(ANIMATED_TEXTURE, left + 26, top + 15, 127, 112, 0.0F, (float)(mats * 112), 127, 112, 127, 2240);
        }

        int sigils = this.menu.getSigilCount();
        mats = this.menu.getMatCount();
        int levels = this.menu.player.experienceLevel;
        this.maxSlot = -1;

        for(int idx = 0; idx < 3; ++idx) {
            Slot slot = this.getMenu().getSlot(3 + idx);
            if (!slot.hasItem()) {
                break;
            }

            int sigilCost = this.menu.getSigilCost(idx);
            int matCost = this.menu.getMatCost(idx);
            int levelCost = this.menu.getLevelCost(idx);
            if (sigils >= sigilCost && levels >= levelCost && mats >= matCost || this.minecraft.player.getAbilities().instabuild) {
                ++this.maxSlot;
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

    protected void drawBorderedString(GuiGraphics gfx, String str, int x, int y, int color, int shadowColor) {
        Component comp = Component.literal(str);
        gfx.drawString(this.font, comp, x, y - 1, shadowColor, false);
        gfx.drawString(this.font, comp, x - 1, y, shadowColor, false);
        gfx.drawString(this.font, comp, x, y + 1, shadowColor, false);
        gfx.drawString(this.font, comp, x + 1, y, shadowColor, false);
        gfx.drawString(this.font, comp, x, y, color, false);
    }

    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;

        for(int k = 0; k < 3; ++k) {
            double d0 = pMouseX - (double)(i + 60);
            double d1 = pMouseY - (double)(j + 14 + 19 * k);
            if (d0 >= 0.0 && d1 >= 0.0 && d0 < 108.0 && d1 < 19.0 && this.menu.clickMenuButton(this.minecraft.player, k)) {
                this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, k);
                return true;
            }
        }

        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    protected void containerTick() {
        ++this.opacityTick;
        if (this.animationTick > 0) {
            --this.animationTick;
            if (this.animationTick == 0) {
                this.opacityTick = 0;
            }
        }

        float sin = Mth.sin((float)this.opacityTick / 60.0F * 3.1415927F);
        float delta = sin * sin;
        this.availableOpacity = Mth.lerpInt(delta, 136, 221);
    }

    /*

    public void renderSlot(GuiGraphics gfx, Slot slot) {
        if (slot instanceof AncientReforgingMenu.ReforgingResultSlot) {
            if (this.animationTick == 0) {
                int opacity = this.maxSlot >= slot.getContainerSlot() ? this.availableOpacity : 64;
                PoseStack pose = gfx.pose();
                pose.pushPose();
                pose.translate(0.0F, 0.0F, 100.0F);
                SalvagingScreen.renderGuiItem(gfx, slot.getItem(), slot.x, slot.y, GhostVertexBuilder.makeGhostBuffer(opacity));
                pose.popPose();
            }
        } else {
            super.renderSlot(gfx, slot);
        }

    }

     */
}

