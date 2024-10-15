package endytkn.randomEvents.events

import endytkn.randomEvents.minecraftEventsObservers.MinecraftEventsObservers
import endytkn.randomEvents.packets.PacketHandler
import endytkn.randomEvents.packets.ParticlePacket
import endytkn.randomEvents.randomEvents.RandomEvent
import endytkn.randomEvents.utils.BlockPosUtils
import endytkn.randomEvents.utils.CommandUtils
import net.minecraft.client.Minecraft
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.network.chat.Component
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal
import net.minecraft.world.entity.animal.Wolf
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.DyeColor
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.phys.Vec3
import net.minecraftforge.event.TickEvent
import net.minecraftforge.event.entity.living.LivingDeathEvent
import net.minecraftforge.event.entity.player.PlayerInteractEvent
import net.minecraftforge.network.PacketDistributor
import thedarkcolour.kotlinforforge.forge.vectorutil.v3d.toVec3
import kotlin.random.Random

class AbandonedWolfEvent : RandomEvent() {
    var wolf: Wolf? = null;
    var cryInterval = 20 * 10
    var cryIntervalPassed = cryInterval
    var player: Player? = null

    val possiblesWolfNames = listOf(
        "Maithe",
        "Cloé"
    )

    init {
        this.eventTag = "abandonedWolf"
    }

    override fun create(): RandomEvent {
        return AbandonedWolfEvent()
    }

    override fun onPrepare() {
        spawnWolf()
        MinecraftEventsObservers.onServerTickEventObserver += ::tickWolf
        MinecraftEventsObservers.onPlayerInteractEntityEventObserver += ::wolfInteract
        MinecraftEventsObservers.onLivingDeathEventObserver += ::wolfDeathTick
        super.onPrepare()
    }

    override fun onFinishing() {
        MinecraftEventsObservers.onServerTickEventObserver -= ::tickWolf
        MinecraftEventsObservers.onPlayerInteractEntityEventObserver -= ::wolfInteract
        MinecraftEventsObservers.onLivingDeathEventObserver -= ::wolfDeathTick
        super.onFinishing()
    }

    override fun onFinishingSuccess() {
        wolf!!.ownerUUID = player!!.uuid
        wolf!!.heal(4.0f)
        wolf!!.collarColor = DyeColor.RED
        wolf!!.customName = null
        //wolf!!.customName = Component.literal(possiblesWolfNames.random())
        wolf!!.isCustomNameVisible = false
        player!!.giveExperiencePoints(100)
        level!!.playSound(wolf, wolf!!.blockPosition(), SoundEvents.WOLF_AMBIENT, SoundSource.NEUTRAL, 1.0f, 1f)

        repeat(10) {
            val motion = Vec3(Random.nextDouble(1.0), Random.nextDouble(1.0), Random.nextDouble(1.0))
            val particlePacket = ParticlePacket(ParticleTypes.HEART, wolf!!.blockPosition().toVec3(), motion)
            PacketHandler.sendToClients(particlePacket, level!!)
        }
        CommandUtils.sendCommand("particle minecraft:heart ${wolf!!.x} ${wolf!!.y} ${wolf!!.z} 1 1 1 1 10 force")

        dropReward()
        super.onFinishingSuccess()
    }

    fun dropReward() {
        val emeraldStack = ItemStack(Items.EMERALD, 1)
        val emeraldEntity = ItemEntity(
            level!!,
            wolf!!.x,
            wolf!!.y + 0.5,
            wolf!!.z,
            emeraldStack
        )

        val direction = Vec3(
            player!!.x - wolf!!.x,
            player!!.y - wolf!!.y,
            player!!.z - wolf!!.z
        ).normalize()

        val speedMultiplier = 0.2
        emeraldEntity.setDeltaMovement(
            direction.x * speedMultiplier,
            direction.y * speedMultiplier + 0.2,
            direction.z * speedMultiplier
        )

        emeraldEntity.setPickUpDelay(10)

        level!!.addFreshEntity(emeraldEntity)
    }

    fun spawnWolf() {
        wolf = EntityType.WOLF.create(level!!)
        wolf!!.isTame = true
        wolf!!.health = wolf!!.health * 0.2f
        wolf!!.isInSittingPose = true
        var pos = BlockPosUtils.findRandomSurfaceBlockNearby(this.level!!, this.targetBlock!!, 3, 3)
        wolf!!.setPos(pos!!.x.toDouble(), pos.y.toDouble(), pos.z.toDouble())
        wolf!!.collarColor = DyeColor.GRAY
        wolf!!.customName = Component.literal("Abandoned Wolf")
        wolf!!.isCustomNameVisible = true
        wolf!!.persistentData.putString(this.eventTag, this.id.toString())
        wolf!!.goalSelector.addGoal(1, LookAtPlayerGoal(wolf!!, Player::class.java, 20.0f))
        level!!.addFreshEntity(wolf!!)
    }

    fun wolfCry() {
        level!!.playSound(wolf, wolf!!.blockPosition(), SoundEvents.WOLF_WHINE, SoundSource.NEUTRAL, 1.0f, 1f)
    }

    fun wolfDeathTick(event: LivingDeathEvent) {
        val entity = event.entity

        if (entity !is Wolf || !this.isEntityOfEvent(entity)) return
        onFinishingCanceled()
    }

    fun wolfInteract(event: PlayerInteractEvent.EntityInteract) {
        val entity = event.target
        val player = event.entity
        if (entity !is Wolf || !this.isEntityOfEvent(entity)) return
        val itemInHand = player.mainHandItem
        if (itemInHand.item.isEdible) {
            this.player = player;
            resolve()
        }
    }

    fun tickWolf(e: TickEvent.ServerTickEvent) {
        if (Minecraft.getInstance().isPaused) return
        if (cryIntervalPassed <= 0) {
            wolfCry()
            cryIntervalPassed = cryInterval
        }
        cryIntervalPassed--
    }
}