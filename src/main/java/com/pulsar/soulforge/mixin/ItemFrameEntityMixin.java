package com.pulsar.soulforge.mixin;

import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.event.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemFrameEntity.class)
public class ItemFrameEntityMixin {
    @Inject(method = "interact", at=@At("HEAD"), cancellable = true)
    public void interact(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        ItemFrameEntity entity = (ItemFrameEntity)(Object)this;
        if (player.getInventory().selectedSlot == 9) {
            entity.playSound(entity.getRotateItemSound(), 1.0f, 1.0f);
            entity.setRotation(entity.getRotation() + 1);
            entity.emitGameEvent(GameEvent.BLOCK_CHANGE, player);
            cir.setReturnValue(ActionResult.CONSUME);
        }
    }
}
