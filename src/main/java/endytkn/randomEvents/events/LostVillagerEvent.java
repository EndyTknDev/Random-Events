package endytkn.randomEvents.events;

import endytkn.randomEvents.eventObservers.MinecraftEventsObservers;
import endytkn.randomEvents.goals.FollowPlayerGoal;
import endytkn.randomEvents.randomEvent.RandomEvent;
import endytkn.randomEvents.utils.BlockPosUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import java.util.function.Consumer;

public class LostVillagerEvent extends RandomEvent  {
    private Villager villager;
    private Player player;
    private boolean followingPlayer = false;
    private final Consumer<PlayerInteractEvent.EntityInteract> playerInteractVillagerConsumer = this::playerInteractVillager;
    private final Consumer<LivingDeathEvent> livingDeathConsumer = this::tickDeath;
    private FollowPlayerGoal followPlayerGoal;

    public LostVillagerEvent() {
        this.eventTag = "lostVillager";
    }

    @Override
    public RandomEvent create() {
        return new LostVillagerEvent();
    }

    @Override
    public void onPrepare() {
        this.villager = EntityType.VILLAGER.create(this.level);
        MinecraftEventsObservers.playerInteractEntityObserver.add(this.playerInteractVillagerConsumer);
        MinecraftEventsObservers.livingDeathObserver.add(this.livingDeathConsumer);
        spawnVillager();
        super.onPrepare();
    }

    @Override
    public void onFinishing() {
        MinecraftEventsObservers.playerInteractEntityObserver.remove(this.playerInteractVillagerConsumer);
        MinecraftEventsObservers.livingDeathObserver.remove(this.livingDeathConsumer);
        super.onFinishing();
    }

    public void tickDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof Villager entity && this.isEntityOfEvent(entity)) {
            this.cancelEvent();
        }
    }

    public void playerInteractVillager(PlayerInteractEvent.EntityInteract event) {
        Player player = event.getEntity();

        if (event.getLevel().isClientSide) return;

        if (isInVillager()) {
            this.player = event.getEntity();
            this.resolve();
            event.setCanceled(true);
            return;
        }

        if (!this.followingPlayer) {
            this.followPlayer(player);
        } else {
            this.unfollowPlayer();
        }
        event.setCanceled(true);
    }

    public void followPlayer(Player player) {
        this.followingPlayer = true;
        this.player = player;
        this.followPlayerGoal = new FollowPlayerGoal(this.villager, 1.0, 5, player);
        this.villager.goalSelector.addGoal(1, followPlayerGoal);
        this.villager.addEffect(new MobEffectInstance(MobEffects.GLOWING, this.timeLimit));
    }

    public void unfollowPlayer() {
        this.followingPlayer = false;
        this.player = null;
        this.villager.removeEffect(MobEffects.GLOWING);
        this.villager.goalSelector.removeGoal(followPlayerGoal);

    }

    @Override
    public void onFinishingSuccess() {
        this.player.giveExperiencePoints(100);
        villager.lookAt(this.player, 1.0f, 1.0f);
        this.villager.goalSelector.removeGoal(followPlayerGoal);
        this.villager.removeEffect(MobEffects.GLOWING);
        this.removeEntityFromEvent(this.villager);
        int emeralds = 10 + (int) (Math.random() * 3); // Random entre 3 e 5
        ItemStack emeraldStack = new ItemStack(Items.EMERALD, emeralds);
        villager.setCustomName(null);
        this.player.addItem(emeraldStack);
        super.onFinishingSuccess();
    }

    public void spawnVillager() {
        BlockPos pos = BlockPosUtils.findRandomSurfaceBlockNearby(level, targetBlock, 3, 3);
        villager.setPos(pos.getX(), pos.getY(), pos.getZ());
        villager.setVillagerData(villager.getVillagerData()
                .setProfession(VillagerProfession.FARMER)
                .setType(VillagerType.PLAINS)
                .setLevel(3));

        villager.setCustomName(Component.literal("Lost Villager"));
        addEntityToEvent(villager);
        this.level.addFreshEntity(this.villager);
    }

    public boolean isInVillager() {
        BlockPos blockPos = this.villager.blockPosition();
        boolean isInVillage = level.getPoiManager()
                .getInRange(
                        poiType -> poiType.is(PoiTypes.HOME), // Verifica se é um POI de qualquer tipo
                        blockPos,
                        10,
                        PoiManager.Occupancy.ANY
                ).findAny().isPresent();
        return isInVillage;
    }
}
