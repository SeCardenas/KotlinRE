package kotlinre

import kotlinre.graph.Globals
import kotlinre.graph.Reactive

class Signal<T>(v: T): Reactive<T>(v) {

    constructor(depend: List<Reactive<out Any?>>, reeval: () -> T) : this(reeval()) {
        dependencies = depend
        reevalFn = reeval
        addDependencies()
    }

    internal constructor(depend: List<Reactive<out Any?>>, init: T, fold: (T) -> T): this(init) {
        dependencies = depend
        foldFn = fold
        reevalFn = {fold(value!!)}
        addDependencies()
    }

    private var foldFn: (T) -> T = {value!!}

    fun get(): T {
        return value!!
    }

    override fun reevaluate(): Boolean {
        val newValue = reevalFn()
        updated = true
        if (newValue != value) {
            value = newValue
            return true
        }
        return false
    }

    fun assign(v: T): Boolean {
        if(dependencies.isEmpty()) {
            value = v
            Globals.propagateChanges(this)
            return true
        }
        return false
    }

    fun changed(): Evt<T> {
        return Evt(listOf(this)) {value!!}
    }

    override fun <R> map(transform: (T) -> R): Signal<R> {
        return Signal(listOf(this)) {transform(value!!)}
    }
}
