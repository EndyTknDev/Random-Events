package endytkn.randomEvents.events;

import endytkn.randomEvents.baseEvents.groupFight.GroupFight;
import endytkn.randomEvents.baseEvents.groupFight.GroupFightBaseEvent;
import endytkn.randomEvents.randomEvent.RandomEvent;
import endytkn.randomEvents.randomEvent.RandomEventsCategory;
import endytkn.randomEvents.randomEvent.RandomEventsRarity;
import endytkn.randomEvents.utils.MobEquipmentPrefab;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;

import java.util.*;

public class IllagerVillagerFightEvent extends GroupFightBaseEvent {
    public IllagerVillagerFightEvent() {
        super(true);
        categories = List.of(RandomEventsCategory.GROUP_FIGHT, RandomEventsCategory.DAY_AFTERNOON,RandomEventsCategory.DAY_EVENING, RandomEventsCategory.DAY_MORNING, RandomEventsCategory.WEATHER_CLEAR, RandomEventsCategory.WEATHER_THUNDER, RandomEventsCategory.WEATHER_RAIN);
        this.rarity = RandomEventsRarity.COMMON;
        this.eventTag = "illager_villager_fight";
    }

    @Override
    public RandomEvent create() {
        return new IllagerVillagerFightEvent();
    }

    @Override
    public void onPrepare() {
        Map<UUID, Mob> illagersMobs = new HashMap<>();

        int illagersCount = 2 + playersGroup.size() * 2 + new Random().nextInt(2);

        for (int i = 0; i < illagersCount; i++) {
            EntityType<? extends Mob> mobType = EntityType.PILLAGER;
            Mob mob = mobType.create(level);
            this.addEntityToEvent(mob);
            MobEquipmentPrefab.setPillagerWeapon(mob);
            illagersMobs.put(mob.getUUID(), mob);
        }

        GroupFight illagerGroup = new GroupFight(this, "illager", illagersMobs, "villager");

        Map<UUID, Mob> villagerMobs = new HashMap<>();
        int villagersCount = 1 + (int) (illagersCount / 1.5);
        int ironGolemCount = (int) (1 + (villagersCount / 1.25));

        for (int i = 0; i < ironGolemCount; i++) {
            EntityType<? extends Mob> mobType = EntityType.IRON_GOLEM;
            Mob mob = mobType.create(level);
            this.addEntityToEvent(mob);
            villagerMobs.put(mob.getUUID(), mob);
        }

        GroupFight villagerGroup = new GroupFight(this, "villager", villagerMobs, "illager");

        mobGroupies.put(illagerGroup.groupName, illagerGroup);
        mobGroupies.put(villagerGroup.groupName, villagerGroup);
        super.onPrepare();
    }
}
