package endytkn.randomEvents.events

import endytkn.randomEvents.baseEvents.GroupFight.GroupFight
import endytkn.randomEvents.baseEvents.GroupFight.GroupFightBaseEvent
import endytkn.randomEvents.randomEvents.RandomEvent
import endytkn.randomEvents.randomEvents.RandomEventsCategories
import endytkn.randomEvents.randomEvents.RandomEventsRarity
import net.minecraft.core.Vec3i
import net.minecraft.network.chat.Component
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.InteractionHand
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.Mob
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.enchantment.Enchantments
import java.util.UUID
import kotlin.random.Random

class SkeletonFightEvent : GroupFightBaseEvent(true) {
    companion object {
        val name: String = "skeleton_fight"
    }

    var minionsCount: Int? = null
    var leaderCount: Int? = null

    init {
        category = RandomEventsCategories.GROUP_FIGHT
        rarity = RandomEventsRarity.COMMON
    }

    override fun create(): RandomEvent {
        return SkeletonFightEvent()
    }

    override fun onPrepare() {
        minionsCount = Random.nextInt(3 + this.playersGroup!!.size * 2, 5 + this.playersGroup!!.size * 2)
        leaderCount = minionsCount!! / 3
        val skeletonMobs = mutableMapOf<UUID, Mob>()

        repeat(minionsCount!!) { index ->
            var mobType: EntityType<out Mob> = EntityType.SKELETON
            var mob = mobType.create(level!!)
            mob?.let {
                val weapon =  ItemStack(if (index % 3 == 0) Items.BOW else Items.STONE_SWORD)
                it.setCanPickUpLoot((false))
                it.addEffect(MobEffectInstance(MobEffects.GLOWING, 1, this.timeLimit))
                it.setItemInHand(InteractionHand.MAIN_HAND, weapon)
                skeletonMobs.put(it.uuid, it)
            }
        }

        repeat(leaderCount!!) { index ->
            var mobType: EntityType<out Mob> = EntityType.SKELETON
            var mob = mobType.create(level!!)
            mob?.let {
                val weapon =  ItemStack(if (index % 3 == 0) Items.BOW else Items.GOLDEN_SWORD)
                it.health = (mob.health * 2)
                it.customName = Component.literal("Skeleton Leader")
                it.armorSlots
                val helmet = ItemStack(Items.GOLDEN_HELMET)
                helmet.enchant(Enchantments.UNBREAKING, 5)
                it.addEffect(MobEffectInstance(MobEffects.GLOWING, this.timeLimit))
                it.setItemSlot(EquipmentSlot.HEAD, helmet)
                it.setCanPickUpLoot((false))
                it.setItemInHand(InteractionHand.MAIN_HAND, weapon)
                skeletonMobs.put(it.uuid, it)
            }
        }
        val skeletonGroup = GroupFight(this, "skeleton", skeletonMobs, null)
        mobGroupies.put(skeletonGroup.groupName, skeletonGroup)
        level!!.playSound(null, targetBlock!!.offset(Vec3i(0, 5, 0)), SoundEvents.SKELETON_AMBIENT, SoundSource.AMBIENT, 10.0f, 1f)
        level!!.playSound(null, targetBlock!!.offset(Vec3i(0, 5, 0)), SoundEvents.SKELETON_AMBIENT, SoundSource.AMBIENT, 5.0f, 1f)
        level!!.playSound(null, targetBlock!!.offset(Vec3i(0, 5, 0)), SoundEvents.SKELETON_AMBIENT, SoundSource.AMBIENT, 3.0f, 1f)
        super.onPrepare()
    }
}