package endytkn.randomEvents.network.packages;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

import java.util.Random;
import java.util.function.Supplier;

public class PoofPackage {
    private final BlockPos position;

    public PoofPackage(BlockPos position) {
        this.position = position;
    }

    public static void encode(PoofPackage msg, FriendlyByteBuf buffer) {
        buffer.writeInt(msg.position.getX());
        buffer.writeInt(msg.position.getY());
        buffer.writeInt(msg.position.getZ());
    }

    public static PoofPackage decode(FriendlyByteBuf buffer) {
        int posX = buffer.readInt();
        int posY = buffer.readInt();
        int posZ = buffer.readInt();

        return new PoofPackage(new BlockPos(posX, posY, posZ));
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        Random random = new Random();
        Level level = Minecraft.getInstance().level;
        for (int i = 0 ; i < 5; i++ ) {
            float motionX = random.nextFloat();
            float motionZ = random.nextFloat();
            level.addParticle(ParticleTypes.POOF, this.position.getX(), this.position.getY() + 1, this.position.getZ(), motionX, 0, motionZ);
        }

    }

}
