package endytkn.randomEvents.goals;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;

import java.util.List;

public class GroupFightGoal extends TargetGoal {
    private final Mob mob;
    private final Class<? extends Mob> targetClass;

    public GroupFightGoal(Mob mob, Class<? extends Mob> targetClass) {
        super(mob, false);
        this.mob = mob;
        this.targetClass = targetClass;
    }

    @Override
    public boolean canUse() {
        TargetingConditions targetConditions = TargetingConditions.forCombat().ignoreLineOfSight();
        List<Mob> nearbyMobs = (List<Mob>) mob.level().getNearbyEntities(targetClass, targetConditions, mob, mob.getBoundingBox().inflate(10.0));

        String groupHate = mob.getPersistentData().getString("REGroupFightHates");

        for (Mob targetMob : nearbyMobs) {
            String targetGroup = targetMob.getPersistentData().getString("REGroupFightName");

            if (targetGroup.equals(groupHate)) {
                mob.setTarget(targetMob); // Set the target mob
                return true;
            }
        }
        return false;
    }

    @Override
    public void start() {
        LivingEntity target = mob.getTarget();
        if (target != null) {
            mob.setTarget(target);
        }
    }
}
