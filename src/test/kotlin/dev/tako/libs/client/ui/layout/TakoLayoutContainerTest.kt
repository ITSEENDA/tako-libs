package dev.tako.libs.client.ui.layout

import org.junit.Assert.assertEquals
import org.junit.Test

class TakoLayoutContainerTest {
    private class Probe : TakoLayoutable {
        var bounds = TakoBounds(0, 0, 0, 0)

        override fun layout(bounds: TakoBounds) {
            this.bounds = bounds
        }
    }

    @Test
    fun flexDistributesWeightedSpaceAfterFixedTracksAndGaps() {
        val fixed = Probe()
        val firstWeight = Probe()
        val secondWeight = Probe()
        val flex = TakoFlexContainer<TakoLayoutable>(
            direction = TakoFlexDirection.ROW,
            padding = TakoInsets(left = 5, top = 3, right = 5, bottom = 3),
            gap = 10
        )
            .add(fixed, TakoSizeSpec.Fixed(50))
            .add(firstWeight, TakoSizeSpec.Weight(1f))
            .add(secondWeight, TakoSizeSpec.Weight(2f))

        flex.layout(TakoBounds(10, 20, 200, 40))

        assertEquals(TakoBounds(15, 23, 50, 34), fixed.bounds)
        assertEquals(TakoBounds(75, 23, 40, 34), firstWeight.bounds)
        assertEquals(TakoBounds(125, 23, 80, 34), secondWeight.bounds)
    }

    @Test
    fun gridPlacesCellsAndAccountsForSpans() {
        val first = Probe()
        val flexible = Probe()
        val span = Probe()
        val grid = TakoGridContainer<TakoLayoutable>(
            columns = listOf(TakoSizeSpec.Fixed(40), TakoSizeSpec.Weight(), TakoSizeSpec.Fixed(60)),
            rows = listOf(TakoSizeSpec.Fixed(18), TakoSizeSpec.Weight()),
            columnGap = 6,
            rowGap = 2,
            padding = TakoInsets(left = 5, top = 4, right = 5, bottom = 4)
        )
            .add(first, row = 0, column = 0)
            .add(flexible, row = 1, column = 1)
            .add(span, row = 0, column = 1, columnSpan = 2)

        grid.layout(TakoBounds(10, 20, 200, 80))

        assertEquals(TakoBounds(15, 24, 40, 18), first.bounds)
        assertEquals(TakoBounds(61, 44, 78, 52), flexible.bounds)
        assertEquals(TakoBounds(61, 24, 144, 18), span.bounds)
    }
}
