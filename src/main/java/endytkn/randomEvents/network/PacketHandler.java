package endytkn.randomEvents.network;

import endytkn.randomEvents.RandomEventsMod;
import endytkn.randomEvents.network.packages.AbandonedWolfLovePackage;
import endytkn.randomEvents.network.packages.PoofPackage;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class PacketHandler {
    private static final String PROTOCOL_VERSION = "1";
    private static final SimpleChannel INSTANCE = NetworkRegistry.ChannelBuilder.named(
            new ResourceLocation(RandomEventsMod.ID, "main"))
            .serverAcceptedVersions((version) -> true)
            .clientAcceptedVersions((version) -> true)
            .networkProtocolVersion(() -> "1")
            .simpleChannel();

    public static void init() {
        INSTANCE.messageBuilder(AbandonedWolfLovePackage.class, 0)
                .encoder(AbandonedWolfLovePackage::encode)
                .decoder(AbandonedWolfLovePackage::decode)
                .consumerMainThread(AbandonedWolfLovePackage::handle)
                .add();

        INSTANCE.messageBuilder(PoofPackage.class, 1)
                .encoder(PoofPackage::encode)
                .decoder(PoofPackage::decode)
                .consumerMainThread(PoofPackage::handle)
                .add();
    }

    public static void sendPacketToPlayer(Object packet, ServerPlayer player) {
        INSTANCE.sendTo(packet, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    }

    public static void sendPacketToNearbyClients(Object packet, ServerLevel level, Vec3 position, double radius) {
        INSTANCE.send(PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(position.x, position.y, position.z, radius, level.dimension())), packet);
    }

    public static void sendPacketToAll(Object packet) {
        INSTANCE.send(PacketDistributor.ALL.noArg(), packet);
    }

    public static void sendPacketToServer(Object packet) {
        INSTANCE.send(PacketDistributor.SERVER.noArg(), packet);
    }
}