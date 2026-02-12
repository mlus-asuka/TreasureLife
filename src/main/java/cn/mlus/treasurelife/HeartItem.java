package cn.mlus.treasurelife;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class HeartItem extends Item {
    public HeartItem() {
        super(new Item.Properties().stacksTo(64));
    }



    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);

        if (!level.isClientSide()) {
            boolean added = LifeManager.addLife(player);
            if (added) {
                if (!player.isCreative()) {
                    itemStack.shrink(1);
                }
                player.displayClientMessage(
                    net.minecraft.network.chat.Component.translatable("treasurelife.life_gained",
                        LifeManager.getLives(player)),
                    true
                );
                return InteractionResultHolder.consume(itemStack);
            } else {
                player.displayClientMessage(
                    net.minecraft.network.chat.Component.translatable("treasurelife.max_lives"),
                    true
                );
                return InteractionResultHolder.fail(itemStack);
            }
        }

        return InteractionResultHolder.pass(itemStack);
    }
}
