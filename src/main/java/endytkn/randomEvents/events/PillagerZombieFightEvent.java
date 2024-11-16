package endytkn.randomEvents.events;

import endytkn.randomEvents.baseEvents.groupFight.GroupFight;
import endytkn.randomEvents.baseEvents.groupFight.GroupFightBaseEvent;
import endytkn.randomEvents.randomEvent.RandomEvent;
import endytkn.randomEvents.randomEvent.RandomEventsCategory;
import endytkn.randomEvents.randomEvent.RandomEventsRarity;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PillagerZombieFightEvent extends GroupFightBaseEvent {
    public PillagerZombieFightEvent() {
        super(true);
        categories = List.of(RandomEventsCategory.GROUP_FIGHT, RandomEventsCategory.DAY_NIGHT, RandomEventsCategory.WEATHER_CLEAR, RandomEventsCategory.WEATHER_THUNDER, RandomEventsCategory.WEATHER_RAIN);
        this.rarity = RandomEventsRarity.COMMON;
        this.eventTag = "pillager_zombie_fight";
    }

    @Override
    public RandomEvent create() {
        return new PillagerZombieFightEvent();
    }

    @Override
    public void onPrepare() {
        Map<UUID, Mob> zombieMobs = new HashMap<>();

        for (int i = 0; i < 5; i++) {
            EntityType<? extends Mob> mobType = EntityType.ZOMBIE;
            Mob mob = mobType.create(level);
            this.addEntityToEvent(mob);
            if (mob != null) {
                mob.setCanPickUpLoot(false);
                zombieMobs.put(mob.getUUID(), mob);
            }
        }
        GroupFight zombieGroup = new GroupFight(this, "zombie", zombieMobs, "pillager");

        Map<UUID, Mob> pillagerMobs = new HashMap<>();
        for (int i = 0; i < 3; i++) {
            EntityType<? extends Mob> mobType = EntityType.PILLAGER;
            Mob mob = mobType.create(level);
            this.addEntityToEvent(mob);
            mob.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.CROSSBOW));
            if (mob != null) {
                pillagerMobs.put(mob.getUUID(), mob);
            }
        }
        GroupFight pillagerGroup = new GroupFight(this, "pillager", pillagerMobs, "zombie");

        mobGroupies.put(zombieGroup.groupName, zombieGroup);
        mobGroupies.put(pillagerGroup.groupName, pillagerGroup);
        super.onPrepare();
    }
}
