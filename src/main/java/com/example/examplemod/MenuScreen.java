package com.example.examplemod;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.client.gui.GuiUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class MenuScreen extends Screen {
    protected MenuScreen() {
        super(TextComponent.EMPTY);
    }

    @Override
    protected void init() {
        super.init();
        addRenderableWidget(new L(this.width, this.height, 48, this.height - 64, 36));
    }

    @Override
    public void render(PoseStack p_96562_, int p_96563_, int p_96564_, float p_96565_) {
        renderBackground(p_96562_);
        super.render(p_96562_, p_96563_, p_96564_, p_96565_);
    }

    private static class L extends ObjectSelectionList<E> {

        public L(int width, int height, int p_94445_, int p_94446_, int p_94447_) {
            super(Minecraft.getInstance(), width, height, p_94445_, p_94446_, p_94447_);
            Drawing d = new Drawing("Karl");

            addEntry(new E(this, d));
            addEntry(new E(this, d));
            addEntry(new E(this, d));
            addEntry(new E(this, d));
        }
    }

    private static class E extends ObjectSelectionList.Entry<E> {

        private final Drawing drawing;
        private final L l;


        private E(L l, Drawing drawing) {
            this.l = l;
            this.drawing = drawing;
        }

        @Override
        public Component getNarration() {
            return new TextComponent("alpha");
        }

        @Override
        public void render(PoseStack p_93523_, int index, int top, int left, int p_93527_, int p_93528_, int p_93529_, int p_93530_, boolean p_93531_, float p_93532_) {
            MessageDigest messageDigest;
            try {
                messageDigest = MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
            int c = Arrays.hashCode(messageDigest.digest(String.valueOf(index).getBytes(StandardCharsets.UTF_8))) & 0xFFFFFF;
            GuiUtils.drawGradientRect(p_93523_.last().pose(), 41, left, top, left + l.getWidth(), top + 64, c, c);
            Minecraft.getInstance().font.draw(p_93523_, drawing.getName(), left, top, 0xFFFFFF);

        }
    }
}
