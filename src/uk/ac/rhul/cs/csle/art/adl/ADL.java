package uk.ac.rhul.cs.csle.art.adl;

import uk.ac.rhul.cs.csle.art.cfg.referenceFamily.Reference;
import uk.ac.rhul.cs.csle.art.term.ITerms;
import uk.ac.rhul.cs.csle.art.term.Value;
import uk.ac.rhul.cs.csle.art.term.__char;
import uk.ac.rhul.cs.csle.art.term.__int32;
import uk.ac.rhul.cs.csle.art.term.__mapChain;
import uk.ac.rhul.cs.csle.art.term.__quote;
import uk.ac.rhul.cs.csle.art.term.__real64;
import uk.ac.rhul.cs.csle.art.term.__string;

public class ADL {
  ITerms iTerms;

  public ADL(ITerms iTerms) {
    this.iTerms = iTerms;
  }

  public Value interpret(int term, __mapChain env) {
    // System.out.println("ADL interpret " + iTerms.toString(term));
    int children[] = iTerms.getTermChildren(term);
    // Preorder
    switch (iTerms.getTermSymbolString(term)) {
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
    case "list":
      break;
    case "lambda":
      break;
    case "scope":
      break;
    case "lhsID":
      return new __quote(children[0]);
    case "deref":
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
      values[i] = interpret(children[i], env);

    // Postorder
    // @formatter:off
    switch (iTerms.getTermSymbolString(term)) {
    case "seq":return values[1];
    case "const":
      /* todo add lock capability to mapChain */
      env.__put(values[0], values[1], true);
      return values[1];
    case "assign":
      env.__put(values[0], values[1]);
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
    case "pos": return values[0];
    case "neg": return values[0].__neg();
    case "not": return values[0].__not();
    default:
      Reference.fatal("unknown constructor in adl term " + iTerms.getTermSymbolString(term));
   // @formatter:on
    }
    return iTerms.valueBottom;
  }
}
