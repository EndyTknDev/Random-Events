package endytkn.randomEvents.events

import endytkn.randomEvents.minecraftEventsObservers.MinecraftEventsObservers
import endytkn.randomEvents.randomEvents.RandomEvent
import endytkn.randomEvents.utils.BlockPosUtils
import endytkn.randomEvents.utils.GameInstances
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.InteractionHand
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.monster.Pillager
import net.minecraft.world.entity.npc.Villager
import net.minecraft.world.entity.npc.VillagerProfession
import net.minecraft.world.entity.npc.VillagerType
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraftforge.event.entity.living.LivingDeathEvent

open class PillagerAmbushVillagerEvent: RandomEvent() {
    var pillager: Pillager? = null
    var villager: Villager? = null

    init {
        this.eventTag = "pillagerAmbushVillager"
    }

    override fun create(): RandomEvent {
        return PillagerAmbushVillagerEvent()
    }

    override fun onPrepare() {
        this.distanceThreshold = 20
        super.onPrepare()
        spawnAmbush()
    }

    override fun onReady() {
        MinecraftEventsObservers.onLivingDeathEventObserver.plusAssign(::tickDeathEvent)
        super.onReady()
    }

    override fun onFinishingSuccess() {
        super.onFinishingSuccess()
    }

    override fun onFinishing() {
        MinecraftEventsObservers.onLivingDeathEventObserver.minusAssign(::tickDeathEvent)
        super.onFinishing()
    }

    open fun onPlayerKillPillager(player: Player) {
        player.giveExperiencePoints(100)
        level!!.playSound(null, player.blockPosition(), SoundEvents.VILLAGER_CELEBRATE, SoundSource.NEUTRAL, 1.0f, 1.0f)
        villager!!.lookAt(player, 1.0f, 1.0f)
        val emeralds = (3..5).random()
        val emeraldStack = ItemStack(Items.EMERALD, emeralds)
        player.addItem(emeraldStack)
    }

    open fun spawnVillager() {
        villager = Villager(EntityType.VILLAGER, level!!)
        val nbtVillager = villager!!.persistentData
        nbtVillager.putString(this.eventTag, this.id.toString())
        villager!!.villagerData
            .setProfession(VillagerProfession.FARMER)
            .setType(VillagerType.PLAINS)
            .setLevel(2)
        villager!!.villagerData.profession = VillagerProfession.FARMER
        villager!!.goalSelector.availableGoals.removeIf { true }
        villager!!.health = villager!!.health * 1.2f
        villager!!.addEffect(MobEffectInstance(MobEffects.GLOWING, this.timeLimit))
        var villagerPos = BlockPosUtils.findRandomSurfaceBlockNearby(this.level!!, this.targetBlock!!, 3, 3)
        villager!!.setPos(villagerPos!!.x.toDouble(), villagerPos.y.toDouble(), villagerPos.z.toDouble())
        level!!.addFreshEntity(villager!!)
    }

    open fun spawnPillager() {
        pillager = EntityType.PILLAGER.create(level!!)
        val nbtPillager = pillager!!.persistentData
        nbtPillager.putString(this.eventTag, this.id.toString())
        var pillagerPos = BlockPosUtils.findRandomSurfaceBlockNearby(this.level!!, this.targetBlock!!, 3, 3)
        pillager!!.addEffect(MobEffectInstance(MobEffects.GLOWING, this.timeLimit))
        pillager!!.setItemInHand(InteractionHand.MAIN_HAND, ItemStack(Items.CROSSBOW))
        pillager!!.setPos(pillagerPos!!.x.toDouble(), pillagerPos.y.toDouble(), pillagerPos.z.toDouble())

        level!!.addFreshEntity(pillager!!)
    }

    open fun spawnAmbush() {
        spawnVillager()
        spawnPillager()
    }


    fun tickDeathEvent(event: LivingDeathEvent) {
        val entity = event.entity
        val source = event.source.entity

        if (!this.isEntityOfEvent(entity)) return

        if (entity is Villager) this.onFinishingCanceled()
        if (entity is Pillager) {
            if (source is Player) {
                onPlayerKillPillager(source)
                return
            }
            this.onFinishingCanceled()
        }
    }
}