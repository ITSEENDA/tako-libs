package dev.tako.libs.client.ui.widget

import dev.tako.libs.client.ui.layout.TakoBounds
import dev.tako.libs.client.ui.layout.TakoLayoutable
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.widget.TextFieldWidget
import net.minecraft.text.Text

data class TakoTextFieldStyle(
    var editableColor: Int = 0xE0E0E0.toInt(),
    var uneditableColor: Int = 0x707070,
    var drawsBackground: Boolean = true
)

/** Reusable text field component with style and native-widget escape hatch. */
class TakoTextField(
    textRenderer: TextRenderer,
    x: Int,
    y: Int,
    width: Int,
    height: Int,
    placeholder: String,
    initialText: String = "",
    maxLength: Int = 128,
    var style: TakoTextFieldStyle = TakoTextFieldStyle()
) : TakoLayoutable {
    val widget = TextFieldWidget(textRenderer, x, y, width, height, Text.literal(placeholder)).also {
        it.text = initialText
        it.setMaxLength(maxLength)
        it.setEditableColor(style.editableColor)
        it.setUneditableColor(style.uneditableColor)
        it.setDrawsBackground(style.drawsBackground)
    }

    var text: String
        get() = widget.text
        set(value) { widget.text = value }

    var visible: Boolean
        get() = widget.visible
        set(value) { widget.visible = value }

    fun onChanged(listener: (String) -> Unit): TakoTextField {
        widget.setChangedListener(listener)
        return this
    }

    override fun layout(bounds: TakoBounds) {
        widget.setDimensionsAndPosition(bounds.width, bounds.height, bounds.x, bounds.y)
    }

}
