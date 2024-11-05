package endytkn.randomEvents.events;

import endytkn.randomEvents.baseEvents.invasion.InvasionBaseEvent;
import endytkn.randomEvents.baseEvents.invasion.InvasionWaveGroup;
import endytkn.randomEvents.randomEvent.RandomEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.*;

public class NetherInvasionEvent extends InvasionBaseEvent {

    public NetherInvasionEvent() {
        this.eventTag = "netherInvasion";
    }

    @Override
    public RandomEvent create() {
        return new NetherInvasionEvent();
    }

    @Override
    public void onPrepare() {
        setupWaves();
        super.onPrepare();
    }

    @Override
    public void onReady() {
        super.onReady();
    }

    public void setupWaves() {
        ArrayList<InvasionWaveGroup> groupies = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            Map<UUID, Mob> mobs = new HashMap<>();
            InvasionWaveGroup waveGroup = new InvasionWaveGroup(this, "Wave Group", mobs);

            for (int j = 0; j < 3; j++) {
                Mob mob = EntityType.WITHER_SKELETON.create(level);
                if (mob != null) {
                    ItemStack stoneSword = new ItemStack(Items.STONE_SWORD);

                    mob.setCanPickUpLoot(false);
                    mob.setItemInHand(InteractionHand.MAIN_HAND, stoneSword);
                    mobs.put(mob.getUUID(), mob);
                }
            }
            groupies.add(waveGroup);
        }
        this.wavesGroupies = groupies;
    }
}
