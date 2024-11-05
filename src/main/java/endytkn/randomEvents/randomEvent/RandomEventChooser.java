package endytkn.randomEvents.randomEvent;

import endytkn.randomEvents.events.ZombieSkeletonFightEvent;
import endytkn.randomEvents.randomEvent.RandomEvent.RandomEventsRarity;
import endytkn.randomEvents.randomEvent.RandomEvent.RandomEventsCategories;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class RandomEventChooser {
    private static final Random random = new Random();

    private static <T> String getMapByWeight(Map<T, ?> mapKeys, Map<T, Integer> weights) {
        Map<String, Integer> keysToTry = new HashMap<>();

        for (T key : mapKeys.keySet()) {
            Integer weight = weights.get(key);
            if (weight != null) {
                keysToTry.put(key.toString(), weight);
            }
        }

        int totalWeight = keysToTry.values().stream().mapToInt(Integer::intValue).sum();

        Map<String, Double> probabilities = new HashMap<>();
        for (Map.Entry<String, Integer> entry : keysToTry.entrySet()) {
            probabilities.put(entry.getKey(), (double) entry.getValue() / totalWeight);
        }

        return selectKeyByWeight(probabilities);
    }

    private static String selectKeyByWeight(Map<String, Double> probabilities) {
        double rand = random.nextDouble();
        double cumulativeProbability = 0.0;

        for (Map.Entry<String, Double> entry : probabilities.entrySet()) {
            cumulativeProbability += entry.getValue();
            if (rand < cumulativeProbability) {
                return entry.getKey();
            }
        }

        throw new IllegalStateException("Key not found.");
    }

    public static RandomEvent getEvent(Level level, BlockPos targetBlock, boolean isUnderground, String biomeKey, boolean isNight) {
        String categoryKey = getMapByWeight(RandomEventRegister.RANDOM_EVENTS, RandomEventRegister.RANDOM_EVENTS_CATEGORIES_WEIGHT);
        Map<RandomEventsRarity, List<RandomEvent>> chooseCategories = RandomEventRegister.RANDOM_EVENTS.get(RandomEventsCategories.valueOf(categoryKey));

        if (chooseCategories == null) return new ZombieSkeletonFightEvent();
        String rarityKey = getMapByWeight(chooseCategories, RandomEventRegister.RANDOM_EVENTS_RARITY_WEIGHT);
        List<RandomEvent> randomEvents = chooseCategories.get(RandomEventsRarity.valueOf(rarityKey));
        int randomIndex = random.nextInt(chooseCategories.size());
        RandomEvent selectedEvent = randomEvents.get(randomIndex);

        return selectedEvent.create();
    }
}
