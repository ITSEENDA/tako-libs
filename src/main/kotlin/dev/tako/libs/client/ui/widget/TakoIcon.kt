package dev.tako.libs.client.ui.widget

import net.minecraft.client.gui.DrawContext
import net.minecraft.item.ItemStack

class TakoIcon(var stack: ItemStack? = null, var size: Int = 18) {
    fun render(context: DrawContext, x: Int, y: Int) {
        stack?.takeUnless(ItemStack::isEmpty)?.let { context.drawItem(it, x, y) }
    }
}
