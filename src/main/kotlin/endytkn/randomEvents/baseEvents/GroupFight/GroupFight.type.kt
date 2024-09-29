package endytkn.randomEvents.baseEvents.GroupFight

import net.minecraft.world.entity.Mob
import java.util.UUID

class GroupFight(val groupFightEvent: GroupFightBaseEvent, val groupName: String, var entities: MutableMap<UUID, Mob>, val groupHates: String?) {
    var entitiesLeft: Int = entities.size;
    var entitiesKilled: MutableMap<UUID, Mob> = mutableMapOf<UUID, Mob>()
    var playersKill: Int = 0;

    fun killEntity(uuid: UUID, killedByPlayer: Boolean?) {
        val killedMob = entities.remove(uuid)
        if (killedMob == null) return
        entitiesKilled.put(uuid, killedMob)
        entitiesLeft = entities.size
        if (killedByPlayer == true)
            playersKill++
    }
}