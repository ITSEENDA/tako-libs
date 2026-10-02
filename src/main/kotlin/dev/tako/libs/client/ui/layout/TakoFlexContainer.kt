package dev.tako.libs.client.ui.layout

import net.minecraft.client.gui.DrawContext

enum class TakoFlexDirection { ROW, COLUMN }
enum class TakoMainAlignment { START, CENTER, END, SPACE_BETWEEN }
enum class TakoCrossAlignment { START, CENTER, END, STRETCH }

data class TakoInsets(val left: Int = 0, val top: Int = 0, val right: Int = 0, val bottom: Int = 0)
data class TakoBounds(val x: Int, val y: Int, val width: Int, val height: Int)

sealed interface TakoSizeSpec {
    data class Fixed(val pixels: Int) : TakoSizeSpec
    data class Weight(val value: Float = 1f) : TakoSizeSpec
}

data class TakoLayoutStyle(
    var background: Int? = null,
    var border: Int? = null,
    var borderWidth: Int = 0
)

data class TakoPlacement<T>(val component: T, val bounds: TakoBounds)

/** Distributes children along one axis; weights share space left after fixed sizes. */
class TakoFlexContainer<T>(
    var direction: TakoFlexDirection = TakoFlexDirection.COLUMN,
    var padding: TakoInsets = TakoInsets(),
    var gap: Int = 0,
    var justifyContent: TakoMainAlignment = TakoMainAlignment.START,
    var alignItems: TakoCrossAlignment = TakoCrossAlignment.STRETCH,
    var style: TakoLayoutStyle = TakoLayoutStyle()
) : TakoLayoutable {
    private data class Item<T>(
        val component: T,
        val main: TakoSizeSpec,
        val cross: TakoSizeSpec
    )

    private val items = mutableListOf<Item<T>>()

    var placements: List<TakoPlacement<T>> = emptyList()
        private set

    fun add(
        component: T,
        main: TakoSizeSpec = TakoSizeSpec.Weight(),
        cross: TakoSizeSpec = TakoSizeSpec.Weight()
    ): TakoFlexContainer<T> {
        items += Item(component, main, cross)
        return this
    }

    fun clear() {
        items.clear()
        placements = emptyList()
    }

    override fun layout(bounds: TakoBounds) {
        val horizontal = direction == TakoFlexDirection.ROW
        val mainStart = if (horizontal) bounds.x + padding.left else bounds.y + padding.top
        val crossStart = if (horizontal) bounds.y + padding.top else bounds.x + padding.left
        val mainSize = ((if (horizontal) bounds.width else bounds.height) -
                if (horizontal) padding.left + padding.right else padding.top + padding.bottom)
            .coerceAtLeast(0)
        val crossSize = ((if (horizontal) bounds.height else bounds.width) -
                if (horizontal) padding.top + padding.bottom else padding.left + padding.right)
            .coerceAtLeast(0)
        val safeGap = gap.coerceAtLeast(0)
        val availableMain = (mainSize - safeGap * (items.size - 1).coerceAtLeast(0)).coerceAtLeast(0)
        val mainSizes = resolveTakoSizes(availableMain, items.map { it.main })
        val freeMain = (availableMain - mainSizes.sum()).coerceAtLeast(0)
        val startOffset = when (justifyContent) {
            TakoMainAlignment.START, TakoMainAlignment.SPACE_BETWEEN -> 0
            TakoMainAlignment.CENTER -> freeMain / 2
            TakoMainAlignment.END -> freeMain
        }
        val distributeBetween = justifyContent == TakoMainAlignment.SPACE_BETWEEN && items.size > 1
        val spaceBetween = if (distributeBetween) {
            freeMain / (items.size - 1)
        } else {
            0
        }
        val extraGapRemainder = if (distributeBetween) freeMain % (items.size - 1) else 0
        var mainCursor = mainStart + startOffset

        placements = items.mapIndexed { index, item ->
            val requestedCross = when (val spec = item.cross) {
                is TakoSizeSpec.Fixed -> spec.pixels.coerceAtLeast(0).coerceAtMost(crossSize)
                is TakoSizeSpec.Weight -> crossSize
            }
            val crossOffset = when (alignItems) {
                TakoCrossAlignment.START, TakoCrossAlignment.STRETCH -> 0
                TakoCrossAlignment.CENTER -> (crossSize - requestedCross) / 2
                TakoCrossAlignment.END -> crossSize - requestedCross
            }
            val itemCross = if (alignItems == TakoCrossAlignment.STRETCH && item.cross is TakoSizeSpec.Weight) {
                crossSize
            } else {
                requestedCross
            }
            val childBounds = if (horizontal) {
                TakoBounds(mainCursor, crossStart + crossOffset, mainSizes[index], itemCross)
            } else {
                TakoBounds(crossStart + crossOffset, mainCursor, itemCross, mainSizes[index])
            }
            (item.component as? TakoLayoutable)?.layout(childBounds)

            val placement = TakoPlacement(item.component, childBounds)
            val remainderForGap = if (index < extraGapRemainder) 1 else 0
            mainCursor += mainSizes[index] + safeGap + spaceBetween + remainderForGap
            placement
        }
    }

    fun renderBackground(context: DrawContext, bounds: TakoBounds) =
        renderTakoLayoutStyle(context, bounds, style)
}

internal fun resolveTakoSizes(available: Int, specs: List<TakoSizeSpec>): List<Int> {
    val fixedTotal = specs.sumOf { (it as? TakoSizeSpec.Fixed)?.pixels?.coerceAtLeast(0) ?: 0 }
    val weights = specs.map { (it as? TakoSizeSpec.Weight)?.value?.takeIf { weight -> weight.isFinite() && weight > 0f } ?: 0f }
    val totalWeight = weights.sumOf(Float::toDouble)
    val weightedSpace = (available.coerceAtLeast(0) - fixedTotal).coerceAtLeast(0)
    var distributed = 0
    var remainingWeight = totalWeight

    return specs.mapIndexed { index, spec ->
        when (spec) {
            is TakoSizeSpec.Fixed -> spec.pixels.coerceAtLeast(0)
            is TakoSizeSpec.Weight -> {
                val weight = weights[index]
                if (weight <= 0f || remainingWeight <= 0.0) return@mapIndexed 0
                val size = if (weight.toDouble() == remainingWeight) {
                    weightedSpace - distributed
                } else {
                    (weightedSpace * weight / totalWeight).toInt()
                }
                distributed += size
                remainingWeight -= weight
                size
            }
        }
    }
}

internal fun renderTakoLayoutStyle(context: DrawContext, bounds: TakoBounds, style: TakoLayoutStyle) {
    style.background?.let {
        context.fill(bounds.x, bounds.y, bounds.x + bounds.width, bounds.y + bounds.height, it)
    }
    val border = style.border ?: return
    repeat(style.borderWidth.coerceAtLeast(0)) { inset ->
        context.fill(bounds.x + inset, bounds.y + inset, bounds.x + bounds.width - inset, bounds.y + inset + 1, border)
        context.fill(bounds.x + inset, bounds.y + bounds.height - inset - 1, bounds.x + bounds.width - inset, bounds.y + bounds.height - inset, border)
        context.fill(bounds.x + inset, bounds.y + inset, bounds.x + inset + 1, bounds.y + bounds.height - inset, border)
        context.fill(bounds.x + bounds.width - inset - 1, bounds.y + inset, bounds.x + bounds.width - inset, bounds.y + bounds.height - inset, border)
    }
}
