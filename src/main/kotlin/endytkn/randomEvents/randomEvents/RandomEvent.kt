package endytkn.randomEvents.randomEvents

import endytkn.randomEvents.minecraftEventsObservers.MinecraftEventsObservers
import endytkn.randomEvents.titleManager.TitleManager
import endytkn.randomEvents.utils.Observer
import net.minecraft.client.Minecraft
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraftforge.event.TickEvent
import java.util.UUID

enum class RandomEventStatus {
     NOT_STARTED, PREPARING, READY, WAITING_PLAYER, FINISHING_SUCCESS, FINISHING_CANCELED, FINISHING, FINISHED
}

open class RandomEvent() {
    var level: Level? = null
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

    companion object {
        val name: String = "random_event"
    }

    open fun create(): RandomEvent {
        return RandomEvent()
    }

    open fun initEvent(level: Level, targetBlock: BlockPos, playersGroup: List<ServerPlayer>) {
        this.level = level
        this.targetBlock = targetBlock
        this.playersGroup = playersGroup
        setEventStatus(RandomEventStatus.NOT_STARTED)
    }

    open fun start() {
        if (level == null || targetBlock == null) throw Error("Level and Target Block Must be Initialized")

        onChangeStatusObserver += ::onChangeStatus
        setEventStatus(RandomEventStatus.PREPARING)
        MinecraftEventsObservers.onPlayerTickEventObserver += ::onPlayerTick
    }

    open fun onPlayerTick(event: TickEvent.PlayerTickEvent) {
        if (Minecraft.getInstance().isPaused) return
        onTimeLimit()
        if (isNearby(event.player) || event.side.isServer && status == RandomEventStatus.WAITING_PLAYER) {
            MinecraftEventsObservers.onPlayerTickEventObserver -= ::onPlayerTick
            onPlayerEnter()
        }
    }

    open fun onTimeLimit() {
        timePassed++;
        if (timePassed >= timeLimit) {
            MinecraftEventsObservers.onPlayerTickEventObserver -= ::onPlayerTick
            cancel()
        }
    }

    private fun isNearby(player: Player): Boolean {
        val distance = player.blockPosition().distSqr(targetBlock!!)
        return distance <= distanceThreshold
    }

    open fun onChangeStatus(newStatus: RandomEventStatus) {
        println(newStatus)
        if (newStatus == RandomEventStatus.PREPARING) {
            this.onPrepare()
            return
        }
        else if (newStatus == RandomEventStatus.FINISHING_SUCCESS) {
            this.onFinishingSuccess()
            return
        }
        else if (newStatus == RandomEventStatus.FINISHING_CANCELED) {
            this.onFinishingCanceled()
            return;
        }
        else if (newStatus == RandomEventStatus.FINISHING) {
            this.onFinishing()
            return;
        }
        else if (newStatus == RandomEventStatus.READY) {
            this.onReady()
            return
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
        setEventStatus(RandomEventStatus.READY)
    }

    open fun onReady() {
        TitleManager.showTitle("Something is happening nearby")
    }

    open fun resolve() {
        setEventStatus(RandomEventStatus.FINISHING_SUCCESS)
    }

    open fun cancel() {
        setEventStatus(RandomEventStatus.FINISHING_CANCELED)
    }
}