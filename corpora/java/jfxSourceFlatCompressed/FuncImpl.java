package com.sun.scenario.effect.compiler.model;
import java.util.List;
import com.sun.scenario.effect.compiler.tree.Expr;
public abstract class FuncImpl {
public String getPreamble(List<Expr> params) {
return null;
}
public abstract String toString(int i, List<Expr> params);
}
