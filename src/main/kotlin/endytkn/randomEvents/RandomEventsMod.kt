package endytkn.randomEvents

import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import thedarkcolour.kotlinforforge.forge.MOD_BUS

/**
 * Main mod class. Should be an `object` declaration annotated with `@Mod`.
 * The modid should be declared in this object and should match the modId entry
 * in mods.toml.
 *
 * An example for blocks is in the `blocks` package of this mod.
 */

import endytkn.randomEvents.randomEvents.RandomEventRegisters
import endytkn.randomEvents.utils.GameInstances
import net.minecraftforge.event.server.ServerStartingEvent
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent

@Mod(RandomEventsMod.ID)
object RandomEventsMod {
    const val ID = "randomevents"

    // the logger for our mod
    private val LOGGER: Logger = LogManager.getLogger(ID)

    init {
        MOD_BUS.addListener(::onClientSetup)
        MOD_BUS.addListener(::onServerSetup)
        MOD_BUS.addListener(::onLoadRegister)
    }

    /**
     * This is used for initializing client specific
     * things such as renderers and keymaps
     * Fired on the mod specific event bus.
     */
    private fun onClientSetup(event: FMLClientSetupEvent) {
        LOGGER.log(Level.INFO, "Initializing client...")
    }

    /**
     * Fired on the global Forge bus.
     */
    private fun onServerSetup(event: FMLDedicatedServerSetupEvent) {
        LOGGER.log(Level.INFO, "Server starting...")
    }

    private fun onServerStartup(event: ServerStartingEvent) {
        GameInstances.minecraftServer = event.server
        GameInstances.overworldLevel = event.server.getLevel(net.minecraft.world.level.Level.OVERWORLD)
        GameInstances.netherLevel = event.server.getLevel(net.minecraft.world.level.Level.NETHER)
        GameInstances.endLevel = event.server.getLevel(net.minecraft.world.level.Level.END)
    }

    private fun onLoadRegister(event: FMLLoadCompleteEvent ) {
        LOGGER.log(Level.DEBUG, "REGISTERING RANDOM EVENTS")
        RandomEventRegisters.registerEvents()
    }
}