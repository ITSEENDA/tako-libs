package dev.tako.libs.client.ui.runtime

/**
 * Main-thread store for runtime overlays such as waypoints or paths.
 * The library only owns identity and lifecycle; feature code owns the model.
 */
class TakoOverlayStore<T>(private val idOf: (T) -> String) {
    private val entries = linkedMapOf<String, T>()

    val items: List<T>
        get() = entries.values.toList()

    fun get(id: String): T? = entries[id]

    fun upsert(item: T) {
        entries[idOf(item)] = item
    }

    fun remove(id: String): T? = entries.remove(id)

    fun clear() {
        entries.clear()
    }
}
