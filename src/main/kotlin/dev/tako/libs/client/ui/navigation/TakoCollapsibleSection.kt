package dev.tako.libs.client.ui.navigation

import dev.tako.libs.client.ui.core.TakoScreenChrome
import dev.tako.libs.client.ui.core.TakoTheme

import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.text.Text

class TakoCollapsibleSection(
    var title: String,
    var expanded: Boolean = true
) {
    fun toggle() { expanded = !expanded }

    fun renderHeader(context: DrawContext, textRenderer: TextRenderer, x: Int, y: Int, width: Int): Boolean {
        TakoScreenChrome.drawPanel(context, x, y, width, 22, TakoTheme.PANEL)
        val marker = if (expanded) "-" else "+"
        context.drawTextWithShadow(textRenderer, Text.literal("[$marker] $title"), x + 4, y + 6, 0xFFCC66)
        TakoScreenChrome.drawDivider(context, x, y + 21, width)
        return expanded
    }

    fun containsHeader(mouseX: Double, mouseY: Double, x: Int, y: Int, width: Int): Boolean =
        mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + 22
}
