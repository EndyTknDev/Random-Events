package endytkn.randomEvents.utils;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class MobEquipmentPrefab {
    public static Mob setSkeletonRandomWeapon(Mob mob) {
        ItemStack weapon = new ItemStack(Items.BOW);
        mob.setItemInHand(InteractionHand.MAIN_HAND, weapon);
        return mob;
    }

    public static Mob setSkeletonWeapon(Mob mob) {
        ItemStack weapon = new ItemStack(Items.BOW);
        mob.setItemInHand(InteractionHand.MAIN_HAND, weapon);
        return mob;
    }

    public static Mob setVindicatorWeapon(Mob mob) {
        ItemStack weapon = new ItemStack(Items.IRON_AXE);
        mob.setItemInHand(InteractionHand.MAIN_HAND, weapon);
        return mob;
    }

    public static Mob setPillagerWeapon(Mob mob) {
        ItemStack weapon = new ItemStack(Items.CROSSBOW);
        mob.setItemInHand(InteractionHand.MAIN_HAND, weapon);
        return mob;
    }
}
