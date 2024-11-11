package endytkn.randomEvents.randomEvent;

import endytkn.randomEvents.events.*;
import endytkn.randomEvents.randomEvent.RandomEvent.RandomEventsCategories;
import endytkn.randomEvents.randomEvent.RandomEvent.RandomEventsRarity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RandomEventRegister {
    public static final Map<RandomEvent.RandomEventsCategories, Integer> RANDOM_EVENTS_CATEGORIES_WEIGHT = new HashMap<>() {{
        put(RandomEventsCategories.STRUCTURE, 3);
        put(RandomEventsCategories.GROUP_FIGHT, 8);
        put(RandomEventsCategories.SPECIAL, 1);
        put(RandomEventsCategories.QUEST, 5);
    }};

    public static final Map<RandomEvent.RandomEventsRarity, Integer> RANDOM_EVENTS_RARITY_WEIGHT = new HashMap<>() {{
        put(RandomEventsRarity.COMMON, 8);
        put(RandomEventsRarity.RARE, 5);
        put(RandomEventsRarity.EPIC, 3);
        put(RandomEventsRarity.LEGENDARY, 1);
    }};

    public static Map<RandomEventsCategories, Map<RandomEventsRarity, List<RandomEvent>>> RANDOM_EVENTS = new HashMap<>();

    public static Map<String, RandomEvent> EVENTS_BY_TAGS = new HashMap<>();

    public static void registerEvent(RandomEvent event) {
        RANDOM_EVENTS.putIfAbsent(event.category, new HashMap<>());
        RANDOM_EVENTS.get(event.category).putIfAbsent(event.rarity, new ArrayList<>());
        RANDOM_EVENTS.get(event.category).get(event.rarity).add(event);

        EVENTS_BY_TAGS.put(event.eventTag, event);
    }

    public static void registerEvents() {
        RANDOM_EVENTS = new HashMap<>();
        registerEvent(new ZombieSkeletonFightEvent());
        registerEvent(new SkeletonFightEvent());
        registerEvent(new VillagerTrapEvent());
        registerEvent(new ZombieFightEvent());
        registerEvent(new PillagerAmbushVillagerEvent());
        registerEvent(new AbandonedWolfEvent());
        registerEvent(new NetherInvasionEvent());
        registerEvent(new PillagerSkeletonFightEvent());
        registerEvent(new PillagerZombieFightEvent());
        registerEvent(new LostVillagerEvent());
        registerEvent(new DrownedThunderFightEvent());
    }
}
