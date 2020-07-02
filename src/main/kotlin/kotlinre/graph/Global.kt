package kotlinre.graph

import kotlinre.Evt

object Globals {
    private var id = 0
    val reactives = Graph<Reactive<out Any?>>()

    fun assignId(): Int {
        return id++
    }

    fun propagateChanges(r: Reactive<out Any?>) {
        reactives.verifySort()
        for(r1 in reactives.adjMap[r]!!) {
            r1.updated = false
        }
        for(r1 in reactives.topoSort) {
            if(!r1.updated) {
                if(r1.reevaluate()) {
                    for(r2 in reactives.adjMap[r1]!!) {
                        r2.updated = false
                    }
                }
            }
        }
        for(r1 in reactives.topoSort) {
            if(r1 is Evt) {
                r1.deactivate()
            }
        }
    }
}
