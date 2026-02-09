package com.alignedcookie88.clockstopper.mixin;

import com.alignedcookie88.clockstopper.ClockStopper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerNetworkIo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {

    @Shadow public abstract ServerNetworkIo getNetworkIo();

    @Inject(method = "tickWorlds", at = @At("HEAD"), cancellable = true)
    public void tickWorlds(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {

        int playerCount = ClockStopper.playerCount((MinecraftServer) (Object) this);

        if (ClockStopper.ticking) {

            if (playerCount == 0) {
                ClockStopper.LOGGER.info("Stopping clock, no players are online.");
                ClockStopper.ticking = false;
            }

            if (ClockStopper.forceStopTicks) {
                ClockStopper.ticking = false;
            }

        } else {

            if (playerCount > 0 && !ClockStopper.forceStopTicks) {
                ClockStopper.LOGGER.info("Restarting clock, a player has joined.");
                ClockStopper.ticking = true;
            } else {
                this.getNetworkIo().tick(); // Players cannot join unless this is called.
                ci.cancel();
            }

        }

    }
}
