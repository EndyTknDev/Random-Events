package endytkn.randomEvents.events

import endytkn.randomEvents.commands.RandomEventsCommands  // Importa o novo comando
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.RegisterCommandsEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

object CommandRegister {
    private val LOGGER: Logger = LogManager.getLogger()

    init {
        // Registra esta classe no evento do Forge
        MinecraftForge.EVENT_BUS.register(this)
    }

    @SubscribeEvent
    fun onRegisterCommands(event: RegisterCommandsEvent) {
        LOGGER.log(Level.INFO, "Registrando comandos...")
        RandomEventsCommands.register(event.dispatcher)  // Registra o novo comando 'newevent'
    }
}
