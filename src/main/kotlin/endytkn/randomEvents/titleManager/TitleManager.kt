package endytkn.randomEvents.titleManager

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Font
import net.minecraftforge.client.event.RenderGuiOverlayEvent
import net.minecraftforge.event.TickEvent
import net.minecraftforge.event.entity.player.PlayerEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod

@Mod.EventBusSubscriber
object TitleManager {
    private var displayMessage: String? = null
    private var initialDisplayTime: Int = 20 * 8
    private var displayTime: Int = 0
    private var alpha: Float = 1.0f

    fun createColorWithAlphaAndTransition(alpha: Float): Int {
        val red: Int
        val green: Int
        val blue: Int
        var currentAlpha = alpha

        when {
            alpha > 0.1f -> {
                red = (255 - (127 * (1 - alpha) * 2)).toInt()
                green = red
                blue = red
            }
            else -> {
                red = 128
                green = 128
                blue = 128
                currentAlpha = 0.1f // Hold at 50% opacity
            }
        }
        val alphaValue = (currentAlpha * 255).toInt()
        return (alphaValue shl 24) or (red shl 16) or (green shl 8) or blue
    }

    fun showTitle(text: String) {
        displayMessage = text // Example text
        displayTime = initialDisplayTime // Reset the display time
        alpha = 1.0f // Reset opacity to full visibility
    }

    @SubscribeEvent
    fun onTick(event: TickEvent.ServerTickEvent) {
        val minecraft = Minecraft.getInstance()
        if (minecraft.isPaused == false) {
            displayTime--
            if (displayTime <= 0) {
                alpha = 0f // Set opacity to 0 when time runs out
            }
        }
    }

    @SubscribeEvent
    fun onRenderOverlay(event: RenderGuiOverlayEvent.Post) {
        val minecraft = Minecraft.getInstance()
        val font: Font = minecraft.font

        if (displayMessage != null && displayTime > 0 && minecraft.isPaused == false) {
            val screenWidth = minecraft.window.guiScaledWidth
            val screenHeight = minecraft.window.guiScaledHeight

            // Calculate alpha for the transition (only use alpha for the fade to gray)
            alpha = displayTime / initialDisplayTime.toFloat()

            // Create a color with both fading alpha and transitioning from white to gray
            val color = createColorWithAlphaAndTransition(alpha)

            // Draw the string on the screen
            event.guiGraphics.pose().pushPose() // Save the current transformation state
            event.guiGraphics.pose().scale(1.2f, 1.2f, 1.2f) // Scale the font by 2x

            // Calculate the X and Y positions without scaling, then scale
            val unscaledX = (screenWidth / 2  - font.width(displayMessage!!) ) // Unscaled X position
            val unscaledY = (screenHeight * 0.2) // Unscaled Y position

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
        }
    }
}
