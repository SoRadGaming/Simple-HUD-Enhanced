package com.soradgaming.simplehudenhanced.hud;

import com.soradgaming.simplehudenhanced.config.SimpleHudEnhancedConfig;
import com.soradgaming.simplehudenhanced.debugStatus.DebugStatus;
import net.minecraft.client.MinecraftClient;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;

public class Scoreboard {
    private static Scoreboard instance;  // Singleton instance
    private final SimpleHudEnhancedConfig config;

    private Scoreboard(SimpleHudEnhancedConfig config) {
        this.config = config;
    }

    // Initialization method (called once)
    public static void initialize(SimpleHudEnhancedConfig config) {
        if (instance == null) {
            instance = new Scoreboard(config);
        } else {
            throw new IllegalStateException("Scoreboard has already been initialized.");
        }
    }

    // Singleton instance getter
    public static Scoreboard getInstance() {
        if (instance == null) {
            throw new IllegalStateException("Scoreboard has not been initialized yet.");
        }
        return instance;
    }

    // Methods
    public boolean hide(ScoreboardObjective objective) {
        if ((DebugStatus.getDebugStatus() && !MinecraftClient.getInstance().options.hudHidden) || config.toggleScoreboard) {
            return true;
        }
        return test(objective);

    }

    public boolean test(ScoreboardObjective objective) {
        // Crash HERE
        int[] scores = objective.getScoreboard().getAllPlayerScores(objective).stream().mapToInt(ScoreboardPlayerScore::getScore).toArray();
        if (scores.length >= 2) {
            for (int i = 1; i < scores.length; i++) {
                if (scores[i - 1] + 1 != scores[i]) {
                    return false;
                }
            }
        }
        return true;
    }

    public SimpleHudEnhancedConfig getConfig() {
        return config;
    }
}
