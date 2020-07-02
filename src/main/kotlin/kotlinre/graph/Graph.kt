package kotlinre.graph

import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class Graph<T> {

    internal val adjMap = HashMap<T, MutableList<T>>()
    private var nV = 0
    var topoSort = ArrayList<T>()
    private var sorted = true

    fun addVertex(v: T) {
        if (!adjMap.containsKey(v)) {
            adjMap[v] = ArrayList()
            ++nV
            sorted = false
        }
    }

    fun addEdge(from: T, to: T) {
        addVertex(from)
        addVertex(to)
        adjMap[from]!!.add(to)
        sorted = false
    }

    private fun topologicalSortRecursive(v: T, visited: HashMap<T,Boolean>, stack: Stack<T>) {
        visited[v] = true
        val adj = adjMap[v]!!
        for(i in adj) {
            if(!visited[i]!!) {
                topologicalSortRecursive(i, visited, stack)
            }
        }
        stack.push(v)
    }

    private fun topologicalSort() {
        val stack = Stack<T>()
        val topologicalList = ArrayList<T>()
        val visited = HashMap<T, Boolean>()
        for(key in adjMap.keys) {
            visited[key] = false
        }

        for(key in adjMap.keys) {
            if(!visited[key]!!) {
                topologicalSortRecursive(key, visited, stack)
            }
        }
        while(!stack.isEmpty()) {
            topologicalList.add(stack.pop())
        }
        topoSort = topologicalList
        sorted = true
    }

    fun verifySort() {
        if(!sorted) {
            topologicalSort()
        }
    }

    override fun toString(): String = StringBuffer().apply {
        for (key in adjMap.keys) {
            append("$key -> ")
            append(adjMap[key]?.joinToString(", ", "[", "]\n"))
        }
    }.toString()
}