package endytkn.randomEvents.chunkManager;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class ChunkManager {
    private static ChunkManagerMapData chunkData;
    private static final int buildingThreshold = 90;
    private static final String CHUNK_MAP_NAME = "chunk_map_data";
    public static ServerLevel serverLevel;

    static {
        serverLevel = getServerLevel(Level.OVERWORLD);
        if (serverLevel != null) {
            chunkData = serverLevel.getDataStorage().computeIfAbsent(
                    ChunkManagerMapData::load,
                    ChunkManagerMapData::new,
                    CHUNK_MAP_NAME
            );
        }
    }

    public static ServerLevel getServerLevel(ResourceKey<Level> dimension) {
        return Minecraft.getInstance().getSingleplayerServer() != null ?
                Minecraft.getInstance().getSingleplayerServer().getLevel(dimension) : null;
    }

    @SubscribeEvent
    public void onPlayerPlaceBlock(BlockEvent.EntityPlaceEvent event) {
        Object entity = event.getEntity();
        Level level = event.getEntity().level();
        if (!(entity instanceof Player) || level.isClientSide()) return;

        var chunkMap = chunkData.chunkMap;

        ChunkAccess chunk = level.getChunk(event.getPos());
        int posX = chunk.getPos().x;
        int posZ = chunk.getPos().z;

        putIfNotExists(posX, posZ);
        int counter = chunkMap.get(posX).getOrDefault(posZ, 0) + 1;
        chunkMap.get(posX).put(posZ, counter);
        event.getEntity().sendSystemMessage(Component.literal("%d %d".formatted(posX, posZ)));
        chunkData.setDirty();
    }

    public static void putIfNotExists(int x, int z) {
        var chunkMap = chunkData.chunkMap;
        chunkMap.putIfAbsent(x, new java.util.HashMap<>());
        chunkMap.get(x).putIfAbsent(z, 0);
    }

    public static boolean hasBuildingAround(int x, int z) {
        return hasBuildingAround(x, z, 1);
    }

    public static boolean hasBuildingAround(int x, int z, int radius) {
        var chunkMap = chunkData.chunkMap;
        int acm = 0;
        for (int i = x - radius; i <= x + radius; i++) {
            for (int j = z - radius; j <= z + radius; j++) {
                acm += chunkMap.getOrDefault(i, new java.util.HashMap<>()).getOrDefault(j, 0);
            }
        }
        return acm > buildingThreshold;
    }

    public static LevelChunk getChunk(Level level, int x, int z) {
        return level.getChunk(x, z);
    }
}
