package io.github.sickandpanik

object AstToCfg {
    fun astToCfg(program: AstProgram): Pair<CfgNode, CfgNode> {
        val entry = EntryNode(program)
        val exit = ExitNode(program)

        val statementsNodes = statementListToCfg(program.statementList)

        entry.pointTo(statementsNodes.first)
        statementsNodes.second.forEach {
            it.pointTo(exit)
        }

        return Pair(entry, exit)
    }

    private fun statementListToCfg(statementList: AstStatementList): Pair<List<CfgNode>, List<CfgNode>> {
        val entryNodes = mutableListOf<CfgNode>()
        var areEntryNodesSet = false
        var lastExitNodes = listOf<CfgNode>()

        statementList.statements.forEach { stmt ->
            val statementNodes = when (stmt) {
                is AstAssignment -> assignmentToCfg(stmt)
                is AstIfBlock -> ifBlockToCfg(stmt)
                is AstWhileBlock -> whileBlockToCfg(stmt)
            }

            lastExitNodes.forEach {
                it.pointTo(statementNodes.first)
            }

            if (!areEntryNodesSet) {
                areEntryNodesSet = true
                entryNodes.addAll(statementNodes.first)
            }
            lastExitNodes = statementNodes.second
        }

        return Pair(entryNodes, lastExitNodes)
    }

    private fun assignmentToCfg(assignment: AstAssignment): Pair<List<CfgNode>, List<CfgNode>> {
        val assignmentNode = CfgNode(assignment)

        return Pair(listOf(assignmentNode), listOf(assignmentNode))
    }

    private fun ifBlockToCfg(ifBlock: AstIfBlock): Pair<List<CfgNode>, List<CfgNode>> {
        val expressionNode = CfgNode(ifBlock.expr)
        val statementsNodes = statementListToCfg(ifBlock.statements)

        expressionNode.pointTo(statementsNodes.first)

        return Pair(listOf(expressionNode), listOf(expressionNode) + statementsNodes.second)
    }

    private fun whileBlockToCfg(whileBlock: AstWhileBlock): Pair<List<CfgNode>, List<CfgNode>> {
        val expressionNode = CfgNode(whileBlock.expr)
        val statementsNodes = statementListToCfg(whileBlock.statements)

        expressionNode.pointTo(statementsNodes.first)
        statementsNodes.second.forEach {
            it.pointTo(expressionNode)
        }

        return Pair(listOf(expressionNode), listOf(expressionNode))
    }
}