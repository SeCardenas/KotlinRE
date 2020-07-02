package kotlinre.graph

abstract class Pulse<P> {
    abstract fun fold(ifNone: () -> Any, ifChance: (P) -> Any): Any
    /*abstract fun current() : Option<P>
    fun toOption(): Option<P> = fold(None, Some.apply)
    fun isChange(): Boolean = fold({ false },{ true }) as Boolean
    fun map(f: (P) -> Any): Pulse<out Any> = fold()*/
}