// @formatter:off
package uk.ac.rhul.cs.csle.art.adl;
import java.util.Iterator;
import java.util.LinkedHashMap;

import uk.ac.rhul.cs.csle.art.term.ITerms;
import uk.ac.rhul.cs.csle.art.term.Value;
import uk.ac.rhul.cs.csle.art.term.__char;
import uk.ac.rhul.cs.csle.art.term.__int32;
import uk.ac.rhul.cs.csle.art.term.__list;
import uk.ac.rhul.cs.csle.art.term.__mapChain;
import uk.ac.rhul.cs.csle.art.term.__proc;
import uk.ac.rhul.cs.csle.art.term.__quote;
import uk.ac.rhul.cs.csle.art.term.__real64;
import uk.ac.rhul.cs.csle.art.term.__string;

public class ADL {
  ITerms iTerms;
  public ADL(ITerms iTerms) { this.iTerms = iTerms;}

  public Value interpret(int term, __mapChain env) {
    // System.out.println("ADL interpret " + iTerms.toString(term));
    Value ret; // tenporary used in some switch cases
    int children[] = iTerms.getTermChildren(term);
    switch (iTerms.getTermSymbolString(term)) {
    case "lambda":   LinkedHashMap<__quote, Value> parameters = new LinkedHashMap<>();
                     for (int i: iTerms.getTermChildren(children[0])) parameters.put(new __quote(i), iTerms.valueEmpty);
                     return new __proc(parameters, children[1]);
    case "apply":    ret = iTerms.valueEmpty;
                     for (int c : children)
                       if (ret instanceof __proc) { // ret is result of previous child
                         __mapChain callEnv = new __mapChain(env);
                         Value argument = interpret(c, env);
                         Iterator<__quote> parameterIterator = ((__proc) ret).getParameters().keySet().iterator();

                         if (argument instanceof __list) {
                           Iterator<Value> argumentIterator = ((__list) argument).javaValue().iterator();
                           while (argumentIterator.hasNext()) callEnv.__put(parameterIterator.next(), argumentIterator.next());
                         } else callEnv.__put(parameterIterator.next(), argument); // singleton case

                         ret = interpret(((__proc) ret).getBodyTerm(), callEnv);
                       } else ret = interpret(c, env);
                     return ret; // Result of expression is the last thing computed
    case "const":    return updateEnvironment(env, children[0],children[1],env, true);
    case "assign":   return updateEnvironment(env, children[0],children[1],env, false);
    case "match":    if (interpret(children[0], env).equals(iTerms.valueBoolTrue)) return interpret(children[1], env);
                     return interpret(children[2], env);
    case "matchrep": ret = iTerms.valueEmpty;
                     while (interpret(children[0], env).equals(iTerms.valueBoolTrue)) ret = interpret(children[1], env);
                     if (!iTerms.hasSymbol(children[2], "skip")) ret = interpret(children[2], env);
                     return ret;
    case "slice":    throw new ADLException("Slice not yet implemented");
    case "blist":    int gc = iTerms.getSubterm(term, 0,0); if (iTerms.hasSymbol(gc, "list")) children = iTerms.getTermChildren(gc);
    case "mtlist":   // blist flattens list as top argument and flows through to list; mtlist just flows through
    case "list":     ret = new __list(); for (int c : children) ret.__put(interpret(c, env)); return ret;
    case "adl":      return iTerms.valueEmpty; // Special case - empty program
    case "seq":      interpret(children[0], env); return interpret(children[1], env);
    case "scope":    return interpret(children[0], new __mapChain(env));
    case "skip":     return iTerms.valueDone;
    case "pair":     ret = new __list(); ret.__add(interpret(children[0], env)); ret.__add(interpret(children[1], env)); return ret;
    case "or":       return interpret(children[0],env).__or(interpret(children[1], env));
    case "xor":      return interpret(children[0],env).__xor(interpret(children[1], env));
    case "and":      return interpret(children[0],env).__and(interpret(children[1], env));
    case "eq":       return interpret(children[0],env).__eq(interpret(children[1], env));
    case "ne":       return interpret(children[0],env).__ne(interpret(children[1], env));
    case "ge":       return interpret(children[0],env).__ge(interpret(children[1], env));
    case "gt":       return interpret(children[0],env).__gt(interpret(children[1], env));
    case "le":       return interpret(children[0],env).__le(interpret(children[1], env));
    case "lt":       return interpret(children[0],env).__lt(interpret(children[1], env));
    case "cat":      return interpret(children[0],env).__cat(interpret(children[1], env));
    case "lsh":      return interpret(children[0],env).__lsh(interpret(children[1], env));
    case "rsh":      return interpret(children[0],env).__rsh(interpret(children[1], env));
    case "rol":      return interpret(children[0],env).__rol(interpret(children[1], env));
    case "ror":      return interpret(children[0],env).__ror(interpret(children[1], env));
    case "ash":      return interpret(children[0],env).__ash(interpret(children[1], env));
    case "add":      return interpret(children[0],env).__add(interpret(children[1], env));
    case "sub":      return interpret(children[0],env).__sub(interpret(children[1], env));
    case "mul":      return interpret(children[0],env).__mul(interpret(children[1], env));
    case "div":      return interpret(children[0],env).__div(interpret(children[1], env));
    case "mod":      return interpret(children[0],env).__div(interpret(children[1], env));
    case "exp":      return interpret(children[0],env).__exp(interpret(children[1], env));
    case "pos":      return interpret(children[0],env); // Add a pos routine to abstract class?
    case "neg":      return interpret(children[0],env).__neg();
    case "not":      return interpret(children[0],env).__not();
    case "true":     return iTerms.valueBoolTrue;
    case "false":    return iTerms.valueBoolFalse;
    case "__char":   return new __char(term);
    case "__int32":  return new __int32(term);
    case "__real64": return new __real64(term);
    case "__string": return new __string(term);
    case "use":      return env.__get(new __quote(children[0]));
    }
    throw new ADLException("in ADL term, unknown constructor '" + iTerms.getTermSymbolString(term) + "'");
  }

  private Value updateEnvironment(__mapChain env, int lhsTerm, int rhsTerm, __mapChain env2, boolean lock) {
    Value ret = iTerms.valueEmpty;
    if (iTerms.getTermArity(lhsTerm) == 1)
      env.__put(new __quote(iTerms.getSubterm(lhsTerm, 0)), ret = interpret(rhsTerm, env), lock);
    else throw new ADLException("lhsList with arity > 1 not yet implemented");
    return ret;
  }
}
