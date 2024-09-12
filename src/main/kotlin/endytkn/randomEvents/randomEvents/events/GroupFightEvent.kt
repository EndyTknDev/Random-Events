package endytkn.randomEvents.randomEvents.events

import endytkn.randomEvents.minecraftEventsObservers.MinecraftEventsObservers
import endytkn.randomEvents.randomEvents.RandomEvent
import net.minecraft.core.BlockPos
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.Mob
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3
import net.minecraftforge.event.entity.living.LivingDeathEvent
import java.util.UUID

open class GroupFightEvent(level: Level, targetBlock: BlockPos) : RandomEvent(level, targetBlock) {
    protected val group1: MutableList<Mob> = mutableListOf()
    protected val group2: MutableList<Mob> = mutableListOf()
    protected val entities1 = mutableMapOf<UUID, Mob>()
    protected val entities2 = mutableMapOf<UUID, Mob>()

    override fun onPrepare() {
        val entitiesList1 = spawnMobsByGroup(group1)
        val entitiesList2 = spawnMobsByGroup(group2)
        addToGroup(entitiesList1, entities1)
        addToGroup(entitiesList2, entities2)
        startFighting()
        MinecraftEventsObservers.onLivingDeathEventObserver += ::onMobDeath
        super.onPrepare()
    }

    override fun onFinishing() {
        super.onFinishing()
        MinecraftEventsObservers.onLivingDeathEventObserver -= ::onMobDeath
    }

     private fun addToGroup(mobList: MutableList<Mob>, group: MutableMap<UUID, Mob>): MutableMap<UUID, Mob> {
        for (mob in mobList) {
            group.put(mob.uuid, mob)
        }
         return group;
    }

    override fun onReady() {
        super.onReady()
        for (mob1 in entities1.values) {
            mob1.isNoAi = false
        }
        for (mob2 in entities2.values) {
            mob2.isNoAi = false
        }
    }

    private fun spawnMobsByGroup(group: MutableList<Mob>): MutableList<Mob> {
        val entities = mutableListOf<Mob>()
        for (mob in group) {
            mob.setPos(Vec3(targetBlock.x.toDouble(), targetBlock.y.toDouble(), targetBlock.z.toDouble()))
            mob.setPersistenceRequired()
            mob.isNoAi = true
            level.addFreshEntity(mob)
            entities.add(mob)
        }
        return entities
    }

    private fun startFighting() {
        for (mob1 in entities1.values) {
            for (mob2 in entities2.values) {
                initiateFight(mob1, mob2)
            }
        }
    }

    fun onMobDeath(event: LivingDeathEvent) {
        val entity = event.entity
        val uuid1 = entities1.remove(entity.uuid)
        val uuid2 = entities2.remove(entity.uuid)

        if (entities1.values.size == 0 && entities2.values.size == 0) {
            resolve()
            return
        }
        if (uuid1 == null && uuid2 == null) return
        startFighting()
    }

    override fun onFinishingCanceled() {
        super.onFinishingCanceled()
        for (mob in entities1.values) {
            mob.remove(Entity.RemovalReason.DISCARDED)
        }
        for (mob in entities2.values) {
            mob.remove(Entity.RemovalReason.DISCARDED)
        }
    }

    private fun initiateFight(mob1: Mob, mob2: Mob) {
        // Implement logic to make mob1 and mob2 fight
        mob1.target = mob2
        mob2.target = mob1
    }
}