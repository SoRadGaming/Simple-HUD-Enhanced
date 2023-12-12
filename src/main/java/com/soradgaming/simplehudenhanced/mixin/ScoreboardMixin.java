package com.soradgaming.simplehudenhanced.mixin;

import com.soradgaming.simplehudenhanced.hud.Scoreboard;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;

@Mixin(InGameHud.class)
public class ScoreboardMixin {
    @Shadow
    private int scaledWidth;
    @Shadow private int scaledHeight;
    @Unique
    private int xShift;
    // Config shit



    // Injected Matrix or Cancel Render
    @Inject(method = "renderScoreboardSidebar", at = @At("HEAD"), cancellable = true)
    private void uglyscoreboardfix$hideOrScale(DrawContext context, ScoreboardObjective objective, CallbackInfo ci) {
        if (Scoreboard.getInstance().hide(objective)) {
            ci.cancel();
            return;
        }
        context.getMatrices().push();
        float scale = 1;
        context.getMatrices().scale(scale, scale, scale);
        scaledWidth *= (int) (1 / scale);
        scaledHeight *= (int) (1 / scale);
    }

//    @ModifyVariable(method = "renderScoreboardSidebar", at = @At(value = "STORE", ordinal = 0), ordinal = 0)
//    private String uglyscoreboardfix$modifyScore(String score, DrawContext context, ScoreboardObjective objective) {
//        return ModConfig.Sidebar.Hiding.hide(HidePart.SCORES, objective) ? "" : score;
//    }
//
//    @ModifyVariable(method = "renderScoreboardSidebar", at = @At(value = "STORE", ordinal = 0), ordinal = 2)
//    private int uglyscoreboardfix$modifySeperatorWidth(int seperatorWidth, DrawContext context, ScoreboardObjective objective) {
//        return ModConfig.Sidebar.Hiding.hide(HidePart.SCORES, objective) ? 0 : seperatorWidth;
//    }
//
//    @Redirect(method = "renderScoreboardSidebar", slice = @Slice(from = @At(value = "INVOKE", target = "Ljava/util/Iterator;hasNext()Z")), at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;getWidth(Ljava/lang/String;)I", ordinal = 0))
//    private int uglyscoreboardfix$modifyScoreWidth(TextRenderer textRenderer, String score, DrawContext context, ScoreboardObjective objective) {
//        return ModConfig.Sidebar.Hiding.hide(HidePart.SCORES, objective) ? 0 : textRenderer.getWidth(score);
//    }
//
//    @ModifyVariable(method = "renderScoreboardSidebar", at = @At(value = "STORE", ordinal = 0), ordinal = 5)
//    private int uglyscoreboardfix$modifyX1(int x1) {
//        if (ModConfig.Sidebar.Position.getX() == HorizontalPosition.LEFT) {
//            xShift = x1 - 1;
//            return 1;
//        }
//        return x1;
//    }

//    @ModifyVariable(method = "renderScoreboardSidebar", at = @At(value = "STORE", ordinal = 0), ordinal = 11)
//    private int uglyscoreboardfix$modifyX2(int x2) {
//        if (ModConfig.Sidebar.Position.getX() == HorizontalPosition.LEFT) {
//            return x2 - xShift;
//        }
//        return x2;
//    }
//
//    @ModifyVariable(method = "renderScoreboardSidebar", at = @At(value = "STORE", ordinal = 0), ordinal = 3)
//    private int uglyscoreboardfix$modifyY(int y) {
//        return MathHelper.clamp(y + ModConfig.Sidebar.Position.getYOffset(), (y - scaledHeight / 2) * 3 + 9 + 1, scaledHeight - 1);
//    }
//
//    @ModifyVariable(method = "renderScoreboardSidebar", at = @At(value = "STORE", ordinal = 0), ordinal = 8)
//    private int uglyscoreboardfix$modifyHeadingBackgroundColor(int color) {
//        return ModConfig.Sidebar.Background.getHeadingColor();
//    }
//
//    @ModifyVariable(method = "renderScoreboardSidebar", at = @At(value = "STORE", ordinal = 0), ordinal = 7)
//    private int uglyscoreboardfix$modifyBackgroundColor(int color) {
//        return ModConfig.Sidebar.Background.getColor();
//    }
//
//    @Redirect(method = "renderScoreboardSidebar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawText(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;IIIZ)I", ordinal = 1))
//    private int uglyscoreboardfix$drawHeadingText(DrawContext context, TextRenderer textRenderer, Text text, int x, int y, int color, boolean shadow) {
//        color = ModConfig.Sidebar.Text.getHeadingColor().getRgb();
//        return context.drawText(textRenderer, text, x, y, color, ModConfig.Sidebar.Text.isHeadingShadow());
//    }
//
//    @Redirect(method = "renderScoreboardSidebar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawText(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;IIIZ)I", ordinal = 0))
//    private int uglyscoreboardfix$drawText(DrawContext context, TextRenderer textRenderer, Text text, int x, int y, int color, boolean shadow) {
//        color = ModConfig.Sidebar.Text.getColor().getRgb();
//        return context.drawText(textRenderer, text, x, y, color, ModConfig.Sidebar.Text.isShadow());
//    }
//
//    @Redirect(method = "renderScoreboardSidebar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawText(Lnet/minecraft/client/font/TextRenderer;Ljava/lang/String;IIIZ)I", ordinal = 0))
//    private int uglyscoreboardfix$drawScoreText(DrawContext context, TextRenderer textRenderer, String text, int x, int y, int color, boolean shadow) {
//        text = Formatting.strip(text);
//        color = ModConfig.Sidebar.Text.getScoreColor().getRgb();
//        return context.drawText(textRenderer, text, x, y, color, ModConfig.Sidebar.Text.isScoreShadow());
//    }
//
//    @ModifyConstant(method = "renderScoreboardSidebar", constant = @Constant(intValue = 15))
//    private int uglyscoreboardfix$modifyMaxLineCount(int maxLineCount) {
//        return ModConfig.Sidebar.getMaxLineCount();
//    }
//
//    @Redirect(method = "renderScoreboardSidebar", at = @At(value = "INVOKE", target = "Ljava/util/Collection;size()I", ordinal = 3))
//    private int uglyscoreboardfix$hideTitle(Collection<ScoreboardPlayerScore> collection) {
//        if (ModConfig.Sidebar.Hiding.hideTitle()) {
//            return 0;
//        }
//        return collection.size();
//    }


    // Remove Injected Matrix
    @Inject(method = "renderScoreboardSidebar", at = @At("TAIL"))
    private void uglyscoreboardfix$pop(DrawContext context, ScoreboardObjective objective, CallbackInfo ci) {
        context.getMatrices().pop();
        float scale = 1;
        scaledWidth *= (int) scale;
        scaledHeight *= (int) scale;
    }
}
