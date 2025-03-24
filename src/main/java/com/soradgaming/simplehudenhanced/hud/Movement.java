package com.soradgaming.simplehudenhanced.hud;

import com.soradgaming.simplehudenhanced.cache.MovementCache;
import com.soradgaming.simplehudenhanced.config.SimpleHudEnhancedConfig;
import com.soradgaming.simplehudenhanced.utli.Utilities;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.joml.Quaternionf;

public class Movement {
    private final MinecraftClient client;
    private final TextRenderer renderer;
    private final SimpleHudEnhancedConfig config;
    private final DrawContext context;
    private final MovementCache movementCache;

    public Movement(DrawContext context, SimpleHudEnhancedConfig config, MovementCache movementCache) {
        this.client = MinecraftClient.getInstance();
        this.renderer = client.textRenderer;
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
    private void draw(DrawContext context, String textKey) {
        final String text = Utilities.translatable(textKey).getString();
        float Scale = (float) config.movementStatus.textScale / 100;

        // Screen Manager
        ScreenManager screenManager = new ScreenManager(this.client.getWindow().getScaledWidth(), this.client.getWindow().getScaledHeight());
        screenManager.setPadding(4);
        int xAxis = screenManager.calculateXAxis(config.movementStatus.movementStatusLocationX, Scale, this.renderer.getWidth(text));
        int yAxis = screenManager.calculateYAxis(this.renderer.fontHeight, 1, config.movementStatus.movementStatusLocationY, Scale);
        screenManager.setScale(context, Scale);

        // Draw Info
        context.drawTextWithShadow(this.renderer, text, xAxis, yAxis, config.uiConfig.textColor);

        screenManager.resetScale(context);
    }

    // Draw the Paper Doll
    public void drawPaperDoll(DrawContext context) {
        if (!config.paperDoll.togglePaperDoll) {
            return;
        }

        // Get Player Entity
        PlayerEntity entity = this.client.player;
        if (entity == null) {
            return;
        }

        // Config
        ScreenManager screenManager = new ScreenManager(this.client.getWindow().getScaledWidth(), this.client.getWindow().getScaledHeight());
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
    private void drawEntity(DrawContext context, int xAxis, int yAxis, float size, LivingEntity entity) {
        // Setup Matrix
        context.getMatrices().push();
        context.getMatrices().translate(xAxis, yAxis, 250.0);
        context.getMatrices().scale(size, size, -size);
        Quaternionf quaternionZ = new Quaternionf().rotateZ(180.0F * 0.017453292F);
        Quaternionf quaternionX = new Quaternionf().rotateX(15.0F * 0.017453292F);
        quaternionZ.mul(quaternionX);
        context.getMatrices().multiply(quaternionZ);

        // Setup Environment
        DiffuseLighting.method_34742();
        EntityRenderDispatcher entityRenderDispatcher = MinecraftClient.getInstance().getEntityRenderDispatcher();

        // Save Rotation
        float xRot = entity.getPitch();
        float yBodyRot = entity.bodyYaw;
        float yHeadRot = entity.headYaw;
        float xRotO = entity.prevPitch;
        float yBodyRotO = entity.prevBodyYaw;
        float yHeadRotO = entity.prevHeadYaw;

        // Modify Rotation
        applyEntityRotations(entity);

        // Disable Shadows
        entityRenderDispatcher.setRenderShadows(false);

        // Render Entity
        float xOffset = 0;
        float yOffset = (entity.getHeight() + (1.0F - movementCache.getCurrentHeightOffset())) * -0.5F;

        entityRenderDispatcher.render(entity, xOffset, yOffset, 0.0, 0.0F, 1.0F, this.context.getMatrices(), context.getVertexConsumers(), 15728880);
        context.draw();

        // Restore Rotation
        entity.setPitch(xRot);
        entity.bodyYaw = yBodyRot;
        entity.headYaw = yHeadRot;
        entity.prevPitch = xRotO;
        entity.prevBodyYaw = yBodyRotO;
        entity.prevHeadYaw = yHeadRotO;

        // Reset Environment
        entityRenderDispatcher.setRenderShadows(true);
        context.getMatrices().pop();
        DiffuseLighting.enableGuiDepthLighting();
    }

    private void applyEntityRotations(LivingEntity entity) {
        // TODO Config
        if (this.config.paperDoll.paperDollLocationY >= 50) {
            entity.setPitch(-7.5F);
            entity.prevPitch = -7.5F;
        } else {
            entity.setPitch(7.5F);
            entity.prevPitch = 7.5F;
        }

        float defaultRotationYaw = 180.0F;
        if (this.config.paperDoll.paperDollLocationX >= 50) {
            defaultRotationYaw += 20.0F;
        } else {
            defaultRotationYaw -= 20.0F;
        }

        float yRotOffset = 0;
        float yRotOffsetO = 0;

        entity.bodyYaw = entity.prevBodyYaw = defaultRotationYaw;
        entity.prevHeadYaw = defaultRotationYaw + yRotOffsetO;
        entity.headYaw = defaultRotationYaw + yRotOffset;
    }
}