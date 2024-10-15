package endytkn.randomEvents.utils

import com.mojang.brigadier.CommandDispatcher
import net.minecraft.commands.CommandSourceStack
import net.minecraftforge.event.server.ServerStartingEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod

@Mod.EventBusSubscriber
object CommandUtils {
    private var commandDispatcher: CommandDispatcher<CommandSourceStack>? = null
    private var commandSource: CommandSourceStack? = null

    @SubscribeEvent
    fun onServerStartup(event: ServerStartingEvent) {
        GameInstances.minecraftServer = event.server
        GameInstances.overworldLevel = event.server.getLevel(net.minecraft.world.level.Level.OVERWORLD)
        GameInstances.netherLevel = event.server.getLevel(net.minecraft.world.level.Level.NETHER)
        GameInstances.endLevel = event.server.getLevel(net.minecraft.world.level.Level.END)
    }

    private fun getCommandDispatcher(): CommandDispatcher<CommandSourceStack>? {
        if (commandDispatcher == null)
            commandDispatcher = GameInstances.minecraftServer!!.commands.dispatcher
        return commandDispatcher
    }

    private fun getCommandSource(): CommandSourceStack? {
        if (commandSource == null) {
            commandSource = GameInstances.minecraftServer!!.createCommandSourceStack()
        }
        return commandSource
    }

    fun sendCommand(command: String) {
        val dispatcher = this.getCommandDispatcher()
        val source = this.getCommandSource()

        dispatcher!!.execute(command, source)
    }
}