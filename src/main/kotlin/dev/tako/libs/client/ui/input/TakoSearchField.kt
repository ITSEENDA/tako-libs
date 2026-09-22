package dev.tako.libs.client.ui.input

import dev.tako.libs.client.ui.widget.TakoTextField
import dev.tako.libs.client.ui.layout.TakoBounds
import dev.tako.libs.client.ui.layout.TakoLayoutable
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.widget.TextFieldWidget

class TakoSearchField(
    textRenderer: TextRenderer,
    x: Int,
    y: Int,
    width: Int,
    height: Int = 20,
    initialQuery: String = "",
    onQueryChanged: (String) -> Unit = {}
) : TakoLayoutable {
    val textField = TakoTextField(
        textRenderer = textRenderer,
        x = x,
        y = y,
        width = width,
        height = height,
        placeholder = "Search...",
        initialText = initialQuery
    ).onChanged(onQueryChanged)

    val widget: TextFieldWidget
        get() = textField.widget

    var query: String
        get() = textField.text
        set(value) {
            textField.text = value
        }

    fun clear() {
        query = ""
    }

    override fun layout(bounds: TakoBounds) {
        textField.layout(bounds)
    }
}
