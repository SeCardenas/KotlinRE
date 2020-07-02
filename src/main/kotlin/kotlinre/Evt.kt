package kotlinre

import kotlinre.graph.Globals
import kotlinre.graph.Reactive

class Evt<T>(): Reactive<T>(null) {

    internal constructor(depend: List<Reactive<out Any?>>, reeval: () -> T): this()  {
        //value = reeval(depend)
        dependencies = depend
        reevalFn = reeval
        addDependencies()
    }
    private var active = false
    private var observeFnc: (T) -> Unit = {}
    private var pred: (T) -> Boolean = {true}
    fun get(): T? {
        return value
    }

    fun fire(v: T) {
        if(dependencies.isEmpty()) {
            value = v
            observeFnc(v)
            active = true
            Globals.propagateChanges(this)
        }
    }

    override fun reevaluate(): Boolean {
        val newValue = reevalFn()
        if(pred(newValue!!)) {
            value = newValue
            updated = true
            active = true
            observeFnc(newValue)
            return true
        }
        return false
    }

    fun observe(f: (T) -> Unit) {
        observeFnc = f
    }

    fun removeHandler() {
        observeFnc = {}
    }

    internal fun deactivate() {
        active = false
    }

    infix fun or(ev: Evt<T>): Evt<T> {
        return Evt<T>(listOf(this, ev)){
            when(active) {
                true -> value!!
                false -> ev.value!!
            }
        }
    }

    fun latest(init: T): Signal<T> {
        return Signal<T>(listOf(this)){
            when(active) {
                false -> init
                else -> value!!
            }
        }
    }

    infix fun filter(p: (T) -> Boolean): Evt<T> {
        val e = Evt<T>(listOf(this)) {
            this.get()!!
        }
        e.pred = p
        return e
    }

    fun count(): Signal<Int> {
        return Signal(listOf(this), 0){
            it+1
        }
    }

    fun last(n: Int): Signal<List<T>> {
        return Signal(listOf(this), mutableListOf()) {
            val list = it.toMutableList()
            list.add(value!!)
            list.subList(maxOf(list.size-n,0),list.size)
        }
    }

    fun list(): Signal<List<T>> {
        return Signal(listOf(this), mutableListOf()) {
            val list = it.toMutableList()
            list.add(value!!)
            list
        }
    }

    override fun <R> map(transform: (T) -> R): Evt<R> {
        return Evt(listOf(this)) {transform(value!!)}
    }

    fun <A:Any> fold(init: A, f: (A,T)->A): Signal<A> {
        return Signal(listOf(this), init) {f(it,value!!)}
    }
}