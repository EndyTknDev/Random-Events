package endytkn.randomEvents.packets

import net.minecraft.network.protocol.Packet
import net.minecraft.server.level.ServerLevel

object PacketHandler {
    /*private const val PROTOCOL_VERSION = "1"
    var packetId = 0
    val channelName: String = "main_channel"
    val CHANNEL: SimpleChannel = NetworkRegistry.newSimpleChannel(
        ResourceLocation(RandomEventsMod.ID, channelName),
        { PROTOCOL_VERSION },
        { PROTOCOL_VERSION == it },
        { PROTOCOL_VERSION == it }
    )


    fun registerPackets() {
        CHANNEL.registerMessage(
            packetId++,
            ParticlePacket::class.java,
            ParticlePacket::encode,
            ParticlePacket::decode,
            ParticlePacket::handle,
            Optional.of(NetworkDirection.PLAY_TO_CLIENT)
        )
    }

    @SubscribeEvent
    fun onCommonSetup(event: FMLCommonSetupEvent) {
        registerPackets()
    }*/

    fun sendToClients(packet: Packet<*>, server: ServerLevel) {
        for (player in server.players()) {
            println(packet)
            player.connection.send(packet)
        }
    }
}