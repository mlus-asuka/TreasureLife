package cn.mlus.treasurelife;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

@EventBusSubscriber
public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    // Life system configuration
    private static final ModConfigSpec.IntValue INITIAL_LIVES = BUILDER.comment("Initial lives for new players").defineInRange("initialLives", 10, 1, 100);
    private static final ModConfigSpec.IntValue MAX_LIVES = BUILDER.comment("Maximum lives a player can have").defineInRange("maxLives", 20, 1, 100);
    private static final ModConfigSpec.BooleanValue ENABLE_DAILY_CLAIM = BUILDER.comment("Enable daily life claim on login").define("enableDailyClaim", true);
    private static final ModConfigSpec.IntValue DAILY_CLAIM_AMOUNT = BUILDER.comment("Number of lives given by daily claim").defineInRange("dailyClaimAmount", 1, 1, 100);

    static final ModConfigSpec SPEC = BUILDER.build();

    public static int initialLives = 10;
    public static int maxLives = 20;
    public static boolean enableDailyClaim = true;
    public static int dailyClaimAmount = 1;

    @SubscribeEvent
    static void onLoad(ModConfigEvent event) {
        initialLives = INITIAL_LIVES.get();
        maxLives = MAX_LIVES.get();
        enableDailyClaim = ENABLE_DAILY_CLAIM.get();
        dailyClaimAmount = DAILY_CLAIM_AMOUNT.get();
        LifeManager.register();
    }
}
