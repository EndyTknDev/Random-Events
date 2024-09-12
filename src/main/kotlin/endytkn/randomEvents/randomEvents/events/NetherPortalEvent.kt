package endytkn.randomEvents.randomEvents.events

import net.minecraft.core.BlockPos
import net.minecraft.core.Vec3i
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.EntityType
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3

class NetherPortalEvent(level: Level, targetBlock: BlockPos) : SpawnStructureEvent(level, targetBlock, "minecraft", "ruined_portal/portal_2") {
    override fun onPrepare() {
        super.onPrepare()
        spawnMobs()
    }

    fun spawnMobs() {
        for (i in 0..1) {
            val mob = EntityType.PIGLIN.create(level)
            mob?.let {
                val stoneSword = ItemStack(Items.GOLDEN_SWORD)
                it.setItemInHand(InteractionHand.MAIN_HAND, stoneSword)
                mob.setPos(Vec3(targetBlock.x.toDouble() + 3, targetBlock.y.toDouble() + 2, targetBlock.z.toDouble() + 2*(i+1)))
                level.addFreshEntity(mob)
                level.playSound(null, targetBlock.offset(Vec3i(0, 5, 0)), SoundEvents.GENERIC_EXPLODE, SoundSource.AMBIENT, 1000.0f, 1.0f)

            }
        }
    }
}