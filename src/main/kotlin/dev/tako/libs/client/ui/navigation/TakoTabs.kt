package dev.tako.libs.client.ui.navigation

import dev.tako.libs.client.ui.core.TakoTheme

import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.text.Text

class TakoTabs<T>(
    val items: List<T>,
    var selectedIndex: Int = 0,
    private val label: (T) -> String
) {
    fun select(index: Int) {
        if (items.isNotEmpty()) selectedIndex = index.coerceIn(items.indices)
    }

    fun tabAt(mouseX: Double, mouseY: Double, x: Int, y: Int, width: Int, height: Int = 20): Int? {
        if (items.isEmpty() || mouseY !in y.toDouble()..(y + height).toDouble()) return null
        val tabWidth = width / items.size
        val index = ((mouseX - x) / tabWidth).toInt()
        return index.takeIf { it in items.indices }
    }

    fun render(context: DrawContext, textRenderer: TextRenderer, x: Int, y: Int, width: Int, height: Int = 20) {
        if (items.isEmpty()) return
        val tabWidth = width / items.size
        items.forEachIndexed { index, item ->
            val tabX = x + index * tabWidth
            val selected = index == selectedIndex
            context.fill(tabX, y, tabX + tabWidth, y + height, if (selected) TakoTheme.ACCENT else TakoTheme.PANEL_CARD)
            context.drawCenteredTextWithShadow(textRenderer, Text.literal(label(item)), tabX + tabWidth / 2, y + 6, TakoTheme.TEXT_PRIMARY)
        }
    }
}
