package endytkn.randomEvents.events;

import endytkn.randomEvents.baseEvents.groupFight.GroupFight;
import endytkn.randomEvents.baseEvents.groupFight.GroupFightBaseEvent;
import endytkn.randomEvents.randomEvent.RandomEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ZombieSkeletonFightEvent extends GroupFightBaseEvent {
    public static final String NAME = "zombie_skeleton_fight";

    public ZombieSkeletonFightEvent() {
        super(true);
        this.category = RandomEventsCategories.GROUP_FIGHT;
        this.rarity = RandomEventsRarity.COMMON;
        this.eventTag = "zombieSkeletonFight";
    }

    @Override
    public RandomEvent create() {
        return new ZombieSkeletonFightEvent();
    }

    @Override
    public void onPrepare() {
        Map<UUID, Mob> skeletonMobs = new HashMap<>();

        for (int i = 0; i < 5; i++) {
            EntityType<? extends Mob> mobType = EntityType.SKELETON;
            Mob mob = mobType.create(level);
            this.addEntityToEvent(mob);
            if (mob != null) {
                ItemStack stoneSword = new ItemStack(Items.STONE_SWORD);
                mob.setCanPickUpLoot(false);
                mob.setItemInHand(InteractionHand.MAIN_HAND, stoneSword);
                skeletonMobs.put(mob.getUUID(), mob);
            }
        }
        GroupFight skeletonGroup = new GroupFight(this, "skeleton", skeletonMobs, "zombie");

        Map<UUID, Mob> zombieMobs = new HashMap<>();
        for (int i = 0; i < 3; i++) {
            EntityType<? extends Mob> mobType = EntityType.ZOMBIE;
            Mob mob = mobType.create(level);
            this.addEntityToEvent(mob);
            if (mob != null) {
                zombieMobs.put(mob.getUUID(), mob);
            }
        }
        GroupFight zombieGroup = new GroupFight(this, "zombie", zombieMobs, "skeleton");

        mobGroupies.put(skeletonGroup.groupName, skeletonGroup);
        mobGroupies.put(zombieGroup.groupName, zombieGroup);
        super.onPrepare();
    }
}
