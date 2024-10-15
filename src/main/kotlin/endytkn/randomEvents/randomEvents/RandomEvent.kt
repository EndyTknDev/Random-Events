package endytkn.randomEvents.randomEvents

import endytkn.randomEvents.minecraftEventsObservers.MinecraftEventsObservers
import endytkn.randomEvents.titleManager.TitleManager
import endytkn.randomEvents.utils.GameInstances
import endytkn.randomEvents.utils.Observer
import net.minecraft.client.Minecraft
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraftforge.event.TickEvent
import java.util.UUID

enum class RandomEventStatus {
     NOT_STARTED, PREPARING, READY, WAITING_PLAYER, FINISHING_SUCCESS, FINISHING_CANCELED, FINISHING, FINISHED
}

open class RandomEvent() {
    var level: ServerLevel? = null
    var targetBlock: BlockPos? = null
    var playersGroup: List<ServerPlayer>? = null
    val id: UUID = UUID.randomUUID()
    val onChangeStatusObserver = Observer<RandomEventStatus>()
    var status: RandomEventStatus = RandomEventStatus.NOT_STARTED
    var distanceThreshold = 50
    var timeLimit = 20 * 60 * 10
    var timePassed = 0
    var title: String = "Random Event"
    var category: RandomEventsCategories = RandomEventsCategories.SPECIAL
    var rarity: RandomEventsRarity = RandomEventsRarity.COMMON
    var eventTag = "randomEvent"

    open fun create(): RandomEvent {
        return RandomEvent()
    }

    open fun initEvent(level: ServerLevel, targetBlock: BlockPos, playersGroup: List<ServerPlayer>) {
        this.level = level
        this.targetBlock = targetBlock
        this.playersGroup = playersGroup
        setEventStatus(RandomEventStatus.NOT_STARTED)
    }

    open fun start() {
        if (level == null || targetBlock == null) throw Error("Level and Target Block Must be Initialized")

        onChangeStatusObserver += ::onChangeStatus
        setEventStatus(RandomEventStatus.PREPARING)
        MinecraftEventsObservers.onPlayerTickEventObserver.plusAssign(::tickEvent)
    }

    open fun tickEvent(event: TickEvent.PlayerTickEvent) {
        try {
            if (Minecraft.getInstance().isPaused || event.player.isSpectator) return
            onTimeLimit()
            if (isNearby(event.player) && event.side.isServer && status == RandomEventStatus.WAITING_PLAYER) {
                onPlayerEnter()
            }
        } catch (e: Error) {
            println(e)
            throw e;
        }
    }

    open fun onTimeLimit() {
        timePassed++;
        if (timePassed >= timeLimit) {
            cancelEvent()
        }
    }

    private fun isNearby(player: Player): Boolean {
        val distance = player.blockPosition().distSqr(targetBlock!!)
        return distance <= distanceThreshold
    }

    open fun onChangeStatus(newStatus: RandomEventStatus) {
        level!!.server!!.sendSystemMessage(Component.literal("Event $eventTag status: $newStatus"))
        when(newStatus) {
            RandomEventStatus.PREPARING -> { this.onPrepare() }
            RandomEventStatus.FINISHING_SUCCESS -> { this.onFinishingSuccess() }
            RandomEventStatus.FINISHING_CANCELED -> { this.onFinishingCanceled(); }
            RandomEventStatus.FINISHING -> { this.onFinishing(); }
            RandomEventStatus.READY -> { this.onReady() }
            else -> {}
        }

    }

    open fun setEventStatus(newStatus: RandomEventStatus) {
        this.status = newStatus
        onChangeStatusObserver.invoke(status)
    }

    open fun onFinishingSuccess() {
        setEventStatus(RandomEventStatus.FINISHING)
    }

    open fun onFinishingCanceled() {
        setEventStatus(RandomEventStatus.FINISHING)
    }

    open fun onFinishing() {
        setEventStatus(RandomEventStatus.FINISHED)
    }

    open fun onPrepare() {
        setEventStatus(RandomEventStatus.WAITING_PLAYER)
    }

    open fun onPlayerEnter() {
        MinecraftEventsObservers.onPlayerTickEventObserver.minusAssign(::tickEvent)
        setEventStatus(RandomEventStatus.READY)
    }

    open fun onReady() {}

    open fun resolve() {
        setEventStatus(RandomEventStatus.FINISHING_SUCCESS)
    }

    open fun cancelEvent() {
        MinecraftEventsObservers.onPlayerTickEventObserver.minusAssign(::tickEvent)
        setEventStatus(RandomEventStatus.FINISHING_CANCELED)
    }

    open fun isEntityOfEvent(entity: Entity): Boolean {
        val nbt = entity.persistentData
        return nbt.contains(this.eventTag) && nbt.getString(this.eventTag) == this.id.toString()
    }
}