package dev.tako.libs.client.ui.input

import dev.tako.libs.client.ui.core.TakoScreenChrome
import dev.tako.libs.client.ui.core.TakoTheme
import dev.tako.libs.client.ui.layout.TakoBounds
import dev.tako.libs.client.ui.layout.TakoLayoutable
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.text.Text
import org.lwjgl.glfw.GLFW

data class TakoKeyCombination(val keyCode: Int, val modifiers: Int = 0)

class TakoKeybindField(
    private val textRenderer: TextRenderer,
    var binding: TakoKeyCombination? = null,
    private val keyName: (Int) -> String = { "KEY_$it" },
    private val onChanged: (TakoKeyCombination?) -> Unit = {}
) : TakoLayoutable {
    var capturing: Boolean = false
        private set
    private var layoutBounds = TakoBounds(0, 0, 0, 0)

    override fun layout(bounds: TakoBounds) {
        layoutBounds = bounds
    }

    fun beginCapture() {
        capturing = true
    }

    fun cancelCapture() {
        capturing = false
    }

    fun keyPressed(keyCode: Int, modifiers: Int): Boolean {
        if (!capturing) return false
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            cancelCapture()
            return true
        }
        binding = TakoKeyCombination(keyCode, modifiers)
        capturing = false
        onChanged(binding)
        return true
    }

    fun render(context: DrawContext, x: Int, y: Int, width: Int, height: Int = 20) {
        TakoScreenChrome.drawPanel(context, x, y, width, height, if (capturing) TakoTheme.ACCENT else TakoTheme.PANEL_CARD)
        context.drawTextWithShadow(textRenderer, Text.literal(if (capturing) "Press a key..." else displayName()), x + 6, y + 6, TakoTheme.TEXT_PRIMARY)
    }

    fun render(context: DrawContext) {
        render(context, layoutBounds.x, layoutBounds.y, layoutBounds.width, layoutBounds.height)
    }

    fun displayName(): String {
        val current = binding ?: return "Unbound"
        val modifiers = buildList {
            if (current.modifiers and GLFW.GLFW_MOD_CONTROL != 0) add("Ctrl")
            if (current.modifiers and GLFW.GLFW_MOD_SHIFT != 0) add("Shift")
            if (current.modifiers and GLFW.GLFW_MOD_ALT != 0) add("Alt")
            if (current.modifiers and GLFW.GLFW_MOD_SUPER != 0) add("Super")
        }
        return (modifiers + keyName(current.keyCode)).joinToString("+")
    }
}
