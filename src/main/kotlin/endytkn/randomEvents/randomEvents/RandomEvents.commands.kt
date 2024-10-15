package endytkn.randomEvents.commands

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import endytkn.randomEvents.randomEvents.RandomEventRegisters
import endytkn.randomEvents.randomEvents.RandomEventRegisters.EVENTS_BY_TAGS
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.network.chat.Component
import java.util.function.Supplier
import endytkn.randomEvents.randomEvents.RandomEventsManager
import endytkn.randomEvents.randomEvents.RandomEventsManagerStatus
import endytkn.randomEvents.utils.GameInstances
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.level.ClipContext
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.HitResult
import net.minecraft.world.phys.Vec3

object RandomEventsCommands {
    fun register(dispatcher: CommandDispatcher<CommandSourceStack>) {
        dispatcher.register(
            Commands.literal("executeEvent")
                .then(Commands.argument("eventTag", StringArgumentType.word())
                    .executes { context: CommandContext<CommandSourceStack> ->
                        val player: ServerPlayer = context.source.entity as? ServerPlayer
                            ?: return@executes 0
                        val endPosition: Vec3 = player.eyePosition.add(player.lookAngle.scale(100.0))
                        val eventTag = StringArgumentType.getString(context, "eventTag")
                        val rayTraceResult: BlockHitResult = player.level().clip(
                            ClipContext(
                                player.eyePosition,
                                endPosition,
                                ClipContext.Block.OUTLINE,
                                ClipContext.Fluid.NONE,
                                player
                            )
                        )

                        if (rayTraceResult.type != HitResult.Type.BLOCK) return@executes 0

                        val chunkPosition = rayTraceResult.blockPos.offset(0, 1, 0)

                        if (!EVENTS_BY_TAGS.contains(eventTag)) return@executes 0

                        val newEvent = EVENTS_BY_TAGS[eventTag]!!.create()

                        newEvent.initEvent(player.level() as ServerLevel, chunkPosition, mutableListOf(player))
                        player.sendSystemMessage(Component.literal("Novo evento apareceu em ${chunkPosition.x}, ${chunkPosition.y}, ${chunkPosition.z} - ${newEvent.title}"))
                        RandomEventsManager.addEvent(newEvent)
                        newEvent.start()

                        1
                    }
                )
        )
        dispatcher.register(
            Commands.literal("startEvent")
                .then(Commands.argument("eventId", StringArgumentType.word())
                    .executes { context: CommandContext<CommandSourceStack> ->
                        val player: ServerPlayer = context.source.entity as? ServerPlayer
                            ?: return@executes 0
                        val eventId = StringArgumentType.getString(context, "eventId")

                        when (eventId) {
                            "tryEvent" -> {
                                RandomEventsManager.setStatus(RandomEventsManagerStatus.TRY_EVENT)

                            }
                            else -> {
                                return@executes 0
                            }
                        }
                        1
                    }
                )
        )
        dispatcher.register(
            Commands.literal("reloadEvents")
                .executes { context: CommandContext<CommandSourceStack> ->
                    RandomEventRegisters.registerEvents()
                    context.source.sendSuccess(Supplier { Component.literal("Eventos recarregados!") }, false)
                    1
                }
        )
    }
}