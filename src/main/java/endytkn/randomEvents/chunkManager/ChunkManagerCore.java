package endytkn.randomEvents.chunkManager;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChunkManagerCore {
    private static final List<ResourceKey<Level>> availableLevels = List.of(Level.OVERWORLD);
    public static Map<ResourceKey<Level>, ChunkManager> chunkManagers = new HashMap<>();

    public static ChunkData onPlayerPlaceBlock(Level level, int x, int y) {
        if (!availableLevels.contains(level.dimension())) throw new RuntimeException("Not available level");

        ChunkManager chunkManager = chunkManagers.putIfAbsent(level.dimension(), new ChunkManager());
        ChunkData chunkdata = chunkManager.addBlock(x, y, 1);
        return chunkdata;
    }

    public static ChunkData initLevelChunkData(ResourceKey<Level> level, int x, int z, int countPlacedBlocks, boolean hasBuilding) {
        ChunkManager chunkManager = chunkManagers.putIfAbsent(level, new ChunkManager());
        assert chunkManager != null;
        return chunkManager.initChunkData(x, z, countPlacedBlocks, hasBuilding);
    }

    public static boolean isLevelAvailable(ResourceKey<Level> levelKey) {
        return availableLevels.contains(levelKey);
    }

    public static boolean isChunkAvailable(ResourceKey<Level> levelKey, int x, int z) {
        if (!chunkManagers.containsKey(levelKey)) return false;
        ChunkManager chunkManager = chunkManagers.get(levelKey);
        return chunkManager.hasBuildingAround(x, z, 3);
    }
}