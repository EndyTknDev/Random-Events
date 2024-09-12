package endytkn.randomEvents.randomEvents

import endytkn.randomEvents.minecraftEventsObservers.MinecraftEventsObservers
import endytkn.randomEvents.utils.Observer
import net.minecraft.client.Minecraft
import net.minecraft.core.BlockPos
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraftforge.event.TickEvent
import java.util.UUID

enum class RandomEventStatus {
     NOT_STARTED, PREPARING, READY, FINISHING_SUCCESS, FINISHING_CANCELED, FINISHING, FINISHED
}

open class RandomEvent(val level: Level, val targetBlock: BlockPos) {
    val id: UUID
    val onChangeStatusObserver = Observer<RandomEventStatus>()
    var status: RandomEventStatus = RandomEventStatus.NOT_STARTED
    var distanceThreshold = 50
    var timeLimit = 20 * 60 * 10
    var timePassed = 0
    init {
        onChangeStatusObserver += ::onChangeStatus
        MinecraftEventsObservers.onPlayerTickEventObserver += ::onPlayerTick
        id = UUID.randomUUID()
    }

    fun start() {
        setEventStatus(RandomEventStatus.PREPARING)
    }

    open fun onPlayerTick(event: TickEvent.PlayerTickEvent) {
        if (Minecraft.getInstance().isPaused) return
        onTimeLimit()
        if (isNearby(event.player) || event.side.isServer) {
            setEventStatus(RandomEventStatus.READY)
            MinecraftEventsObservers.onPlayerTickEventObserver -= ::onPlayerTick
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
        val distance = player.blockPosition().distSqr(targetBlock)
        return distance <= distanceThreshold
    }

    open fun onChangeStatus(newStatus: RandomEventStatus) {
        if (newStatus == RandomEventStatus.PREPARING) {
            onPrepare()
            return
        }
        else if (newStatus == RandomEventStatus.FINISHING_SUCCESS) {
            onFinishingSuccess()
            return
        }
        else if (newStatus == RandomEventStatus.FINISHING_CANCELED) {
            onFinishingCanceled()
        }
        else if (newStatus == RandomEventStatus.FINISHING) {
            onFinishing()
        }
        else if (newStatus == RandomEventStatus.READY) {
            onReady()
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

    open fun onPrepare() {}

    open fun onReady() {}

    open fun resolve() {
        setEventStatus(RandomEventStatus.FINISHING_SUCCESS)
    }

    open fun cancel() {
        setEventStatus(RandomEventStatus.FINISHING_CANCELED)
    }

    companion object
}