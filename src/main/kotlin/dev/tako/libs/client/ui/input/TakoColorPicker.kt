package dev.tako.libs.client.ui.input

import dev.tako.libs.client.ui.core.TakoScreenChrome
import dev.tako.libs.client.ui.core.TakoTheme
import dev.tako.libs.client.ui.layout.TakoBounds
import dev.tako.libs.client.ui.layout.TakoLayoutable
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.text.Text

class TakoColorPicker(
    private val textRenderer: TextRenderer,
    initialColor: Int = 0xFFFFFFFF.toInt(),
    private val onChanged: (Int) -> Unit = {}
) : TakoLayoutable {
    var color: Int = initialColor
        private set
    private var layoutBounds = TakoBounds(0, 0, 0, 0)

    override fun layout(bounds: TakoBounds) {
        layoutBounds = bounds
    }

    fun setColor(value: Int) {
        if (color == value) return
        color = value
        onChanged(value)
    }

    fun setHex(value: String): Boolean {
        val normalized = value.trim().removePrefix("#")
        val parsed = when (normalized.length) {
            6 -> normalized.toLongOrNull(16)?.toInt()?.or(0xFF000000.toInt())
            8 -> normalized.toLongOrNull(16)?.toInt()
            else -> null
        } ?: return false
        setColor(parsed)
        return true
    }

    fun hex(): String = "#%08X".format(color)

    fun render(context: DrawContext, x: Int, y: Int, width: Int, height: Int = 20) {
        TakoScreenChrome.drawPanel(context, x, y, width, height, TakoTheme.PANEL_CARD)
        context.fill(x + 4, y + 4, x + 20, y + height - 4, color)
        context.drawTextWithShadow(textRenderer, Text.literal(hex()), x + 28, y + 6, TakoTheme.TEXT_PRIMARY)
    }

    fun render(context: DrawContext) {
        render(context, layoutBounds.x, layoutBounds.y, layoutBounds.width, layoutBounds.height)
    }
}
