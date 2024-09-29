package endytkn.randomEvents.utils

import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerLevel
import org.apache.logging.log4j.core.jmx.Server

object GameInstances {
    var minecraftServer: MinecraftServer? = null
    var overworldLevel: ServerLevel? = null
    var netherLevel: ServerLevel? = null
    var endLevel: ServerLevel? = null
}