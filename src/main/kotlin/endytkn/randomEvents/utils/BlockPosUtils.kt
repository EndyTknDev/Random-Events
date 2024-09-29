package endytkn.randomEvents.utils

import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import kotlin.random.Random
import kotlin.random.nextInt

object BlockPosUtils {
    fun findRandomSurfaceBlockNearby(level: Level, blockPos: BlockPos, xRange: Int, zRange: Int): BlockPos? {
        var randomX = Random.nextInt(blockPos.x - xRange, blockPos.x + xRange)
        var randomZ = Random.nextInt(blockPos.z - zRange, blockPos.z + zRange)
        var newBlockPos = BlockPos(randomX, blockPos.y, randomZ)
        return findSurfaceBlock(level, newBlockPos, level.maxBuildHeight)
    }

    fun findSurfaceBlock(level: Level, blockPos: BlockPos, maxY: Int): BlockPos? {
        val x = blockPos.x
        val y = blockPos.y
        val z = blockPos.z

        for (itY in y..maxY) {
            val actualPos = BlockPos(x, itY, z)
            val blockState: BlockState = level.getBlockState(actualPos)
            if (blockState.isAir) {
                return actualPos
            }
        }
        return null
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
}