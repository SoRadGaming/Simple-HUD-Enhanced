package com.soradgaming.simplehudenhanced.hud;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ScreenManager {
    private final int screenWidth;
    private final int screenHeight;
    private int padding;

    public ScreenManager(int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.padding = 0;
    }

    public void setPadding(int padding) {
        this.padding = padding;
    }

    public int calculateXAxis(int configX, double scale, int lineWidth) {
        int adjustedWidth = (int) Math.round(screenWidth - (padding) - (lineWidth * scale));
        int calculatedValue = (int) Math.round(adjustedWidth / scale * configX / 100);

        if (scale < 1) {
            int adjustedScreenWidth = (int) Math.round(screenWidth * scale);
            int maximumX = padding;
            int minimumX = Math.min(screenWidth - adjustedScreenWidth - padding, maximumX);
            return Math.max(Math.max(calculatedValue, minimumX), maximumX);
        } else {
            int adjustedLineWidth = (int) Math.round(lineWidth * scale);
            return Math.min(Math.max(calculatedValue, padding), screenWidth - padding - adjustedLineWidth);
        }
    }

    public int calculateYAxis(int lineHeight, int size, int configY, double scale) {
        int adjustedHeight = (int) Math.round(screenHeight - padding - (lineHeight * size) * scale);
        int calculatedValue = (int) Math.round(adjustedHeight / scale * configY / 100);

        if (scale < 1) {
            return Math.max(calculatedValue, padding);
        } else {
            int adjustedLineHeight = (int) Math.round((lineHeight * size) * scale);
            return Math.min(Math.max(calculatedValue, padding), screenHeight - adjustedLineHeight - padding);
        }
    }

    public void setScale(MatrixStack matrix, float scale) {
        // Change Matrix Stack to draw on the screen
        matrix.push();
        matrix.scale(scale, scale, scale);
    }

    public void resetScale(MatrixStack matrix) {
        // Change Matrix Stack back to normal
        matrix.pop();
    }

    private float lastZ = 0;

    public void zSet(MatrixStack matrix, float z) {
        // Change Matrix Stack to draw on the screen
        lastZ = z;
        matrix.translate(0.0D, 0.0D, z);
    }

    public void zRevert(MatrixStack matrix) {
        // Change Matrix Stack back to normal
        matrix.translate(0.0D, 0.0D, -lastZ);
    }

    // Custom Icon Scaling (Bruh this is just Minecraft 1.19.4 Code)
    public void renderInGuiWithOverrides(MatrixStack matrices, ItemStack stack, int x, int y) {
        this.innerRenderInGui(matrices, MinecraftClient.getInstance().player, MinecraftClient.getInstance().world, stack, x, y, 0);
    }

    private void innerRenderInGui(MatrixStack matrices, @Nullable LivingEntity entity, @Nullable World world, ItemStack stack, int x, int y, int seed) {
        this.innerRenderInGui(matrices, entity, world, stack, x, y, seed, 0);
    }

    private void innerRenderInGui(MatrixStack matrices, @Nullable LivingEntity entity, @Nullable World world, ItemStack stack, int x, int y, int seed, int depth) {
        if (!stack.isEmpty()) {
            BakedModel bakedModel =  MinecraftClient.getInstance().getItemRenderer().getHeldItemModel(stack, world, entity, seed);
            matrices.push();
            matrices.translate(0.0F, 0.0F, (float)(50 + (bakedModel.hasDepth() ? depth : 0)));

            try {
                this.renderGuiItemModel(matrices, stack, x, y, bakedModel);
            } catch (Throwable var11) {
                CrashReport crashReport = CrashReport.create(var11, "Rendering item");
                CrashReportSection crashReportSection = crashReport.addElement("Item being rendered");
                crashReportSection.add("Item Type", () -> {
                    return String.valueOf(stack.getItem());
                });
                crashReportSection.add("Item Damage", () -> {
                    return String.valueOf(stack.getDamage());
                });
                crashReportSection.add("Item NBT", () -> {
                    return String.valueOf(stack.getTag());
                });
                crashReportSection.add("Item Foil", () -> {
                    return String.valueOf(stack.hasGlint());
                });
                throw new CrashException(crashReport);
            }


            matrices.pop();
        }
    }

    protected void renderGuiItemModel(MatrixStack matrices, ItemStack stack, int x, int y, BakedModel model) {
        matrices.push();
        matrices.translate((float)x, (float)y, 100.0F);
        matrices.translate(8.0F, 8.0F, 0.0F);
        matrices.peek().getModel().multiply(Matrix4f.scale(1.0F, -1.0F, 1.0F));
        matrices.scale(16.0F, 16.0F, 16.0F);
        VertexConsumerProvider.Immediate immediate = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
        boolean bl = !model.isSideLit();
        if (bl) {
            DiffuseLighting.disableGuiDepthLighting();
        }

        MatrixStack matrixStack = RenderSystem.getModelViewStack();
        matrixStack.push();
        matrixStack.peek().getModel().multiply(matrices.peek().getModel());
        RenderSystem.applyModelViewMatrix();
        MinecraftClient.getInstance().getItemRenderer().renderItem(stack, ModelTransformation.Mode.GUI, false, new MatrixStack(), immediate, 15728880, OverlayTexture.DEFAULT_UV, model);
        immediate.draw();
        RenderSystem.enableDepthTest();
        if (bl) {
            DiffuseLighting.enableGuiDepthLighting();
        }

        matrices.pop();
        matrixStack.pop();
        RenderSystem.applyModelViewMatrix();
    }
}
