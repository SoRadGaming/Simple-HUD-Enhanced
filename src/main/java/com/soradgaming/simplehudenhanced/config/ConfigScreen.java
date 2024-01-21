package com.soradgaming.simplehudenhanced.config;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class ConfigScreen extends Screen {
    private final Screen parent;

    protected ConfigScreen(Screen parent) {
        super(Text.literal("Simple HUD Enhanced"));
        this.parent = parent;
    }

    // ModMenuIntegration.java: return parent -> ConfigScreen.getScreen(parent);
    public static Screen getScreen(Screen parent) {
        return new ConfigScreen(parent);
    }


    // Config Code


    // Elements
    public ButtonWidget button1;
    public ButtonWidget button2;



    @Override
    protected void init() {
        // Create the Buttons
        button1 = ButtonWidget.builder(Text.literal("Button 1"), button -> System.out.println("You clicked button1!"))
                .dimensions(width / 2 - 205, 20, 200, 20)
                .tooltip(Tooltip.of(Text.literal("Tooltip of button1")))
                .build();
        button2 = ButtonWidget.builder(Text.literal("Button 2"), button -> System.out.println("You clicked button2!"))
                .dimensions(width / 2 + 5, 20, 200, 20)
                .tooltip(Tooltip.of(Text.literal("Tooltip of button2")))
                .build();

        // Position the buttons
        button1.setPosition(width / 2 - 205, 20);
        button2.setPosition(width / 2 + 5, 20);

        // Add the buttons to the screen
        addDrawableChild(button1);
        addDrawableChild(button2);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(textRenderer, Text.literal("You must see me"), width / 2, height / 2, 0xffffff);
    }

    @Override
    public void close() {
        if (client != null) {
            client.setScreen(parent);
        }
    }
}

