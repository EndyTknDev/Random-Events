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

class ZombieFightEvent : GroupFightBaseEvent(true) {
    companion object {
        val name: String = "skeleton_fight"
    }

    var minionsCount: Int? = null
    var leaderCount: Int? = null

    init {
        category = RandomEventsCategories.GROUP_FIGHT
        rarity = RandomEventsRarity.COMMON
        this.eventTag = "zombieFight"
    }

    override fun create(): RandomEvent {
        return ZombieFightEvent()
    }

    override fun onPrepare() {
        minionsCount = Random.nextInt(3 + this.playersGroup!!.size * 2, 5 + this.playersGroup!!.size * 2)
        leaderCount = minionsCount!! / 3
        val zombieMobs = mutableMapOf<UUID, Mob>()

        repeat(minionsCount!!) { index ->
            var mobType: EntityType<out Mob> = EntityType.ZOMBIE
            var mob = mobType.create(level!!)
            mob?.let {
                it.setCanPickUpLoot((false))
                zombieMobs.put(it.uuid, it)
            }
        }

        repeat(leaderCount!!) { index ->
            var mobType: EntityType<out Mob> = EntityType.ZOMBIE
            var mob = mobType.create(level!!)

            mob?.let {
                val weapon =  ItemStack(Items.GOLDEN_SWORD)
                it.health = (mob.health * 2)
                it.customName = Component.literal("Zombie Leader")
                it.armorSlots
                val helmet = ItemStack(Items.GOLDEN_HELMET)
                helmet.enchant(Enchantments.UNBREAKING, 5)
                it.setItemSlot(EquipmentSlot.HEAD, helmet)
                it.setCanPickUpLoot((false))
                it.setItemInHand(InteractionHand.MAIN_HAND, weapon)
                zombieMobs.put(it.uuid, it)
            }
        }
        val zombieGroup = GroupFight(this, "zombie", zombieMobs, null)
        mobGroupies.put(zombieGroup.groupName, zombieGroup)
        super.onPrepare()
    }
}