package endytkn.randomEvents.randomEvent;

import java.util.*;
import java.util.stream.Collectors;

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

    private static List<RandomEvent> filterEventsByCategories(RandomEventScene scene) {
        List<RandomEventsCategory> sceneCategories = List.of(scene.getTimeCategory(), scene.getWeatherCategory());
        System.out.println(sceneCategories);
        return RandomEventRegister.RANDOM_EVENTS.values().stream()
                .filter(event -> event.categories.containsAll(sceneCategories))
                .collect(Collectors.toList());
        /*
        List<RandomEvent> combinedEvents = sceneCategories.stream()
                .filter(RandomEventRegister.RANDOM_EVENTS_CATEGORIES::containsKey)
                .flatMap(category -> RandomEventRegister.RANDOM_EVENTS_CATEGORIES.get(category).stream())
                .collect(Collectors.toList());
         */
    }

    private static RandomEvent findByRarity(List<RandomEvent> events) {
        HashMap<RandomEventsRarity, List<RandomEvent>> eventsByRarities = new HashMap<>();
        for (RandomEvent event: events) {
            eventsByRarities.putIfAbsent(event.rarity, new ArrayList<>());
            eventsByRarities.get(event.rarity).add(event);
        }
        String rarity = getMapByWeight(eventsByRarities, RandomEventRegister.RANDOM_EVENTS_RARITY_WEIGHT);
        List<RandomEvent> selectedEvents = eventsByRarities.get(RandomEventsRarity.valueOf(rarity));
        int randomIndex = random.nextInt(selectedEvents.size());
        return selectedEvents.get(randomIndex);
    }

    public static RandomEvent findRandomEventByScene(RandomEventScene scene) {
        List<RandomEvent> events = filterEventsByCategories(scene);
        RandomEvent randomEvent = findByRarity(events);

        return randomEvent.create();
    }
}
