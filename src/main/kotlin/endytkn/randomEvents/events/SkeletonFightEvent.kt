package endytkn.randomEvents.events

import endytkn.randomEvents.baseEvents.GroupFight.GroupFight
import endytkn.randomEvents.baseEvents.GroupFight.GroupFightBaseEvent
import endytkn.randomEvents.randomEvents.RandomEvent
import endytkn.randomEvents.randomEvents.RandomEventsCategories
import endytkn.randomEvents.randomEvents.RandomEventsRarity
import net.minecraft.network.chat.Component
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
        this.eventTag = "skeletonFight"
    }

    override fun create(): RandomEvent {
        return SkeletonFightEvent()
    }

    override fun onPrepare() {
        val lightningBolt = EntityType.LIGHTNING_BOLT.create(level!!)
        lightningBolt!!.setPos(targetBlock!!.x.toDouble(), targetBlock!!.y.toDouble(), targetBlock!!.z.toDouble())
        lightningBolt.setSecondsOnFire(0)
        level!!.addFreshEntity(lightningBolt)
        minionsCount = Random.nextInt(2 + this.playersGroup!!.size * 2, 4 + this.playersGroup!!.size * 2)
        leaderCount = minionsCount!! / 3
        val skeletonMobs = mutableMapOf<UUID, Mob>()

        repeat(minionsCount!!) { index ->
            var mobType: EntityType<out Mob> = EntityType.SKELETON
            var mob = mobType.create(level!!)
            mob?.let {
                val weapon = ItemStack(if (index % 3 == 0) Items.BOW else Items.STONE_SWORD)
                it.setCanPickUpLoot((false))
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
                val helmet = ItemStack(Items.GOLDEN_HELMET)
                val chestplate = ItemStack(Items.GOLDEN_CHESTPLATE)
                helmet.enchant(Enchantments.UNBREAKING, 5)
                chestplate.enchant(Enchantments.UNBREAKING, 5)
                it.addEffect(MobEffectInstance(MobEffects.GLOWING, this.timeLimit))
                it.setItemSlot(EquipmentSlot.HEAD, helmet)
                it.setItemSlot(EquipmentSlot.CHEST, chestplate)
                it.setCanPickUpLoot((false))
                it.setItemInHand(InteractionHand.MAIN_HAND, weapon)
                skeletonMobs.put(it.uuid, it)
            }
        }
        val skeletonGroup = GroupFight(this, "skeleton", skeletonMobs, null)
        mobGroupies.put(skeletonGroup.groupName, skeletonGroup)
        super.onPrepare()
    }
}