package dev.tako.libs.client.ui.input

import dev.tako.libs.client.ui.core.TakoTheme
import dev.tako.libs.client.ui.layout.TakoBounds
import dev.tako.libs.client.ui.layout.TakoLayoutable
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.text.Text

class TakoSegmentedControl<T>(
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

    fun render(context: DrawContext) {
        render(context, layoutBounds.x, layoutBounds.y, layoutBounds.width, layoutBounds.height)
    }

    fun render(context: DrawContext, x: Int, y: Int, width: Int, height: Int = 20) {
        if (items.isEmpty()) return
        val itemWidth = width / items.size
        items.forEachIndexed { index, item ->
            val itemX = x + index * itemWidth
            context.fill(itemX, y, itemX + itemWidth, y + height, if (index == selectedIndex) TakoTheme.ACCENT else TakoTheme.PANEL_CARD)
            context.drawCenteredTextWithShadow(textRenderer, Text.literal(label(item)), itemX + itemWidth / 2, y + 6, TakoTheme.TEXT_PRIMARY)
        }
    }

    fun mouseClicked(mouseX: Double, mouseY: Double, x: Int, y: Int, width: Int, height: Int = 20): Boolean {
        if (items.isEmpty() || mouseX < x || mouseX >= x + width || mouseY < y || mouseY >= y + height) return false
        val index = ((mouseX - x) / (width / items.size)).toInt().coerceIn(items.indices)
        selectedIndex = index
        onSelected(items[index])
        return true
    }

    fun mouseClicked(mouseX: Double, mouseY: Double): Boolean =
        mouseClicked(mouseX, mouseY, layoutBounds.x, layoutBounds.y, layoutBounds.width, layoutBounds.height)
}
