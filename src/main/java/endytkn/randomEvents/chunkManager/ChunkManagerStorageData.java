package endytkn.randomEvents.chunkManager;

import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

@Mod.EventBusSubscriber
public class ChunkManagerStorageData extends SavedData {
    private static ChunkManagerStorageData savedData;
    public static final String COMPOUND_TAG_NAME = "randomEventsChunksLevels";
    public CompoundTag chunkManagerData = new CompoundTag();

    @SubscribeEvent
    public static void onServerStarted(ServerStartedEvent event) {
        ServerLevel level = event.getServer().overworld();
        savedData = level.getDataStorage().computeIfAbsent(
                ChunkManagerStorageData::load,
                ChunkManagerStorageData::new,
                ChunkManagerStorageData.COMPOUND_TAG_NAME
        );
    }

    @SubscribeEvent
    public static void onPlayerPlaceBlock(BlockEvent.EntityPlaceEvent event) {
        Player player = (Player) event.getEntity();
        Level level = player.level();

        if (level.isClientSide()) return;

        ChunkAccess chunk = level.getChunk(event.getPos());
        int posX = chunk.getPos().x;
        int posZ = chunk.getPos().z;

        try {
            ChunkData chunkData = ChunkManagerCore.onPlayerPlaceBlock(level, posX, posZ);

            CompoundTag levelProps = getOrCreateLevelProps(level);
            CompoundTag positions = getOrCreatePositions(levelProps);
            CompoundTag positionX = getOrCreatePositionX(positions, posX);
            CompoundTag positionZ = getOrCreatePositionZ(positionX, posZ);

            updateChunkData(positionZ, chunkData);

            savedData.setDirty();
            player.sendSystemMessage(Component.literal("%d %d %d".formatted(posX, posZ, chunkData.countPlacedBlocks)));
        } catch (Exception e) {
            e.printStackTrace();
            event.getEntity().sendSystemMessage(Component.literal(e.toString() + " " + level.dimension()));
        }
    }

    private static CompoundTag getOrCreateLevelProps(Level level) {
        if (!savedData.chunkManagerData.contains(level.dimension().toString())) {
            savedData.chunkManagerData.put(level.dimension().toString(), new CompoundTag());
        }
        return savedData.chunkManagerData.getCompound(level.dimension().toString());
    }

    private static CompoundTag getOrCreatePositions(CompoundTag levelProps) {
        if (!levelProps.contains("positions")) {
            levelProps.put("positions", new CompoundTag());
        }
        return levelProps.getCompound("positions");
    }

    private static CompoundTag getOrCreatePositionX(CompoundTag positions, int posX) {
        if (!positions.contains(String.valueOf(posX))) {
            positions.put(String.valueOf(posX), new CompoundTag());
        }
        return positions.getCompound(String.valueOf(posX));
    }

    private static CompoundTag getOrCreatePositionZ(CompoundTag positionX, int posZ) {
        if (!positionX.contains(String.valueOf(posZ))) {
            positionX.put(String.valueOf(posZ), new CompoundTag());
        }
        return positionX.getCompound(String.valueOf(posZ));
    }

    private static void updateChunkData(CompoundTag chunkProps, ChunkData chunkData) {
        chunkProps.putBoolean("hasBuilding", chunkData.hasBuilding);
        chunkProps.putInt("countPlacedBlocks", chunkData.countPlacedBlocks);
    }

    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag tag) {
        tag.put(COMPOUND_TAG_NAME, this.chunkManagerData);
        return tag;
    }

    public static ChunkManagerStorageData load(CompoundTag levels) {
        ChunkManagerStorageData data = new ChunkManagerStorageData();

        for (String level : levels.getAllKeys()) {
            CompoundTag levelProps = levels.getCompound(level);
            CompoundTag positionsX = levelProps.getCompound("positions");

            CompoundTag xTags = new CompoundTag();

            for (String xKey : positionsX.getAllKeys()) {
                CompoundTag positionsY = positionsX.getCompound(xKey);
                CompoundTag yTags = new CompoundTag();

                for (String yKey : positionsY.getAllKeys()) {
                    CompoundTag chunk = positionsY.getCompound(yKey);

                    boolean hasBuilding = chunk.getBoolean("hasBuilding");
                    int countPlacedBlocks = chunk.getInt("countPlacedBlocks");

                    CompoundTag newChunk = new CompoundTag();
                    newChunk.putBoolean("hasBuilding", hasBuilding);
                    newChunk.putInt("countPlacedBlocks", countPlacedBlocks);

                    yTags.put(yKey, newChunk);

                    ResourceKey<Level> levelKey = ResourceKey.create(Registries.DIMENSION, new ResourceLocation(level));
                    ChunkManagerCore.initLevelChunkData(levelKey, Integer.parseInt(xKey), Integer.parseInt(yKey), countPlacedBlocks, hasBuilding);
                }

                xTags.put(xKey, yTags);
            }
            data.chunkManagerData.put(level, xTags);
        }

        return data;
    }
}
