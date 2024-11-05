package endytkn.randomEvents.events;

import endytkn.randomEvents.eventObservers.MinecraftEventsObservers;
import endytkn.randomEvents.network.PacketHandler;
import endytkn.randomEvents.network.packages.AbandonedWolfLovePackage;
import endytkn.randomEvents.randomEvent.RandomEvent;
import endytkn.randomEvents.utils.BlockPosUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import java.util.*;
import java.util.function.Consumer;

public class AbandonedWolfEvent extends RandomEvent {
    private Wolf wolf;
    private final int cryInterval = 20 * 40;
    private int cryIntervalPassed = cryInterval;
    private ServerPlayer player;
    private final List<String> possiblesWolfNames = Arrays.asList("Maithe", "Cloé", "Bidu", "Kuro", "Zeus", "Hulk", "Bolt");
    private final List<Item> possiblesFavoriteFood = Arrays.asList(Items.CHICKEN, Items.BEEF, Items.MUTTON);
    private final Consumer<TickEvent.ServerTickEvent> tickWolfConsumer = this::tickWolf;
    private final Consumer<PlayerInteractEvent.EntityInteract> wolfInteractConsumer = this::wolfInteract;
    private final Consumer<LivingDeathEvent> wolfDeathTickConsumer = this::wolfDeathTick;
    private final String wolfName;
    private final Item favoriteFood;
    private boolean loreDropped = false;

    public AbandonedWolfEvent() {
        this.eventTag = "abandonedWolf";
        this.wolfName = generateRandomName();
        this.favoriteFood = generateRandomFavoriteFood();
    }

    @Override
    public RandomEvent create() {
        return new AbandonedWolfEvent();
    }

    private String generateRandomName() {
        Random random = new Random();
        int randomIndex = random.nextInt(possiblesWolfNames.size());
        return possiblesWolfNames.get(randomIndex);
    }

    private Item generateRandomFavoriteFood() {
        Random random = new Random();
        int randomIndex = random.nextInt(possiblesFavoriteFood.size());
        return possiblesFavoriteFood.get(randomIndex);
    }

    private void wolfDeathTick(LivingDeathEvent event) {
        if (event.getEntity() instanceof Wolf entity && this.isEntityOfEvent(entity)) {
            this.cancelEvent();
        }
    }

    private void wolfInteract(PlayerInteractEvent.EntityInteract event) {
        if (event.getTarget() instanceof Wolf entity && this.isEntityOfEvent(entity)) {
            this.player = (ServerPlayer) event.getEntity();
            if (!loreDropped) {
                event.setCanceled(true);
                dropLore();
                return;
            }
            if (player.getMainHandItem().getItem() == favoriteFood) {
                this.resolve();
            }
        }
    }

    private void tickWolf(TickEvent.ServerTickEvent e) {
        if (Minecraft.getInstance().isPaused()) return;
        if (this.cryIntervalPassed <= 0) {
            this.wolfCry();
            this.cryIntervalPassed = this.cryInterval;
        }
        this.cryIntervalPassed--;
    }

    @Override
    public void onPrepare() {
        spawnWolf();
        MinecraftEventsObservers.serverTickObserver.add(this.tickWolfConsumer);
        MinecraftEventsObservers.playerInteractEntityObserver.add(this.wolfInteractConsumer);
        MinecraftEventsObservers.livingDeathObserver.add(this.wolfDeathTickConsumer);
        super.onPrepare();
    }

    @Override
    public void onFinishing() {
        MinecraftEventsObservers.serverTickObserver.remove(this.tickWolfConsumer);
        MinecraftEventsObservers.playerInteractEntityObserver.remove(this.wolfInteractConsumer);
        MinecraftEventsObservers.livingDeathObserver.remove(this.wolfDeathTickConsumer);
        super.onFinishing();
    }

