package dev.tako.libs.client.ui.core

import net.minecraft.client.gui.DrawContext
import net.minecraft.client.font.TextRenderer
import net.minecraft.text.Text

/** Shared screen framing helpers; feature screens own their layout and state. */
object TakoScreenChrome {
    fun drawPanel(context: DrawContext, x: Int, y: Int, width: Int, height: Int, color: Int = TakoTheme.PANEL) {
        context.fill(x, y, x + width, y + height, color)
    }

    fun drawTitle(context: DrawContext, textRenderer: TextRenderer, title: Text, screenWidth: Int, y: Int = 16) {
        context.drawCenteredTextWithShadow(textRenderer, title, screenWidth / 2, y, TakoTheme.TEXT_PRIMARY)
    }

    fun drawDivider(context: DrawContext, x: Int, y: Int, width: Int) {
        context.fill(x, y, x + width, y + 1, TakoTheme.DIVIDER)
    }
}
