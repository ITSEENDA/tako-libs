package dev.tako.libs.client.ui.widget

import dev.tako.libs.client.ui.core.TakoTheme

import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.text.Text

data class TakoBadgeStyle(
    val background: Int = TakoTheme.PANEL_CARD,
    val border: Int = TakoTheme.ACCENT,
    val text: Int = TakoTheme.TEXT_PRIMARY
)

class TakoBadge(var label: String, var style: TakoBadgeStyle = TakoBadgeStyle()) {
    fun render(context: DrawContext, textRenderer: TextRenderer, x: Int, y: Int, padding: Int = 4): Int {
        val text = Text.literal(label)
        val width = textRenderer.getWidth(text) + padding * 2
        context.fill(x, y, x + width, y + 16, style.background)
        context.fill(x, y, x + width, y + 1, style.border)
        context.fill(x, y + 15, x + width, y + 16, style.border)
        context.drawTextWithShadow(textRenderer, text, x + padding, y + 4, style.text)
        return width
    }
}
