package dev.tako.libs.client.ui.widget

import dev.tako.libs.client.ui.layout.TakoBounds
import dev.tako.libs.client.ui.layout.TakoLayoutable
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder
import net.minecraft.client.gui.tooltip.Tooltip
import net.minecraft.client.gui.widget.ClickableWidget
import net.minecraft.text.Text

enum class TakoButtonIcon {
    UNDO,
    REDO,
    ZOOM_OUT,
    ZOOM_IN,
    RESET,
    RESET_ALL
}

class TakoIconButton(
    private val icon: TakoButtonIcon,
    label: Text,
    x: Int,
    y: Int,
    size: Int = 20,
    private val onPress: () -> Unit
) : ClickableWidget(x, y, size, size, label), TakoLayoutable {
    init {
        setTooltip(Tooltip.of(label))
    }

    override fun layout(bounds: TakoBounds) {
        setDimensionsAndPosition(bounds.width, bounds.height, bounds.x, bounds.y)
    }

    override fun onClick(mouseX: Double, mouseY: Double) {
        if (active) onPress()
    }

    override fun renderWidget(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        val background = when {
            !active -> 0xFF333333.toInt()
            isHovered -> 0xFF777777.toInt()
            else -> 0xFF555555.toInt()
        }
        context.fill(x, y, x + width, y + height, background)
        context.fill(x, y, x + width, y + 1, 0xFF202020.toInt())
        context.fill(x, y + height - 1, x + width, y + height, 0xFF202020.toInt())
        context.fill(x, y, x + 1, y + height, 0xFF202020.toInt())
        context.fill(x + width - 1, y, x + width, y + height, 0xFF202020.toInt())
        drawIcon(context, x + width / 2, y + height / 2, if (active) 0xFFFFFFFF.toInt() else 0xFF888888.toInt())
    }

    override fun appendClickableNarrations(builder: NarrationMessageBuilder) {
        appendDefaultNarrations(builder)
    }

    private fun drawIcon(context: DrawContext, centerX: Int, centerY: Int, color: Int) {
        when (icon) {
            TakoButtonIcon.UNDO -> drawArrow(context, centerX, centerY, color, false)
            TakoButtonIcon.REDO -> drawArrow(context, centerX, centerY, color, true)
            TakoButtonIcon.ZOOM_OUT -> drawZoom(context, centerX, centerY, color, false)
            TakoButtonIcon.ZOOM_IN -> drawZoom(context, centerX, centerY, color, true)
            TakoButtonIcon.RESET -> drawReset(context, centerX, centerY, color, false)
            TakoButtonIcon.RESET_ALL -> drawReset(context, centerX, centerY, color, true)
        }
    }

    private fun drawArrow(context: DrawContext, centerX: Int, centerY: Int, color: Int, right: Boolean) {
        val direction = if (right) 1 else -1
        context.fill(centerX - 5, centerY - 1, centerX + 5, centerY + 1, color)
        context.fill(centerX + direction * 4, centerY - 4, centerX + direction * 6, centerY + 1, color)
        context.fill(centerX + direction * 4, centerY + 1, centerX + direction * 6, centerY + 4, color)
    }

    private fun drawZoom(context: DrawContext, centerX: Int, centerY: Int, color: Int, plus: Boolean) {
        context.fill(centerX - 5, centerY - 6, centerX - 3, centerY + 1, color)
        context.fill(centerX - 5, centerY - 6, centerX + 1, centerY - 4, color)
        context.fill(centerX - 5, centerY - 1, centerX + 1, centerY + 1, color)
        context.fill(centerX - 3, centerY - 6, centerX + 1, centerY - 4, color)
        context.fill(centerX + 1, centerY, centerX + 3, centerY + 2, color)
        context.fill(centerX + 3, centerY + 2, centerX + 6, centerY + 4, color)
        if (plus) {
            context.fill(centerX - 5, centerY - 3, centerX - 1, centerY - 1, color)
            context.fill(centerX - 4, centerY - 4, centerX - 2, centerY, color)
        }
    }

    private fun drawReset(context: DrawContext, centerX: Int, centerY: Int, color: Int, double: Boolean) {
        drawResetLoop(context, centerX - if (double) 3 else 0, centerY, color)
        if (double) drawResetLoop(context, centerX + 3, centerY, color)
    }

    private fun drawResetLoop(context: DrawContext, centerX: Int, centerY: Int, color: Int) {
        context.fill(centerX - 5, centerY - 5, centerX + 4, centerY - 3, color)
        context.fill(centerX - 5, centerY - 4, centerX - 3, centerY + 4, color)
        context.fill(centerX - 4, centerY + 3, centerX + 4, centerY + 5, color)
        context.fill(centerX + 3, centerY - 3, centerX + 5, centerY + 4, color)
        context.fill(centerX + 3, centerY - 5, centerX + 6, centerY - 2, color)
    }
}
