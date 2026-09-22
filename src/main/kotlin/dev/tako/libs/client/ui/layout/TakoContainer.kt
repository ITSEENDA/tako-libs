package dev.tako.libs.client.ui.layout

import net.minecraft.client.gui.DrawContext

enum class TakoAxis { ROW, COLUMN }

data class TakoInsets(val left: Int = 0, val top: Int = 0, val right: Int = 0, val bottom: Int = 0)

data class TakoBounds(val x: Int, val y: Int, val width: Int, val height: Int)

sealed interface TakoSizeSpec {
    data class Fixed(val pixels: Int) : TakoSizeSpec
    data class Weight(val value: Float = 1f) : TakoSizeSpec
}

data class TakoContainerStyle(
    var background: Int? = null,
    var border: Int? = null,
    var borderWidth: Int = 0
)

data class TakoPlacement<T>(val component: T, val bounds: TakoBounds)

/** Generic row/column layout container that also applies bounds to layout-aware children. */
class TakoContainer<T>(
    var axis: TakoAxis = TakoAxis.COLUMN,
    var padding: TakoInsets = TakoInsets(),
    var gap: Int = 0,
    var style: TakoContainerStyle = TakoContainerStyle()
) {
    private data class Item<T>(
        val component: T,
        val main: TakoSizeSpec,
        val cross: TakoSizeSpec
    )

    private val items = mutableListOf<Item<T>>()

    fun add(
        component: T,
        main: TakoSizeSpec = TakoSizeSpec.Weight(),
        cross: TakoSizeSpec = TakoSizeSpec.Weight()
    ): TakoContainer<T> {
        items += Item(component, main, cross)
        return this
    }

    fun clear() {
        items.clear()
    }

    fun layout(bounds: TakoBounds): List<TakoPlacement<T>> {
        if (items.isEmpty()) return emptyList()
        val horizontal = axis == TakoAxis.ROW
        val mainSize = (if (horizontal) bounds.width else bounds.height) -
            if (horizontal) padding.left + padding.right else padding.top + padding.bottom
        val crossSize = (if (horizontal) bounds.height else bounds.width) -
            if (horizontal) padding.top + padding.bottom else padding.left + padding.right
        val availableMain = (mainSize - gap * (items.size - 1)).coerceAtLeast(0)
        val fixedMain = items.sumOf { (it.main as? TakoSizeSpec.Fixed)?.pixels?.coerceAtLeast(0) ?: 0 }
        val weightTotal = items.sumOf { (it.main as? TakoSizeSpec.Weight)?.value?.coerceAtLeast(0f)?.toDouble() ?: 0.0 }
        val remainingMain = (availableMain - fixedMain).coerceAtLeast(0)
        var cursor = if (horizontal) bounds.x + padding.left else bounds.y + padding.top

        return items.map { item ->
            val itemMain = when (val spec = item.main) {
                is TakoSizeSpec.Fixed -> spec.pixels.coerceAtLeast(0)
                is TakoSizeSpec.Weight -> if (weightTotal == 0.0) 0 else (remainingMain * spec.value / weightTotal).toInt()
            }
            val itemCross = when (val spec = item.cross) {
                is TakoSizeSpec.Fixed -> spec.pixels.coerceAtLeast(0)
                is TakoSizeSpec.Weight -> crossSize
            }
            val placement = if (horizontal) {
                TakoPlacement(item.component, TakoBounds(cursor, bounds.y + padding.top, itemMain, itemCross))
            } else {
                TakoPlacement(item.component, TakoBounds(bounds.x + padding.left, cursor, itemCross, itemMain))
            }
            cursor += itemMain + gap
            (item.component as? TakoLayoutable)?.layout(placement.bounds)
            placement
        }
    }

    fun renderBackground(context: DrawContext, bounds: TakoBounds) {
        style.background?.let { context.fill(bounds.x, bounds.y, bounds.x + bounds.width, bounds.y + bounds.height, it) }
        val border = style.border ?: return
        val width = style.borderWidth.coerceAtLeast(0)
        repeat(width) { inset ->
            context.fill(bounds.x + inset, bounds.y + inset, bounds.x + bounds.width - inset, bounds.y + inset + 1, border)
            context.fill(bounds.x + inset, bounds.y + bounds.height - inset - 1, bounds.x + bounds.width - inset, bounds.y + bounds.height - inset, border)
            context.fill(bounds.x + inset, bounds.y + inset, bounds.x + inset + 1, bounds.y + bounds.height - inset, border)
            context.fill(bounds.x + bounds.width - inset - 1, bounds.y + inset, bounds.x + bounds.width - inset, bounds.y + bounds.height - inset, border)
        }
    }
}
