package io.github.sickandpanik

import java.util.*


class Analyzer {
    fun unusedAssignmentsAnalysis(entry: CfgNode): List<CfgNode> {
        val cfgNodes = traverseCfg(entry)
        val liveVars = liveVarsAnalysis(cfgNodes)

        return cfgNodes
            .filter { it.astNode is AstAssignment }
            .filter { cfgNode ->
                val varName = (cfgNode.astNode as AstAssignment).variable.varName
                !liveVars.join(cfgNode).contains(varName)
            }
    }

    fun liveVarsAnalysis(entry: CfgNode): MapLattice =
        liveVarsAnalysis(traverseCfg(entry))

    fun liveVarsAnalysis(cfgNodes: List<CfgNode>): MapLattice {
        val mapLattice = cfgNodes.associateWith { setOf<String>() }.toMutableMap()

        var unchanged = false
        while (!unchanged) {
            unchanged = true

            cfgNodes.forEach {
                val newValue = mapLattice.f(it)

                if (newValue != mapLattice[it]!!) {
                    unchanged = false
                    mapLattice[it] = newValue
                }
            }
        }

        return mapLattice
    }

    private fun traverseCfg(entry: CfgNode): List<CfgNode> {
        val visitedNodes = mutableSetOf(entry)
        val nodesToVisit = ArrayDeque(entry.succ)

        while (!nodesToVisit.isEmpty()) {
            val currentNode = nodesToVisit.pop()
            visitedNodes.add(currentNode)

            currentNode.succ.forEach {
                if (it !in visitedNodes) {
                    nodesToVisit.push(it)
                }
            }
        }

        return visitedNodes.toList()
    }

    private fun AstExpression.vars(): PowerSetLatticeElem =
        when (this) {
            is AstBinaryOp -> this.lhs.vars() + this.rhs.vars()
            is AstConstant -> setOf()
            is AstNestedExpr -> this.expr.vars()
            is AstVariable -> setOf(this.varName)
        }

    private fun MapLattice.join(v: CfgNode): PowerSetLatticeElem =
        v.succ.map { this[it]!! }.fold(setOf()) { acc, latticeElem -> acc + latticeElem }

    private fun MapLattice.f(v: CfgNode): PowerSetLatticeElem =
        when (val it = v.astNode) {
            is AstAssignment -> {
                val varName = it.variable.varName
                this.join(v) - setOf(varName) + it.expr.vars()
            }
            is AstExpression -> this.join(v) + it.vars()
            else -> this.join(v)
        }
}

