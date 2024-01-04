# LLVM Dataflow Analysis — Test Task

Usage:

```
./gradlew run --args='--ast --livevars example/simple_while'
```

Replace `example/simple_while` with your own code.

Use flags 
* `--ast` to show generated AST,
* `--livevars` to show the results of the live variables analysis.

## Comments on parser

The parser implementation is based on the recursive descent method,
and is adapted from [the book “Crafting Interpreters”](https://craftinginterpreters.com/parsing-expressions.html).

## Comments on the analysis

The analysis is based on the live variables analysis; the latter is adapted from
[the lecture notes of the “Static Analysis” course in Aarhus University](https://cs.au.dk/~amoeller/spa/).

To get the list of the unused assignments, we simply look through all CFG nodes
which represent assignments and then check through succeeding nodes whether 
the assigned variable or is used in later point in the program with no writes
in-between. Live variables analysis uses simple round-robin solver.