    @Override
    public void onFinishingSuccess() {
        if (wolf != null && player != null) {
            AttributeModifier modifier = new AttributeModifier(UUID.randomUUID(), "Wolf Boost", 0.3, AttributeModifier.Operation.ADDITION);
            wolf.setOwnerUUID(player.getUUID());
            Objects.requireNonNull(wolf.getAttribute(Attributes.MOVEMENT_SPEED)).addPermanentModifier(modifier);
            Objects.requireNonNull(wolf.getAttribute(Attributes.MAX_HEALTH)).addPermanentModifier(modifier);
            Objects.requireNonNull(wolf.getAttribute(Attributes.ATTACK_DAMAGE)).addPermanentModifier(modifier);
            this.removeEntityFromEvent(wolf);
            wolf.heal(4.0f);
            wolf.setCollarColor(DyeColor.RED);
            wolf.setCustomName(null);

            player.giveExperiencePoints(100);
            level.playSound(wolf, wolf.blockPosition(), SoundEvents.WOLF_AMBIENT, SoundSource.NEUTRAL, 1.0f, 1f);Random random = new Random();

            AbandonedWolfLovePackage particlePacket = new AbandonedWolfLovePackage(this.wolf.getId());
            PacketHandler.sendPacketToNearbyClients(particlePacket, this.level, this.wolf.position(), 50);
            dropRewards();
        }
        super.onFinishingSuccess();
    }

    private ItemEntity createDropRewardEntity(ItemStack itemStack) {
        return new ItemEntity(
                level,
                wolf.getX(),
                wolf.getY() + 0.5,
                wolf.getZ(),
                itemStack
        );
    }

    private ItemEntity createLorePaper() {
        ItemStack paper = new ItemStack(Items.PAPER, 1);
        paper.setHoverName(Component.literal("A letter"));
        String lore = String.format("Soon, unfortunately, I will not be in the same world as my beloved. Please take care of %s.", this.wolfName);
        String favoriteFood = String.format("Favorite food is %s.", this.favoriteFood.toString());
        ListTag loreList = new ListTag();
        loreList.add(StringTag.valueOf(Component.Serializer.toJson(Component.literal(lore).withStyle(ChatFormatting.GRAY))));
        loreList.add(StringTag.valueOf(Component.Serializer.toJson(Component.literal(favoriteFood).withStyle(ChatFormatting.YELLOW))));
        paper.getOrCreateTagElement("display").put("Lore", loreList);
        return createDropRewardEntity(paper);
    }

    private ItemEntity createTagName() {
        ItemStack nameTag = new ItemStack(Items.NAME_TAG, 1);
        nameTag.setHoverName(Component.literal(this.wolfName));
        return createDropRewardEntity(nameTag);
    }

    private ItemEntity createMainReward() {
        ItemStack emeraldStack = new ItemStack(Items.EMERALD, 1);
        return createDropRewardEntity(emeraldStack);
    }

    private void spawnReward(ItemEntity item, Vec3 direction, double speedMultiplier) {
        item.setDeltaMovement( direction.x * speedMultiplier, direction.y * speedMultiplier + 0.2, direction.z * speedMultiplier);
        level.addFreshEntity(item);
    }

    private void dropLore() {
        Vec3 direction = new Vec3(
                player.getX() - wolf.getX(),
                player.getY() - wolf.getY(),
                player.getZ() - wolf.getZ()
        ).normalize();

        double speedMultiplier = 0.2;
        ItemEntity paper = createLorePaper();
        spawnReward(paper, direction, speedMultiplier);
        this.loreDropped = true;
    }

    private void dropRewards() {
        Vec3 direction = new Vec3(
                player.getX() - wolf.getX(),
                player.getY() - wolf.getY(),
                player.getZ() - wolf.getZ()
        ).normalize();

        double speedMultiplier = 0.2;

        ItemEntity tagName = createTagName();
        ItemEntity mainReward = createMainReward();

        spawnReward(tagName, direction, speedMultiplier);
        spawnReward(mainReward, direction, speedMultiplier);
    }

    private void spawnWolf() {
        wolf = EntityType.WOLF.create(level);
        wolf.setTame(true);
        wolf.setHealth(wolf.getHealth() * 0.2f);
        wolf.setInSittingPose(true);
        BlockPos pos = BlockPosUtils.findRandomSurfaceBlockNearby(level, targetBlock, 3, 3);
        wolf.setPos(pos.getX(), pos.getY(), pos.getZ());
        wolf.setCollarColor(DyeColor.GRAY);
        wolf.setCustomName(Component.literal("Abandoned Wolf"));
        wolf.setCustomNameVisible(true);
        wolf.getPersistentData().putString(eventTag, id.toString());
        this.addEntityToEvent(wolf);
        wolf.goalSelector.addGoal(1, new LookAtPlayerGoal(wolf, Player.class, 20.0f));
        level.addFreshEntity(wolf);
    }

    private void wolfCry() {
        level.playSound(wolf, wolf.blockPosition(), SoundEvents.WOLF_WHINE, SoundSource.NEUTRAL, 1.0f, 1f);
    }
}
