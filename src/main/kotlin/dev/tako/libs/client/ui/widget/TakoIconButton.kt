package dev.tako.libs.client.ui.widget

import dev.tako.libs.client.ui.layout.TakoBounds
import dev.tako.libs.client.ui.layout.TakoLayoutable
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder
import net.minecraft.client.gui.tooltip.Tooltip
import net.minecraft.client.gui.widget.ClickableWidget
import net.minecraft.text.Text
import net.minecraft.util.Identifier

enum class TakoButtonIcon(val assetName: String) {
    UNDO("undo-2"),
    REDO("redo-2"),
    ZOOM_OUT("zoom-out"),
    ZOOM_IN("zoom-in"),
    RESET("rotate-ccw"),
    RESET_ALL("rotate-ccw-square")
}

class TakoIconButton(
    private val icon: TakoButtonIcon,
    label: Text,
    x: Int,
    y: Int,
    size: Int = 20,
    private val onPress: () -> Unit
) : ClickableWidget(x, y, size, size, label), TakoLayoutable {
    private val texture = requireNotNull(
        Identifier.tryParse("tako-libs:textures/gui/icons-generated/${icon.assetName}.png")
    )

    init {
        tooltip = Tooltip.of(label)
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
        val iconSize = (minOf(width, height) * 35 / 100).coerceAtLeast(1)
        drawIcon(
            context,
            x + (width - iconSize) / 2,
            y + (height - iconSize) / 2,
            iconSize,
            if (active) 0xFFFFFFFF.toInt() else 0xFF888888.toInt()
        )
    }

    override fun appendClickableNarrations(builder: NarrationMessageBuilder) {
        appendDefaultNarrations(builder)
    }

    private fun drawIcon(context: DrawContext, x: Int, y: Int, size: Int, tint: Int) {
        context.setShaderColor(
            ((tint shr 16) and 0xFF) / 255f,
            ((tint shr 8) and 0xFF) / 255f,
            (tint and 0xFF) / 255f,
            ((tint ushr 24) and 0xFF) / 255f
        )
        try {
            context.drawTexture(
                texture,
                x,
                y,
                size,
                size,
                0f,
                0f,
                32,
                32,
                32,
                32
            )
        } finally {
            context.setShaderColor(1f, 1f, 1f, 1f)
        }
    }
}
