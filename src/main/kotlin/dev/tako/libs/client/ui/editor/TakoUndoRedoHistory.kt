package dev.tako.libs.client.ui.editor

/** A small command used by editors to apply and reverse one user edit. */
class TakoEditCommand(
    private val redoAction: () -> Unit,
    private val undoAction: () -> Unit
) {
    fun redo() = redoAction()
    fun undo() = undoAction()
}

/** Bounded undo/redo history for editor state. All calls are expected on the client thread. */
class TakoUndoRedoHistory(maxSize: Int = 100) {
    private val capacity = maxSize.coerceAtLeast(1)
    private val undoStack = ArrayDeque<TakoEditCommand>()
    private val redoStack = ArrayDeque<TakoEditCommand>()

    val canUndo: Boolean
        get() = undoStack.isNotEmpty()

    val canRedo: Boolean
        get() = redoStack.isNotEmpty()

    fun execute(command: TakoEditCommand) {
        command.redo()
        undoStack.addLast(command)
        redoStack.clear()
        while (undoStack.size > capacity) undoStack.removeFirst()
    }

    fun undo(): Boolean {
        val command = undoStack.removeLastOrNull() ?: return false
        command.undo()
        redoStack.addLast(command)
        return true
    }

    fun redo(): Boolean {
        val command = redoStack.removeLastOrNull() ?: return false
        command.redo()
        undoStack.addLast(command)
        return true
    }

    fun clear() {
        undoStack.clear()
        redoStack.clear()
    }
}
