package endytkn.randomEvents.network.packages;

import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;
import java.util.Random;
import java.util.function.Supplier;

public class AbandonedWolfLovePackage {
    private final int wolfId;

    public AbandonedWolfLovePackage(int wolfUUID) {
        this.wolfId = wolfUUID;
    }

    public static void encode(AbandonedWolfLovePackage msg, FriendlyByteBuf buffer) {
       buffer.writeInt(msg.wolfId);
    }

    public static AbandonedWolfLovePackage decode(FriendlyByteBuf buffer) {
        int uuid = buffer.readInt();

        return new AbandonedWolfLovePackage(uuid);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (ctx.get().getDirection().getReceptionSide().isClient()) {
                ctx.get().setPacketHandled(true);
                Random random = new Random();
                Level level = Minecraft.getInstance().level;
                Wolf wolf = (Wolf) level.getEntity(this.wolfId);
                if (wolf == null) return;

                for (int i = 0; i < 10; i++) {
                    Vec3 motion = new Vec3((random.nextDouble() - 0.5) * 0.04, random.nextDouble() * 0.05, (random.nextDouble() - 0.5) * 0.04);
                    Vec3 offsetPos = new Vec3((random.nextDouble(1)), random.nextDouble(1), (random.nextDouble(1)));
                    level.addParticle(ParticleTypes.HEART, wolf.getX() + offsetPos.x, wolf.getY() + 1 + offsetPos.y, wolf.getZ() + offsetPos.z, motion.x, motion.y, motion.z);
                }
            }
        });

    }

}
