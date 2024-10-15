package endytkn.randomEvents.baseEvents.GroupFight

import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.ai.goal.target.TargetGoal
import net.minecraft.world.entity.ai.targeting.TargetingConditions

class GroupFightGoal(
    private val mob: Mob,
    private val targetClass: Class<out Mob>,
): TargetGoal(mob, false) {
    override fun canUse(): Boolean {
        val targetConditions = TargetingConditions.forCombat().ignoreLineOfSight()
        val nearbyMobs = mob.level().getNearbyEntities(targetClass, targetConditions, mob, mob.boundingBox.inflate(10.0))

        val groupHate = mob.persistentData.getString("REGroupFightHates")

        for (targetMob in nearbyMobs) {
            val targetGroup = targetMob.persistentData.getString("REGroupFightName")

            if (targetGroup == groupHate) {
                mob.target = targetMob as LivingEntity  // Set the target mob
                return true
            }
        }
        return false
    }

    override fun start() {
        mob.target?.let {
            mob.setTarget(it)
        }
    }
}