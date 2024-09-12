package endytkn.randomEvents.randomEvents.events

import net.minecraft.core.BlockPos
import net.minecraft.world.InteractionHand
import net.minecraft.world.level.Level
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.Mob
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items

class ZombieSkeletonFightEvent(level: Level, targetBlock: BlockPos): GroupFightEvent(level, targetBlock) {
    init {
        repeat(8) {
            val mobType: EntityType<out Mob> = EntityType.ZOMBIE
            val mob = mobType.create(level)
            mob?.let {
                val stoneSword = ItemStack(Items.STONE_SWORD)
                it.setItemInHand(InteractionHand.MAIN_HAND, stoneSword)
                group1.add(it)
            }
        }
        repeat (8) {
            val mobType: EntityType<out Mob> = EntityType.SKELETON
            val mob = mobType.create(level)
            mob?.let {
                val stoneSword = ItemStack(Items.STONE_SWORD)
                it.setItemInHand(InteractionHand.MAIN_HAND, stoneSword)
                group2.add(it)
            }
        }
    }
}