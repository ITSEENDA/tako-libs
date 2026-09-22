package dev.tako.libs.client.ui.feedback

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.text.Text

/** Small client-side toast queue reusable by Minecraft mods. */
object TakoToastManager {
    private class ToastSlot {
        var text: String = ""
        var expiresAt: Long = 0L

        fun reset() {
            text = ""
            expiresAt = 0L
        }
    }

    private const val DEFAULT_DURATION_MS = 5_000L
    private const val MAX_MESSAGES = 6
    private const val HEIGHT = 24
    private const val GAP = 4
    private val lock = Any()
    private val active = mutableListOf<ToastSlot>()
    private val pool = ArrayDeque<ToastSlot>()

    fun show(message: String, durationMs: Long = DEFAULT_DURATION_MS) {
        synchronized(lock) {
            recycleExpired(System.currentTimeMillis())
            while (active.size >= MAX_MESSAGES) recycle(active.removeAt(0))
            val slot = pool.removeFirstOrNull() ?: ToastSlot()
            slot.text = message
            slot.expiresAt = System.currentTimeMillis() + durationMs
            active += slot
        }
    }

    fun render(context: DrawContext, client: MinecraftClient) {
        val active = synchronized(lock) {
            val now = System.currentTimeMillis()
            recycleExpired(now)
            this.active.toList()
        }
        active.forEachIndexed { index, slot ->
            val text = Text.literal(slot.text)
            val width = (client.textRenderer.getWidth(text) + 20).coerceAtLeast(180)
            val x = client.window.scaledWidth - width - 8
            val y = 8 + index * (HEIGHT + GAP)
            context.fill(x, y, x + width, y + HEIGHT, 0xE8202020.toInt())
            context.fill(x, y, x + 3, y + HEIGHT, 0xFF55AAFF.toInt())
            context.drawTextWithShadow(client.textRenderer, text, x + 10, y + 8, 0xFFFFFF)
        }
    }

    private fun recycleExpired(now: Long) {
        val iterator = active.iterator()
        while (iterator.hasNext()) {
            val slot = iterator.next()
            if (slot.expiresAt <= now) {
                iterator.remove()
                recycle(slot)
            }
        }
    }

    private fun recycle(slot: ToastSlot) {
        slot.reset()
        pool.addLast(slot)
    }
}
