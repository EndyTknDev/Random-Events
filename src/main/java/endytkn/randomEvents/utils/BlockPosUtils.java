package endytkn.randomEvents.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.Random;

public class BlockPosUtils {
    private static final Random random = new Random();

    public static BlockPos findRandomSurfaceBlockNearby(Level level, BlockPos blockPos, int xRange, int zRange) {
        int randomX = blockPos.getX() - xRange + random.nextInt(2 * xRange);
        int randomZ = blockPos.getZ() - zRange + random.nextInt(2 * zRange);

        int surfaceY = level.getHeight(Heightmap.Types.MOTION_BLOCKING, blockPos.getX(), blockPos.getZ());
        return new BlockPos(randomX, surfaceY, randomZ);
    }
}
