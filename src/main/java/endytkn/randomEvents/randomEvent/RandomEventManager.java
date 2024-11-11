package endytkn.randomEvents.randomEvent;

import endytkn.randomEvents.RandomEventsMod;
import endytkn.randomEvents.chunkManager.ChunkManager;
import endytkn.randomEvents.utils.Observer;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.io.IOException;
import java.util.*;

import static java.lang.Math.*;


@Mod.EventBusSubscriber
public class RandomEventManager {
    enum RandomEventManagerStatus {
        TICKING, TRY_EVENT, START_EVENT, FINISH_EVENT
    }

    private static final int eventInterval = 20 * 60 * 2;
    private static int eventIntervalLeft = eventInterval;
    private static RandomEventManagerStatus status = RandomEventManagerStatus.TICKING;
    private static final Observer<RandomEventManagerStatus> onChangeStatusObserver = new Observer<>();
    private static final int playerThresholdXDistance = 200;
    private static final int playerThresholdYDistance = 20;
    private static final int maxTriesFindEventPosition = 10;
    private static final int positionMaxDistance = 5;
    private static final int positionMinDistance = 4;
    private static final Map<UUID, RandomEvent> events = new HashMap<>();
    private static final Observer<RandomEvent> onEventFinishObserver = new Observer<>();

    static {
        onChangeStatusObserver.add(RandomEventManager::onChangeEvent);
    }

    @SubscribeEvent
    public static void onServerStopRemoveMobs(ServerStoppingEvent event) {
        for (ServerLevel level : event.getServer().getAllLevels()) {
            level.getEntities().getAll().forEach(entity -> {
                if (entity == null) return;
                CompoundTag nbt = entity.getPersistentData();
                if (nbt.contains(RandomEventsMod.ID)) {
                    entity.setRemoved(Entity.RemovalReason.DISCARDED);
                }
            });
        }
    }

    @SubscribeEvent
    public static void onTick(TickEvent.ClientTickEvent event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (event.phase != TickEvent.Phase.START || minecraft.isPaused()) return;
        if (status == RandomEventManagerStatus.TICKING) {
            if (eventIntervalLeft <= 0) {
                setStatus(RandomEventManagerStatus.TRY_EVENT);
            }
            eventIntervalLeft--;
        }
    }

    public static void triggerNewEvent() {
        setStatus(RandomEventManagerStatus.TRY_EVENT);
    }

    public static Map<UUID, RandomEvent> getEvents() {
        return events;
    }

    public static void addEvent(RandomEvent event) {
        onEventFinishObserver.add(RandomEventManager::removeEvents);
        events.put(event.id, event);
    }

    public static void removeEvents(RandomEvent event) {
        onEventFinishObserver.remove(RandomEventManager::removeEvents);
        events.remove(event.id);
    }

    public static void setStatus(RandomEventManagerStatus newStatus) {
        status = newStatus;
        onChangeStatusObserver.notify(status);
    }

    private static void cancelEvent() {
        eventIntervalLeft = eventInterval;
        setStatus(RandomEventManagerStatus.TICKING);
    }

    private static void onChangeEvent(RandomEventManagerStatus status) {
        if (status == RandomEventManagerStatus.TRY_EVENT) {
            tryEvent();
        } else if (status == RandomEventManagerStatus.FINISH_EVENT) {
            cancelEvent();
        }
    }

