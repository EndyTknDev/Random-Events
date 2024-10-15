package endytkn.randomEvents.events

import com.mojang.brigadier.CommandDispatcher
import endytkn.randomEvents.entities.GenericEntity
import endytkn.randomEvents.minecraftEventsObservers.MinecraftEventsObservers
import endytkn.randomEvents.randomEvents.RandomEvent
import endytkn.randomEvents.randomEvents.RandomEventStatus
import endytkn.randomEvents.randomEvents.RandomEventsCategories
import endytkn.randomEvents.randomEvents.RandomEventsRarity
import endytkn.randomEvents.utils.BlockPosUtils
import endytkn.randomEvents.utils.GameInstances
import net.minecraft.commands.CommandSourceStack
import net.minecraft.core.BlockPos
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.ai.behavior.VillagerGoalPackages
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal
import net.minecraft.world.entity.monster.Pillager
import net.minecraft.world.entity.npc.Villager
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3
import net.minecraftforge.event.TickEvent
import net.minecraftforge.event.entity.living.LivingDeathEvent
import kotlin.random.Random

class VillagerTrapEvent: RandomEvent()  {
    private var commandDispatcher: CommandDispatcher<CommandSourceStack>? = null;
    private var commandSource: CommandSourceStack? = null;
    private var villager: GenericEntity<Villager>? = null
    private var pillagersLeft = Random.nextInt(3, 4)

    init {
        category = RandomEventsCategories.GROUP_FIGHT
        rarity = RandomEventsRarity.COMMON
        this.eventTag = "villagerTrap"
    }

    override fun create(): RandomEvent {
        return VillagerTrapEvent()
    }

    override fun onPrepare() {
        this.distanceThreshold = 10
        commandSource = level!!.server!!.createCommandSourceStack()
        commandDispatcher = level!!.server!!.commands.dispatcher
        MinecraftEventsObservers.onLivingDeathEventObserver += ::tickDeath

        spawnVillager()
        super.onPrepare()
    }

    override fun onPlayerEnter() {
        super.onPlayerEnter()
    }

    override fun onReady() {
        super.onReady()
        spawnPillagers()
    }

    override fun onFinishing() {
        super.onFinishing()
        MinecraftEventsObservers.onLivingDeathEventObserver -= ::tickDeath
    }

    fun spawnVillager() {
        villager = GenericEntity(EntityType.VILLAGER, level!!)
        villager!!.setPos(Vec3(targetBlock!!.x.toDouble(), targetBlock!!.y.toDouble(), targetBlock!!.z.toDouble()))
        villager!!.goalSelector.addGoal(1, LookAtPlayerGoal(villager!!, Player::class.java, 15.0f))
        val nbt = villager!!.persistentData
        nbt.putString(this.eventTag, this.id.toString())

        level!!.addFreshEntity(villager!!)
    }

    fun spawnPillagers() {
        villager!!.remove(Entity.RemovalReason.DISCARDED)
        spawnPillager(villager!!.blockPosition())

        repeat(pillagersLeft - 1) {
            var mobPos = BlockPosUtils.findRandomSurfaceBlockNearby(this.level!!, this.targetBlock!!, 10, 10)
            if (mobPos == null) mobPos = this.targetBlock
            spawnPillager(mobPos!!)
        }
    }

    fun spawnPillager(blockPos: BlockPos) {
        var pillager = EntityType.PILLAGER.create(level!!)
        pillager!!.setPos(Vec3(blockPos.x.toDouble(), blockPos.y.toDouble(), blockPos.z.toDouble()))
        pillager.setPersistenceRequired()
        pillager.setItemInHand(InteractionHand.MAIN_HAND, ItemStack(Items.CROSSBOW))
        pillager.persistentData.putString(this.eventTag, this.id.toString())
        spawnParticle(pillager.blockPosition())
        level!!.addFreshEntity(pillager)
    }

    fun tickDeath(event: LivingDeathEvent) {
        val entity = event.entity

        if (this.isEntityOfEvent(entity)) return

        if (entity is Villager) {
            this.setEventStatus(RandomEventStatus.FINISHING_CANCELED)
            return
        }

        if (entity is Pillager)
            pillagersLeft--

        if (pillagersLeft <= 0)
            this.setEventStatus(RandomEventStatus.FINISHING_SUCCESS)
    }

    fun spawnParticle(blockPos: BlockPos) {
        val particleCommand = "particle minecraft:cloud ${blockPos.x} ${blockPos.y} ${blockPos.z} 0 1 0.5 0 100 force"
        level!!.playSound(null, blockPos, SoundEvents.ENDERMAN_TELEPORT, SoundSource.HOSTILE, 1.0f, 1.0f)
        commandDispatcher!!.execute(particleCommand, commandSource)
    }

    override fun onFinishingCanceled() {
        villager!!.remove(Entity.RemovalReason.DISCARDED)
        super.onFinishingCanceled()
    }
}