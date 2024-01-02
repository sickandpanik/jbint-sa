# LLVM Dataflow Analysis — Test Task

Usage:

```
./gradlew run --args='--ast example/simple_while'
```

Replace `example/simple_while` with your own code.

Use flags `--ast` and `--cfg` to show generated AST and CFG structure.

## Comments on parser

The parser implementation is based on the recursive descent method,
and is adapted from [the book “Crafting Interpreters”](https://craftinginterpreters.com/parsing-expressions.html).
