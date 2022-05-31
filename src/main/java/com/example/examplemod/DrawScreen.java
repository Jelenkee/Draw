package com.example.examplemod;

import net.minecraft.Util;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.client.gui.GuiUtils;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.awt.geom.Point2D;

public class DrawScreen extends Screen {
    private Drawing drawing = null;
    private Drawing savedDrawing = null;
    private final boolean createMode;

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
                GuiUtils.drawGradientRect(poseStack.last().pose(), -1, (int) (point.getX() - 2), (int) (point.getY() - 2), (int) (point.getX() + 2), (int) (point.getY() + 2), Color.YELLOW.getRGB(), Color.YELLOW.getRGB());
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
        if (GLFW.GLFW_MOUSE_BUTTON_1 == key) {
            drawing.addPoint(mouseX, mouseY);
            if (drawing.valid()) {
                drawing.finish();
                if (createMode) {
                    //TODO persist drawing
                } else {
                    //TODO check for match
                }
                if (savedDrawing == null) {
                    savedDrawing = drawing;
                } else {
                    System.out.println("score: " + drawing.compare(savedDrawing));
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
