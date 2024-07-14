package com.soradgaming.simplehudenhanced.hud;

import com.soradgaming.simplehudenhanced.config.SimpleHudEnhancedConfig;
import com.soradgaming.simplehudenhanced.utli.Utilities;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3f;

public class Movement {
    private final MinecraftClient client;
    private final TextRenderer renderer;
    private final SimpleHudEnhancedConfig config;
    private final MatrixStack matrixStack;

    public Movement(MatrixStack matrixStack, SimpleHudEnhancedConfig config) {
        this.client = MinecraftClient.getInstance();
        this.renderer = client.textRenderer;
        this.config = config;
        this.matrixStack = matrixStack;
    }

    public void init(GameInfo GameInformation) {
        if (config.movementStatus.movementTypes.toggleSwimmingStatus && GameInformation.isPlayerSwimming()) {
            draw("text.hud.simplehudenhanced.swimming");
        } else if (config.movementStatus.movementTypes.toggleFlyingStatus && GameInformation.isPlayerFlying()) {
            draw("text.hud.simplehudenhanced.flying");
        } else if (config.movementStatus.movementTypes.toggleSneakStatus && GameInformation.isPlayerSneaking()) {
            draw("text.hud.simplehudenhanced.sneaking");
        } else if (config.movementStatus.movementTypes.toggleSprintStatus && GameInformation.isPlayerSprinting()) {
            draw("text.hud.simplehudenhanced.sprinting");
        }
    }

    // Draw the movement status on the screen
    private void draw(String textKey) {
        final String text = Utilities.translatable(textKey).getString();
        float Scale = (float) config.movementStatus.textScale / 100;

        // Screen Manager
        ScreenManager screenManager = new ScreenManager(this.client.getWindow().getScaledWidth(), this.client.getWindow().getScaledHeight());
        screenManager.setPadding(4);
        int xAxis = screenManager.calculateXAxis(config.movementStatus.movementStatusLocationX, Scale, this.renderer.getWidth(text));
        int yAxis = screenManager.calculateYAxis(this.renderer.fontHeight, 1, config.movementStatus.movementStatusLocationY, Scale);
        screenManager.setScale(matrixStack, Scale);

        // Draw Info
        this.renderer.drawWithShadow(this.matrixStack, text, xAxis, yAxis, config.uiConfig.textColor);

        screenManager.resetScale(matrixStack);
    }

    // Draw the Paper Doll
    public void drawPaperDoll(MatrixStack matrixStack) {
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
        drawEntity(matrixStack, xAxis, yAxis, size, entity);
    }

    // InventoryScreen.java
    private void drawEntity(MatrixStack matrixStack, int xAxis, int yAxis, float size, LivingEntity entity) {
        // Setup Matrix
        matrixStack.push();
        matrixStack.translate(xAxis, yAxis, 250.0);
        matrixStack.scale(size, size, -size);

        Quaternion quaternionZ = Vec3f.POSITIVE_Z.getDegreesQuaternion(180.0F);
        Quaternion quaternionX = Vec3f.POSITIVE_X.getDegreesQuaternion(20.0F);
        quaternionZ.hamiltonProduct(quaternionX);
        matrixStack.multiply(quaternionZ);

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

        // Setup Vertex Consumer
        VertexConsumerProvider.Immediate immediate = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();

        // Render Entity
        float xOffset = 0;
        float yOffset = (entity.getHeight() + (1.0F - getCurrentHeightOffset(entity))) * -0.5F;

        entityRenderDispatcher.render(entity, xOffset, yOffset, 0.0, 0.0F, 1.0F, matrixStack, immediate, 15728880);
        immediate.draw();

        // Restore Rotation
        entity.setPitch(xRot);
        entity.bodyYaw = yBodyRot;
        entity.headYaw = yHeadRot;
        entity.prevPitch = xRotO;
        entity.prevBodyYaw = yBodyRotO;
        entity.prevHeadYaw = yHeadRotO;

        // Reset Environment
        entityRenderDispatcher.setRenderShadows(true);
        matrixStack.pop();
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

    private static float getCurrentHeightOffset(LivingEntity player) {
        // Crouching check after Elytra since you can do both at the same time
        float height = player.getEyeHeight(EntityPose.STANDING);
        if (player.isFallFlying()) {
            float ticksElytraFlying = player.fallDistance + (float) 1.0;
            float flyingAnimation = MathHelper.clamp(ticksElytraFlying * 0.09F, 0.0F, 1.0F);
            float flyingHeight = player.getEyeHeight(EntityPose.FALL_FLYING) / height;
            return MathHelper.lerp(flyingAnimation, 1.0F, flyingHeight);
        } else if (player.isSwimming()) {
            float swimmingAnimation = player.isInSwimmingPose() ? 1.0F : player.handSwingProgress;
            float swimmingHeight = player.getEyeHeight(EntityPose.SWIMMING) / height;
            return MathHelper.lerp(swimmingAnimation, 1.0F, swimmingHeight);
        } else if (player.isUsingRiptide()) {
            return player.getEyeHeight(EntityPose.SPIN_ATTACK) / height;
        } else if (player.isSneaking()) {
            return player.getEyeHeight(EntityPose.CROUCHING) / height;
        } else if (player.isSleeping()) {
            return player.getEyeHeight(EntityPose.SLEEPING) / height;
        } else if (player.deathTime > 0) {
            float dyingAnimation = ((float) player.deathTime + (float) 1.0 - 1.0F) / 20.0F * 1.6F;
            dyingAnimation = Math.min(1.0F, MathHelper.sqrt(dyingAnimation));
            float dyingHeight = player.getEyeHeight(EntityPose.DYING) / height;
            return MathHelper.lerp(dyingAnimation, 1.0F, dyingHeight);
        } else {
            return 1.0F;
        }
    }
}