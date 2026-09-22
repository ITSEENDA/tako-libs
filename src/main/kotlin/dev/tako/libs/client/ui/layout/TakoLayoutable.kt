package dev.tako.libs.client.ui.layout

import net.minecraft.client.gui.widget.ClickableWidget

/** Component contract used by TakoContainer to apply calculated bounds. */
interface TakoLayoutable {
    fun layout(bounds: TakoBounds)
}

/** Adapter for vanilla widgets that cannot implement TakoLayoutable directly. */
class TakoWidgetLayoutAdapter(private val widget: ClickableWidget) : TakoLayoutable {
    override fun layout(bounds: TakoBounds) {
        widget.setDimensionsAndPosition(bounds.width, bounds.height, bounds.x, bounds.y)
    }
}
