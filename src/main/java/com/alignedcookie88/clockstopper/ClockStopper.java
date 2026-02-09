package com.alignedcookie88.clockstopper;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static net.minecraft.server.command.CommandManager.*;

public class ClockStopper implements ModInitializer {

    public static final Logger LOGGER = LoggerFactory.getLogger("ClockStopper");

    public static boolean ticking = true;

    public static boolean forceStopTicks = false;

    @Override
    public void onInitialize() {

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {

            dispatcher.register(
                    literal("clockstopper").then(
                            literal("stop")
                                    .requires(requirePermissionLevel(ADMINS_CHECK))
                                    .executes(
                                    context -> {
                                        if (forceStopTicks) {
                                            context.getSource().sendError(Text.literal("The clock is already stopped."));
                                            return 1;
                                        }

                                        forceStopTicks = true;
                                        context.getSource().sendMessage(Text.literal("Stopped the clock."));
                                        LOGGER.info("The clock has been forcefully stopped by {}.", context.getSource().getDisplayName().getString());

                                        return 1;
                                    }
                            )
                    ).then(
                            literal("start")
                                    .requires(requirePermissionLevel(ADMINS_CHECK))
                                    .executes(
                                    context -> {
                                        if (!forceStopTicks) {
                                            context.getSource().sendError(Text.literal("The clock is not stopped."));
                                            return 1;
                                        }

                                        forceStopTicks = false;
                                        context.getSource().sendMessage(Text.literal("Started the clock."));
                                        LOGGER.info("The clock has been restarted by {}.", context.getSource().getDisplayName().getString());

                                        return 1;
                                    }
                            )
                    ).then(
                            literal("about")
                                    .executes(
                                            context -> {

                                                context.getSource().sendMessage(
                                                        Text.literal("ClockStopper is a fabric mod developed by AlignedCookie88 that stops the game's tick cycle when no players are online. This allows the server to use practically zero system resources when no players are online. The source code is available at https://github.com/AlignedCookie88/ClockStopper.")
                                                );

                                                return 1;
                                            }
                                    )
                    )
            );

        });

    }

    public static int playerCount(MinecraftServer server) {
        return server.getPlayerManager().getPlayerList().size();
    }
}
