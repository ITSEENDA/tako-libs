package dev.tako.libs.client.ui.widget

import dev.tako.libs.client.ui.core.TakoTheme

import net.minecraft.client.gui.DrawContext

object TakoProgressBar {
    fun draw(
        context: DrawContext,
        x: Int,
        y: Int,
        width: Int,
        height: Int,
        progress: Float,
        background: Int = 0xAA101010.toInt(),
        foreground: Int = TakoTheme.ACCENT
    ) {
        val safeProgress = progress.coerceIn(0f, 1f)
        context.fill(x, y, x + width, y + height, background)
        context.fill(x, y, x + (width * safeProgress).toInt(), y + height, foreground)
    }
}
