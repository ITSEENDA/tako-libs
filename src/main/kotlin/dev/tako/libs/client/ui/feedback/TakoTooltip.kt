package dev.tako.libs.client.ui.feedback

import dev.tako.libs.client.ui.core.TakoScreenChrome
import dev.tako.libs.client.ui.core.TakoTheme

import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.text.Text

object TakoTooltip {
    fun render(
        context: DrawContext,
        textRenderer: TextRenderer,
        lines: List<String>,
        mouseX: Int,
        mouseY: Int,
        screenWidth: Int,
        screenHeight: Int
    ) {
        if (lines.isEmpty()) return
        val width = lines.maxOf { textRenderer.getWidth(it) } + 12
        val height = lines.size * 10 + 8
        val x = (mouseX + 12).coerceAtMost(screenWidth - width - 4)
        val y = (mouseY + 12).coerceAtMost(screenHeight - height - 4)
        TakoScreenChrome.drawPanel(context, x, y, width, height, 0xF0101010.toInt())
        lines.forEachIndexed { index, line ->
            context.drawTextWithShadow(textRenderer, Text.literal(line), x + 6, y + 4 + index * 10, TakoTheme.TEXT_PRIMARY)
        }
    }
}
