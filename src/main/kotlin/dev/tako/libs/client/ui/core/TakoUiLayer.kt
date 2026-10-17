package dev.tako.libs.client.ui.core

import net.minecraft.client.gui.DrawContext

enum class TakoUiLayer(val z: Double) {
    CONTENT(0.0),
    POPOVER(100.0),
    MODAL(200.0),
    TOOLTIP(300.0)
}

fun DrawContext.withUiLayer(layer: TakoUiLayer, draw: () -> Unit) {
    val matrices = this.matrices
    matrices.push()
    matrices.translate(0.0, 0.0, layer.z)
    try {
        draw()
    } finally {
        matrices.pop()
    }
}
