package endytkn.randomEvents.commands

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.network.chat.Component
import java.util.function.Supplier
import endytkn.randomEvents.randomEvents.RandomEventsManager
import endytkn.randomEvents.randomEvents.RandomEventsManagerStatus  // Certifique-se de importar corretamente

object RandomEventsCommands {
    fun register(dispatcher: CommandDispatcher<CommandSourceStack>) {
        dispatcher.register(
            Commands.literal("newevent")
                .executes { context: CommandContext<CommandSourceStack> ->
                    RandomEventsManager.setStatus(RandomEventsManagerStatus.TRY_EVENT)  // Aciona o evento
                    context.source.sendSuccess(Supplier { Component.literal("Novo evento disparado!") }, false)
                    1
                }
        )
    }
}
