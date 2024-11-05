package endytkn.randomEvents.entities;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;

public class GenericEntity<T extends PathfinderMob> extends PathfinderMob {
    public GenericEntity(EntityType<T> entityType, Level level) {
        super(entityType, level);
    }
}
