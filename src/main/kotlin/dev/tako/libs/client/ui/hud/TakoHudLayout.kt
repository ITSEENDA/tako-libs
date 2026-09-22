package dev.tako.libs.client.ui.hud

import dev.tako.libs.client.ui.core.TakoScreenChrome
import dev.tako.libs.client.ui.core.TakoTheme
import dev.tako.libs.client.ui.editor.TakoEditCommand
import dev.tako.libs.client.ui.editor.TakoUndoRedoHistory

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.text.Text
import org.lwjgl.glfw.GLFW
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.roundToInt

enum class TakoHudAnchor { TOP_LEFT, TOP_CENTER, TOP_RIGHT, CENTER_LEFT, CENTER, CENTER_RIGHT, BOTTOM_LEFT, BOTTOM_CENTER, BOTTOM_RIGHT }

data class TakoHudLayout(
    var anchor: TakoHudAnchor = TakoHudAnchor.TOP_LEFT,
    var offsetX: Int = 8,
    var offsetY: Int = 8,
    var scale: Float = 1f
)

data class TakoHudRect(val x: Int, val y: Int, val width: Int, val height: Int)

interface TakoHudComponent {
    val id: String
    val label: String
    val layout: TakoHudLayout
    fun measure(client: MinecraftClient): Pair<Int, Int>
    fun render(context: DrawContext, client: MinecraftClient, bounds: TakoHudRect)
}

object TakoHudLayoutMath {
    fun resolve(layout: TakoHudLayout, screenWidth: Int, screenHeight: Int, width: Int, height: Int): TakoHudRect {
        val x = when (layout.anchor) {
            TakoHudAnchor.TOP_LEFT, TakoHudAnchor.CENTER_LEFT, TakoHudAnchor.BOTTOM_LEFT -> layout.offsetX
            TakoHudAnchor.TOP_CENTER, TakoHudAnchor.CENTER, TakoHudAnchor.BOTTOM_CENTER -> screenWidth / 2 - width / 2 + layout.offsetX
            TakoHudAnchor.TOP_RIGHT, TakoHudAnchor.CENTER_RIGHT, TakoHudAnchor.BOTTOM_RIGHT -> screenWidth - width - layout.offsetX
        }
        val y = when (layout.anchor) {
            TakoHudAnchor.TOP_LEFT, TakoHudAnchor.TOP_CENTER, TakoHudAnchor.TOP_RIGHT -> layout.offsetY
            TakoHudAnchor.CENTER_LEFT, TakoHudAnchor.CENTER, TakoHudAnchor.CENTER_RIGHT -> screenHeight / 2 - height / 2 + layout.offsetY
            TakoHudAnchor.BOTTOM_LEFT, TakoHudAnchor.BOTTOM_CENTER, TakoHudAnchor.BOTTOM_RIGHT -> screenHeight - height - layout.offsetY
        }
        return TakoHudRect(x, y, width, height)
    }

    fun offsetXFor(anchor: TakoHudAnchor, screenX: Int, screenWidth: Int, width: Int): Int = when (anchor) {
        TakoHudAnchor.TOP_LEFT, TakoHudAnchor.CENTER_LEFT, TakoHudAnchor.BOTTOM_LEFT -> screenX
        TakoHudAnchor.TOP_CENTER, TakoHudAnchor.CENTER, TakoHudAnchor.BOTTOM_CENTER ->
            screenX - (screenWidth / 2 - width / 2)
        TakoHudAnchor.TOP_RIGHT, TakoHudAnchor.CENTER_RIGHT, TakoHudAnchor.BOTTOM_RIGHT ->
            screenWidth - width - screenX
    }

    fun offsetYFor(anchor: TakoHudAnchor, screenY: Int, screenHeight: Int, height: Int): Int = when (anchor) {
        TakoHudAnchor.TOP_LEFT, TakoHudAnchor.TOP_CENTER, TakoHudAnchor.TOP_RIGHT -> screenY
        TakoHudAnchor.CENTER_LEFT, TakoHudAnchor.CENTER, TakoHudAnchor.CENTER_RIGHT ->
            screenY - (screenHeight / 2 - height / 2)
        TakoHudAnchor.BOTTOM_LEFT, TakoHudAnchor.BOTTOM_CENTER, TakoHudAnchor.BOTTOM_RIGHT ->
            screenHeight - height - screenY
    }
}

