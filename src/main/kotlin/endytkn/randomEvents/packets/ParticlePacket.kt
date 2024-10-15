package endytkn.randomEvents.packets

import net.minecraft.client.Minecraft
import net.minecraft.client.multiplayer.ClientPacketListener
import net.minecraft.core.particles.ParticleOptions
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.PacketListener
import net.minecraft.network.protocol.Packet
import net.minecraft.world.phys.Vec3
import net.minecraftforge.network.NetworkEvent
import java.util.function.Supplier

class ParticlePacket(val particle: ParticleOptions, val position: Vec3, val motion: Vec3): Packet<PacketListener> {
    override fun write(buffer: FriendlyByteBuf) {
        println("NNNNNNNNNNNNNNNNNNNNN")
        buffer.writeUtf(particle.writeToString()) // Codifica o tipo de partícula como string
        buffer.writeDouble(position.x)
        buffer.writeDouble(position.y)
        buffer.writeDouble(position.z)
        buffer.writeDouble(motion.x)
        buffer.writeDouble(motion.y)
        buffer.writeDouble(motion.z)
    }

    override fun handle(listener: PacketListener) {
        if (listener is ClientPacketListener) {
            val level = listener.level
            level.addParticle(
                particle,
                position.x, position.y, position.z,
                motion.x, motion.y, motion.z
            )
        }
    }
}
