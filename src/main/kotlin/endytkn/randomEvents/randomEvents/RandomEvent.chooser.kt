package endytkn.randomEvents.randomEvents

import endytkn.randomEvents.events.ZombieSkeletonFightEvent
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level
import kotlin.random.Random

object RandomEventChooser {
    private fun <T>getMapByWeight(mapKeys: MutableMap<T, *>, weights: MutableMap<T, Int>): String {
        val keysToTry = mutableMapOf<String, Int>()
        for (key in mapKeys.keys) {
            val weight = weights[key]
            keysToTry.put(key.toString(), weight!!)
        }
        val totalWeight = keysToTry.values.sum()
        keysToTry.mapValues {  }
        val probabilities = keysToTry.mapValues { (key, weight) ->
            weight.toDouble() / totalWeight
        }
        return selectKeyByWeight(probabilities)
    }

    fun selectKeyByWeight(probabilities: Map<String, Double>): String {
        val rand = Random.nextDouble()
        var cumulativeProbability = 0.0
        for ((key, prob) in probabilities) {
            cumulativeProbability += prob
            if (rand < cumulativeProbability) {
                return key
            }
        }

        throw IllegalStateException("Key not found.")
    }

    fun getEvent(level: Level, targetBlock: BlockPos, isUnderground: Boolean, biomeKey: String, isNight: Boolean): RandomEvent {
        val categoryKey: String = getMapByWeight<RandomEventsCategories>(RandomEventRegisters.RANDOM_EVENTS, RandomEventRegisters.RANDOM_EVENTS_CATEGORIES_WEIGHT)
        val chooseCategories = RandomEventRegisters.RANDOM_EVENTS[RandomEventsCategories.valueOf(categoryKey)]
        if (chooseCategories == null) return ZombieSkeletonFightEvent()
        val rarityKey = getMapByWeight<RandomEventsRarity>(chooseCategories, RandomEventRegisters.RANDOM_EVENTS_RARITY_WEIGHT)
        val events = chooseCategories[RandomEventsRarity.valueOf(rarityKey)]
        if (events == null) return ZombieSkeletonFightEvent()
        val randomIndex = Random.nextInt(events.size)
        val event = events[randomIndex]

        return event.create()
    }
}