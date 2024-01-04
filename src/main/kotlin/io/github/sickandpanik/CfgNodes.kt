package io.github.sickandpanik

open class CfgNode(val astNode: AstNode) {
    val pred: MutableList<CfgNode> = mutableListOf()
    val succ: MutableList<CfgNode> = mutableListOf()

    fun pointTo(other: CfgNode) {
        succ.add(other)
        other.pred.add(this)
    }

    fun pointTo(others: List<CfgNode>) {
        others.forEach {
            pointTo(it)
        }
    }
}

class EntryNode(astNode: AstProgram): CfgNode(astNode)

class ExitNode(astNode: AstProgram): CfgNode(astNode)

