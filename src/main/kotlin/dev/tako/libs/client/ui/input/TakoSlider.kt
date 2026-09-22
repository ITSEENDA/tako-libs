package dev.tako.libs.client.ui.input

import dev.tako.libs.client.ui.core.TakoTheme
import dev.tako.libs.client.ui.layout.TakoBounds
import dev.tako.libs.client.ui.layout.TakoLayoutable
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder
import net.minecraft.client.gui.widget.ClickableWidget
import net.minecraft.text.Text
import kotlin.math.roundToInt

class TakoSlider(
    private val label: String,
    private val textRenderer: TextRenderer,
    x: Int,
    y: Int,
    width: Int,
    height: Int = 20,
    min: Float = 0f,
    max: Float = 1f,
    initialValue: Float = min,
    private val onChanged: (Float) -> Unit = {}
) : ClickableWidget(x, y, width, height, Text.literal(label)), TakoLayoutable {
    override fun layout(bounds: TakoBounds) {
        setDimensionsAndPosition(bounds.width, bounds.height, bounds.x, bounds.y)
    }

    var minValue: Float = min
    var maxValue: Float = max
    var value: Float = initialValue.coerceIn(min, max)
        set(newValue) {
            val next = newValue.coerceIn(minValue, maxValue)
            if (field == next) return
            field = next
            onChanged(next)
        }

    override fun onClick(mouseX: Double, mouseY: Double) {
        value = valueAt(mouseX)
    }

    override fun mouseDragged(mouseX: Double, mouseY: Double, button: Int, deltaX: Double, deltaY: Double): Boolean {
        if (button != 0) return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)
        value = valueAt(mouseX)
        return true
    }

    override fun renderWidget(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        val trackY = y + height / 2
        val trackLeft = x + 4
        val trackRight = x + width - 4
        val ratio = ratio()
        context.drawTextWithShadow(textRenderer, Text.literal("$label: ${format(value)}"), x, y, TakoTheme.TEXT_PRIMARY)
        context.fill(trackLeft, trackY + 5, trackRight, trackY + 7, TakoTheme.PANEL_CARD)
        context.fill(trackLeft, trackY + 5, trackLeft + ((trackRight - trackLeft) * ratio).roundToInt(), trackY + 7, TakoTheme.ACCENT)
        val knobX = trackLeft + ((trackRight - trackLeft) * ratio).roundToInt()
        context.fill(knobX - 3, trackY + 1, knobX + 3, trackY + 11, TakoTheme.TEXT_PRIMARY)
    }

    override fun appendClickableNarrations(builder: NarrationMessageBuilder) {
        appendDefaultNarrations(builder)
    }

    private fun ratio(): Float = if (maxValue == minValue) 0f else (value - minValue) / (maxValue - minValue)

    private fun valueAt(mouseX: Double): Float {
        val ratio = ((mouseX - (x + 4)) / (width - 8).coerceAtLeast(1)).toFloat().coerceIn(0f, 1f)
        return minValue + (maxValue - minValue) * ratio
    }

    private fun format(value: Float): String = "%.2f".format(value)
}
