package endytkn.randomEvents.events;

import endytkn.randomEvents.baseEvents.groupFight.GroupFightBaseEvent;
import endytkn.randomEvents.randomEvent.RandomEvent;
import endytkn.randomEvents.randomEvent.RandomEventsCategory;
import endytkn.randomEvents.randomEvent.RandomEventsRarity;
import endytkn.randomEvents.utils.MobEquipmentPrefab;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Pillager;
import net.minecraft.world.entity.monster.Vindicator;

import java.util.*;

public class IllagerFightEvent extends GroupFightBaseEvent {
    public IllagerFightEvent() {
        super(true);
        categories = List.of(RandomEventsCategory.GROUP_FIGHT, RandomEventsCategory.DAY_AFTERNOON,RandomEventsCategory.DAY_EVENING, RandomEventsCategory.DAY_MORNING, RandomEventsCategory.WEATHER_CLEAR, RandomEventsCategory.WEATHER_THUNDER, RandomEventsCategory.WEATHER_RAIN);
        this.rarity = RandomEventsRarity.COMMON;
        this.eventTag = "illager_fight";
    }

    @Override
    public RandomEvent create() {
        return new IllagerFightEvent();
    }

    @Override
    public void onPrepare() {
        int minionsCount = 2 + playersGroup.size() * 2 + new Random().nextInt(2);
        int leaderCount = minionsCount / 3;

        Map<UUID, Mob> pillagerMobs = new HashMap<>();

        for (int i = 0; i < minionsCount; i++) {
            Pillager mob = EntityType.PILLAGER.create(level);
            this.addEntityToEvent(mob);
            mob.setSpawnCancelled(true);
            MobEquipmentPrefab.setPillagerWeapon(mob);
            pillagerMobs.put(mob.getUUID(), mob);
        }

        for (int i = 0; i < leaderCount; i++) {
            Vindicator mob = EntityType.VINDICATOR.create(level);
            this.addEntityToEvent(mob);
            mob.setSpawnCancelled(true);
            MobEquipmentPrefab.setVindicatorWeapon(mob);
            if (mob != null) {
                pillagerMobs.put(mob.getUUID(), mob);
            }
        }
        super.onPrepare();
    }
}
