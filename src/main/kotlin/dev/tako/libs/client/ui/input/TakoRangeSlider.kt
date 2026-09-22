package dev.tako.libs.client.ui.input

import dev.tako.libs.client.ui.core.TakoTheme
import dev.tako.libs.client.ui.layout.TakoBounds
import dev.tako.libs.client.ui.layout.TakoLayoutable
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder
import net.minecraft.client.gui.widget.ClickableWidget
import net.minecraft.text.Text
import kotlin.math.abs
import kotlin.math.roundToInt

class TakoRangeSlider(
    private val label: String,
    private val textRenderer: TextRenderer,
    x: Int,
    y: Int,
    width: Int,
    height: Int = 20,
    min: Float = 0f,
    max: Float = 1f,
    initialLow: Float = min,
    initialHigh: Float = max,
    private val onChanged: (Float, Float) -> Unit = { _, _ -> }
) : ClickableWidget(x, y, width, height, Text.literal(label)), TakoLayoutable {
    override fun layout(bounds: TakoBounds) {
        setDimensionsAndPosition(bounds.width, bounds.height, bounds.x, bounds.y)
    }

    var minValue: Float = min
    var maxValue: Float = max
    var lowValue: Float = initialLow.coerceIn(min, max)
        private set
    var highValue: Float = initialHigh.coerceIn(min, max)
        private set
    private var activeHandle = 0

    override fun onClick(mouseX: Double, mouseY: Double) {
        activeHandle = if (abs(ratioAt(mouseX) - lowRatio()) <= abs(ratioAt(mouseX) - highRatio())) 0 else 1
        updateFromMouse(mouseX)
    }

    override fun mouseDragged(mouseX: Double, mouseY: Double, button: Int, deltaX: Double, deltaY: Double): Boolean {
        if (button != 0) return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)
        updateFromMouse(mouseX)
        return true
    }

    override fun renderWidget(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        val trackY = y + height / 2
        val left = x + 4
        val right = x + width - 4
        val lowX = left + ((right - left) * lowRatio()).roundToInt()
        val highX = left + ((right - left) * highRatio()).roundToInt()
        context.drawTextWithShadow(textRenderer, Text.literal("$label: ${format(lowValue)} - ${format(highValue)}"), x, y, TakoTheme.TEXT_PRIMARY)
        context.fill(left, trackY + 5, right, trackY + 7, TakoTheme.PANEL_CARD)
        context.fill(lowX, trackY + 5, highX, trackY + 7, TakoTheme.ACCENT)
        context.fill(lowX - 3, trackY + 1, lowX + 3, trackY + 11, TakoTheme.TEXT_PRIMARY)
        context.fill(highX - 3, trackY + 1, highX + 3, trackY + 11, TakoTheme.TEXT_PRIMARY)
    }

    override fun appendClickableNarrations(builder: NarrationMessageBuilder) {
        appendDefaultNarrations(builder)
    }

    private fun updateFromMouse(mouseX: Double) {
        val ratio = ratioAt(mouseX)
        val value = minValue + (maxValue - minValue) * ratio
        if (activeHandle == 0) lowValue = value.coerceAtMost(highValue)
        else highValue = value.coerceAtLeast(lowValue)
        onChanged(lowValue, highValue)
    }

    private fun ratioAt(mouseX: Double): Float =
        ((mouseX - (x + 4)) / (width - 8).coerceAtLeast(1)).toFloat().coerceIn(0f, 1f)

    private fun lowRatio(): Float = if (maxValue == minValue) 0f else (lowValue - minValue) / (maxValue - minValue)

    private fun highRatio(): Float = if (maxValue == minValue) 1f else (highValue - minValue) / (maxValue - minValue)

    private fun format(value: Float): String = "%.2f".format(value)
}
