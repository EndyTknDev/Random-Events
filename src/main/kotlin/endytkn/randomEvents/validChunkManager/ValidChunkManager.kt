package endytkn.randomEvents.validChunkManager

import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceKey
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.chunk.LevelChunk
import net.minecraftforge.event.level.BlockEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod

@Mod.EventBusSubscriber
object ValidChunkManager {
    var chunkData: ChunkMapData = ChunkMapData();

    private const val buildingThreshold = 90
    private const val CHUNK_MAP_NAME = "chunk_map_data"
    init {
        val serverLevel = getServerLevel(Level.OVERWORLD)
        if (serverLevel != null) {
            chunkData = serverLevel.dataStorage.computeIfAbsent({ ChunkMapData.load(it) }, { ChunkMapData() }, CHUNK_MAP_NAME)
        }
    }

    fun getServerLevel(dimension: ResourceKey<Level>): ServerLevel? {
        return Minecraft.getInstance().singleplayerServer?.getLevel(dimension) // Obtém o nível no lado do servidor
    }

    @SubscribeEvent
    fun onPlayerPlaceBlock(event: BlockEvent.EntityPlaceEvent) {
        val entity = event.entity;
        val level = event.level;
        if ((entity !is Player) or event.level.isClientSide)  return;

        val chunkMap = chunkData.chunkMap
        val chunk = level.getChunk(event.pos)
        val posX = chunk.pos.x
        val posZ = chunk.pos.z

        putIfNotExists(posX, posZ)
        var counter = chunkMap[posX]?.get(posZ)?.plus(1) ?: 1
        chunkMap[posX]?.set(posZ, counter)
        entity?.sendSystemMessage(Component.literal("chunk (${chunk.pos.x}, ${chunk.pos.z}) = $counter "))
        if (hasBuildingAround(posX, posZ))
            entity?.sendSystemMessage(Component.literal(("Has building around!!")))
        chunkData.setDirty()
    }

    fun putIfNotExists(x: Int, z: Int) {
        val chunkMap = chunkData.chunkMap
        chunkMap.putIfAbsent(x, mutableMapOf())
        chunkMap[x]?.putIfAbsent(z, 0)
    }

    fun hasBuildingAround(x: Int, z: Int): Boolean {
        return this.hasBuildingAround(x, z, 1)
    }

    fun hasBuildingAround(x: Int, z: Int, radius: Int): Boolean {
        val chunkMap = chunkData.chunkMap
        var acm = 0
        for (x in x-radius..x+radius)
            for (z in z-radius..z+radius)
                acm += chunkMap[x]?.get(z) ?: 0

        return acm > buildingThreshold
    }

    fun getChunk(level: Level, x: Int, z: Int): LevelChunk {
        return level.getChunk(x, z)
    }
}