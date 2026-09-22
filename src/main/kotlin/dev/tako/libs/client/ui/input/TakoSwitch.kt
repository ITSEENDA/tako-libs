package dev.tako.libs.client.ui.input

import dev.tako.libs.client.ui.core.TakoTheme
import dev.tako.libs.client.ui.layout.TakoBounds
import dev.tako.libs.client.ui.layout.TakoLayoutable
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder
import net.minecraft.client.gui.widget.ClickableWidget
import net.minecraft.text.Text

class TakoSwitch(
    label: String,
    private val textRenderer: TextRenderer,
    x: Int,
    y: Int,
    width: Int,
    height: Int = 20,
    initialOn: Boolean = false,
    private val onChanged: (Boolean) -> Unit = {}
) : ClickableWidget(x, y, width, height, Text.literal(label)), TakoLayoutable {
    override fun layout(bounds: TakoBounds) {
        setDimensionsAndPosition(bounds.width, bounds.height, bounds.x, bounds.y)
    }

    var on: Boolean = initialOn
        set(value) {
            if (field == value) return
            field = value
            onChanged(value)
        }

    override fun onClick(mouseX: Double, mouseY: Double) {
        on = !on
    }

    override fun renderWidget(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        val trackWidth = 28
        val trackHeight = 12
        val trackY = y + (height - trackHeight) / 2
        val trackColor = if (on) TakoTheme.ACCENT else TakoTheme.PANEL_CARD
        context.fill(x, trackY, x + trackWidth, trackY + trackHeight, trackColor)
        val knobX = if (on) x + trackWidth - 10 else x + 2
        context.fill(knobX, trackY + 2, knobX + 8, trackY + 10, TakoTheme.TEXT_PRIMARY)
        context.drawTextWithShadow(textRenderer, message, x + trackWidth + 8, y + (height - 8) / 2, TakoTheme.TEXT_PRIMARY)
    }

    override fun appendClickableNarrations(builder: NarrationMessageBuilder) {
        appendDefaultNarrations(builder)
    }
}
