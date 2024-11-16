package endytkn.randomEvents.randomEvent;

import endytkn.randomEvents.RandomEventsMod;
import endytkn.randomEvents.events.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RandomEventRegister {
    public static final Map<RandomEventsCategory, Integer> RANDOM_EVENTS_CATEGORIES_WEIGHT = new HashMap<>() {{
        put(RandomEventsCategory.STRUCTURE, 3);
        put(RandomEventsCategory.GROUP_FIGHT, 8);
        put(RandomEventsCategory.SPECIAL, 1);
        put(RandomEventsCategory.QUEST, 5);
    }};

    public static final Map<RandomEventsRarity, Integer> RANDOM_EVENTS_RARITY_WEIGHT = new HashMap<>() {{
        put(RandomEventsRarity.COMMON, 20);
        put(RandomEventsRarity.RARE, 12);
        put(RandomEventsRarity.EPIC, 4);
        put(RandomEventsRarity.LEGENDARY, 1);
    }};
    public static Map<RandomEventsCategory, List<RandomEvent>> RANDOM_EVENTS_CATEGORIES = new HashMap<>();
    public static Map<String, RandomEvent> RANDOM_EVENTS = new HashMap<>();
    public static Map<RandomEventsRarity, List<RandomEvent>> RANDOM_EVENTS_RARITIES = new HashMap<>();

    static {
        for (RandomEventsRarity rarity : RandomEventsRarity.values()) {
            RANDOM_EVENTS_RARITIES.put(rarity, new ArrayList<>());
        }
    }

    private static void registerEvent(RandomEvent event) {
        RandomEventsMod.LOGGER.info("Registering %s".formatted(event.eventTag));
        if (RANDOM_EVENTS.containsKey(event.eventTag)) {
            throw new RuntimeException("tag id %s already registered.".formatted(event.eventTag));
        }

        RANDOM_EVENTS.put(event.eventTag, event);
        RANDOM_EVENTS_RARITIES.get(event.rarity).add(event);
        for (RandomEventsCategory category: event.categories) {
            RANDOM_EVENTS_CATEGORIES.putIfAbsent(category, new ArrayList<>());
            RANDOM_EVENTS_CATEGORIES.get(category).add(event);
        }
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
        registerEvent(new IllagerFightEvent());
        registerEvent(new IllagerVillagerFightEvent());
    }
}
