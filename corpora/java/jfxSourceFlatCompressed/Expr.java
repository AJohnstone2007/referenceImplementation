package com.sun.scenario.effect.compiler.tree;
import com.sun.scenario.effect.compiler.model.Type;
public abstract class Expr implements Tree {
private final Type resultType;
protected Expr(Type resultType) {
this.resultType = resultType;
}
public Type getResultType() {
return resultType;
}
}
