which functions inside VoidVisitorAdapter need to override to do the following Besides common branching statements,
keep in mind to include catch clauses, conditional statements (?:) and logical short circuit evaluations (&&, ||).
The following instructions are not included: else, switch without explicit case labels, default labels of switch statements,
try and finally blocks.


To identify all relevant control flow elements **excluding** `else`, `switch` without case labels, `default` labels, `try`, and `finally`, but **including**:

* Common branching statements (`if`, `switch` with cases)
* `catch` clauses
* Conditional (ternary) expressions (`?:`)
* Logical short-circuit expressions (`&&`, `||`)

you need to override the following `VoidVisitorAdapter` methods when working with the JavaParser library (e.g., com.github.javaparser.ast.visitor.VoidVisitorAdapter):

---

### ✅ **Override These Visitor Methods**

| Control Structure      | Visitor Method to Override      | AST Node                                                                                                                                     |                            |                                                                                                                                    |
| ---------------------- | ------------------------------- | -------------------------------------------------------------------------------------------------------------------------------------------- | -------------------------- | ---------------------------------------------------------------------------------------------------------------------------------- |
| `if` statements        | `visit(IfStmt, A arg)`          | [`IfStmt`](https://javadoc.io/doc/com.github.javaparser/javaparser-core/latest/com/github/javaparser/ast/stmt/IfStmt.html)                   |                            |                                                                                                                                    |
| `switch` with cases    | `visit(SwitchEntry, A arg)`     | [`SwitchEntry`](https://javadoc.io/doc/com.github.javaparser/javaparser-core/latest/com/github/javaparser/ast/stmt/SwitchEntry.html)         |                            |                                                                                                                                    |
| `catch` clauses        | `visit(CatchClause, A arg)`     | [`CatchClause`](https://javadoc.io/doc/com.github.javaparser/javaparser-core/latest/com/github/javaparser/ast/stmt/CatchClause.html)         |                            |                                                                                                                                    |
| ternary (`?:`)         | `visit(ConditionalExpr, A arg)` | [`ConditionalExpr`](https://javadoc.io/doc/com.github.javaparser/javaparser-core/latest/com/github/javaparser/ast/expr/ConditionalExpr.html) |                            |                                                                                                                                    |
| short-circuit `&&`, \` |                                 | \`                                                                                                                                           | `visit(BinaryExpr, A arg)` | [`BinaryExpr`](https://javadoc.io/doc/com.github.javaparser/javaparser-core/latest/com/github/javaparser/ast/expr/BinaryExpr.html) |

You'll need to **filter** `BinaryExpr` nodes for:

```java
BinaryExpr.Operator.AND
BinaryExpr.Operator.OR
```

---

### ❌ **Don't Override These (excluded by instructions)**

* `visit(ElseStmt, ...)` → not needed (excluded)
* `visit(SwitchStmt, ...)` → ignore unless checking for presence of `SwitchEntry`
* `visit(SwitchEntry)` with `default` → ignore if entry is `default`
* `visit(TryStmt, ...)` or `visit(FinallyBlock, ...)` → excluded

---

### Sample Skeleton

```java
@Override
public void visit(IfStmt n, Void arg) {
    super.visit(n, arg);
    // handle if-statement
}

@Override
public void visit(SwitchEntry n, Void arg) {
    super.visit(n, arg);
    if (!n.getLabels().isEmpty()) {
        // handle case label (but ignore 'default' which has empty label)
    }
}

@Override
public void visit(CatchClause n, Void arg) {
    super.visit(n, arg);
    // handle catch clause
}

@Override
public void visit(ConditionalExpr n, Void arg) {
    super.visit(n, arg);
    // handle ternary conditional (?:)
}

@Override
public void visit(BinaryExpr n, Void arg) {
    super.visit(n, arg);
    if (n.getOperator() == BinaryExpr.Operator.AND || n.getOperator() == BinaryExpr.Operator.OR) {
        // handle logical short-circuit evaluation
    }
}
```

Let me know if you want help writing a full visitor class that collects or counts these control flows.

