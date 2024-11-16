package endytkn.randomEvents.events;

import endytkn.randomEvents.baseEvents.groupFight.GroupFight;
import endytkn.randomEvents.baseEvents.groupFight.GroupFightBaseEvent;
import endytkn.randomEvents.randomEvent.RandomEvent;
import endytkn.randomEvents.randomEvent.RandomEventsCategory;
import endytkn.randomEvents.randomEvent.RandomEventsRarity;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Pillager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PillagerSkeletonFightEvent extends GroupFightBaseEvent {
    public PillagerSkeletonFightEvent() {
        super(true);
        categories = List.of(RandomEventsCategory.GROUP_FIGHT, RandomEventsCategory.DAY_NIGHT, RandomEventsCategory.WEATHER_CLEAR, RandomEventsCategory.WEATHER_RAIN, RandomEventsCategory.WEATHER_THUNDER);
        this.rarity = RandomEventsRarity.COMMON;
        this.eventTag = "pillager_skeleton_fight";
    }

    @Override
    public RandomEvent create() {
        return new PillagerSkeletonFightEvent();
    }

    @Override
    public void onPrepare() {
        Map<UUID, Mob> skeletonMobs = new HashMap<>();

        for (int i = 0; i < 5; i++) {
            EntityType<? extends Mob> mobType = EntityType.SKELETON;
            Mob mob = mobType.create(level);
            if (mob != null) {
                ItemStack stoneSword = new ItemStack(Items.STONE_SWORD);
                mob.setCanPickUpLoot(false);
                this.addEntityToEvent(mob);
                mob.setItemInHand(InteractionHand.MAIN_HAND, stoneSword);
                skeletonMobs.put(mob.getUUID(), mob);
            }
        }
        GroupFight skeletonGroup = new GroupFight(this, "skeleton", skeletonMobs, "pillager");

        Map<UUID, Mob> pillagerMobs = new HashMap<>();
        for (int i = 0; i < 3; i++) {
            Pillager mob = EntityType.PILLAGER.create(level);
            this.addEntityToEvent(mob);
            mob.setSpawnCancelled(true);
            mob.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.CROSSBOW));
            if (mob != null) {
                pillagerMobs.put(mob.getUUID(), mob);
            }
        }
        GroupFight pillagerGroup = new GroupFight(this, "pillager", pillagerMobs, "skeleton");

        mobGroupies.put(skeletonGroup.groupName, skeletonGroup);
        mobGroupies.put(pillagerGroup.groupName, pillagerGroup);
        super.onPrepare();
    }
}
