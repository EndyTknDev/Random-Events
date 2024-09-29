package endytkn.randomEvents.baseEvents.GroupFight

import endytkn.randomEvents.baseEvents.InvasionEvent.InvasionWaveStatus
import endytkn.randomEvents.minecraftEventsObservers.MinecraftEventsObservers
import endytkn.randomEvents.randomEvents.RandomEvent
import endytkn.randomEvents.randomEvents.RandomEventStatus
import endytkn.randomEvents.utils.BlockPosUtils
import endytkn.randomEvents.utils.Observer
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.player.Player
import net.minecraft.world.phys.Vec3
import net.minecraftforge.event.entity.living.LivingDeathEvent
import kotlin.collections.get

open class GroupFightBaseEvent(val removeDrops: Boolean) : RandomEvent() {
    companion object {
        val name: String = "group_fight"
    }

    protected val mobGroupies: MutableMap<String, GroupFight> = mutableMapOf()
    protected val playersInEvents = mutableListOf<Player>()

    override fun onPrepare() {
        spawnMobs()
        startFighting()
        super.onPrepare()
        MinecraftEventsObservers.onLivingDeathEventObserver += ::tickDeath
    }

    override fun onReady() {
        super.onReady()
        for (group in mobGroupies.values) {
            for (mob in group.entities.values) {
                mob.isNoAi = false
            }
        }
    }

    override fun onFinishingCanceled() {
        super.onFinishingCanceled()
        for (group in mobGroupies.values) {
            for (mob in group.entities.values) {
                mob.remove(Entity.RemovalReason.DISCARDED)
            }
        }
    }

    override fun onFinishing() {
        super.onFinishing()
        MinecraftEventsObservers.onLivingDeathEventObserver -= ::tickDeath
    }

    open fun onGroupiesKilled() {
        setEventStatus(RandomEventStatus.FINISHING_SUCCESS)
    }

    private fun spawnMobs() {
        for (group in mobGroupies.values) {
            var groupPos = BlockPosUtils.findRandomSurfaceBlockNearby(this.level!!, this.targetBlock!!, 10, 10)
            if (groupPos == null) groupPos = targetBlock

            for (mob in group.entities.values) {
                var mobPos = BlockPosUtils.findRandomSurfaceBlockNearby(this.level!!, groupPos!!, 4, 4)
                if (mobPos == null) mobPos = groupPos
                mob.setPos(Vec3(mobPos.x.toDouble(), mobPos.y.toDouble(), mobPos.z.toDouble()))
                mob.setPersistenceRequired()
                mob.isNoAi = true
                val nbt: CompoundTag = mob.serializeNBT() // Obtém os dados NBT do zombie
                nbt.putString("REGroupFight", this.id.toString())
                nbt.putString("REGroupFightName", group.groupName)
                mob.deserializeNBT(nbt)
                level!!.addFreshEntity(mob)
            }
        }
    }

    private fun startFighting() {
        for (group in mobGroupies.values) {
            for (mob in group.entities.values) {
                fightNextTarget(group, mob)
            }
        }
    }

    private fun tickDeath(event: LivingDeathEvent) {
        val entity = event.entity
        val nbt: CompoundTag = entity.serializeNBT()

        if (
            status != RandomEventStatus.READY ||
            !nbt.contains("REGroupFight") ||
            !nbt.contains("REGroupFightName") ||
            (nbt.get("REGroupFight").toString()) == this.id.toString()
        ) return;

        val groupName = nbt.get("REGroupFightName").toString()
        val group = mobGroupies[groupName]
        group!!.killEntity(entity.uuid, entity.killCredit is Player)

        if (verifyGroupiesLeft())
            onGroupiesKilled()
    }

    private fun verifyGroupiesLeft(): Boolean {
        mobGroupies.forEach { (_, value) ->
            if (value.entitiesLeft > 0) return false
        }
        return true
    }

    private fun fightNextTarget(group: GroupFight, mob: Mob) {
        val hatedGroup = mobGroupies[group.groupHates]
        if (hatedGroup == null) return
        var mostCloseMob: Mob? = null
        var mostCloseDistance = 10000000
        for (targetMob in hatedGroup.entities.values) {
            val distance = mob.distanceTo(targetMob)
            if (distance < mostCloseDistance) {
                mostCloseMob = targetMob
            }
        }
        if (mostCloseMob == null) return
        initiateFight(mob, mostCloseMob)
    }

    private fun initiateFight(mob1: Mob, mob2: Mob) {
        mob1.target = mob2
        mob2.target = mob1
    }
}