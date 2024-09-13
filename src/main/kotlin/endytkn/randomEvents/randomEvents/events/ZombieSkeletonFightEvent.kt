package endytkn.randomEvents.randomEvents.events

import net.minecraft.core.BlockPos
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.Level
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.Mob
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import java.util.UUID

class ZombieSkeletonFightEvent(level: Level, targetBlock: BlockPos): GroupFightEvent(level, targetBlock, true) {
    init {
        val skeletonMobs = mutableMapOf<UUID, Mob>()
        repeat(8) {
            var mobType: EntityType<out Mob> = EntityType.SKELETON
            var mob = mobType.create(level)
            mob?.let {
                val stoneSword = ItemStack(Items.STONE_SWORD)
                it.setCanPickUpLoot((false))
                it.setItemInHand(InteractionHand.MAIN_HAND, stoneSword)
                skeletonMobs.put(it.uuid, it)
            }
        }
        val skeletonGroup = GroupFight("skeleton", "zombie", skeletonMobs)

        val zombieMobs = mutableMapOf<UUID, Mob>()
        repeat (8) {
            var mobType: EntityType<out Mob> = EntityType.ZOMBIE
            var mob = mobType.create(level)

            mob?.let {
                val stoneSword = ItemStack(Items.STONE_SWORD)
                it.setCanPickUpLoot(false)
                it.setItemInHand(InteractionHand.MAIN_HAND, stoneSword)
                zombieMobs.put(it.uuid, it)
            }
        }

        val zombieGroup = GroupFight("zombie", "skeleton", zombieMobs)

        mobGroupies.put(skeletonGroup.getGroupName(), skeletonGroup)
        mobGroupies.put(zombieGroup.getGroupName(), zombieGroup)
    }
}