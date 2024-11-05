package endytkn.randomEvents.baseEvents.structure;

import endytkn.randomEvents.randomEvent.RandomEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

public class StructureBaseEvent extends RandomEvent {
    private final String modId;
    private final String structureName;

    public static final String name = "spawn_structure";

    public StructureBaseEvent(String modId, String structureName) {
        this.modId = modId;
        this.structureName = structureName;
    }

    public void spawnStructure() {
        ServerLevel serverLevel = (ServerLevel) this.level;
        if (serverLevel == null) return;

        BlockPos structurePos = new BlockPos(targetBlock.getX(), targetBlock.getY() - 1, targetBlock.getZ());
        var structureManager = serverLevel.getServer().getStructureManager();
        StructureTemplate template = structureManager.getOrCreate(new ResourceLocation(modId, structureName));
        StructurePlaceSettings settings = new StructurePlaceSettings();
        template.placeInWorld((ServerLevelAccessor) serverLevel, structurePos, structurePos, settings, serverLevel.getRandom(), 2);

    }

    @Override
    public void onPrepare() {
        super.onPrepare();
        spawnStructure();
    }
}
