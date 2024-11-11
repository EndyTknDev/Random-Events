package endytkn.randomEvents.events;

import endytkn.randomEvents.eventObservers.MinecraftEventsObservers;
import endytkn.randomEvents.randomEvent.RandomEvent;
import endytkn.randomEvents.utils.BlockPosUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Illusioner;
import net.minecraft.world.entity.monster.Pillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

import java.util.function.Consumer;

public class PillagerAmbushVillagerEvent extends RandomEvent {
    private Villager villager;
    private Player player;
    public PillagerAmbushVillagerEvent() {
        this.eventTag = "pillagerAmbushVillager";
    }
    private final Consumer<LivingDeathEvent> tickDeathConsumer = this::tickDeathEvent;

    @Override
    public RandomEvent create() {
        return new PillagerAmbushVillagerEvent();
    }

    @Override
    public void onPrepare() {
        this.distanceThreshold = 20;
        super.onPrepare();
        spawnAmbush();
    }

    private void tickDeathEvent(LivingDeathEvent event) {
        if (!this.isEntityOfEvent(event.getEntity())) return;

        if (event.getEntity() instanceof Villager) {
            this.onFinishingCanceled();
            return;
        }

        if (event.getEntity() instanceof Pillager) {
            if (event.getSource().getEntity() instanceof Player) {
                this.player = (Player) event.getSource().getEntity();
                this.resolve();
            } else {
                this.onFinishingCanceled();
            }
        }
    }

    @Override
    public void onReady() {
        MinecraftEventsObservers.livingDeathObserver.add(this.tickDeathConsumer);
        super.onReady();
    }

    @Override
    public void onFinishingSuccess() {
        this.rewardPlayer();
        super.onFinishingSuccess();
    }

    @Override
    public void onFinishing() {
        MinecraftEventsObservers.livingDeathObserver.remove(this.tickDeathConsumer);
        super.onFinishing();
    }

    private void rewardPlayer() {
        this.player.giveExperiencePoints(100);
        villager.lookAt(this.player, 1.0f, 1.0f);
        int emeralds = 3 + (int) (Math.random() * 3); // Random entre 3 e 5
        ItemStack emeraldStack = new ItemStack(Items.EMERALD, emeralds);
        this.player.addItem(emeraldStack);
    }

    private void spawnVillager() {
        villager = new Villager(EntityType.VILLAGER, level);
        villager.getPersistentData().putString(this.eventTag, this.id.toString());
        villager.setVillagerData(villager.getVillagerData()
                .setProfession(VillagerProfession.FARMER)
                .setType(VillagerType.PLAINS)
                .setLevel(2));
        villager.setHealth(villager.getHealth() * 1.2f);
        villager.addEffect(new MobEffectInstance(MobEffects.GLOWING, this.timeLimit));
        this.addEntityToEvent(villager);
        BlockPos villagerPos = BlockPosUtils.findRandomSurfaceBlockNearby(this.level, this.targetBlock, 3, 3);
        villager.setPos(villagerPos.getX(), villagerPos.getY(), villagerPos.getZ());
        level.addFreshEntity(villager);
    }

    private void spawnPillager() {
        Pillager pillager = EntityType.PILLAGER.create(level);
        pillager.getPersistentData().putString(this.eventTag, this.id.toString());
        BlockPos pillagerPos = BlockPosUtils.findRandomSurfaceBlockNearby(this.level, this.targetBlock, 3, 3);
        this.addEntityToEvent(pillager);
        pillager.addEffect(new MobEffectInstance(MobEffects.GLOWING, this.timeLimit));
        pillager.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.CROSSBOW));
        pillager.setPos(pillagerPos.getX(), pillagerPos.getY(), pillagerPos.getZ());
        level.addFreshEntity(pillager);
    }

    private void spawnAmbush() {
        spawnVillager();
        spawnPillager();
    }
}
