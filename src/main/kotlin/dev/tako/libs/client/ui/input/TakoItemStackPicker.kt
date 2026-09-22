package dev.tako.libs.client.ui.input

import dev.tako.libs.client.ui.core.TakoScreenChrome
import dev.tako.libs.client.ui.core.TakoTheme
import dev.tako.libs.client.ui.layout.TakoBounds
import dev.tako.libs.client.ui.layout.TakoLayoutable
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.item.ItemStack
import net.minecraft.text.Text

class TakoItemStackPicker(
    private val textRenderer: TextRenderer,
    initialStack: ItemStack = ItemStack.EMPTY,
    private val onChanged: (ItemStack) -> Unit = {},
    private val onClicked: () -> Unit = {}
) : TakoLayoutable {
    var stack: ItemStack = initialStack
        private set
    private var layoutBounds = TakoBounds(0, 0, 0, 0)

    override fun layout(bounds: TakoBounds) {
        layoutBounds = bounds
    }

    fun setStack(value: ItemStack) {
        stack = value.copy()
        onChanged(stack)
    }

    fun render(context: DrawContext, x: Int, y: Int, width: Int, height: Int = 20) {
        TakoScreenChrome.drawPanel(context, x, y, width, height, TakoTheme.PANEL_CARD)
        if (!stack.isEmpty) {
            context.drawItem(stack, x + 2, y + 1)
            context.drawTextWithShadow(textRenderer, stack.name, x + 24, y + 6, TakoTheme.TEXT_PRIMARY)
        } else {
            context.drawTextWithShadow(textRenderer, Text.literal("Select item"), x + 6, y + 6, TakoTheme.TEXT_SECONDARY)
        }
    }

    fun mouseClicked(mouseX: Double, mouseY: Double, x: Int, y: Int, width: Int, height: Int = 20): Boolean {
        if (mouseX !in x.toDouble()..(x + width).toDouble() || mouseY !in y.toDouble()..(y + height).toDouble()) return false
        onClicked()
        return true
    }

    fun render(context: DrawContext) {
        render(context, layoutBounds.x, layoutBounds.y, layoutBounds.width, layoutBounds.height)
    }

    fun mouseClicked(mouseX: Double, mouseY: Double): Boolean =
        mouseClicked(mouseX, mouseY, layoutBounds.x, layoutBounds.y, layoutBounds.width, layoutBounds.height)
}
