package endytkn.randomEvents.baseEvents.InvasionEvent

import net.minecraft.world.entity.Mob
import java.util.UUID

open class InvasionWaveGroup(val invasion: InvasionBaseEvent, val title: String, var entities: MutableMap<UUID, Mob>) {
    var entitiesLeft: Int = entities.size;
    var entitiesKilled: MutableMap<UUID, Mob> = mutableMapOf<UUID, Mob>()

    fun addMob(mob: Mob) {
        this.entities.put(mob.uuid, mob)
    }

    fun killEntity(uuid: UUID) {
        val killedMob = entities.remove(uuid)
        if (killedMob == null) return
        entitiesKilled.put(uuid, killedMob)
        entitiesLeft = entities.size
    }
}