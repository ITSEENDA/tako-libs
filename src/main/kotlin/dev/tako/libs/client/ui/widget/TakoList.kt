package dev.tako.libs.client.ui.widget

import dev.tako.libs.client.ui.layout.TakoScrollState
import dev.tako.libs.client.ui.layout.TakoBounds
import dev.tako.libs.client.ui.layout.TakoLayoutable
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.roundToInt
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext

/**
 * Fixed-row list that only asks the renderer to draw rows inside the viewport.
 * The list owns scrolling and hit testing; callers keep ownership of row data.
 */
class TakoList<T>(
    var items: List<T> = emptyList(),
    val scrollState: TakoScrollState = TakoScrollState(),
    var rowHeight: Int = 40,
    var gap: Int = 0
) : TakoLayoutable {
    private var layoutBounds = TakoBounds(0, 0, 0, 0)

    override fun layout(bounds: TakoBounds) {
        layoutBounds = bounds
    }

    fun contentHeight(): Int {
        if (items.isEmpty()) return 0
        return items.size * rowHeight.coerceAtLeast(1) + (items.size - 1) * gap.coerceAtLeast(0)
    }

    fun update(viewportHeight: Int) {
        scrollState.update(contentHeight(), viewportHeight)
    }

    fun visibleRange(viewportHeight: Int): IntRange {
        if (items.isEmpty()) return IntRange.EMPTY
        val stride = rowHeight.coerceAtLeast(1) + gap.coerceAtLeast(0)
        val first = floor(scrollState.offset / stride).toInt().coerceIn(0, items.lastIndex)
        val endExclusive = ceil((scrollState.offset + viewportHeight.coerceAtLeast(1)) / stride).toInt()
        val last = (endExclusive - 1).coerceIn(first, items.lastIndex)
        return first..last
    }

    fun render(
        context: DrawContext,
        textRenderer: TextRenderer,
        x: Int,
        y: Int,
        width: Int,
        viewportHeight: Int,
        selectedIndex: Int? = null,
        renderItem: (context: DrawContext, textRenderer: TextRenderer, item: T, index: Int, x: Int, y: Int, width: Int, height: Int, selected: Boolean) -> Unit
    ) {
        update(viewportHeight)
        val height = rowHeight.coerceAtLeast(1)
        val stride = height + gap.coerceAtLeast(0)
        context.enableScissor(x, y, x + width, y + viewportHeight.coerceAtLeast(0))
        try {
            visibleRange(viewportHeight).forEach { index ->
                val itemY = y + (index * stride - scrollState.offset).roundToInt()
                renderItem(context, textRenderer, items[index], index, x, itemY, width, height, index == selectedIndex)
            }
        } finally {
            context.disableScissor()
        }
    }

    fun render(
        context: DrawContext,
        textRenderer: TextRenderer,
        selectedIndex: Int? = null,
        renderItem: (context: DrawContext, textRenderer: TextRenderer, item: T, index: Int, x: Int, y: Int, width: Int, height: Int, selected: Boolean) -> Unit
    ) {
        render(
            context,
            textRenderer,
            layoutBounds.x,
            layoutBounds.y,
            layoutBounds.width,
            layoutBounds.height,
            selectedIndex,
            renderItem
        )
    }

    fun indexAt(mouseX: Double, mouseY: Double, x: Int, y: Int, width: Int, viewportHeight: Int): Int? {
        if (mouseX < x || mouseX >= x + width || mouseY < y || mouseY >= y + viewportHeight) return null
        val height = rowHeight.coerceAtLeast(1)
        val stride = height + gap.coerceAtLeast(0)
        val localY = mouseY - y + scrollState.offset
        val index = floor(localY / stride).toInt()
        val rowOffset = localY - index * stride
        return index.takeIf { it in items.indices && rowOffset < height }
    }
}
