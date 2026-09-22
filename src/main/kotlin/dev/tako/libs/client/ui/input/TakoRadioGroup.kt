package dev.tako.libs.client.ui.input

import dev.tako.libs.client.ui.core.TakoTheme
import dev.tako.libs.client.ui.layout.TakoBounds
import dev.tako.libs.client.ui.layout.TakoLayoutable
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.text.Text

class TakoRadioGroup<T>(
    private val textRenderer: TextRenderer,
    var items: List<T>,
    private val label: (T) -> String,
    initialIndex: Int = 0,
    private val onSelected: (T) -> Unit = {}
) : TakoLayoutable {
    var selectedIndex: Int = initialIndex.coerceIn(0, (items.size - 1).coerceAtLeast(0))
    private var layoutBounds = TakoBounds(0, 0, 0, 0)

    override fun layout(bounds: TakoBounds) {
        layoutBounds = bounds
    }

    fun render(context: DrawContext, rowHeight: Int = 20) {
        render(context, layoutBounds.x, layoutBounds.y, rowHeight)
    }

    fun render(context: DrawContext, x: Int, y: Int, rowHeight: Int = 20) {
        items.forEachIndexed { index, item ->
            val rowY = y + index * rowHeight
            context.fill(x, rowY + 5, x + 10, rowY + 15, TakoTheme.PANEL_CARD)
            if (index == selectedIndex) context.fill(x + 3, rowY + 8, x + 7, rowY + 12, TakoTheme.ACCENT)
            context.drawTextWithShadow(textRenderer, Text.literal(label(item)), x + 16, rowY + 6, TakoTheme.TEXT_PRIMARY)
        }
    }

    fun mouseClicked(mouseX: Double, mouseY: Double, x: Int, y: Int, width: Int, rowHeight: Int = 20): Boolean {
        if (mouseX < x || mouseX >= x + width || mouseY < y) return false
        val index = ((mouseY - y) / rowHeight).toInt()
        if (index !in items.indices) return false
        selectedIndex = index
        onSelected(items[index])
        return true
    }

    fun mouseClicked(mouseX: Double, mouseY: Double): Boolean =
        mouseClicked(mouseX, mouseY, layoutBounds.x, layoutBounds.y, layoutBounds.width)
}
