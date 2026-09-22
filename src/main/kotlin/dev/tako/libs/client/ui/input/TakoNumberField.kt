package dev.tako.libs.client.ui.input

import dev.tako.libs.client.ui.widget.TakoTextField
import dev.tako.libs.client.ui.layout.TakoBounds
import dev.tako.libs.client.ui.layout.TakoLayoutable
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.widget.TextFieldWidget
import java.util.Locale

class TakoNumberField(
    textRenderer: TextRenderer,
    x: Int,
    y: Int,
    width: Int,
    height: Int,
    minValue: Double = Double.NEGATIVE_INFINITY,
    maxValue: Double = Double.POSITIVE_INFINITY,
    initialValue: Double = 0.0,
    private val step: Double = 1.0,
    private val decimals: Int = 2,
    private val onChanged: (Double) -> Unit = {}
) : TakoLayoutable {
    private val min = minValue
    private val max = maxValue
    private var updatingText = false

    val textField = TakoTextField(
        textRenderer = textRenderer,
        x = x,
        y = y,
        width = width,
        height = height,
        placeholder = "Number",
        initialText = format(initialValue.coerceIn(min, max))
    )

    val widget: TextFieldWidget
        get() = textField.widget

    var value: Double = initialValue.coerceIn(min, max)
        set(newValue) {
            val next = newValue.coerceIn(min, max)
            if (field == next) return
            field = next
            updatingText = true
            textField.text = format(next)
            updatingText = false
            onChanged(next)
        }

    init {
        textField.onChanged { text ->
            if (updatingText) return@onChanged
            text.toDoubleOrNull()?.let { parsed -> value = parsed }
        }
    }

    fun increment() {
        value += step
    }

    fun decrement() {
        value -= step
    }

    fun commit(): Double {
        value = textField.text.toDoubleOrNull() ?: value
        return value
    }

    override fun layout(bounds: TakoBounds) {
        textField.layout(bounds)
    }

    private fun format(value: Double): String = String.format(Locale.ROOT, "%.${decimals.coerceAtLeast(0)}f", value)
}
