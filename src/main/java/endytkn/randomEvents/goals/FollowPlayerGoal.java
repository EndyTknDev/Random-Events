package endytkn.randomEvents.goals;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;

public class FollowPlayerGoal extends Goal {
    private final Mob mob;
    private Player player;
    private final Double speedModifier;
    private final float maxDistance;

    public FollowPlayerGoal(Mob mob, Double speedModifier, float maxDistance, Player player) {
        this.mob = mob;
        this.speedModifier = speedModifier;
        this.maxDistance = maxDistance;
        this.player = player;
    }

    @Override
    public boolean canUse() {
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        return this.player != null && this.player.isAlive() && this.mob.distanceToSqr(this.player) > 2.0D;
    }

    @Override
    public void start() {
        this.mob.getNavigation().moveTo(this.player, this.speedModifier);
    }

    @Override
    public void tick() {
        if (this.mob.distanceToSqr(this.player) > this.maxDistance) {
            this.mob.getNavigation().moveTo(this.player, this.speedModifier);
        }
    }
}

