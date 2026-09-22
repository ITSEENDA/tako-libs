package dev.tako.libs.client.ui.input

import dev.tako.libs.client.ui.core.TakoTheme
import dev.tako.libs.client.ui.layout.TakoBounds
import dev.tako.libs.client.ui.layout.TakoLayoutable
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.text.Text

class TakoSlotPicker(
    private val textRenderer: TextRenderer,
    var slotCount: Int = 36,
    var columns: Int = 9,
    var slotSize: Int = 20,
    initialSlot: Int = -1,
    private val onSelected: (Int) -> Unit = {}
) : TakoLayoutable {
    var selectedSlot: Int = initialSlot.coerceIn(-1, (slotCount - 1).coerceAtLeast(-1))
        private set
    private var layoutBounds = TakoBounds(0, 0, 0, 0)

    override fun layout(bounds: TakoBounds) {
        layoutBounds = bounds
    }

    fun render(context: DrawContext, x: Int, y: Int) {
        val safeColumns = columns.coerceAtLeast(1)
        repeat(slotCount.coerceAtLeast(0)) { slot ->
            val slotX = x + (slot % safeColumns) * slotSize
            val slotY = y + (slot / safeColumns) * slotSize
            context.fill(slotX, slotY, slotX + slotSize - 1, slotY + slotSize - 1, if (slot == selectedSlot) TakoTheme.ACCENT else TakoTheme.PANEL_CARD)
            context.drawCenteredTextWithShadow(textRenderer, Text.literal(slot.toString()), slotX + slotSize / 2, slotY + 6, TakoTheme.TEXT_PRIMARY)
        }
    }

    fun mouseClicked(mouseX: Double, mouseY: Double, x: Int, y: Int): Boolean {
        val safeColumns = columns.coerceAtLeast(1)
        val localX = mouseX - x
        val localY = mouseY - y
        if (localX < 0 || localY < 0) return false
        val column = (localX / slotSize).toInt()
        val row = (localY / slotSize).toInt()
        val slot = row * safeColumns + column
        if (column >= safeColumns || slot !in 0 until slotCount) return false
        selectedSlot = slot
        onSelected(slot)
        return true
    }

    fun render(context: DrawContext) {
        render(context, layoutBounds.x, layoutBounds.y)
    }

    fun mouseClicked(mouseX: Double, mouseY: Double): Boolean =
        mouseClicked(mouseX, mouseY, layoutBounds.x, layoutBounds.y)
}
