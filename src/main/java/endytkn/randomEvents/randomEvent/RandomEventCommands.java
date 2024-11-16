package endytkn.randomEvents.randomEvent;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class RandomEventCommands {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("randomevent")
                        .then(Commands.argument("eventTag", StringArgumentType.word())
                                .executes(context -> {
                                    try {
                                        ServerPlayer player = (ServerPlayer) context.getSource().getEntity();
                                        if (player == null) return 0;

                                        Vec3 endPosition = player.getEyePosition().add(player.getLookAngle().scale(100.0));
                                        String eventTag = StringArgumentType.getString(context, "eventTag");
                                        BlockHitResult rayTraceResult = player.level().clip(
                                                new ClipContext(
                                                        player.getEyePosition(),
                                                        endPosition,
                                                        ClipContext.Block.OUTLINE,
                                                        ClipContext.Fluid.NONE,
                                                        player
                                                )
                                        );

                                        if (rayTraceResult.getType() != HitResult.Type.BLOCK) return 0;

                                        var chunkPosition = rayTraceResult.getBlockPos().offset(0, 1, 0);

                                        if (!RandomEventRegister.RANDOM_EVENTS.containsKey(eventTag)) return 0;

                                        var newEvent = RandomEventRegister.RANDOM_EVENTS.get(eventTag).create();
                                        newEvent.initEvent((ServerLevel) player.level(), chunkPosition, List.of(player));
                                        player.sendSystemMessage(Component.literal("Novo evento apareceu em " + chunkPosition.getX() + ", " + chunkPosition.getY() + ", " + chunkPosition.getZ() + " - " + newEvent.title));
                                        newEvent.start();
                                    } catch (Error e) {
                                        System.out.println(e);
                                        throw e;
                                    }

                                    return 1;
                                })
                        )
        );

        dispatcher.register(
                Commands.literal("startEvent")
                        .executes(context -> {
                            RandomEventManager.triggerNewEvent();
                            return 1;
                        })
        );
        dispatcher.register(
                Commands.literal("removeMobs")
                        .executes(context -> {
                            ServerPlayer player = (ServerPlayer) context.getSource().getEntity();
                            int range = 50;
                            AABB areaAroundPlayer = new AABB(
                                    player.getX() - range, player.getY() - range, player.getZ() - range,
                                    player.getX() + range, player.getY() + range, player.getZ() + range
                            );
                            List<Mob> nearbyMobs = player.level().getNearbyEntities(Mob.class, TargetingConditions.DEFAULT, player, areaAroundPlayer);
                            for (Mob mob : nearbyMobs) {
                                mob.remove(Entity.RemovalReason.DISCARDED); // Exemplo para despawn
                            }
                            return 1;
                        })
        );
    }
}
