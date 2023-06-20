package com.soradgaming.simplehudenhanced.hud;

import net.minecraft.item.ItemStack;

public class EquipmentInfoStack {
    private final ItemStack item;
    private int color;
    private String text;

    public EquipmentInfoStack(ItemStack item) {
        this.item = item;
        this.text = "";
        this.color = 0x00E0E0E0;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setText(String text) {
        this.text = text;
    }

    public ItemStack getItem() {
        return item;
    }

    public int getColor() {
        return color;
    }

    public String getText() {
        return text;
    }
}
