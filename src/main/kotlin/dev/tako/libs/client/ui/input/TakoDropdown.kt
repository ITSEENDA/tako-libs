package dev.tako.libs.client.ui.input

import dev.tako.libs.client.ui.core.TakoScreenChrome
import dev.tako.libs.client.ui.core.TakoTheme
import dev.tako.libs.client.ui.layout.TakoBounds
import dev.tako.libs.client.ui.layout.TakoLayoutable
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.text.Text

class TakoDropdown<T>(
    private val textRenderer: TextRenderer,
    var items: List<T>,
    private val label: (T) -> String,
    initialIndex: Int = 0,
    private val onSelected: (T) -> Unit = {}
) : TakoLayoutable {
    var selectedIndex: Int = initialIndex.coerceIn(0, (items.size - 1).coerceAtLeast(0))
    var expanded: Boolean = false
    private var layoutBounds = TakoBounds(0, 0, 0, 0)

    val selectedItem: T?
        get() = items.getOrNull(selectedIndex)

    override fun layout(bounds: TakoBounds) {
        layoutBounds = bounds
    }

    fun render(context: DrawContext) {
        render(context, layoutBounds.x, layoutBounds.y, layoutBounds.width, layoutBounds.height)
    }

    fun render(context: DrawContext, x: Int, y: Int, width: Int, height: Int = 20) {
        val selectedText = selectedItem?.let(label) ?: "-"
        TakoScreenChrome.drawPanel(context, x, y, width, height, TakoTheme.PANEL_CARD)
        context.drawTextWithShadow(textRenderer, Text.literal(selectedText), x + 6, y + 6, TakoTheme.TEXT_PRIMARY)
        context.drawTextWithShadow(textRenderer, Text.literal(if (expanded) "^" else "v"), x + width - 12, y + 6, TakoTheme.TEXT_SECONDARY)
        if (!expanded) return
        items.forEachIndexed { index, item ->
            val optionY = y + height * (index + 1)
            TakoScreenChrome.drawPanel(context, x, optionY, width, height, if (index == selectedIndex) TakoTheme.ACCENT else TakoTheme.PANEL)
            context.drawTextWithShadow(textRenderer, Text.literal(label(item)), x + 6, optionY + 6, TakoTheme.TEXT_PRIMARY)
        }
    }

    fun mouseClicked(mouseX: Double, mouseY: Double, x: Int, y: Int, width: Int, height: Int = 20): Boolean {
        if (mouseX < x || mouseX >= x + width) return false
        if (expanded) {
            val index = ((mouseY - y - height) / height).toInt()
            if (index in items.indices) {
                selectedIndex = index
                expanded = false
                onSelected(items[index])
                return true
            }
            if (mouseY < y || mouseY >= y + height * (items.size + 1)) expanded = false
            return mouseY in y.toDouble()..(y + height).toDouble()
        }
        if (mouseY in y.toDouble()..(y + height).toDouble()) {
            expanded = true
            return true
        }
        return false
    }

    fun mouseClicked(mouseX: Double, mouseY: Double): Boolean =
        mouseClicked(mouseX, mouseY, layoutBounds.x, layoutBounds.y, layoutBounds.width, layoutBounds.height)
}
