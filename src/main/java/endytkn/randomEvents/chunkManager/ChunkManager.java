package endytkn.randomEvents.chunkManager;

import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;

@Mod.EventBusSubscriber
public class ChunkManager {
    private static final int buildingThreshold = 90;
    private Map<Integer, Map<Integer, ChunkData>> chunkMap = new HashMap<>();

    ChunkManager(Map<Integer, Map<Integer, ChunkData>> chunkMap) {
        this.chunkMap = chunkMap;
    }

    ChunkManager() {}

    public void initIfChunkNotRegistered(int x, int z) {
        this.chunkMap.putIfAbsent(x, new java.util.HashMap<>());
        this.chunkMap.get(x).putIfAbsent(z, new ChunkData(x, z));
    }

    public ChunkData addBlock(int x, int z, int count) {
        initIfChunkNotRegistered(x, z);
        ChunkData chunkData = chunkMap.get(x).get(z);
        chunkData.addBlocks(count);
        return chunkData;
    }

    public ChunkData initChunkData(int x, int z, int countPlacedBlocks, boolean hasBuilding) {
        this.chunkMap.putIfAbsent(x, new java.util.HashMap<>());
        return this.chunkMap.get(x).putIfAbsent(z, new ChunkData(x, z, countPlacedBlocks, hasBuilding));
    }

    public boolean hasBuildingAround(int x, int z) {
        return hasBuildingAround(x, z, 1);
    }

    public boolean hasBuildingAround(int x, int z, int radius) {
        int acm = 0;
        for (int i = x - radius; i <= x + radius; i++) {
            for (int j = z - radius; j <= z + radius; j++) {
                ChunkData chunkData = this.chunkMap.getOrDefault(i, new HashMap<>()).getOrDefault(j, new ChunkData(i, j));
                if (chunkData.hasBuilding) return true;
                acm += chunkData.countPlacedBlocks;
            }
        }
        return acm > buildingThreshold;
    }

    public static int getBuildingThreshold() {
        return ChunkManager.buildingThreshold;
    }
}
