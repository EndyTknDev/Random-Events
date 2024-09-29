package endytkn.randomEvents.events

import endytkn.randomEvents.baseEvents.GroupFight.GroupFight
import endytkn.randomEvents.baseEvents.GroupFight.GroupFightBaseEvent
import endytkn.randomEvents.randomEvents.RandomEventsCategories
import endytkn.randomEvents.randomEvents.RandomEventsRarity
import endytkn.randomEvents.randomEvents.RandomEvent
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.Mob
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import java.util.UUID

class ZombieSkeletonFightEvent: GroupFightBaseEvent(true) {
    companion object {
        val name: String = "zombie_skeleton_fight"
    }

    init {
        category = RandomEventsCategories.GROUP_FIGHT
        rarity = RandomEventsRarity.COMMON
    }

    override fun create(): RandomEvent {
        return ZombieSkeletonFightEvent()
    }

    override fun onPrepare() {
        val skeletonMobs = mutableMapOf<UUID, Mob>()
        repeat(8) {
            var mobType: EntityType<out Mob> = EntityType.SKELETON
            var mob = mobType.create(level!!)
            mob?.let {
                val stoneSword = ItemStack(Items.STONE_SWORD)
                it.setCanPickUpLoot((false))
                it.setItemInHand(InteractionHand.MAIN_HAND, stoneSword)
                skeletonMobs.put(it.uuid, it)
            }
        }
        val skeletonGroup = GroupFight(this, "skeleton", skeletonMobs, "zombie")

        val zombieMobs = mutableMapOf<UUID, Mob>()
        repeat (8) {
            var mobType: EntityType<out Mob> = EntityType.ZOMBIE
            var mob = mobType.create(level!!)

            mob?.let {
                val stoneSword = ItemStack(Items.STONE_SWORD)
                it.setCanPickUpLoot(false)
                it.setItemInHand(InteractionHand.MAIN_HAND, stoneSword)
                zombieMobs.put(it.uuid, it)
            }
        }

        val zombieGroup = GroupFight(this, "zombie", zombieMobs, "skeleton")

        mobGroupies.put(skeletonGroup.groupName, skeletonGroup)
        mobGroupies.put(zombieGroup.groupName, zombieGroup)
        super.onPrepare()
    }
}