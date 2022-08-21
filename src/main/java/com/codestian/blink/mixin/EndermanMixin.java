package com.codestian.blink.mixin;

import com.codestian.blink.util.IEntityDataSaver;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EndermanEntity.class)
public class EndermanMixin {
    //  Player is considered staring only if eyes are not closed.
    @Inject(method="isPlayerStaring", at = @At("HEAD"), cancellable = true)
    private void injectMethod(PlayerEntity serverPlayer, CallbackInfoReturnable<Boolean> cir) {
        IEntityDataSaver player = (IEntityDataSaver) serverPlayer;
        if(player.getPersistentData().getBoolean("isEyesClosed")) {
            cir.setReturnValue(false);
        }
    }
}
