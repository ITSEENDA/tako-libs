package dev.tako.libs.client.ui.widget

import dev.tako.libs.client.ui.core.TakoScreenChrome
import dev.tako.libs.client.ui.core.TakoTheme

import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.text.Text

data class TakoRowAction(val label: String, val onClick: () -> Unit)

class TakoListRow(
    var title: String,
    var subtitle: String = "",
    var icon: TakoIcon? = null,
    var actions: List<TakoRowAction> = emptyList()
) {
    fun render(context: DrawContext, textRenderer: TextRenderer, x: Int, y: Int, width: Int, height: Int = 40, selected: Boolean = false) {
        TakoScreenChrome.drawPanel(context, x, y, width, height, if (selected) 0xCC252525.toInt() else TakoTheme.PANEL_CARD)
        icon?.render(context, x + 4, y + 5)
        val textX = x + if (icon == null) 8 else 28
        context.drawTextWithShadow(textRenderer, Text.literal(title), textX, y + 6, TakoTheme.TEXT_PRIMARY)
        if (subtitle.isNotBlank()) context.drawTextWithShadow(textRenderer, Text.literal(subtitle), textX, y + 22, TakoTheme.TEXT_SECONDARY)
    }
}
