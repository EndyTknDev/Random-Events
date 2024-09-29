package endytkn.randomEvents.commands

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import endytkn.randomEvents.events.SkeletonFightEvent
import endytkn.randomEvents.randomEvents.RandomEventChooser
import endytkn.randomEvents.randomEvents.RandomEventRegisters
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.network.chat.Component
import java.util.function.Supplier
import endytkn.randomEvents.randomEvents.RandomEventsManager
import endytkn.randomEvents.randomEvents.RandomEventsManager.addEvent
import endytkn.randomEvents.randomEvents.RandomEventsManagerStatus  // Certifique-se de importar corretamente
import net.minecraft.server.level.ServerPlayer

object RandomEventsCommands {
    fun register(dispatcher: CommandDispatcher<CommandSourceStack>) {
        dispatcher.register(
            Commands.literal("randomEvent")
                .executes { context: CommandContext<CommandSourceStack> ->
                    val player: ServerPlayer = context.source.entity as? ServerPlayer
                        ?: return@executes 0
                    val newEvent = SkeletonFightEvent()
                    val chunkPosition = player.blockPosition()
                    newEvent.initEvent(player.level(), chunkPosition, mutableListOf<ServerPlayer>(player))
                    player.sendSystemMessage(Component.literal("novo evento apareceu ${chunkPosition.x}, ${chunkPosition.y}, ${chunkPosition.z} ${newEvent.title} "))
                    addEvent(newEvent)
                    newEvent.start()

                    context.source.sendSuccess(Supplier { Component.literal("Novo evento disparado!") }, false)
                    1
                }
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
