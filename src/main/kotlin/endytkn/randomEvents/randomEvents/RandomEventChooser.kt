package endytkn.randomEvents.randomEvents

import endytkn.randomEvents.randomEvents.events.NetherPortalEvent
import endytkn.randomEvents.randomEvents.events.ZombieSkeletonFightEvent
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level
import kotlin.random.Random

object RandomEventChooser {
    fun getEvent(level: Level, targetBlock: BlockPos, isUnderground: Boolean, biomeKey: String, isNight: Boolean): RandomEvent {
        val rand = Random.nextInt(0, 100)
        if (rand < 5) { //spawn structure
            return NetherPortalEvent(level, targetBlock)
        }
        return ZombieSkeletonFightEvent(level, targetBlock)
    }
}