package cn.mlus.treasurelife;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LifeManager {
    private static LifeManager INSTANCE;

    private static final String LIVES_KEY = "treasurelife:lives";
    private static final String LAST_CLAIM_DATE_KEY = "treasurelife:last_claim_date";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    private LifeManager() {}

    public static void register() {
        if (INSTANCE == null) {
            INSTANCE = new LifeManager();
        }
    }

    public static int getLives(Player player) {
        CompoundTag data = player.getPersistentData();
        if (data.contains(LIVES_KEY)) {
            return data.getInt(LIVES_KEY);
        }
        // New player - give initial lives
        int initialLives = Config.initialLives;
        setLives(player, initialLives);
        return initialLives;
    }

    public static void setLives(Player player, int lives) {
        CompoundTag data = player.getPersistentData();
        int clampedLives = Math.max(0, Math.min(lives, Config.maxLives));
        data.putInt(LIVES_KEY, clampedLives);
    }

    public static boolean addLife(Player player) {
        int currentLives = getLives(player);
        if (currentLives >= Config.maxLives) {
            return false;
        }
        setLives(player, currentLives + 1);
        return true;
    }

    public static boolean removeLife(Player player) {
        int currentLives = getLives(player);
        if (currentLives <= 0) {
            return true; // Already dead
        }
        setLives(player, currentLives - 1);
        return getLives(player) > 0;
    }

    public static boolean canClaimDailyLife(Player player) {
        if (!Config.enableDailyClaim) {
            return false;
        }

        CompoundTag data = player.getPersistentData();
        if (!data.contains(LAST_CLAIM_DATE_KEY)) {
            return true; // Never claimed
        }

        String lastClaimDate = data.getString(LAST_CLAIM_DATE_KEY);
        String today = LocalDate.now().format(DATE_FORMATTER);

        return !today.equals(lastClaimDate);
    }

    public static void claimDailyLife(Player player) {
        if (canClaimDailyLife(player)) {
            int claimAmount = Config.dailyClaimAmount;
            if (player.isSpectator()) {
                for (int i = 0; i < claimAmount; i++) {
                    if (!addLife(player)) {
                        break;
                    }
                }
            } else {
                player.addItem(new ItemStack(Treasurelife.HEART.get(),claimAmount));
            }
            CompoundTag data = player.getPersistentData();
            data.putString(LAST_CLAIM_DATE_KEY, LocalDate.now().format(DATE_FORMATTER));
        }
    }

    public static boolean hasLives(Player player) {
        return getLives(player) > 0;
    }

    public static void respawnAsSpectator(ServerPlayer player) {
        if (!player.isSpectator()) {
            player.setGameMode(GameType.SPECTATOR);
        }
    }

    public static void respawnAsSurvival(ServerPlayer player) {
        if (player.isSpectator()) {
            player.setGameMode(GameType.SURVIVAL);
        }
    }
}
