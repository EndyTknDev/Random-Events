package endytkn.randomEvents.utils

import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component
import net.minecraft.server.MinecraftServer
import net.minecraft.world.level.Level

object MessageUtils {
    fun sendMessage(message: String) {
        val chatMessage = Component.literal(message)
        GameInstances.minecraftServer!!.sendSystemMessage(chatMessage)
    }
}