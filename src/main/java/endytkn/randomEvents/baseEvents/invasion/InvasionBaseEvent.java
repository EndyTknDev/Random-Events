package endytkn.randomEvents.baseEvents.invasion;

import endytkn.randomEvents.eventObservers.MinecraftEventsObservers;
import endytkn.randomEvents.randomEvent.RandomEvent;
import endytkn.randomEvents.utils.BlockPosUtils;
import endytkn.randomEvents.utils.Observer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

import java.util.ArrayList;
import java.util.function.Consumer;

public class InvasionBaseEvent extends RandomEvent {
    public enum InvasionBaseWaveStatus {
        NOT_STARTED_WAVE, PREPARING_WAVE, STARTED_WAVE, FINISHED_WAVE
    }
    public int invasionTimer = 20 * 60 * 5; // 5 minutes
    public int invasionTimerPassed = invasionTimer;
    public int actualWave = 0;
    public ArrayList<InvasionWaveGroup> wavesGroupies = new ArrayList<>();
    public InvasionBaseWaveStatus waveStatus = InvasionBaseWaveStatus.NOT_STARTED_WAVE;
    public final Observer<InvasionBaseEvent.InvasionBaseWaveStatus> onWaveStatusChangeObserver = new Observer<>();
    public int waveInterval = 20 * 5; // 5 seconds
    public int waveIntervalPassed = waveInterval;

    public static final String name = "invasion_event";

    private final Consumer<LivingDeathEvent> tickDeathConsumer = this::tickMobDeath;
    private final Consumer<TickEvent> tickParticleConsumer = this::tickParticle;
    private final Consumer<TickEvent> tickTimerConsumer = this::tickTimer;
    private final Consumer<TickEvent> tickWaveConsumer = this::tickWaveInterval;

    public InvasionBaseEvent() {
        this.title = "Invasion Event";
        this.distanceThreshold = 5;
    }


    public void tickTimer(TickEvent event) {
        if (this.invasionTimerPassed <= 0) {
            this.setEventStatus(RandomEventStatus.FINISHING_CANCELED);
        }
        this.invasionTimerPassed--;
    }

    private void tickParticle(TickEvent event) {
        // String particleCommand = "particle minecraft:portal " + targetBlock.getX() + " " + targetBlock.getY() + " " + targetBlock.getZ() + " 0 2 0 1 40 force";
        // commandDispatcher.execute(particleCommand, commandSource);
    }

    private void tickMobDeath(LivingDeathEvent event) {
        if (this.waveStatus != InvasionBaseWaveStatus.STARTED_WAVE) return;
        var entity = event.getEntity();
        var group = this.wavesGroupies.get(this.actualWave);

        if (!group.entities.containsKey(entity.getUUID())) return;
        group.killEntity(entity.getUUID());

        if (group.entitiesLeft <= 0) {
            this.onWaveKilled();
        }
    }

    private void tickWaveInterval(TickEvent event) {
        if (this.waveIntervalPassed <= 0) {
            this.setWaveInvasionStatus(InvasionBaseWaveStatus.STARTED_WAVE);
        }
        this.waveIntervalPassed--;
    }

    @Override
    public RandomEvent create() {
        return new InvasionBaseEvent();
    }

    @Override
    public void onPrepare() {
        onWaveStatusChangeObserver.add(this::onChangeWaveStatus);
        super.onPrepare();
    }

    @Override
    public void onReady() {
        setWaveInvasionStatus(InvasionBaseWaveStatus.STARTED_WAVE);
        MinecraftEventsObservers.tickObserver.add((this.tickTimerConsumer));
        MinecraftEventsObservers.tickObserver.add((this.tickParticleConsumer));
        super.onReady();
    }

    @Override
    public void onFinishing() {
        MinecraftEventsObservers.tickObserver.remove((this.tickTimerConsumer));
        MinecraftEventsObservers.tickObserver.remove((this.tickParticleConsumer));
        MinecraftEventsObservers.livingDeathObserver.remove((this.tickDeathConsumer));
        MinecraftEventsObservers.tickObserver.remove((this.tickWaveConsumer));

        super.onFinishing();
    }

    public void onChangeWaveStatus(InvasionBaseWaveStatus newStatus) {
        switch (newStatus) {
            case STARTED_WAVE:
                onStartWave();
                break;
            case FINISHED_WAVE:
                onFinishWave();
                break;
            case PREPARING_WAVE:
                onPrepareWave();
                break;
            case NOT_STARTED_WAVE:
                onWaveNotStarted();
                break;
            default:
                break;
        }
    }

    public void onStartWave() {
        MinecraftEventsObservers.tickObserver.add((this.tickWaveConsumer));
        MinecraftEventsObservers.livingDeathObserver.add((this.tickDeathConsumer));
        spawnWave(wavesGroupies.get(actualWave));
    }

    public void onFinishWave() {
        MinecraftEventsObservers.livingDeathObserver.remove((this.tickDeathConsumer));
        actualWave++;
        if (actualWave == wavesGroupies.size()) {
            setEventStatus(RandomEventStatus.FINISHING_SUCCESS);
            return;
        }
        setWaveInvasionStatus(InvasionBaseWaveStatus.PREPARING_WAVE);
    }

    public void onWaveNotStarted() {}

    public void onPrepareWave() {
        MinecraftEventsObservers.tickObserver.add((this.tickWaveConsumer));
    }

    public void spawnWave(InvasionWaveGroup waveGroup) {
        BlockPos groupPos = BlockPosUtils.findRandomSurfaceBlockNearby(this.level, targetBlock, 10, 10);
        if (groupPos == null) groupPos = targetBlock;

        for (var mob : waveGroup.entities.values()) {
            BlockPos mobPos = BlockPosUtils.findRandomSurfaceBlockNearby(this.level, groupPos, 4, 4);
            if (mobPos == null) mobPos = groupPos;
            mob.addEffect(new MobEffectInstance(MobEffects.GLOWING, 1, invasionTimer));
            mob.setPos(new Vec3(mobPos.getX(), mobPos.getY(), mobPos.getZ()));
            mob.addEffect(new MobEffectInstance(MobEffects.GLOWING, timeLimit));
            spawnAnimation(mobPos);
            level.addFreshEntity(mob);
        }
    }

    public void setWaveInvasionStatus(InvasionBaseWaveStatus newStatus) {
        if (waveStatus == newStatus) return;
        this.waveStatus = newStatus;
        this.onWaveStatusChangeObserver.notify(newStatus);
    }

    public void onWaveKilled() {
        setWaveInvasionStatus(InvasionBaseWaveStatus.FINISHED_WAVE);
    }

    public void spawnAnimation(BlockPos blockPos) {
        // String particleCommand = "particle minecraft:portal " + blockPos.getX() + " " + blockPos.getY() + " " + blockPos.getZ() + " 0 0 0 1 200 force";
        // commandDispatcher.execute(particleCommand, commandSource);
    }
}