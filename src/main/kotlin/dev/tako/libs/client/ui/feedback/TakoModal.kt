package dev.tako.libs.client.ui.feedback

import dev.tako.libs.client.ui.core.TakoScreenChrome
import dev.tako.libs.client.ui.core.TakoTheme

import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.text.Text

data class TakoModalAction(val label: String, val onClick: () -> Unit)

/** Lightweight modal rendered and hit-tested by the owning Screen. */
class TakoModal(
    var title: String,
    var body: List<String> = emptyList(),
    var actions: List<TakoModalAction> = emptyList(),
    var visible: Boolean = false
) {
    fun render(context: DrawContext, textRenderer: TextRenderer, screenWidth: Int, screenHeight: Int) {
        if (!visible) return
        val width = 300
        val height = 70 + body.size * 12
        val x = (screenWidth - width) / 2
        val y = (screenHeight - height) / 2
        context.fill(0, 0, screenWidth, screenHeight, 0x88000000.toInt())
        TakoScreenChrome.drawPanel(context, x, y, width, height, 0xF0101010.toInt())
        context.drawCenteredTextWithShadow(textRenderer, Text.literal(title), screenWidth / 2, y + 10, TakoTheme.TEXT_PRIMARY)
        body.forEachIndexed { index, line ->
            context.drawTextWithShadow(textRenderer, Text.literal(line), x + 12, y + 30 + index * 12, TakoTheme.TEXT_SECONDARY)
        }
        actions.forEachIndexed { index, action ->
            val buttonX = x + 12 + index * 92
            context.fill(buttonX, y + height - 26, buttonX + 82, y + height - 6, TakoTheme.PANEL_CARD)
            context.drawCenteredTextWithShadow(textRenderer, Text.literal(action.label), buttonX + 41, y + height - 20, TakoTheme.TEXT_PRIMARY)
        }
    }

    fun mouseClicked(mouseX: Double, mouseY: Double, screenWidth: Int, screenHeight: Int): Boolean {
        if (!visible) return false
        val width = 300
        val height = 70 + body.size * 12
        val x = (screenWidth - width) / 2
        val y = (screenHeight - height) / 2
        actions.forEachIndexed { index, action ->
            val buttonX = x + 12 + index * 92
            if (mouseX in buttonX.toDouble()..(buttonX + 82).toDouble() &&
                mouseY in (y + height - 26).toDouble()..(y + height - 6).toDouble()
            ) {
                action.onClick()
                return true
            }
        }
        return mouseX in x.toDouble()..(x + width).toDouble() && mouseY in y.toDouble()..(y + height).toDouble()
    }
}
