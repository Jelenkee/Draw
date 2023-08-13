package com.example.examplemod;

import com.google.common.collect.Iterables;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.Util;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.client.gui.GuiUtils;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class DrawScreen extends Screen {
    private Drawing drawing = null;
    private Drawing savedDrawing = null;
    private final boolean createMode;

    private static Map<String, Drawing> map = new HashMap<>();

    protected DrawScreen(boolean createMode) {
        super(new TextComponent("toll"));
        this.createMode = createMode;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        super.render(poseStack, mouseX, mouseY, partialTicks);
        //TODO draw grid
        if (drawing != null) {
            for (Point2D.Double point : drawing.getPoints()) {
                poseStack.pushPose();
                float f = .25f;
                poseStack.scale(f, f, f);
                int offset = (int) (3 / f);
                int x = (int) (point.getX() / f);
                int y = (int) (point.getY() / f);
                Color c = new Color(255, 255, 0, 255);
                GuiUtils.drawGradientRect(poseStack.last().pose(), -1, (int) (x - offset / 2f), y - offset, (int) (x + offset / 2f), y + offset, c.getRGB(), c.getRGB());
                GuiUtils.drawGradientRect(poseStack.last().pose(), -1, x - offset, (int) (y - offset / 2f), x + offset, (int) (y + offset / 2f), c.getRGB(), c.getRGB());
                poseStack.popPose();
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int key) {
        boolean res = super.mouseClicked(mouseX, mouseY, key);
        if (GLFW.GLFW_MOUSE_BUTTON_1 == key) {
            drawing = new Drawing();
            drawing.addPoint(mouseX, mouseY);
        }
        return res;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int key) {
        boolean res = super.mouseReleased(mouseX, mouseY, key);
        if (GLFW.GLFW_MOUSE_BUTTON_1 == key && drawing != null) {
            drawing.addPoint(mouseX, mouseY);
            if (drawing.valid()) {
                drawing.finish();
                if (createMode) {
                    //TODO persist drawing
                    //TODO check for similar drawing and warn
                } else {
                    //TODO check for match
                }
                String id = Iterables.getLast(minecraft.gui.getChat().getRecentChat());
                if (id.length() == 1) {
                    map.put(id, drawing);
                    System.out.println("saved " + id);
                } else {
                    String s = map.entrySet().stream()
                            .sorted(Comparator.comparing(Map.Entry::getKey))
                            .map(e -> e.getKey() + ":" + (int) drawing.compare(e.getValue(), true))
                            .collect(Collectors.joining(" ; "));
                    getMinecraft().player.sendMessage(new TextComponent(s), Util.NIL_UUID);
                }
                if (savedDrawing == null) {
                    savedDrawing = drawing;
                } else {
                    //getMinecraft().player.sendMessage(new TextComponent((int) drawing.compare(savedDrawing, false) + " ; " + (int) drawing.compare(savedDrawing, true)), Util.NIL_UUID);
                }
                //System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(drawing));
                //PacketHandler.INSTANCE.sendToServer(new PacketHandler.M(PacketHandler.M.Action.CREATE_DRAW, "ga", drawing));
            } else {
                getMinecraft().player.sendMessage(new TextComponent("Invlaid Draw"), Util.NIL_UUID);
            }
            drawing = null;
        }
        return res;
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        if (drawing != null) {
            drawing.addPoint(mouseX, mouseY);
        }
    }

    @Override
    public void tick() {
        //TODO if certain key is not pressed, close window
    }
}
