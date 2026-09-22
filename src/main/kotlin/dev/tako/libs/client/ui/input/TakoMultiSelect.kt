package dev.tako.libs.client.ui.input

import dev.tako.libs.client.ui.core.TakoScreenChrome
import dev.tako.libs.client.ui.core.TakoTheme
import dev.tako.libs.client.ui.layout.TakoBounds
import dev.tako.libs.client.ui.layout.TakoLayoutable
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.text.Text

class TakoMultiSelect<T>(
    private val textRenderer: TextRenderer,
    var items: List<T>,
    private val label: (T) -> String,
    initialSelected: Set<T> = emptySet(),
    private val onChanged: (Set<T>) -> Unit = {}
) : TakoLayoutable {
    private val selected = initialSelected.toMutableSet()
    var expanded: Boolean = false
    private var layoutBounds = TakoBounds(0, 0, 0, 0)

    val selectedItems: Set<T>
        get() = selected.toSet()

    override fun layout(bounds: TakoBounds) {
        layoutBounds = bounds
    }

    fun render(context: DrawContext) {
        render(context, layoutBounds.x, layoutBounds.y, layoutBounds.width, layoutBounds.height)
    }

    fun render(context: DrawContext, x: Int, y: Int, width: Int, height: Int = 20) {
        val selectedText = if (selected.isEmpty()) "-" else selected.joinToString(", ", transform = label)
        TakoScreenChrome.drawPanel(context, x, y, width, height, TakoTheme.PANEL_CARD)
        context.drawTextWithShadow(textRenderer, Text.literal(selectedText), x + 6, y + 6, TakoTheme.TEXT_PRIMARY)
        if (!expanded) return
        items.forEachIndexed { index, item ->
            val optionY = y + height * (index + 1)
            val active = item in selected
            TakoScreenChrome.drawPanel(context, x, optionY, width, height, if (active) TakoTheme.ACCENT else TakoTheme.PANEL)
            context.drawTextWithShadow(textRenderer, Text.literal(if (active) "[x] ${label(item)}" else "[ ] ${label(item)}"), x + 6, optionY + 6, TakoTheme.TEXT_PRIMARY)
        }
    }

    fun mouseClicked(mouseX: Double, mouseY: Double, x: Int, y: Int, width: Int, height: Int = 20): Boolean {
        if (mouseX < x || mouseX >= x + width) return false
        if (!expanded) {
            if (mouseY !in y.toDouble()..(y + height).toDouble()) return false
            expanded = true
            return true
        }
        val index = ((mouseY - y - height) / height).toInt()
        if (index in items.indices) {
            val item = items[index]
            if (!selected.add(item)) selected.remove(item)
            onChanged(selectedItems)
            return true
        }
        if (mouseY < y || mouseY >= y + height * (items.size + 1)) expanded = false
        return mouseY in y.toDouble()..(y + height).toDouble()
    }

    fun mouseClicked(mouseX: Double, mouseY: Double): Boolean =
        mouseClicked(mouseX, mouseY, layoutBounds.x, layoutBounds.y, layoutBounds.width, layoutBounds.height)
}
