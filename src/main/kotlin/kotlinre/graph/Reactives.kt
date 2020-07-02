package kotlinre.graph

abstract class Reactive<T>(protected var value: T?) {
    val id = Globals.assignId()
    protected var dependencies: List<Reactive<out Any?>> = ArrayList()
    protected var reevalFn: () -> T? = {value}
    internal var updated = true

    init {
        addToGraph()
    }

    abstract fun reevaluate(): Boolean
    abstract fun <R> map(transform: (T)->R): Reactive<R>

    private fun addToGraph() {
        Globals.reactives.addVertex(this)
    }
    protected fun addDependencies() {
        for(r in dependencies) {
            Globals.reactives.addEdge(r, this)
        }
    }
}
