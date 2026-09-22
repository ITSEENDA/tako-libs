package dev.tako.libs.client.ui.input

import dev.tako.libs.client.ui.core.TakoTheme
import dev.tako.libs.client.ui.layout.TakoBounds
import dev.tako.libs.client.ui.layout.TakoLayoutable
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder
import net.minecraft.client.gui.widget.ClickableWidget
import net.minecraft.text.Text

class TakoCheckbox(
    label: String,
    private val textRenderer: TextRenderer,
    x: Int,
    y: Int,
    width: Int,
    height: Int = 20,
    initialChecked: Boolean = false,
    private val onChanged: (Boolean) -> Unit = {}
) : ClickableWidget(x, y, width, height, Text.literal(label)), TakoLayoutable {
    override fun layout(bounds: TakoBounds) {
        setDimensionsAndPosition(bounds.width, bounds.height, bounds.x, bounds.y)
    }

    var checked: Boolean = initialChecked
        set(value) {
            if (field == value) return
            field = value
            onChanged(value)
        }

    override fun onClick(mouseX: Double, mouseY: Double) {
        checked = !checked
    }

    override fun renderWidget(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        val boxY = y + (height - 12) / 2
        context.fill(x, boxY, x + 12, boxY + 12, if (checked) TakoTheme.ACCENT else TakoTheme.PANEL_CARD)
        context.fill(x, boxY, x + 12, boxY + 1, TakoTheme.DIVIDER)
        context.fill(x, boxY + 11, x + 12, boxY + 12, TakoTheme.DIVIDER)
        if (checked) context.fill(x + 3, boxY + 3, x + 9, boxY + 9, TakoTheme.TEXT_PRIMARY)
        context.drawTextWithShadow(textRenderer, message, x + 18, y + (height - 8) / 2, TakoTheme.TEXT_PRIMARY)
    }

    override fun appendClickableNarrations(builder: NarrationMessageBuilder) {
        appendDefaultNarrations(builder)
    }
}
