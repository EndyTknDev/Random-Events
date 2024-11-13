package endytkn.randomEvents.chunkManager;

import net.minecraft.world.phys.Vec2;
import org.joml.Vector2i;

public class ChunkData {
    private final int x;
    private final int z;
    public boolean hasBuilding = false;
    public int countPlacedBlocks = 0;

    ChunkData(int x, int z) {
        this.x = x;
        this.z = z;
    }

    ChunkData(int x, int z, int countPlacedBlocks) {
        this.x = x;
        this.z = z;
        this.countPlacedBlocks = countPlacedBlocks;
    }

    ChunkData(int x, int z, int countPlacedBlocks, boolean hasBuilding) {
        this.x = x;
        this.z = z;
        this.countPlacedBlocks = countPlacedBlocks;
        this.hasBuilding = hasBuilding;
    }

    public Vector2i getChunkPosition() {
        return new Vector2i(this.x, this.z);
    }

    public void addBlocks(int count) {
        this.countPlacedBlocks += count;
        if (ChunkManager.getBuildingThreshold() > this.countPlacedBlocks) {
            this.hasBuilding = true;
        }
    }
}
