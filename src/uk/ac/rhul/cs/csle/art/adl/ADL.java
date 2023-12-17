package uk.ac.rhul.cs.csle.art.adl;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import uk.ac.rhul.cs.csle.art.cfg.referenceFamily.Reference;
import uk.ac.rhul.cs.csle.art.term.ITerms;
import uk.ac.rhul.cs.csle.art.term.Value;
import uk.ac.rhul.cs.csle.art.term.ValueException;
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

  public ADL(ITerms iTerms) {
    this.iTerms = iTerms;
  }

  public Value interpret(int term, __mapChain env) {
    int children[] = iTerms.getTermChildren(term);
    // System.out.println("ADL interpret " + iTerms.toString(term));
    // Preorder
    switch (iTerms.getTermSymbolString(term)) {
    case "adl":
      return iTerms.valueEmpty; // Empty program
    case "seq":
      interpret(children[0], env);
      return interpret(children[1], env);
    case "appitr": {
      Value ret = iTerms.valueEmpty;
      for (int c : children)
        if (ret instanceof __proc) {
          Value argument = interpret(c, env);

          LinkedHashMap<__quote, Value> paramaters = ((__proc) ret).getParameters();
          Iterator<__quote> parameterIterator = paramaters.keySet().iterator();

          __mapChain callEnv = new __mapChain(env);

          int bodyTerm = ((__proc) ret).getBodyTerm();

          if (argument instanceof __list) {
            Iterator<Value> argumentIterator = ((__list) argument).javaValue().iterator();
            while (argumentIterator.hasNext())
              callEnv.__put(parameterIterator.next(), argumentIterator.next());
          } else
            callEnv.__put(parameterIterator.next(), argument); // singleton case
          // System.out.println("Applying " + iTerms.toString(bodyTerm) + "\nwith environment " + callEnv);
          ret = interpret(bodyTerm, callEnv);
        } else
          ret = interpret(c, env);

      return ret; // Result of expression is the last thing computed
    }
    case "iter": {
      Value ret;
      while (interpret(children[0], env).equals(iTerms.valueBoolTrue))
        ret = interpret(children[1], env);
      ret = interpret(children[2], env);
      return ret;
    }
    case "sel":
      if (interpret(children[0], env).equals(iTerms.valueBoolTrue))
        return interpret(children[1], env);
      else
        return interpret(children[2], env);
    case "skip":
      return iTerms.valueDone;
    case "pair":
      return iTerms.valueBottom;
    case "slice":
      return iTerms.valueBottom;
    case "list": {
      __list ret = new __list();
      for (int c : children)
        ret.__put(interpret(c, env));
      return ret;
    }
    case "lambda":
      LinkedHashMap<__quote, Value> parameters = new LinkedHashMap<>();
      processParameters(children[0], parameters);
      return new __proc(parameters, children[1]);
    case "scope":
      break;
    case "lhsLst": {
      __list ret = new __list();
      for (int c : children)
        ret.__put(new __quote(c));
      return ret;
    }
    case "use":
      return env.__get(new __quote(children[0]));
    case "true":
      return iTerms.valueBoolTrue;
    case "false":
      return iTerms.valueBoolFalse;
    case "__int32":
      return new __int32(term);
    case "__real64":
      return new __real64(term);
    case "__char":
      return new __char(term);
    case "__string":
      return new __string(term);
    default:
      ;
    }

    Value values[] = new Value[children.length];

    for (int i = 0; i < children.length; i++)
      values[i] =

          interpret(children[i], env);

    // Postorder
    // @formatter:off
    switch (iTerms.getTermSymbolString(term)) {
    case "seq":return values[1]; // Result of a sequence is the result of the last element
    case "const":
      if (values[0] instanceof __list && values[1] instanceof __list) {
        LinkedList<Value> lhsList = ((__list) values[0]).javaValue();
        LinkedList<Value> rhsList = ((__list) values[1]).javaValue();

        if (lhsList.size() != rhsList.size()) throw new ADLException(" operator = (constant assignment) - lhs list must be the same size as rhs list");
        Iterator<Value> lhsIterator = lhsList.iterator();
        Iterator<Value> rhsIterator = rhsList.iterator();
        while (lhsIterator.hasNext()) env.__put(lhsIterator.next(), rhsIterator.next(), true);
      }
      else
      env.__put(values[0], values[1], true);
      return values[1];
    case "assign":
      env.__put(values[0], values[1], false);
      return values[1];
    case "or": return values[0].__or(values[1]);
    case "xor": return values[0].__xor(values[1]);
    case "and": return values[0].__and(values[1]);
    case "eq": return values[0].__eq(values[1]);
    case "ne": return values[0].__ne(values[1]);
    case "ge": return values[0].__ge(values[1]);
    case "gt": return values[0].__gt(values[1]);
    case "le": return values[0].__le(values[1]);
    case "lt": return values[0].__lt(values[1]);
    case "cat": return values[0].__cat(values[1]);
    case "lsh": return values[0].__lsh(values[1]);
    case "rsh": return values[0].__rsh(values[1]);
    case "rol": return values[0].__rol(values[1]);
    case "ror": return values[0].__ror(values[1]);
    case "ash": return values[0].__ash(values[1]);
    case "add": return values[0].__add(values[1]);
    case "sub": return values[0].__sub(values[1]);
    case "mul": return values[0].__mul(values[1]);
    case "div": return values[0].__div(values[1]);
    case "mod": return values[0].__div(values[1]);
    case "exp": return values[0].__exp(values[1]);
    case "pos": return values[0]; // Add a pos routine to abstract class
    case "neg": return values[0].__neg();
    case "not": return values[0].__not();
    default:
      Reference.fatal("in ADL term, unknown constructor '" + iTerms.getTermSymbolString(term) + "'");
   // @formatter:on
    }
    return iTerms.valueBottom;
  }

  private void processParameters(int term, LinkedHashMap<__quote, Value> parameters) {
    String str = iTerms.getTermSymbolString(term);
    if (!str.equals("par")) throw new ValueException("lambda parameter - unexpected constructor '" + str + "'");
    int tmp = iTerms.getSubterm(term, 1);
    parameters.put(new __quote(iTerms.getSubterm(term, 0)), iTerms.getTermSymbolString(tmp).equals("skip") ? iTerms.valueEmpty : iTerms.valueFromTerm(tmp));
    if (iTerms.getTermArity(term) == 3) processParameters(iTerms.getSubterm(term, 2), parameters);
  }
}
