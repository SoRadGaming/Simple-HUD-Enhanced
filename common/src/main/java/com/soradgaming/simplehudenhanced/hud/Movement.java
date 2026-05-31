package com.soradgaming.simplehudenhanced.hud;

import com.mojang.blaze3d.platform.Lighting;
import com.soradgaming.simplehudenhanced.cache.MovementCache;
import com.soradgaming.simplehudenhanced.config.SimpleHudEnhancedConfig;
import com.soradgaming.simplehudenhanced.utli.Utilities;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.joml.Quaternionf;

public class Movement {
    private final Minecraft client;
    private final Font renderer;
    private final SimpleHudEnhancedConfig config;
    private final GuiGraphics context;
    private final MovementCache movementCache;

    public Movement(GuiGraphics context, SimpleHudEnhancedConfig config, MovementCache movementCache) {
        this.client = Minecraft.getInstance();
        this.renderer = client.font;
        this.config = config;
        this.context = context;
        this.movementCache = movementCache;
    }

    public void init(GameInfo GameInformation) {
        if (config.movementStatus.movementTypes.toggleSwimmingStatus && GameInformation.isPlayerSwimming()) {
            draw(context, "text.hud.simplehudenhanced.swimming");
        } else if (config.movementStatus.movementTypes.toggleFlyingStatus && GameInformation.isPlayerFlying()) {
            draw(context, "text.hud.simplehudenhanced.flying");
        } else if (config.movementStatus.movementTypes.toggleSneakStatus && GameInformation.isPlayerSneaking()) {
            draw(context, "text.hud.simplehudenhanced.sneaking");
        } else if (config.movementStatus.movementTypes.toggleSprintStatus && GameInformation.isPlayerSprinting()) {
            draw(context, "text.hud.simplehudenhanced.sprinting");
        }
    }

    // Draw the movement status on the screen
    private void draw(GuiGraphics context, String textKey) {
        final String text = Utilities.translatable(textKey).getString();
        float Scale = (float) config.movementStatus.textScale / 100;

        // Screen Manager
        ScreenManager screenManager = new ScreenManager(this.client.getWindow().getGuiScaledWidth(), this.client.getWindow().getGuiScaledHeight());
        screenManager.setPadding(4);
        int xAxis = screenManager.calculateXAxis(config.movementStatus.movementStatusLocationX, Scale, this.renderer.width(text));
        int yAxis = screenManager.calculateYAxis(this.renderer.lineHeight, 1, config.movementStatus.movementStatusLocationY, Scale);
        screenManager.setScale(context, Scale);

        // Draw Info
        context.drawString(this.renderer, text, xAxis, yAxis, config.uiConfig.textColor);

        screenManager.resetScale(context);
    }

    // Draw the Paper Doll
    public void drawPaperDoll(GuiGraphics context) {
        if (!config.paperDoll.togglePaperDoll) {
            return;
        }

        // Get Player Entity
        Player entity = this.client.player;
        if (entity == null) {
            return;
        }

        // Config
        ScreenManager screenManager = new ScreenManager(this.client.getWindow().getGuiScaledWidth(), this.client.getWindow().getGuiScaledHeight());
        float scale = (float) config.paperDoll.textScale / 100;
        float size = 20 * scale;

        screenManager.setPadding((int) (26 * scale));
        int xAxis = screenManager.calculateXAxis(this.config.paperDoll.paperDollLocationX, 1, 0);
        screenManager.setPadding((int) (26 * scale));
        int yAxis = screenManager.calculateYAxis(0, 1, this.config.paperDoll.paperDollLocationY, 1);

        // Draw the Paper Doll
        drawEntity(context, xAxis, yAxis, size, entity);
    }

    // InventoryScreen.java
    private void drawEntity(GuiGraphics context, int xAxis, int yAxis, float size, LivingEntity entity) {
        // Setup Matrix
        context.pose().pushPose();
        context.pose().translate(xAxis, yAxis, 250.0);
        context.pose().scale(size, size, -size);
        Quaternionf quaternionZ = new Quaternionf().rotateZ(180.0F * 0.017453292F);
        Quaternionf quaternionX = new Quaternionf().rotateX(15.0F * 0.017453292F);
        quaternionZ.mul(quaternionX);
        context.pose().mulPose(quaternionZ);

        // Setup Environment
        Lighting.setupForEntityInInventory();
        EntityRenderDispatcher entityRenderDispatcher = Minecraft.getInstance().getEntityRenderDispatcher();

        // Save Rotation
        float xRot = entity.getXRot();
        float yBodyRot = entity.yBodyRot;
        float yHeadRot = entity.yHeadRot;
        float xRotO = entity.xRotO;
        float yBodyRotO = entity.yBodyRotO;
        float yHeadRotO = entity.yHeadRotO;

        // Modify Rotation
        applyEntityRotations(entity);

        // Disable Shadows
        entityRenderDispatcher.setRenderShadow(false);

        // Render Entity
        float xOffset = 0;
        float yOffset = (entity.getBbHeight() + (1.0F - movementCache.getCurrentHeightOffset())) * -0.5F;

        entityRenderDispatcher.render(entity, xOffset, yOffset, 0.0, 0.0F, 1.0F, this.context.pose(), context.bufferSource(), 15728880);
        context.flush();

        // Restore Rotation
        entity.setXRot(xRot);
        entity.yBodyRot = yBodyRot;
        entity.yHeadRot = yHeadRot;
        entity.xRotO = xRotO;
        entity.yBodyRotO = yBodyRotO;
        entity.yHeadRotO = yHeadRotO;

        // Reset Environment
        entityRenderDispatcher.setRenderShadow(true);
        context.pose().popPose();
        Lighting.setupFor3DItems();
    }

    private void applyEntityRotations(LivingEntity entity) {
        // TODO Config
        if (this.config.paperDoll.paperDollLocationY >= 50) {
            entity.setXRot(-7.5F);
            entity.xRotO = -7.5F;
        } else {
            entity.setXRot(7.5F);
            entity.xRotO = 7.5F;
        }

        float defaultRotationYaw = 180.0F;
        if (this.config.paperDoll.paperDollLocationX >= 50) {
            defaultRotationYaw += 20.0F;
        } else {
            defaultRotationYaw -= 20.0F;
        }

        float yRotOffset = 0;
        float yRotOffsetO = 0;

        entity.yBodyRot = entity.yBodyRotO = defaultRotationYaw;
        entity.yHeadRotO = defaultRotationYaw + yRotOffsetO;
        entity.yHeadRot = defaultRotationYaw + yRotOffset;
    }
}