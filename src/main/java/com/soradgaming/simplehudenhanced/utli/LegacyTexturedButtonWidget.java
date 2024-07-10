package com.soradgaming.simplehudenhanced.utli;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class LegacyTexturedButtonWidget extends TexturedButtonWidget {
    private final int u;
    private final int v;
    private final int hoveredVOffset;

    private final Identifier texture;

    private final int textureWidth;
    private final int textureHeight;

    public LegacyTexturedButtonWidget(int x, int y, int width, int height, int u, int v, int hoveredVOffset, Identifier texture, int textureWidth, int textureHeight, ButtonWidget.PressAction pressAction, Text message) {
        super(x, y, width, height, null, pressAction, message);

        this.u = u;
        this.v = v;
        this.hoveredVOffset = hoveredVOffset;

        this.texture = texture;

        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
    }

    @Override
    public void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
        int v = this.v;

        if (!this.isNarratable()) {
            v += this.hoveredVOffset * 2;
        } else if (this.isSelected()) {
            v += this.hoveredVOffset;
        }

        context.drawTexture(this.texture, this.getX(), this.getY(), this.u, v, this.width, this.height, this.textureWidth, this.textureHeight);
    }
}