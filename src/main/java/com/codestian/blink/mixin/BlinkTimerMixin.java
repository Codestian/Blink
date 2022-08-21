package com.codestian.blink.mixin;

import com.codestian.blink.BlinkClient;
import com.codestian.blink.config.ModConfigs;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

import static com.codestian.blink.BlinkClient.enableBlinkingTimer;

@Mixin(ClientWorld.class)
public class BlinkTimerMixin {
	@Unique
	private final Random rn = new Random();
	public int ticksUntilSomething = rn.nextInt(ModConfigs.DURATION_RANGE_END - ModConfigs.DURATION_RANGE_START + 1) + ModConfigs.DURATION_RANGE_START;

	@Inject(method = "tick", at = @At("TAIL"))
	private void onTick(CallbackInfo ci) {
		if (--this.ticksUntilSomething <= 0 && enableBlinkingTimer.get()) {
			BlinkClient.enableBlinking.set(true);
			this.ticksUntilSomething = rn.nextInt(ModConfigs.DURATION_RANGE_END - ModConfigs.DURATION_RANGE_START + 1) + ModConfigs.DURATION_RANGE_START;
		}
	}
}

