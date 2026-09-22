package dev.tako.libs.client.ui.layout

/** Small UI-independent scroll model for Minecraft screens and lists. */
class TakoScrollState(private val step: Double = 32.0) {
    var offset: Double = 0.0
        private set
    var maxOffset: Double = 0.0
        private set

    fun update(contentSize: Int, viewportSize: Int) {
        maxOffset = (contentSize - viewportSize.coerceAtLeast(1)).coerceAtLeast(0).toDouble()
        offset = offset.coerceIn(0.0, maxOffset)
    }

    fun reset() {
        offset = 0.0
    }

    fun setOffset(value: Double) {
        offset = value.coerceIn(0.0, maxOffset)
    }

    fun scroll(verticalAmount: Double) {
        setOffset(offset - verticalAmount * step)
    }
}