    private static void tryEvent() {
        Map<Integer, List<ServerPlayer>> proximityGroups = groupPlayersByProximity();
        for (List<ServerPlayer> group : proximityGroups.values()) {
            ServerPlayer player = group.get(0);
            String biomeKey = getPlayerBiomeKey(player);
            boolean isUnderground = isPlayerUnderground(player);
            if (isUnderground) return;
            ChunkAccess chunk = findEventChunk(player);
            if (chunk == null) continue;
            BlockPos chunkPosition = findRandomChunkPosition(chunk, player.level());
            if (chunkPosition == null) continue;
            RandomEvent newEvent = RandomEventChooser.getEvent(player.level(), chunkPosition, isUnderground, biomeKey, false);
            newEvent.initEvent((ServerLevel) player.level(), chunkPosition, group);
            player.sendSystemMessage(Component.literal("novo evento apareceu " + chunkPosition.getX() + ", " + chunkPosition.getY() + ", " + chunkPosition.getZ() + " " + newEvent.title));
            addEvent(newEvent);
            newEvent.start();
        }
        setStatus(RandomEventManagerStatus.FINISH_EVENT);
    }

    private static ChunkAccess findEventChunk(Player player) {
        Random random = new Random();
        ChunkAccess chunkPosition = player.level().getChunk(player.blockPosition());
        double angle = random.nextDouble() * 2 * PI;
        for (int i = 0; i <= maxTriesFindEventPosition; i++) {
            double randomRadius = random.nextDouble(positionMinDistance, positionMaxDistance);
            int x = (int) (randomRadius * cos(angle)) + chunkPosition.getPos().x;
            int z = (int) (randomRadius * sin(angle)) + chunkPosition.getPos().z;
            if (!ChunkManager.hasBuildingAround(x, z)) {
                return ChunkManager.getChunk(player.level(), x, z);
            }
        }
        return null;
    }

    private static BlockPos findRandomChunkPosition(ChunkAccess chunk, Level level) {
        Random random = new Random();
        int posX = random.nextInt(chunk.getPos().getMinBlockX(), chunk.getPos().getMaxBlockX());
        int posZ = random.nextInt(chunk.getPos().getMinBlockZ(), chunk.getPos().getMaxBlockZ());
        int posY = level.getHeight(Heightmap.Types.MOTION_BLOCKING, posX, posZ);

        return new BlockPos(posX, posY, posZ);
    }

    private static void findNearbyPlayers(ServerPlayer player, List<ServerPlayer> players, List<ServerPlayer> group, Set<ServerPlayer> visited) {
        for (ServerPlayer otherPlayer : players) {
            if (otherPlayer != player && !visited.contains(otherPlayer)) {
                Vec3 pos1 = player.position();
                Vec3 pos2 = otherPlayer.position();
                double dx = abs(pos1.x - pos2.x);
                double dy = abs(pos1.y - pos2.y);

                if (dx <= playerThresholdXDistance && dy <= playerThresholdYDistance) {
                    group.add(otherPlayer);
                    visited.add(otherPlayer);
                    findNearbyPlayers(otherPlayer, players, group, visited);
                }
            }
        }
    }

    private static Map<Integer, List<ServerPlayer>> groupPlayersByProximity() {
        ServerLevel serverLevel = ServerLifecycleHooks.getCurrentServer().getLevel(Level.OVERWORLD);
        if (serverLevel != null) {
            List<ServerPlayer> players = serverLevel.players();
            Map<Integer, List<ServerPlayer>> proximityGroups = new HashMap<>();
            Set<ServerPlayer> visited = new HashSet<>();

            for (ServerPlayer player : players) {
                if (!visited.contains(player)) {
                    List<ServerPlayer> group = new ArrayList<>();
                    group.add(player);
                    visited.add(player);
                    findNearbyPlayers(player, players, group, visited);
                    proximityGroups.put(proximityGroups.size(), group);
                }
            }
            return proximityGroups;
        }
        return new HashMap<>();
    }

    private static String getPlayerBiomeKey(ServerPlayer player) {
        try(Level world = player.level()) {
            Biome biome = world.getBiome(player.blockPosition()).value();
            return Objects.requireNonNull(world.registryAccess().registryOrThrow(Registries.BIOME).getKey(biome)).toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean isPlayerUnderground(ServerPlayer player) {
        BlockPos playerPos = player.blockPosition();
        Level world = player.level();
        return world.dimensionType().hasSkyLight() && !world.canSeeSky(playerPos);
    }
}
