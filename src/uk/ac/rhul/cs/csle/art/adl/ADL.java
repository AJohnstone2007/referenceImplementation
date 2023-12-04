package uk.ac.rhul.cs.csle.art.adl;

import uk.ac.rhul.cs.csle.art.cfg.referenceFamily.Reference;
import uk.ac.rhul.cs.csle.art.term.ITerms;

class __value {
  int val;
}

public class ADL {
  ITerms iTerms;

  public ADL(ITerms iTerms) {
    this.iTerms = iTerms;
  }

  public __value interpret(int term) {
    System.out.println("ADL interpret " + iTerms.getTermSymbolString(term));
    __value ret = new __value();
    // Preorder
    switch (iTerms.getTermSymbolString(term)) {
    case "iter":
      break;
    case "sel":
      break;
    case "lambda":
      break;
    case "scope":
      break;
    case "ID":
      break;
    case "true":
      return new __value();
    case "false":
      return new __value();
    case "INTEGER":
      return new __value();
    case "REAL":
      return new __value();
    case "CHARACTER":
      return new __value();
    case "STRING":
      return new __value();
    default:
      ;
    }

    int children[] = iTerms.getTermChildren(term);
    __value values[] = new __value[children.length];

    for (int i = 0; i < children.length; i++)
      values[i] = interpret(children[i]);

    // Postorder
    switch (iTerms.getTermSymbolString(term)) {
    case "seq":
      break;
    case "const":
      break;
    case "assign":
      break;
    case "list":
      break;
    case "iter":
      break;
    case "sel":
      break;
    case "or":
      break;
    case "xor":
      break;
    case "and":
      break;
    case "eq":
      break;
    case "ne":
      break;
    case "ge":
      break;
    case "gt":
      break;
    case "le":
      break;
    case "lt":
      break;
    case "cat":
      break;
    case "lsh":
      break;
    case "rsh":
      break;
    case "rol":
      break;
    case "ror":
      break;
    case "ash":
      break;
    case "add":
      break;
    case "sub":
      break;
    case "mul":
      break;
    case "div":
      break;
    case "mod":
      break;
    case "exp":
      break;
    case "pos":
      break;
    case "neg":
      break;
    case "not":
      break;
    case "scope":
      break;
    default:
      Reference.fatal("unknown constructor in adl term " + iTerms.getTermSymbolString(term));
    }
    return ret;
  }
}