class TakoHudLayoutEditorScreen(
    private val parent: Screen?,
    private val components: List<TakoHudComponent>,
    private val onSave: () -> Unit = {}
) : Screen(Text.literal("HUD Layout")) {
    private enum class Handle { NW, NE, SW, SE }
    private var selected = 0
    private var dragging = false
    private var dragX = 0
    private var dragY = 0
    private var resizeHandle: Handle? = null
    private var resizeRect = TakoHudRect(0, 0, 0, 0)
    private var resizeScale = 1f
    private var resizeBaseWidth = 1
    private var resizeBaseHeight = 1
    private val history = TakoUndoRedoHistory()
    private var undoButton: ButtonWidget? = null
    private var redoButton: ButtonWidget? = null
    private var editComponent: TakoHudComponent? = null
    private var editStartLayout: TakoHudLayout? = null
    private val anchorButtons = linkedMapOf<TakoHudAnchor, ButtonWidget>()

    override fun init() {
        val undo = ButtonWidget.builder(Text.literal("Undo")) { history.undo() }
            .dimensions(width - 210, 112, 94, 20).build()
        val redo = ButtonWidget.builder(Text.literal("Redo")) { history.redo() }
            .dimensions(width - 110, 112, 98, 20).build()
        undoButton = undo
        redoButton = redo
        addDrawableChild(undo)
        addDrawableChild(redo)
        TakoHudAnchor.entries.forEachIndexed { index, anchor ->
            val button = ButtonWidget.builder(Text.literal(anchorLabel(anchor))) {
                setAnchor(anchor)
            }.dimensions(width - 210 + (index % 3) * 66, 164 + (index / 3) * 22, 62, 20).build()
            anchorButtons[anchor] = button
            addDrawableChild(button)
        }
        addDrawableChild(ButtonWidget.builder(Text.literal("Save")) {
            onSave()
            client?.setScreen(parent)
        }.dimensions(width - 90, height - 30, 78, 20).build())
    }

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        val minecraft = client ?: return
        undoButton?.active = history.canUndo
        redoButton?.active = history.canRedo
        val selectedAnchor = components.getOrNull(selected)?.layout?.anchor
        anchorButtons.forEach { (anchor, button) -> button.active = anchor != selectedAnchor }
        super.render(context, mouseX, mouseY, delta)
        TakoScreenChrome.drawTitle(context, textRenderer, title, width, 16)
        components.forEachIndexed { index, component ->
            val rect = bounds(component, minecraft)
            component.render(context, minecraft, rect)
            if (index == selected) drawHandles(context, rect)
        }
        val selectedComponent = components.getOrNull(selected)
        if (selectedComponent != null) {
            TakoScreenChrome.drawPanel(context, width - 210, 40, 198, 190)
            context.drawTextWithShadow(textRenderer, Text.literal(selectedComponent.label), width - 202, 48, TakoTheme.TEXT_PRIMARY)
            context.drawTextWithShadow(textRenderer, Text.literal("Anchor: ${selectedComponent.layout.anchor}"), width - 202, 65, TakoTheme.TEXT_SECONDARY)
            context.drawTextWithShadow(textRenderer, Text.literal("Scale: ${"%.2f".format(selectedComponent.layout.scale)}x"), width - 202, 82, TakoTheme.TEXT_SECONDARY)
            context.drawTextWithShadow(textRenderer, Text.literal("Select anchor"), width - 202, 150, TakoTheme.TEXT_SECONDARY)
        }
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (super.mouseClicked(mouseX, mouseY, button)) return true
        if (button != 0) return false
        val component = components.getOrNull(selected) ?: return false
        val selectedRect = bounds(component, client ?: return false)
        handleAt(selectedRect, mouseX, mouseY)?.let {
            editComponent = component
            editStartLayout = component.layout.copy()
            resizeHandle = it
            resizeRect = selectedRect
            resizeScale = component.layout.scale
            val size = component.measure(client!!)
            resizeBaseWidth = (size.first * resizeScale).roundToInt().coerceAtLeast(1)
            resizeBaseHeight = (size.second * resizeScale).roundToInt().coerceAtLeast(1)
            return true
        }
        val hit = components.indexOfFirst { contains(bounds(it, client!!), mouseX, mouseY) }
        if (hit >= 0) {
            selected = hit
            editComponent = components[hit]
            editStartLayout = components[hit].layout.copy()
            val rect = bounds(components[hit], client!!)
            dragX = mouseX.toInt() - rect.x
            dragY = mouseY.toInt() - rect.y
            dragging = true
            return true
        }
        return false
    }

    override fun mouseDragged(mouseX: Double, mouseY: Double, button: Int, deltaX: Double, deltaY: Double): Boolean {
        if (button != 0) return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)
        val component = components.getOrNull(selected) ?: return false
        val handle = resizeHandle
        if (handle != null) {
            val newWidth = max(1, if (handle == Handle.NW || handle == Handle.SW) resizeRect.x + resizeRect.width - mouseX.toInt() else mouseX.toInt() - resizeRect.x)
            val newHeight = max(1, if (handle == Handle.NW || handle == Handle.NE) resizeRect.y + resizeRect.height - mouseY.toInt() else mouseY.toInt() - resizeRect.y)
            val factor = max(newWidth.toFloat() / resizeBaseWidth, newHeight.toFloat() / resizeBaseHeight)
            val scale = (resizeScale * factor).coerceIn(0.5f, 2f)
            val componentWidth = (resizeBaseWidth * scale).roundToInt()
            val componentHeight = (resizeBaseHeight * scale).roundToInt()
            val newX = if (handle == Handle.NW || handle == Handle.SW) {
                resizeRect.x + resizeRect.width - componentWidth
            } else {
                resizeRect.x
            }
            val newY = if (handle == Handle.NW || handle == Handle.NE) {
                resizeRect.y + resizeRect.height - componentHeight
            } else {
                resizeRect.y
            }
            component.layout.scale = scale
            component.layout.offsetX = TakoHudLayoutMath.offsetXFor(component.layout.anchor, newX, width, componentWidth)
            component.layout.offsetY = TakoHudLayoutMath.offsetYFor(component.layout.anchor, newY, height, componentHeight)
            return true
        }
        if (dragging) {
            val rect = bounds(component, client ?: return false)
            val proposedX = (mouseX.toInt() - dragX).coerceIn(0, (width - rect.width).coerceAtLeast(0))
            val proposedY = (mouseY.toInt() - dragY).coerceIn(0, (height - rect.height).coerceAtLeast(0))
            component.layout.offsetX = TakoHudLayoutMath.offsetXFor(component.layout.anchor, proposedX, width, rect.width)
            component.layout.offsetY = TakoHudLayoutMath.offsetYFor(component.layout.anchor, proposedY, height, rect.height)
            return true
        }
        return false
    }

    override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int): Boolean {
        commitInteraction()
        dragging = false
        resizeHandle = null
        return super.mouseReleased(mouseX, mouseY, button)
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        if (hasControlDown()) {
            if (keyCode == GLFW.GLFW_KEY_Z) {
                if (hasShiftDown()) history.redo() else history.undo()
                return true
            }
            if (keyCode == GLFW.GLFW_KEY_Y) {
                history.redo()
                return true
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers)
    }

    override fun close() {
        onSave()
        client?.setScreen(parent)
    }

    private fun bounds(component: TakoHudComponent, client: MinecraftClient): TakoHudRect {
        val size = component.measure(client)
        return TakoHudLayoutMath.resolve(component.layout, width, height, (size.first * component.layout.scale).roundToInt(), (size.second * component.layout.scale).roundToInt())
    }

    private fun drawHandles(context: DrawContext, rect: TakoHudRect) {
        listOf(rect.x to rect.y, rect.x + rect.width to rect.y, rect.x to rect.y + rect.height, rect.x + rect.width to rect.y + rect.height).forEach { (x, y) ->
            context.fill(x - 3, y - 3, x + 3, y + 3, TakoTheme.TEXT_PRIMARY)
        }
    }

    private fun setAnchor(anchor: TakoHudAnchor) {
        val component = components.getOrNull(selected) ?: return
        val rect = bounds(component, client ?: return)
        val before = component.layout.copy()
        component.layout.anchor = anchor
        component.layout.offsetX = TakoHudLayoutMath.offsetXFor(anchor, rect.x, width, rect.width)
        component.layout.offsetY = TakoHudLayoutMath.offsetYFor(anchor, rect.y, height, rect.height)
        val after = component.layout.copy()
        if (before != after) {
            applyLayout(component.layout, before)
            history.execute(TakoEditCommand(
                redoAction = { applyLayout(component.layout, after) },
                undoAction = { applyLayout(component.layout, before) }
            ))
        }
    }

    private fun anchorLabel(anchor: TakoHudAnchor): String = when (anchor) {
        TakoHudAnchor.TOP_LEFT -> "TL"
        TakoHudAnchor.TOP_CENTER -> "TC"
        TakoHudAnchor.TOP_RIGHT -> "TR"
        TakoHudAnchor.CENTER_LEFT -> "CL"
        TakoHudAnchor.CENTER -> "C"
        TakoHudAnchor.CENTER_RIGHT -> "CR"
        TakoHudAnchor.BOTTOM_LEFT -> "BL"
        TakoHudAnchor.BOTTOM_CENTER -> "BC"
        TakoHudAnchor.BOTTOM_RIGHT -> "BR"
    }

    private fun contains(rect: TakoHudRect, x: Double, y: Double): Boolean =
        x >= rect.x && x <= rect.x + rect.width && y >= rect.y && y <= rect.y + rect.height

    private fun handleAt(rect: TakoHudRect, x: Double, y: Double): Handle? = when {
        near(rect.x, rect.y, x, y) -> Handle.NW
        near(rect.x + rect.width, rect.y, x, y) -> Handle.NE
        near(rect.x, rect.y + rect.height, x, y) -> Handle.SW
        near(rect.x + rect.width, rect.y + rect.height, x, y) -> Handle.SE
        else -> null
    }

    private fun near(cx: Int, cy: Int, x: Double, y: Double): Boolean =
        kotlin.math.abs(cx - x) <= 8 && kotlin.math.abs(cy - y) <= 8

    private fun commitInteraction() {
        val component = editComponent ?: return
        val before = editStartLayout ?: return
        val after = component.layout.copy()
        if (before != after) {
            applyLayout(component.layout, before)
            history.execute(TakoEditCommand(
                redoAction = { applyLayout(component.layout, after) },
                undoAction = { applyLayout(component.layout, before) }
            ))
        }
        editComponent = null
        editStartLayout = null
    }

    private fun applyLayout(target: TakoHudLayout, source: TakoHudLayout) {
        target.anchor = source.anchor
        target.offsetX = source.offsetX
        target.offsetY = source.offsetY
        target.scale = source.scale
    }
}
