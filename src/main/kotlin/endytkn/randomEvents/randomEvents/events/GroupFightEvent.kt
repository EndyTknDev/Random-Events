package endytkn.randomEvents.randomEvents.events

import endytkn.randomEvents.minecraftEventsObservers.MinecraftEventsObservers
import endytkn.randomEvents.randomEvents.RandomEvent
import net.minecraft.core.BlockPos
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3
import net.minecraftforge.event.entity.living.LivingDeathEvent
import net.minecraftforge.event.entity.living.LivingDropsEvent
import java.util.UUID

class GroupFight(private val groupName: String, private val groupHates: String, private var entities: MutableMap<UUID, Mob>) {
    private var entitiesLeft: Int = entities.size;
    private var entitiesKilled: MutableMap<UUID, Mob> = mutableMapOf<UUID, Mob>()

    fun killEntities(uuid: UUID) {
        val killedMob = entities.remove(uuid)
        if (killedMob == null) return
        entitiesKilled.put(uuid, killedMob)
        entitiesLeft = entities.size
    }

    fun getEntities(): MutableMap<UUID, Mob> {
        return entities
    }

    fun getEntitiesList(): MutableCollection<Mob> {
        return entities.values
    }

    fun getEntitiesKilled(): MutableMap<UUID, Mob> {
        return entitiesKilled
    }

    fun entitiesKilledList(): MutableCollection<Mob> {
        return entitiesKilled.values
    }

    fun getGroupName(): String {
        return groupName
    }

    fun getEntitiesLeft(): Int {
        return entitiesLeft
    }

    fun getGroupHates(): String {
        return groupHates
    }
}

open class GroupFightEvent(level: Level, targetBlock: BlockPos, val removeDrops: Boolean) : RandomEvent(level, targetBlock) {
    protected val mobGroupies: MutableMap<String, GroupFight> = mutableMapOf()
    protected val playersInEvents = mutableListOf<Player>()

    override fun onPrepare() {
        spawnMobs()

        startFighting()
        MinecraftEventsObservers.onLivingDeathEventObserver += ::onMobDeath
        if (removeDrops) {
            MinecraftEventsObservers.onLivingDropsEventObserver += ::onRemoveDrops
        }
        super.onPrepare()
    }

    fun onRemoveDrops(event: LivingDropsEvent) {
        val entity = event.entity
        val level = entity.level()
        if (entity !is Mob) return
        val group = getEntityGroup(entity)
        if (!level.isClientSide and (group != null)) {
            event.drops.clear()
            event.isCanceled = true
        }
    }

    override fun onFinishing() {
        super.onFinishing()
        MinecraftEventsObservers.onLivingDeathEventObserver -= ::onMobDeath
        if (removeDrops) {
            MinecraftEventsObservers.onLivingDropsEventObserver -= ::onRemoveDrops
        }
    }

    override fun onReady() {
        super.onReady()
        for (group in mobGroupies.values) {
            for (mob in group.getEntities().values) {
                mob.isNoAi = false
            }
        }
    }

    private fun spawnMobs() {
        for (group in mobGroupies.values) {
            for (mob in group.getEntities().values) {
                mob.setPos(Vec3(targetBlock.x.toDouble(), targetBlock.y.toDouble(), targetBlock.z.toDouble()))
                mob.setPersistenceRequired()
                mob.isNoAi = true
                level.addFreshEntity(mob)
            }
        }
    }

    private fun startFighting() {
        for (group in mobGroupies.values) {
            for (mob in group.getEntities().values) {
                fightNextTarget(group, mob)
            }
        }
    }

    private fun fightNextTarget(group: GroupFight, mob: Mob) {
        val hatedGroup = mobGroupies[group.getGroupHates()]
        if (hatedGroup == null) return
        var mostCloseMob: Mob? = null
        var mostCloseDistance = 10000000
        for (targetMob in hatedGroup.getEntities().values) {
            val distance = mob.distanceTo(targetMob)
            if (distance < mostCloseDistance) {
                mostCloseMob = targetMob
            }
        }
        if (mostCloseMob == null) return
        initiateFight(mob, mostCloseMob)
    }

    override fun resolve() {

    }

    fun getEntityGroup(entity: Entity): GroupFight? {
        for (group in mobGroupies.values) {
            if (group.getEntities().contains(entity.uuid)) {
                group.killEntities(entity.uuid)
                return group
            }
        }
        return null
    }

    fun onMobDeath(event: LivingDeathEvent) {
        val entity = event.entity
        val group = getEntityGroup(entity)
        if (group == null) return

        if (isAllMobsDead()) {
            resolve()
            return
        }
        startFighting()
    }

    fun isAllMobsDead(): Boolean {
        for (group in mobGroupies.values) {
            if (group.getEntitiesLeft() > 0) return false
        }
        return true
    }

    override fun onFinishingCanceled() {
        super.onFinishingCanceled()
        for (group in mobGroupies.values) {
            for (mob in group.getEntities().values) {
                mob.remove(Entity.RemovalReason.DISCARDED)
            }
        }
    }


    private fun initiateFight(mob1: Mob, mob2: Mob) {
            mob1.target = mob2
            mob2.target = mob1
    }
}