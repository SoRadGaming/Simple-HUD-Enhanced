package com.soradgaming.simplehudenhanced.hud;

import com.soradgaming.simplehudenhanced.cache.MovementCache;
import com.soradgaming.simplehudenhanced.config.SimpleHudEnhancedConfig;
import com.soradgaming.simplehudenhanced.utli.Utilities;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import static com.soradgaming.simplehudenhanced.utli.Utilities.addAlpha;
import static net.minecraft.client.gui.screen.ingame.InventoryScreen.drawEntity;

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
        context.drawTextWithShadow(this.renderer, text, xAxis, yAxis, addAlpha(config.uiConfig.textColor));

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
        int x1 = Math.round((xAxis - (60 * scale)));
        int y1 = Math.round((yAxis - (60 * scale)));
        int x2 = Math.round((xAxis + (60 * scale)));
        int y2 = Math.round((yAxis + (60 * scale)));
        drawEntityInternal(context, x1, y1, x2, y2, size, entity);
    }

    // Custom method for rendering the paper doll. FROM InventoryScreen.java
    private void drawEntityInternal(DrawContext context, int x1, int y1, int x2, int y2, float size, LivingEntity entity) {
        // --- Calculate Scissor Area ---
        context.enableScissor(x1, y1, x2, y2);
        Quaternionf quaternionZ = new Quaternionf().rotateZ(180.0F * 0.017453292F);
        Quaternionf quaternionX = new Quaternionf().rotateX(15.0F * 0.017453292F);
        quaternionZ.mul(quaternionX);

        // --- Save Entity's Original Rotation State ---
        // This is crucial so the paper doll rendering doesn't affect the main player model.
        float originalPitch = entity.getPitch();
        float originalBodyYaw = entity.bodyYaw;
        float originalHeadYaw = entity.headYaw;
        float originalPrevPitch = entity.lastPitch;
        float originalPrevBodyYaw = entity.lastBodyYaw;
        float originalPrevHeadYaw = entity.lastHeadYaw;

        // --- Modify Entity's Rotation for Paper Doll Display ---
        applyEntityRotations(entity);

        // --- Calculate Entity Position and Scale ---
        float yOffset = (entity.getHeight() + (1.0F - movementCache.getCurrentHeightOffset())) * 0.5F;
        Vector3f vector3f = new Vector3f(0.0F, yOffset, 0.0F);
        drawEntity(context, x1, y1, x2, y2, size, vector3f, quaternionZ, quaternionX, entity);

        // --- Restore Entity's Original Rotation State ---
        entity.setPitch(originalPitch);
        entity.bodyYaw = originalBodyYaw;
        entity.headYaw = originalHeadYaw;
        entity.lastPitch = originalPrevPitch;
        entity.lastBodyYaw = originalPrevBodyYaw;
        entity.lastHeadYaw = originalPrevHeadYaw;
        context.disableScissor();
    }

    private void applyEntityRotations(LivingEntity entity) {
        // TODO Config
        if (this.config.paperDoll.paperDollLocationY >= 50) {
            entity.setPitch(-7.5F);
            entity.lastPitch = -7.5F;
        } else {
            entity.setPitch(7.5F);
            entity.lastPitch = 7.5F;
        }

        float defaultRotationYaw = 180.0F;
        if (this.config.paperDoll.paperDollLocationX >= 50) {
            defaultRotationYaw += 20.0F;
        } else {
            defaultRotationYaw -= 20.0F;
        }

        float yRotOffset = 0;
        float yRotOffsetO = 0;

        entity.bodyYaw = entity.lastBodyYaw = defaultRotationYaw;
        entity.lastHeadYaw = defaultRotationYaw + yRotOffsetO;
        entity.headYaw = defaultRotationYaw + yRotOffset;
    }
}