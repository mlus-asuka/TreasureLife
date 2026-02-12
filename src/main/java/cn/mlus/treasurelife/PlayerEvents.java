package cn.mlus.treasurelife;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@EventBusSubscriber(modid = Treasurelife.MODID)
public class PlayerEvents {

    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        if (player instanceof ServerPlayer serverPlayer) {
            // Claim daily life on login
            LifeManager.claimDailyLife(player);

            // Check if player needs respawn (had no lives, got life from daily claim)
            if (player.isSpectator() && LifeManager.hasLives(player)) {
                LifeManager.respawnAsSurvival(serverPlayer);
            }

            // Notify player of their lives
            int lives = LifeManager.getLives(player);
            player.displayClientMessage(
                net.minecraft.network.chat.Component.translatable("treasurelife.lives_status", lives),
                false
            );
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        Player player = event.getEntity();
        if (player instanceof ServerPlayer serverPlayer) {
            // Check if player has lives after respawn
            if (!LifeManager.hasLives(player)) {
                // No lives - force spectator mode
                LifeManager.respawnAsSpectator(serverPlayer);
                player.displayClientMessage(
                    net.minecraft.network.chat.Component.translatable("treasurelife.no_lives"),
                    true
                );
            } else if (player.isSpectator()) {
                // Has lives but is in spectator - respawn as survival
                LifeManager.respawnAsSurvival(serverPlayer);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        LivingEntity player = event.getEntity();
        if (player instanceof ServerPlayer serverPlayer) {
            // Remove one life on death
            LifeManager.removeLife(serverPlayer);

            serverPlayer.displayClientMessage(
                net.minecraft.network.chat.Component.translatable("treasurelife.death_lost_life", LifeManager.getLives(serverPlayer)),
                true
            );
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        // Copy lives data to new entity on respawn
        Player originalPlayer = event.getOriginal();
        Player newPlayer = event.getEntity();

        if (originalPlayer instanceof ServerPlayer && newPlayer instanceof ServerPlayer) {
            // Manually copy lives data from original player to new player
            net.minecraft.nbt.CompoundTag originalData = originalPlayer.getPersistentData();
            net.minecraft.nbt.CompoundTag newData = newPlayer.getPersistentData();

            if (originalData.contains("treasurelife:lives")) {
                newData.putInt("treasurelife:lives", originalData.getInt("treasurelife:lives"));
            }
            if (originalData.contains("treasurelife:last_claim_date")) {
                newData.putString("treasurelife:last_claim_date", originalData.getString("treasurelife:last_claim_date"));
            }

            // Check if player needs to be in spectator mode
            if (!LifeManager.hasLives(newPlayer)) {
                LifeManager.respawnAsSpectator((ServerPlayer) newPlayer);
            }
        }
    }
}
