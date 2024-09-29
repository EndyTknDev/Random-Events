package endytkn.randomEvents.baseEvents.SpawnStructure

import endytkn.randomEvents.randomEvents.RandomEvent
import net.minecraft.core.BlockPos
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.ServerLevelAccessor
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate

open class SpawnStructureBaseEvent(val modId: String, val structureName: String): RandomEvent() {
    companion object {
        val name: String = "spawn_structure"
    }

    fun spawnStructure() {
        val structureManager = this.level!!.server?.structureManager
        if (structureManager == null || level !is ServerLevel) return
        val structurePos = BlockPos(targetBlock!!.x, targetBlock!!.y - 1, targetBlock!!.z)
        try {
            val template: StructureTemplate? = structureManager.getOrCreate(ResourceLocation(modId, structureName))
            if (template == null) return
            val settings = StructurePlaceSettings()
            template.placeInWorld(this.level!! as ServerLevelAccessor, structurePos, structurePos, settings, this.level!!.random, 2)
        } catch(e: Error) {
            return
        }
    }

    override fun onPrepare() {
        super.onPrepare()
        spawnStructure()
    }

}