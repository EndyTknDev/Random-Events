package endytkn.randomEvents.randomEvent;

import endytkn.randomEvents.RandomEventsMod;
import endytkn.randomEvents.eventObservers.MinecraftEventsObservers;
import endytkn.randomEvents.utils.Observer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraft.client.Minecraft;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class RandomEvent {
    public enum RandomEventStatus {
        NOT_STARTED, PREPARING, READY, WAITING_PLAYER, FINISHING_SUCCESS, FINISHING_CANCELED, FINISHING, FINISHED
    }
    protected ServerLevel level;
    protected BlockPos targetBlock;
    protected List<ServerPlayer> playersGroup;
    protected final UUID id = UUID.randomUUID();
    protected final Observer<RandomEventStatus> onChangeStatusObserver = new Observer<>();
    public RandomEventStatus status = RandomEventStatus.NOT_STARTED;
    protected int distanceThreshold = 650;
    protected int timeLimit = 20 * 60 * 10;
    protected int timePassed = 0;
    public List<RandomEventsCategory> categories = List.of(RandomEventsCategory.SPECIAL);
    public RandomEventsRarity rarity = RandomEventsRarity.COMMON;
    public String title = "Random Event";
    public String eventTag = "default_event";

    private final Consumer<TickEvent.PlayerTickEvent> playerTickConsumer = this::playerTick;

    public RandomEvent create() {
        return new RandomEvent();
    }

    public void initEvent(ServerLevel level, BlockPos targetBlock, List<ServerPlayer> playersGroup) {
        this.level = level;
        this.targetBlock = targetBlock;
        this.playersGroup = playersGroup;
        setEventStatus(RandomEventStatus.NOT_STARTED);
    }

    public void playerTick(TickEvent.PlayerTickEvent event) {
        if (Minecraft.getInstance().isPaused() || event.player.isSpectator() || !event.side.isServer()) return;

        onTimeLimit();
        if (isNearby(event.player) && status == RandomEventStatus.WAITING_PLAYER) {
            onPlayerEnter();
        }
    }

    public void start() {
        if (level == null || targetBlock == null) {
            throw new Error("Level and Target Block must be initialized");
        }
        onChangeStatusObserver.add(this::onChangeStatus);
        setEventStatus(RandomEventStatus.PREPARING);
        MinecraftEventsObservers.playerTickObserver.add(this.playerTickConsumer);
    }

    public void onTimeLimit() {
        timePassed++;
        if (timePassed >= timeLimit) {
            cancelEvent();
        }
    }

    public boolean isNearby(Player player) {
        double distance = player.blockPosition().distSqr(targetBlock);
        return distance <= distanceThreshold;
    }

    public void onChangeStatus(RandomEventStatus newStatus) {
        level.getServer().sendSystemMessage(Component.literal("Event " + eventTag + " status: " + newStatus));
        switch (newStatus) {
            case PREPARING -> onPrepare();
            case FINISHING_SUCCESS -> onFinishingSuccess();
            case FINISHING_CANCELED -> onFinishingCanceled();
            case FINISHING -> onFinishing();
            case READY -> onReady();
        }
    }

    public void setEventStatus(RandomEventStatus newStatus) {
        this.status = newStatus;
        onChangeStatusObserver.notify(status);
    }

    protected void onFinishingSuccess() {
        setEventStatus(RandomEventStatus.FINISHING);
    }

    protected void onFinishingCanceled() {
        MinecraftEventsObservers.playerTickObserver.remove(this.playerTickConsumer);
        setEventStatus(RandomEventStatus.FINISHING);
    }

    protected void onFinishing() {
        setEventStatus(RandomEventStatus.FINISHED);
    }

    protected void onPrepare() {
        setEventStatus(RandomEventStatus.WAITING_PLAYER);
    }

    public void onPlayerEnter() {
        MinecraftEventsObservers.playerTickObserver.remove(this.playerTickConsumer);
        setEventStatus(RandomEventStatus.READY);
    }

    protected void onReady() {}

    public void resolve() {
        setEventStatus(RandomEventStatus.FINISHING_SUCCESS);
    }

    public void cancelEvent() {
        this.setEventStatus(RandomEventStatus.FINISHING_CANCELED);
    }

    public boolean isEntityOfEvent(Entity entity) {
        CompoundTag nbt = entity.getPersistentData();
        return nbt.contains(eventTag) && nbt.getString(eventTag).equals(this.id.toString());
    }

    protected void addEntityToEvent(Entity entity) {
        CompoundTag nbt = entity.getPersistentData();
        nbt.putString(RandomEventsMod.ID, this.id.toString());
    }

    protected void removeEntityFromEvent(Entity entity) {
        CompoundTag nbt = entity.getPersistentData();
        nbt.remove(RandomEventsMod.ID);
    }
}
