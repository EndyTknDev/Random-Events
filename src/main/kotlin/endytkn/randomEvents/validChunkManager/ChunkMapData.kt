package endytkn.randomEvents.validChunkManager

import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.world.level.saveddata.SavedData

class ChunkMapData : SavedData() {
    val chunkMap = mutableMapOf<Int, MutableMap<Int, Int>>()

    override fun save(tag: CompoundTag): CompoundTag {
        val chunksTag = ListTag()
        chunkMap.forEach { (x, zMap) ->
            zMap.forEach { (z, count) ->
                val chunkTag = CompoundTag()
                chunkTag.putInt("x", x)
                chunkTag.putInt("z", z)
                chunkTag.putInt("count", count)
                chunksTag.add(chunkTag)
            }
        }
        tag.put("chunks", chunksTag)
        return tag
    }

    companion object {
        fun load(tag: CompoundTag): ChunkMapData {
            val data = ChunkMapData()
            val chunksTag = tag.getList("chunks", 10) // 10 é o tipo para CompoundTag
            chunksTag.forEach {
                val chunkTag = it as CompoundTag
                val x = chunkTag.getInt("x")
                val z = chunkTag.getInt("z")
                val count = chunkTag.getInt("count")

                data.chunkMap.putIfAbsent(x, mutableMapOf())
                data.chunkMap[x]?.put(z, count)
            }
            return data
        }
    }
}