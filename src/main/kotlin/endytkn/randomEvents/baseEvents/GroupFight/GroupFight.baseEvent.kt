package endytkn.randomEvents.baseEvents.GroupFight

import endytkn.randomEvents.baseEvents.InvasionEvent.InvasionWaveStatus
import endytkn.randomEvents.minecraftEventsObservers.MinecraftEventsObservers
import endytkn.randomEvents.randomEvents.RandomEvent
import endytkn.randomEvents.randomEvents.RandomEventStatus
import endytkn.randomEvents.utils.BlockPosUtils
import endytkn.randomEvents.utils.Observer
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.Tag
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
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

    override fun onPrepare() {
        this.distanceThreshold = 100
        spawnMobs()
        MinecraftEventsObservers.onLivingDeathEventObserver -= ::tickDeath
        MinecraftEventsObservers.onLivingDeathEventObserver += ::tickDeath
        super.onPrepare()
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
                mob.addEffect(MobEffectInstance(MobEffects.GLOWING, this.timeLimit))
                mob.isNoAi = true
                val nbt: CompoundTag = mob.persistentData
                nbt.putString(this.eventTag, this.id.toString())
                nbt.putString("REGroupFightName", group.groupName)
                if (group.groupHates != null) {
                    nbt.putString("REGroupFightHates", group.groupHates)
                    mob.goalSelector.addGoal(1, GroupFightGoal(mob, Mob::class.java))
                }
                mob.addAdditionalSaveData(nbt)
                level!!.addFreshEntity(mob)
            }
        }
    }

    private fun tickDeath(event: LivingDeathEvent) {
        val entity = event.entity
        val nbt: CompoundTag = entity.persistentData
        if (
            status != RandomEventStatus.READY ||
            !nbt.contains("REGroupFightName") ||
            !this.isEntityOfEvent(entity)
        ) return;

        val groupName = nbt.getString("REGroupFightName")
        val group = this.mobGroupies[groupName]

        group!!.killEntity(entity.uuid, event.source.entity is Player)

        if (verifyGroupiesLeft())
            onGroupiesKilled()
    }

    private fun verifyGroupiesLeft(): Boolean {
        mobGroupies.forEach { (_, value) ->
            if (value.entitiesLeft > 0) return false
        }
        return true
    }

}