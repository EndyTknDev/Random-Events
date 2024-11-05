package endytkn.randomEvents.chunkManager;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class ChunkManagerMapData extends SavedData {
    public final Map<Integer, Map<Integer, Integer>> chunkMap = new HashMap<>();

    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag tag) {
        ListTag chunksTag = new ListTag();
        for (Map.Entry<Integer, Map<Integer, Integer>> entry : chunkMap.entrySet()) {
            int x = entry.getKey();
            Map<Integer, Integer> zMap = entry.getValue();
            for (Map.Entry<Integer, Integer> zEntry : zMap.entrySet()) {
                int z = zEntry.getKey();
                int count = zEntry.getValue();
                CompoundTag chunkTag = new CompoundTag();
                chunkTag.putInt("x", x);
                chunkTag.putInt("z", z);
                chunkTag.putInt("count", count);
                chunksTag.add(chunkTag);
            }
        }
        tag.put("chunks", chunksTag);
        return tag;
    }

    public static ChunkManagerMapData load(CompoundTag tag) {
        ChunkManagerMapData data = new ChunkManagerMapData();
        ListTag chunksTag = tag.getList("chunks", 10); // 10 é o tipo para CompoundTag
        for (int i = 0; i < chunksTag.size(); i++) {
            CompoundTag chunkTag = chunksTag.getCompound(i);
            int x = chunkTag.getInt("x");
            int z = chunkTag.getInt("z");
            int count = chunkTag.getInt("count");

            data.chunkMap.putIfAbsent(x, new HashMap<>());
            data.chunkMap.get(x).put(z, count);
        }
        return data;
    }
}
