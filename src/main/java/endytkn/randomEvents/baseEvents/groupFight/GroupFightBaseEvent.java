package endytkn.randomEvents.baseEvents.groupFight;

import endytkn.randomEvents.eventObservers.MinecraftEventsObservers;
import endytkn.randomEvents.randomEvent.RandomEvent;
import endytkn.randomEvents.utils.BlockPosUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class GroupFightBaseEvent extends RandomEvent {
    public static final String name = "group_fight";
    protected final Map<String, GroupFight> mobGroupies = new HashMap<>();
    private final boolean removeDrops;

    private final Consumer<LivingDeathEvent> tickDeathConsumer = this::tickDeath;

    public GroupFightBaseEvent(boolean removeDrops) {
        this.removeDrops = removeDrops;
    }

    public void tickDeath(LivingDeathEvent event) {
        Entity entity = event.getEntity();
        CompoundTag nbt = entity.getPersistentData();
        if (this.status != RandomEventStatus.READY || !nbt.contains("REGroupFightName") || !this.isEntityOfEvent(entity)) {
            return;
        }

        String groupName = nbt.getString("REGroupFightName");
        GroupFight group = this.mobGroupies.get(groupName);
        if (group != null) {
            group.killEntity(entity.getUUID(), event.getSource().getEntity() instanceof Player);
        }

        if (this.verifyGroupiesLeft()) {
            this.onGroupiesKilled();
        }
    }

    @Override
    public void onPrepare() {
        this.distanceThreshold = 100;
        spawnMobs();
        MinecraftEventsObservers.livingDeathObserver.remove(this.tickDeathConsumer);
        MinecraftEventsObservers.livingDeathObserver.add(this.tickDeathConsumer);
        super.onPrepare();
    }

    @Override
    public void onReady() {
        super.onReady();
        for (GroupFight group : mobGroupies.values()) {
            for (Mob mob : group.entities.values()) {
                mob.setNoAi(false);
            }
        }
    }

    @Override
    public void onFinishingCanceled() {
        super.onFinishingCanceled();
        for (GroupFight group : mobGroupies.values()) {
            for (Mob mob : group.entities.values()) {
                mob.remove(Entity.RemovalReason.DISCARDED);
            }
        }
    }

    @Override
    public void onFinishing() {
        super.onFinishing();
        MinecraftEventsObservers.livingDeathObserver.remove(this.tickDeathConsumer);
    }

    public void onGroupiesKilled() {
        setEventStatus(RandomEventStatus.FINISHING_SUCCESS);
    }

    private void spawnMobs() {
        for (GroupFight group : mobGroupies.values()) {
            BlockPos groupPos = BlockPosUtils.findRandomSurfaceBlockNearby(this.level, this.targetBlock, 10, 10);
            if (groupPos == null) {
                groupPos = targetBlock;
            }

            for (Mob mob : group.entities.values()) {
                BlockPos mobPos = BlockPosUtils.findRandomSurfaceBlockNearby(this.level, groupPos, 4, 4);
                if (mobPos == null) {
                    mobPos = groupPos;
                }
                mob.setPos(new Vec3(mobPos.getX(), mobPos.getY(), mobPos.getZ()));
                mob.setPersistenceRequired();
                mob.addEffect(new MobEffectInstance(MobEffects.GLOWING, this.timeLimit));
                mob.setNoAi(true);
                CompoundTag nbt = mob.getPersistentData();
                nbt.putString(this.eventTag, this.id.toString());
                nbt.putString("REGroupFightName", group.groupName);
                if (group.groupHates != null) {
                    nbt.putString("REGroupFightHates", group.groupHates);
                    mob.goalSelector.addGoal(1, new GroupFightGoal(mob, Mob.class));
                }
                mob.addAdditionalSaveData(nbt);
                level.addFreshEntity(mob);
            }
        }
    }

    private boolean verifyGroupiesLeft() {
        for (GroupFight group : mobGroupies.values()) {
            if (group.entitiesLeft > 0) {
                return false;
            }
        }
        return true;
    }
}
