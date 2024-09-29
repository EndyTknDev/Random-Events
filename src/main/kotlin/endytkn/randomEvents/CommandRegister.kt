package endytkn.randomEvents.events

import endytkn.randomEvents.commands.RandomEventsCommands
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.RegisterCommandsEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

@Mod.EventBusSubscriber
object CommandRegister {
    private val LOGGER: Logger = LogManager.getLogger()

    init {
        MinecraftForge.EVENT_BUS.register(this)
    }

    @SubscribeEvent
    fun onRegisterCommands(event: RegisterCommandsEvent) {
        LOGGER.log(Level.INFO, "Registrando comandos...")
        RandomEventsCommands.register(event.dispatcher)
    }
}
