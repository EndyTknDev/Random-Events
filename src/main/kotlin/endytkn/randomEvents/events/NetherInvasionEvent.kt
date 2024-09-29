package endytkn.randomEvents.events

import endytkn.randomEvents.baseEvents.InvasionEvent.InvasionBaseEvent
import endytkn.randomEvents.baseEvents.InvasionEvent.InvasionWaveGroup
import endytkn.randomEvents.randomEvents.RandomEvent
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal
import net.minecraft.world.entity.animal.IronGolem
import net.minecraft.world.entity.monster.WitherSkeleton
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import java.util.UUID

class NetherInvasionEvent : InvasionBaseEvent() {
    override fun create(): RandomEvent {
        return NetherInvasionEvent()
    }

    override fun onPrepare() {
        setupWaves()
        super.onPrepare()
    }

    override fun onReady() {
        super.onReady()
    }

    fun setupWaves() {
        val groupies = mutableListOf<InvasionWaveGroup>()
        repeat(2) {
            val mobs = mutableMapOf<UUID, Mob>()
            var waveGroup: InvasionWaveGroup = InvasionWaveGroup(this, "Wave Group", mobs)

            repeat(3) {
                var mob = EntityType.WITHER_SKELETON.create(level!!)
                mob.let {
                    val stoneSword = ItemStack(Items.STONE_SWORD)
                    it!!.setCanPickUpLoot(false)
                    it.setItemInHand(InteractionHand.MAIN_HAND, stoneSword)
                    mobs.put(it.uuid, it)
                }
            }
            groupies.add(waveGroup)
        }
        this.wavesGroupies = groupies
    }
}