package endytkn.randomEvents.baseEvents.InvasionEvent

import com.mojang.brigadier.CommandDispatcher
import endytkn.randomEvents.minecraftEventsObservers.MinecraftEventsObservers
import endytkn.randomEvents.randomEvents.RandomEvent
import endytkn.randomEvents.randomEvents.RandomEventStatus
import endytkn.randomEvents.utils.BlockPosUtils
import endytkn.randomEvents.utils.Observer
import net.minecraft.commands.CommandSourceStack
import net.minecraft.core.BlockPos
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.phys.Vec3
import net.minecraftforge.event.TickEvent
import net.minecraftforge.event.entity.living.LivingDeathEvent

enum class InvasionWaveStatus {
    NOT_STARTED_WAVE, PREPARING_WAVE, STARTED_WAVE, FINISHED_WAVE
}

open class InvasionBaseEvent: RandomEvent() {
    private var commandDispatcher: CommandDispatcher<CommandSourceStack>? = null;
    private var commandSource: CommandSourceStack? = null;
    var invasionTimer = 20 * 60 * 5
    var invasionTimerPassed = invasionTimer
    var actualWave = 0
    var wavesGroupies = mutableListOf<InvasionWaveGroup>()
    var waveStatus: InvasionWaveStatus = InvasionWaveStatus.NOT_STARTED_WAVE
    var onWaveStatusChangeObserver = Observer<InvasionWaveStatus>()
    var waveInterval = 20 * 5
    var waveIntervalPassed = waveInterval

    companion object {
        val name: String = "invasion_event"
    }

    init {
        title = "Invasion Event"
        distanceThreshold = 5
    }

    override fun create(): RandomEvent {
        return InvasionBaseEvent()
    }

    override fun onPrepare() {
        commandSource = level!!.server!!.createCommandSourceStack()
        commandDispatcher = level!!.server!!.commands.dispatcher
        onWaveStatusChangeObserver += ::onChangeWaveStatus
        super.onPrepare()
    }

    override fun onReady() {
        setWaveInvasionStatus(InvasionWaveStatus.STARTED_WAVE)
        MinecraftEventsObservers.onPlayerTickEventObserver += ::tickTimer
        MinecraftEventsObservers.onPlayerTickEventObserver += ::tickParticle
        super.onReady()
    }

    override fun onFinishing() {
        MinecraftEventsObservers.onLivingDeathEventObserver -= ::tickMobDeath
        MinecraftEventsObservers.onPlayerTickEventObserver -= ::tickTimer
        MinecraftEventsObservers.onPlayerTickEventObserver -= ::tickParticle
        MinecraftEventsObservers.onPlayerTickEventObserver -= ::tickWaveInterval
        super.onFinishing()
    }

    open fun onChangeWaveStatus(newStatus: InvasionWaveStatus) {
        when (newStatus) {
            InvasionWaveStatus.STARTED_WAVE -> {
                this.onStartWave()
            }
            InvasionWaveStatus.FINISHED_WAVE -> {
                this.onFinishWave()
            }
            InvasionWaveStatus.PREPARING_WAVE -> {
                this.onPrepareWave()
            }
            InvasionWaveStatus.NOT_STARTED_WAVE -> {
                this.onWaveNotStarted()
            }
            else -> {}
        }
    }

    open fun onStartWave() {
        MinecraftEventsObservers.onPlayerTickEventObserver -= ::tickWaveInterval
        MinecraftEventsObservers.onLivingDeathEventObserver += ::tickMobDeath
        spawnWave(wavesGroupies[actualWave])
    }

    open fun onFinishWave() {
        MinecraftEventsObservers.onLivingDeathEventObserver -= ::tickMobDeath
        actualWave++
        if (actualWave == (wavesGroupies.size)) {
            setEventStatus(RandomEventStatus.FINISHING_SUCCESS)
            return
        }
        setWaveInvasionStatus(InvasionWaveStatus.PREPARING_WAVE)
    }

    open fun onWaveNotStarted() {}

    open fun onPrepareWave() {
        MinecraftEventsObservers.onPlayerTickEventObserver += ::tickWaveInterval
    }

    open fun spawnWave(waveGroup: InvasionWaveGroup) {
        var groupPos = BlockPosUtils.findRandomSurfaceBlockNearby(this.level!!, this.targetBlock!!, 10, 10)
        if (groupPos == null) groupPos = targetBlock

        for (mob in waveGroup.entities.values) {
            var mobPos = BlockPosUtils.findRandomSurfaceBlockNearby(this.level!!, groupPos!!, 4, 4)
            if (mobPos == null) mobPos = groupPos
            mob.addEffect(MobEffectInstance(MobEffects.GLOWING, 1, this.invasionTimer))
            mob.setPos(Vec3(mobPos.x.toDouble(), mobPos.y.toDouble(), mobPos.z.toDouble()))
            runSpawnParticle(mobPos)
            level!!.addFreshEntity(mob)
        }

    }

    open fun setWaveInvasionStatus(newStatus: InvasionWaveStatus) {
        if (waveStatus == newStatus) return
        this.waveStatus = newStatus
        this.onWaveStatusChangeObserver.invoke(newStatus)
    }

    open fun onWaveKilled() {
        setWaveInvasionStatus(InvasionWaveStatus.FINISHED_WAVE)
    }

    open fun tickWaveInterval(event: TickEvent.PlayerTickEvent) {
        if (waveIntervalPassed <= 0) {
            setWaveInvasionStatus(InvasionWaveStatus.STARTED_WAVE)
        }
        waveIntervalPassed--
    }

    open fun tickTimer(event: TickEvent.PlayerTickEvent) {
        if (invasionTimerPassed <= 0) {
            this.setEventStatus(RandomEventStatus.FINISHING_CANCELED)
        }
        invasionTimerPassed--
    }

    open fun tickMobDeath(event: LivingDeathEvent) {
        if (waveStatus != InvasionWaveStatus.STARTED_WAVE) return
        val entity = event.entity
        val group = wavesGroupies[actualWave]

        if (entity.uuid !in group.entities) return
        group.killEntity(entity.uuid)

        if (group.entitiesLeft <= 0) {
            onWaveKilled()
        }
    }

    open fun tickParticle(event: TickEvent.PlayerTickEvent) {
        //val particleCommand = "particle minecraft:portal ${targetBlock!!.x} ${targetBlock!!.y} ${targetBlock!!.z} 0 2 0 1 40 force"
        //commandDispatcher!!.execute(particleCommand, commandSource)
    }

    open fun runSpawnParticle(blockPos: BlockPos) {
        //val particleCommand = "particle minecraft:portal ${blockPos.x} ${blockPos.y} ${blockPos.z} 0 0 0 1 200 force"
        //commandDispatcher!!.execute(particleCommand, commandSource)
    }
}