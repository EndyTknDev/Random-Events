package endytkn.randomEvents.events;

import endytkn.randomEvents.entities.GenericEntity;
import endytkn.randomEvents.eventObservers.MinecraftEventsObservers;
import endytkn.randomEvents.network.PacketHandler;
import endytkn.randomEvents.network.packages.PoofPackage;
import endytkn.randomEvents.randomEvent.RandomEvent;
import endytkn.randomEvents.randomEvent.RandomEventsCategory;
import endytkn.randomEvents.randomEvent.RandomEventsRarity;
import endytkn.randomEvents.utils.BlockPosUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.monster.Illusioner;
import net.minecraft.world.entity.monster.Pillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

public class VillagerTrapEvent extends RandomEvent {
    private GenericEntity villager;
    private int pillagersLeft = new Random().nextInt(2) + 3;

    private final Consumer<LivingDeathEvent> tickDeathConsumer = this::tickDeath;

    public VillagerTrapEvent() {
        categories = List.of(RandomEventsCategory.DAY_MORNING, RandomEventsCategory.DAY_NIGHT, RandomEventsCategory.DAY_AFTERNOON, RandomEventsCategory.DAY_EVENING, RandomEventsCategory.WEATHER_CLEAR);
        this.rarity = RandomEventsRarity.COMMON;
        this.eventTag = "villager_trap";
    }

    @Override
    public RandomEvent create() {
        return new VillagerTrapEvent();
    }


    public void tickDeath(LivingDeathEvent event) {
        Entity entity = event.getEntity();
        if (!this.isEntityOfEvent(entity)) return;
        if (entity instanceof Villager) {
            this.setEventStatus(RandomEventStatus.FINISHING_CANCELED);
            return;
        }
        if (entity instanceof Pillager) {
            this.pillagersLeft--;
        }
        if (this.pillagersLeft <= 0) {
            this.setEventStatus(RandomEventStatus.FINISHING_SUCCESS);
        }
    }

    @Override
    public void onPrepare() {
        this.distanceThreshold = 10;
        MinecraftEventsObservers.livingDeathObserver.add((this.tickDeathConsumer));

        spawnVillager();
        super.onPrepare();
    }

    @Override
    public void onPlayerEnter() {
        super.onPlayerEnter();
    }

    @Override
    public void onReady() {
        super.onReady();
        spawnPillagers();
    }

    @Override
    public void onFinishing() {
        super.onFinishing();
        MinecraftEventsObservers.livingDeathObserver.remove((this.tickDeathConsumer));
    }

    private void spawnVillager() {
        villager = new GenericEntity<>(EntityType.WANDERING_TRADER, level);
        villager.setPos(new Vec3(targetBlock.getX(), targetBlock.getY(), targetBlock.getZ()));
        villager.goalSelector.addGoal(1, new LookAtPlayerGoal(villager, Player.class, 15.0f));
        villager.getPersistentData().putString(this.eventTag, this.id.toString());
        this.addEntityToEvent(villager);
        level.addFreshEntity(villager);
    }

    private void spawnPillagers() {
        villager.remove(Entity.RemovalReason.DISCARDED);
        spawnIllusioner(villager.blockPosition());

        for (int i = 0; i < pillagersLeft - 1; i++) {
            BlockPos mobPos = BlockPosUtils.findRandomSurfaceBlockNearby(level, targetBlock, 10, 10);
            spawnPillager(mobPos);
        }
    }

    private void spawnIllusioner(BlockPos blockPos) {
        Illusioner illusioner = EntityType.ILLUSIONER.create(level);
        this.addEntityToEvent(illusioner);
        illusioner.setPos(new Vec3(blockPos.getX(), blockPos.getY(), blockPos.getZ()));
        illusioner.setPersistenceRequired();
        illusioner.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.CROSSBOW));
        illusioner.getPersistentData().putString(this.eventTag, this.id.toString());
        spawnParticle(illusioner.blockPosition());
        level.addFreshEntity(illusioner);
    }

    private void spawnPillager(BlockPos blockPos) {
        Pillager pillager = EntityType.PILLAGER.create(level);
        this.addEntityToEvent(pillager);
        pillager.setPos(new Vec3(blockPos.getX(), blockPos.getY(), blockPos.getZ()));
        pillager.setPersistenceRequired();
        pillager.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.CROSSBOW));
        pillager.getPersistentData().putString(this.eventTag, this.id.toString());
        spawnParticle(pillager.blockPosition());
        level.addFreshEntity(pillager);
    }

    private void spawnParticle(BlockPos blockPos) {
        level.playSound(null, blockPos, SoundEvents.ENDERMAN_TELEPORT, SoundSource.HOSTILE, 1.0f, 1.0f);
        PoofPackage poofPackage = new PoofPackage(blockPos);
        PacketHandler.sendPacketToAll(poofPackage);
    }

    @Override
    public void onFinishingCanceled() {
        villager.remove(Entity.RemovalReason.DISCARDED);
        super.onFinishingCanceled();
    }
}
