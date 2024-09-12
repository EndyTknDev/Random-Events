package example.examplemod.event

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Font
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.client.event.ClientChatEvent
import net.minecraftforge.client.event.RenderGuiOverlayEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod

@Mod.EventBusSubscriber(modid = "examplemod", bus = Mod.EventBusSubscriber.Bus.FORGE, value = [Dist.CLIENT])
object ModEvents {

    private var displayMessage: String? = null
    private var initialDisplayTime: Int = 3000 // 15 seconds (20 ticks = 1 second)
    private var displayTime: Int = 0
    private var alpha: Float = 1.0f // Start with full opacity (1.0)

    // Create a color that starts white, transitions to gray, and stays at 50% opacity until time runs out
    fun createColorWithAlphaAndTransition(alpha: Float): Int {
        // Phase 1: White -> Gray transition
        val red: Int
        val green: Int
        val blue: Int
        var currentAlpha = alpha // Full opacity in the beginning

        when {
            alpha > 0.5f -> {
                // First half of the transition: color fades from white (255) to gray (128)
                red = (255 - (127 * (1 - alpha) * 2)).toInt()
                green = red
                blue = red
            }
            else -> {
                // Second half: color remains gray (128), hold at 50% opacity
                red = 128
                green = 128
                blue = 128
                currentAlpha = 0.5f // Hold at 50% opacity
            }
        }

        // Combine the alpha value with the RGB color
        val alphaValue = (currentAlpha * 255).toInt()
        return (alphaValue shl 24) or (red shl 16) or (green shl 8) or blue
    }

    // Capture the chat message and initialize the display time and opacity
    @SubscribeEvent
    fun onChatMessage(event: ClientChatEvent) {
        displayMessage = "Overworld" // Example text
        displayTime = initialDisplayTime // Reset the display time
        alpha = 1.0f // Reset opacity to full visibility
    }

    // Render the message on the screen with the fade-out and color transition effect
    @SubscribeEvent
    fun onRenderOverlay(event: RenderGuiOverlayEvent.Post) {
        val minecraft = Minecraft.getInstance()
        val font: Font = minecraft.font

        if (displayMessage != null && displayTime > 0) {
            val screenWidth = minecraft.window.guiScaledWidth
            val screenHeight = minecraft.window.guiScaledHeight

            // Calculate alpha for the transition (only use alpha for the fade to gray)
            alpha = displayTime / initialDisplayTime.toFloat()

            // Create a color with both fading alpha and transitioning from white to gray
            val color = createColorWithAlphaAndTransition(alpha)

            // Draw the string on the screen
            event.guiGraphics.pose().pushPose() // Save the current transformation state
            event.guiGraphics.pose().scale(2.0f, 2.0f, 2.0f) // Scale the font by 2x

            // Calculate the X and Y positions without scaling, then scale
            val unscaledX = (screenWidth / 2 - font.width(displayMessage!!) ) // Unscaled X position
            val unscaledY = (screenHeight / 2) // Unscaled Y position

            event.guiGraphics.drawString(
                font, 
                displayMessage!!,
                (unscaledX / 2).toFloat(),  // Divide by 2 after calculating unscaled positions
                (unscaledY / 2).toFloat(),  // Divide by 2 for scaling purposes
                color, 
                true
            )

            event.guiGraphics.pose().popPose() // Restore the previous transformation state

            // Decrease display time, but do not decrease opacity further if displayTime > 0
            displayTime--
            if (displayTime <= 0) {
                alpha = 0f // Set opacity to 0 when time runs out
            }
        }
    }
}
