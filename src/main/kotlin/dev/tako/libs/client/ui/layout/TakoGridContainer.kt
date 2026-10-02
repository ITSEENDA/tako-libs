package dev.tako.libs.client.ui.layout

import net.minecraft.client.gui.DrawContext

/** Explicit row/column tracks with optional cells spanning adjacent tracks. */
class TakoGridContainer<T>(
    val columns: List<TakoSizeSpec>,
    val rows: List<TakoSizeSpec>,
    var columnGap: Int = 0,
    var rowGap: Int = 0,
    var padding: TakoInsets = TakoInsets(),
    var style: TakoLayoutStyle = TakoLayoutStyle()
) : TakoLayoutable {
    private data class Cell<T>(
        val component: T,
        val row: Int,
        val column: Int,
        val rowSpan: Int,
        val columnSpan: Int
    )

    private val cells = mutableListOf<Cell<T>>()

    var placements: List<TakoPlacement<T>> = emptyList()
        private set

    fun add(
        component: T,
        row: Int,
        column: Int,
        rowSpan: Int = 1,
        columnSpan: Int = 1
    ): TakoGridContainer<T> {
        require(row >= 0 && column >= 0 && rowSpan > 0 && columnSpan > 0)
        require(row + rowSpan <= rows.size && column + columnSpan <= columns.size) {
            "Grid cell is outside declared tracks"
        }
        cells += Cell(component, row, column, rowSpan, columnSpan)
        return this
    }

    fun clear() {
        cells.clear()
        placements = emptyList()
    }

    override fun layout(bounds: TakoBounds) {
        if (columns.isEmpty() || rows.isEmpty() || cells.isEmpty()) {
            placements = emptyList()
            return
        }

        val safeColumnGap = columnGap.coerceAtLeast(0)
        val safeRowGap = rowGap.coerceAtLeast(0)
        val availableWidth = (bounds.width - padding.left - padding.right - safeColumnGap * (columns.size - 1))
            .coerceAtLeast(0)
        val availableHeight = (bounds.height - padding.top - padding.bottom - safeRowGap * (rows.size - 1))
            .coerceAtLeast(0)
        val columnSizes = resolveTakoSizes(availableWidth, columns)
        val rowSizes = resolveTakoSizes(availableHeight, rows)
        val columnStarts = trackStarts(bounds.x + padding.left, columnSizes, safeColumnGap)
        val rowStarts = trackStarts(bounds.y + padding.top, rowSizes, safeRowGap)

        placements = cells.map { cell ->
            val cellBounds = TakoBounds(
                x = columnStarts[cell.column],
                y = rowStarts[cell.row],
                width = spannedSize(columnSizes, cell.column, cell.columnSpan, safeColumnGap),
                height = spannedSize(rowSizes, cell.row, cell.rowSpan, safeRowGap)
            )
            (cell.component as? TakoLayoutable)?.layout(cellBounds)
            TakoPlacement(cell.component, cellBounds)
        }
    }

    fun renderBackground(context: DrawContext, bounds: TakoBounds) =
        renderTakoLayoutStyle(context, bounds, style)

    private fun trackStarts(origin: Int, sizes: List<Int>, gap: Int): List<Int> {
        var cursor = origin
        return sizes.map { size ->
            val start = cursor
            cursor += size + gap
            start
        }
    }

    private fun spannedSize(sizes: List<Int>, start: Int, span: Int, gap: Int): Int =
        sizes.subList(start, start + span).sum() + gap * (span - 1)
}
