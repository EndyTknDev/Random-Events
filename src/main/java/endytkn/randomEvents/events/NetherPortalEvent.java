package endytkn.randomEvents.events;

import endytkn.randomEvents.baseEvents.structure.StructureBaseEvent;
import endytkn.randomEvents.randomEvent.RandomEvent;
import net.minecraft.core.Vec3i;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;

public class NetherPortalEvent extends StructureBaseEvent {

    public static final String name = "nether_portal";

    public NetherPortalEvent() {
        super("minecraft", "ruined_portal/portal_2");
        this.category = RandomEventsCategories.STRUCTURE;
        this.rarity = RandomEventsRarity.RARE;
        this.eventTag = "netherPortal";
    }

    @Override
    public RandomEvent create() {
        return new NetherPortalEvent();
    }

    @Override
    public void onPrepare() {
        super.onPrepare();
        spawnMobs();
    }

    public void spawnMobs() {
        for (int i = 0; i <= 1; i++) {
            EntityType<?> entityType = EntityType.PIGLIN;
            Mob mob = (Mob) entityType.create(level);
            if (mob != null) {
                ItemStack stoneSword = new ItemStack(Items.GOLDEN_SWORD);
                mob.setItemInHand(InteractionHand.MAIN_HAND, stoneSword);
                mob.setPos(new Vec3(
                        targetBlock.getX() + 3,
                        targetBlock.getY() + 2,
                        targetBlock.getZ() + 2 * (i + 1)
                ));
                level.addFreshEntity(mob);
                level.playSound(
                        null,
                        targetBlock.offset(new Vec3i(0, 5, 0)),
                        SoundEvents.GENERIC_EXPLODE,
                        SoundSource.AMBIENT,
                        100.0f,
                        0.1f
                );
            }
        }
    }
}
