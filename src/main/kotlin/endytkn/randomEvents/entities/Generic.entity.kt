package endytkn.randomEvents.entities

import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.level.Level

class GenericEntity<T : PathfinderMob>(entityType: EntityType<T>, level: Level): PathfinderMob(entityType, level) {

}