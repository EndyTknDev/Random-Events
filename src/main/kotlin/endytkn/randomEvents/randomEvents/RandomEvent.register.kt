package endytkn.randomEvents.randomEvents

import endytkn.randomEvents.events.AbandonedWolfEvent
import endytkn.randomEvents.events.NetherInvasionEvent
import endytkn.randomEvents.events.PillagerAmbushVillagerEvent
import endytkn.randomEvents.events.SkeletonFightEvent
import endytkn.randomEvents.events.VillagerTrapEvent
import endytkn.randomEvents.events.ZombieFightEvent
import endytkn.randomEvents.events.ZombieSkeletonFightEvent

enum class RandomEventsCategories {
    STRUCTURE,
    GROUP_FIGHT,
    QUEST,
    SPECIAL,
}

enum class RandomEventsRarity {
    COMMON,
    RARE,
    EPIC,
    LEGENDARY
}

object RandomEventRegisters {
    val RANDOM_EVENTS_CATEGORIES_WEIGHT: MutableMap<RandomEventsCategories, Int> = mutableMapOf(
        RandomEventsCategories.STRUCTURE to 3,
        RandomEventsCategories.GROUP_FIGHT to 8,
        RandomEventsCategories.SPECIAL to 1,
        RandomEventsCategories.QUEST to 5,
    )

    val RANDOM_EVENTS_RARITY_WEIGHT: MutableMap<RandomEventsRarity, Int> = mutableMapOf(
        RandomEventsRarity.COMMON to 8,
        RandomEventsRarity.RARE to 5,
        RandomEventsRarity.EPIC to 3,
        RandomEventsRarity.LEGENDARY to 1,
    )

    var RANDOM_EVENTS = mutableMapOf<RandomEventsCategories, MutableMap<RandomEventsRarity, kotlin.collections.MutableList<RandomEvent>>>()

    var EVENTS_BY_TAGS = mutableMapOf<String, RandomEvent>()
    //val ZOMBIE_SKELETON_EVENT: RegistryObject<ZombieSkeletonFightEvent> = RANDOM_EVENT_REGISTRY.register(ZombieSkeletonFightEvent.name, ::ZombieSkeletonFightEvent)
    //val NETHER_PORTAL_EVENT: RegistryObject<NetherPortalEvent> = RANDOM_EVENT_REGISTRY.register(NetherPortalEvent.name, ::NetherPortalEvent)

    fun registerEvent(event: RandomEvent) {
        RANDOM_EVENTS.putIfAbsent(event.category, mutableMapOf<RandomEventsRarity, MutableList<RandomEvent>>())
        RANDOM_EVENTS[event.category]!!.putIfAbsent(event.rarity, mutableListOf<RandomEvent>())
        RANDOM_EVENTS[event.category]!![event.rarity]!!.add(event)

        EVENTS_BY_TAGS[event.eventTag] = event
    }

    fun registerEvents() {
        RANDOM_EVENTS = mutableMapOf<RandomEventsCategories, MutableMap<RandomEventsRarity, kotlin.collections.MutableList<RandomEvent>>>()
        registerEvent(ZombieSkeletonFightEvent())
        registerEvent(SkeletonFightEvent())
        registerEvent(VillagerTrapEvent())
        registerEvent(ZombieFightEvent())
        registerEvent(PillagerAmbushVillagerEvent())
        registerEvent(AbandonedWolfEvent())
        //registerEvent(NetherPortalEvent())
        registerEvent(NetherInvasionEvent())
    }


}