package endytkn.randomEvents.events;

import endytkn.randomEvents.baseEvents.groupFight.GroupFight;
import endytkn.randomEvents.baseEvents.groupFight.GroupFightBaseEvent;
import endytkn.randomEvents.randomEvent.RandomEvent;
import endytkn.randomEvents.randomEvent.RandomEventsCategory;
import endytkn.randomEvents.randomEvent.RandomEventsRarity;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;

import java.util.*;

public class SkeletonFightEvent extends GroupFightBaseEvent {
    public SkeletonFightEvent() {
        super(true);
        categories = List.of(RandomEventsCategory.GROUP_FIGHT, RandomEventsCategory.DAY_NIGHT, RandomEventsCategory.WEATHER_CLEAR, RandomEventsCategory.WEATHER_THUNDER, RandomEventsCategory.WEATHER_RAIN);
        this.rarity = RandomEventsRarity.COMMON;
        this.eventTag = "skeleton_fight";
    }

    @Override
    public RandomEvent create() {
        return new SkeletonFightEvent();
    }

    @Override
    public void onPrepare() {
        LightningBolt lightningBolt = EntityType.LIGHTNING_BOLT.create(level);
        lightningBolt.setCause(null);
        lightningBolt.setPos(targetBlock.getX(), targetBlock.getY(), targetBlock.getZ());
        lightningBolt.setSecondsOnFire(0);
        level.addFreshEntity(lightningBolt);

        int minionsCount = 2 + playersGroup.size() * 2 + new Random().nextInt(2);
        int leaderCount = minionsCount / 3;

        Map<UUID, Mob> skeletonMobs = new HashMap<>();

        for (int i = 0; i < minionsCount; i++) {
            EntityType<? extends Mob> mobType = EntityType.SKELETON;
            Mob mob = mobType.create(level);
            this.addEntityToEvent(mob);
            if (mob != null) {
                ItemStack weapon = new ItemStack(i % 3 == 0 ? Items.BOW : Items.STONE_SWORD);
                mob.setCanPickUpLoot(false);
                mob.setItemInHand(InteractionHand.MAIN_HAND, weapon);
                skeletonMobs.put(mob.getUUID(), mob);
            }
        }

        for (int i = 0; i < leaderCount; i++) {
            EntityType<? extends Mob> mobType = EntityType.SKELETON;
            Mob mob = mobType.create(level);
            if (mob != null) {
                ItemStack weapon = new ItemStack(i % 3 == 0 ? Items.BOW : Items.GOLDEN_SWORD);
                mob.setHealth(mob.getHealth() * 2);
                mob.setCustomName(Component.literal("Skeleton Leader"));

                ItemStack helmet = new ItemStack(Items.LEATHER_HELMET);
                ItemStack chestplate = new ItemStack(Items.LEATHER_CHESTPLATE);
                helmet.enchant(Enchantments.UNBREAKING, 5);
                chestplate.enchant(Enchantments.UNBREAKING, 5);
                this.addEntityToEvent(mob);
                mob.addEffect(new MobEffectInstance(MobEffects.GLOWING, this.timeLimit));
                mob.setItemSlot(EquipmentSlot.HEAD, helmet);
                mob.setItemSlot(EquipmentSlot.CHEST, chestplate);
                mob.setCanPickUpLoot(false);
                mob.setItemInHand(InteractionHand.MAIN_HAND, weapon);
                skeletonMobs.put(mob.getUUID(), mob);
            }
        }

        GroupFight skeletonGroup = new GroupFight(this, "skeleton", skeletonMobs, null);
        mobGroupies.put(skeletonGroup.groupName, skeletonGroup);

        super.onPrepare();
    }
}
