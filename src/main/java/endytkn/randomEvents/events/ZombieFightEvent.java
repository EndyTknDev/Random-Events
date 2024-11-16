package endytkn.randomEvents.events;

import endytkn.randomEvents.baseEvents.groupFight.GroupFight;
import endytkn.randomEvents.baseEvents.groupFight.GroupFightBaseEvent;
import endytkn.randomEvents.randomEvent.RandomEvent;
import endytkn.randomEvents.randomEvent.RandomEventsCategory;
import endytkn.randomEvents.randomEvent.RandomEventsRarity;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;

import java.util.*;

public class ZombieFightEvent extends GroupFightBaseEvent {
    public ZombieFightEvent() {
        super(true);
        categories = List.of(RandomEventsCategory.GROUP_FIGHT, RandomEventsCategory.DAY_NIGHT, RandomEventsCategory.WEATHER_CLEAR, RandomEventsCategory.WEATHER_THUNDER, RandomEventsCategory.WEATHER_RAIN);
        this.rarity = RandomEventsRarity.COMMON;
        this.eventTag = "zombie_fight";
    }

    @Override
    public RandomEvent create() {
        return new ZombieFightEvent();
    }

    @Override
    public void onPrepare() {
        int minionsCount = new Random().nextInt(3 + this.playersGroup.size() * 2, 5 + this.playersGroup.size() * 2);
        int leaderCount = minionsCount / 3;
        Map<UUID, Mob> zombieMobs = new HashMap<>();

        for (int i = 0; i < minionsCount; i++) {
            spawnZombie(zombieMobs, false);
        }

        for (int i = 0; i < leaderCount; i++) {
            spawnZombie(zombieMobs, true);
        }

        // Create the group fight
        GroupFight zombieGroup = new GroupFight(this, "zombie", zombieMobs, null);
        mobGroupies.put(zombieGroup.groupName, zombieGroup);
        super.onPrepare();
    }

    private void spawnZombie(Map<UUID, Mob> zombieMobs, boolean isLeader) {
        Zombie mob = EntityType.ZOMBIE.create(level);

        if (mob != null) {
            if (isLeader) {
                this.addEntityToEvent(mob);
                mob.setHealth(mob.getHealth() * 2); // Double health for leaders
                mob.setCustomName(Component.literal("Zombie Leader"));
                ItemStack helmet = new ItemStack(Items.GOLDEN_HELMET);
                helmet.enchant(Enchantments.UNBREAKING, 5);
                mob.setItemSlot(EquipmentSlot.HEAD, helmet);
            }

            mob.setCanPickUpLoot(false);
            zombieMobs.put(mob.getUUID(), mob);
        }
    }
}
