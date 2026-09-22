package dev.tako.libs.client.ui.widget

import dev.tako.libs.client.ui.layout.TakoBounds
import dev.tako.libs.client.ui.layout.TakoLayoutable
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder
import net.minecraft.client.gui.widget.ClickableWidget
import net.minecraft.text.Text

data class TakoButtonStyle(
    var background: Int = 0xFF555555.toInt(),
    var hoveredBackground: Int = 0xFF777777.toInt(),
    var disabledBackground: Int = 0xFF333333.toInt(),
    var border: Int = 0xFF202020.toInt(),
    var text: Int = 0xFFFFFF,
    var disabledText: Int = 0xFF888888.toInt(),
    var borderWidth: Int = 1
)

/** Reusable styled button component; the native widget contract remains intact. */
class TakoButton(
    label: String,
    x: Int,
    y: Int,
    width: Int,
    height: Int = 20,
    private val onPress: () -> Unit,
    val textRenderer: TextRenderer,
    var style: TakoButtonStyle = TakoButtonStyle()
) : ClickableWidget(x, y, width, height, Text.literal(label)), TakoLayoutable {
    override fun layout(bounds: TakoBounds) {
        setDimensionsAndPosition(bounds.width, bounds.height, bounds.x, bounds.y)
    }

    override fun onClick(mouseX: Double, mouseY: Double) {
        if (active) onPress()
    }

    override fun renderWidget(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        val background = when {
            !active -> style.disabledBackground
            isHovered -> style.hoveredBackground
            else -> style.background
        }
        context.fill(x, y, x + width, y + height, background)
        if (style.borderWidth > 0) {
            repeat(style.borderWidth) { inset ->
                context.fill(x + inset, y + inset, x + width - inset, y + inset + 1, style.border)
                context.fill(x + inset, y + height - inset - 1, x + width - inset, y + height - inset, style.border)
                context.fill(x + inset, y + inset, x + inset + 1, y + height - inset, style.border)
                context.fill(x + width - inset - 1, y + inset, x + width - inset, y + height - inset, style.border)
            }
        }
        context.drawCenteredTextWithShadow(
            textRenderer,
            message,
            x + width / 2,
            y + (height - 8) / 2,
            if (active) style.text else style.disabledText
        )
    }

    override fun appendClickableNarrations(builder: NarrationMessageBuilder) {
        appendDefaultNarrations(builder)
    }

    var text: Text
        get() = message
        set(value) { setMessage(value) }
}
