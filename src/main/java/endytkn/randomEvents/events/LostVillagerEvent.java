package endytkn.randomEvents.events;

import endytkn.randomEvents.randomEvent.RandomEvent;
import endytkn.randomEvents.utils.BlockPosUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import java.util.function.Consumer;

public class LostVillagerEvent extends RandomEvent  {
    private final Villager villager;
    private final Consumer<PlayerInteractEvent> playerInteractVillagerConsumer = this::playerInteractVillager;

    public LostVillagerEvent() {
        this.villager = EntityType.VILLAGER.create(this.level);
    }

    public void playerInteractVillager(PlayerInteractEvent event) {

    }

    public void spawnVillager() {
        BlockPos pos = BlockPosUtils.findRandomSurfaceBlockNearby(level, targetBlock, 3, 3);
        villager.setPos(pos.getX(), pos.getY(), pos.getZ());
    }
}
