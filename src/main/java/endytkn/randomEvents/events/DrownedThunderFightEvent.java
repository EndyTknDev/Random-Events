package endytkn.randomEvents.events;

import endytkn.randomEvents.baseEvents.groupFight.GroupFight;
import endytkn.randomEvents.baseEvents.groupFight.GroupFightBaseEvent;
import endytkn.randomEvents.randomEvent.RandomEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class DrownedThunderFightEvent extends GroupFightBaseEvent {
    private int minionsCount;
    private int leaderCount;
    public static String name = "drowned_thunder";

    public DrownedThunderFightEvent() {
        super(true);
        this.eventTag = "drownedThunderFight";
    }

    @Override
    public RandomEvent create() {
        return new DrownedThunderFightEvent();
    }

    @Override
    public void onPrepare() {
        minionsCount = 4 + playersGroup.size() * 2 + new Random().nextInt(2);
        leaderCount = minionsCount / 3;

        Map<UUID, Mob> mobs = new HashMap<>();

        for (int i = 0; i < minionsCount; i++) {
            EntityType<? extends Mob> mobType = EntityType.DROWNED;
            Mob mob = mobType.create(level);
            this.addEntityToEvent(mob);
            mobs.put(mob.getUUID(), mob);
        }

        for (int i = 0; i < leaderCount; i++) {
            Drowned mob = EntityType.DROWNED.create(level);
            if (mob != null) {
                mob.setSecondsOnFire(0);
                ItemStack weapon = new ItemStack(Items.TRIDENT);
                mob.setHealth(mob.getHealth() * 2);
                mob.setHealth((mob.getHealth() * 2));
                this.addEntityToEvent(mob);
                mob.addEffect(new MobEffectInstance(MobEffects.GLOWING, this.timeLimit, 0));
                mob.setCanPickUpLoot(true);
                mob.setItemInHand(InteractionHand.MAIN_HAND, weapon);
                mobs.put(mob.getUUID(), mob);
            }
        }

        GroupFight group = new GroupFight(this, "drowners", mobs, null);
        mobGroupies.put(group.groupName, group);

        super.onPrepare();
    }
}
