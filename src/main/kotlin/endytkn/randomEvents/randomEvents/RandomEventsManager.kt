package endytkn.randomEvents.randomEvents

import endytkn.randomEvents.randomEvents.events.ZombieSkeletonFightEvent
import endytkn.randomEvents.utils.Observer
import endytkn.randomEvents.validChunkManager.ValidChunkManager
import net.minecraft.client.Minecraft
import net.minecraft.core.BlockPos
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.chunk.LevelChunk
import net.minecraftforge.event.TickEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.server.ServerLifecycleHooks
import java.util.UUID
import kotlin.collections.mutableMapOf
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

enum class RandomEventsManagerStatus {
    TICKING, TRY_EVENT, START_EVENT, FINISH_EVENT
}

@Mod.EventBusSubscriber
object RandomEventsManager {
    private const val eventInterval: Int = 20 * 20
    private var eventIntervalLeft: Int = eventInterval
    private var status: RandomEventsManagerStatus = RandomEventsManagerStatus.TICKING
    private val onChangeStatusObserver = Observer<RandomEventsManagerStatus>()
    private const val playerThresholdXDistance = 200
    private const val playerThresholdYDistance = 20
    private const val maxTriesFindEventPosition = 10
    private const val positionMaxDistance = 5
    private const val positionMinDistance = 4
    private val events = mutableMapOf<UUID, RandomEvent>()
    private val onEventFinishObserver = Observer<RandomEvent>()

    init {
        onChangeStatusObserver += ::onChangeEvent
    }

    @SubscribeEvent
    fun onTick(event: TickEvent.ClientTickEvent) {
        val minecraft = Minecraft.getInstance()
        if (event.phase != TickEvent.Phase.START || minecraft.isPaused) return
        if (status == RandomEventsManagerStatus.TICKING) {
            if (eventIntervalLeft <= 0) {
                setStatus(RandomEventsManagerStatus.TRY_EVENT)
            }
            eventIntervalLeft--
        }
    }

    fun getEvents(): MutableMap<UUID, RandomEvent> {
        return events
    }

    fun addEvent(event: RandomEvent) {
        onEventFinishObserver += ::removeEvents
        events.put(event.id, event)
    }

    fun removeEvents(event: RandomEvent) {
        onEventFinishObserver -= ::removeEvents
        events.remove(event.id)
    }

    fun setStatus(newStatus: RandomEventsManagerStatus) {
        this.status = newStatus
        this.onChangeStatusObserver(this.status)
    }

    private fun cancelEvent() {
        eventIntervalLeft = eventInterval
        setStatus(RandomEventsManagerStatus.TICKING)
    }

    private fun onChangeEvent(status: RandomEventsManagerStatus) {
        if (status == RandomEventsManagerStatus.TRY_EVENT) {
            this.tryEvent()
            return
        }
        else if (status == RandomEventsManagerStatus.FINISH_EVENT) {
            this.cancelEvent()
            return
        }
    }

    private fun tryEvent() {
        println("TRY EVENT")
        val proximityGroups = groupPlayersByProximity()
        for ((_, group) in proximityGroups.withIndex()) {
            val player = group[0]
            val biomeKey = getPlayerBiomeKey(player)
            val isUnderground = isPlayerUnderground(player)
            val chunk = findEventChunk(player)
            if (chunk == null) continue
            val chunkPosition = findRandomChunkPosition(chunk)
            if (chunkPosition == null) continue
            val newEvent = ZombieSkeletonFightEvent(player.level(), chunkPosition)
            player.sendSystemMessage(Component.literal("novo evento apareceu ${chunkPosition.x}, ${chunkPosition.y}, ${chunkPosition.z}"))
            addEvent(newEvent)
            newEvent.start()
        }
        setStatus(RandomEventsManagerStatus.FINISH_EVENT)
    }

    private fun findEventChunk(player: Player): LevelChunk? {
        val chunkPosition = player.level().getChunk(player.blockPosition())
        val angle = Random.nextDouble(0.0, 2 * PI)
        for (i in 0..maxTriesFindEventPosition) {
            val randomRadius = Random.nextDouble(positionMinDistance.toDouble(), positionMaxDistance.toDouble())
            val x = (randomRadius * cos(angle)).toInt() + chunkPosition.pos.x
            val z = (randomRadius * sin(angle)).toInt() + chunkPosition.pos.z
            if (!ValidChunkManager.hasBuildingAround(x, z))
                return ValidChunkManager.getChunk(player.level(), x, z)
        }
        return null
    }

    private fun findRandomChunkPosition(chunk: LevelChunk): BlockPos? {
        val posX = Random.nextInt(chunk.pos.minBlockX, chunk.pos.maxBlockX)
        val posZ = Random.nextInt(chunk.pos.minBlockZ, chunk.pos.maxBlockZ)

        val level = chunk.level
        val yRange = IntRange(level.minBuildHeight, level.maxBuildHeight)
        return findSurfaceBlock(level, posX, posZ, yRange)
    }

    fun findSurfaceBlock(level: Level, x: Int, z: Int, yRange: IntRange): BlockPos? {
        for (y in yRange) {
            val pos = BlockPos(x, y, z)
            val blockState: BlockState = level.getBlockState(pos)
            if (blockState.isAir) {
                return BlockPos(x, y, z)
            }
        }
        return null
    }

    private fun findNearbyPlayers(
        player: ServerPlayer,
        players: List<ServerPlayer>,
        group: MutableList<ServerPlayer>,
        visited: MutableSet<ServerPlayer>
    ) {
        for (otherPlayer in players) {
            if (otherPlayer != player && otherPlayer !in visited) {
                val pos1 = player.position()
                val pos2 = otherPlayer.position()
                val dx = abs(pos1.x - pos2.x)
                val dy = abs(pos1.y - pos2.y)

                if (dx <= this.playerThresholdXDistance && dy <= this.playerThresholdYDistance) {
                    group.add(otherPlayer)
                    visited.add(otherPlayer)
                    findNearbyPlayers(otherPlayer, players, group, visited)
                }
            }
        }
    }

    private fun groupPlayersByProximity(): MutableList<MutableList<ServerPlayer>> {
        val serverLevel: ServerLevel? = ServerLifecycleHooks.getCurrentServer()?.getLevel(Level.OVERWORLD)
        serverLevel?.let { world ->
            val players = world.players()
            val proximityGroups = mutableListOf<MutableList<ServerPlayer>>()
            val visited = mutableSetOf<ServerPlayer>()

            for (player in players) {
                if (player !in visited) {
                    val group = mutableListOf(player)
                    visited.add(player)
                    findNearbyPlayers(player, players, group, visited)
                    proximityGroups.add(group)
                }
            }
            return proximityGroups
        }
        return mutableListOf()
    }

    private fun getPlayerBiomeKey(player: ServerPlayer): String {
        val world = player.level()
        val biome = world.getBiome(player.blockPosition()).value()

        return world.registryAccess().registryOrThrow(Registries.BIOME).getKey(biome).toString()
    }

    private fun isPlayerUnderground(player: ServerPlayer): Boolean {
        val playerPos = player.blockPosition();
        val world = player.level();
        return world.dimensionType().hasSkyLight() && !world.canSeeSky(playerPos);
    }
}