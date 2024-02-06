package uk.ac.rhul.cs.csle.art.old.cfg.rdFamily;

import uk.ac.rhul.cs.csle.art.old.core.OLDDirectives;
import uk.ac.rhul.cs.csle.art.old.util.text.ARTText;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.ARTGrammar;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.element.ARTGrammarElementAttribute;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.element.ARTGrammarElementNonterminal;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.element.ARTGrammarElementTerminal;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.instance.ARTGrammarInstance;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.instance.ARTGrammarInstanceCat;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.instance.ARTGrammarInstanceEpsilon;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.instance.ARTGrammarInstanceNonterminal;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.instance.ARTGrammarInstanceSlot;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.instance.ARTGrammarInstanceTerminal;

public class ARTOSBRDGenerator {
  ARTGrammar artGrammar;
  OLDDirectives artDirectives;

  public ARTOSBRDGenerator(ARTGrammar artGrammar, OLDDirectives artDirectives) {
    this.artGrammar = artGrammar;
    this.artDirectives = artDirectives;
  }

  private String foldKind(ARTGrammarInstance i) {
    switch (i.fold) {
    case EMPTY:
      return "NONE";
    case NONE:
      return "NONE";
    case OVER:
      return "FOLD_OVER";
    case TEAR:
      return "???";
    case UNDER:
      return ("FOLD_UNDER");
    default:
      return ("???");
    }
  }

  public void generateParser(ARTText artText) {
    artText.println("import java.io.FileNotFoundException;\n\nclass ARTGeneratedParser extends uk.ac.rhul.cs.csle.art.cfg.rdFamily.ARTOSBRDBase {\n");

    // Write parse functions
    for (ARTGrammarElementNonterminal n : artGrammar.getNonterminals()) {
      artText.printf("boolean parse_%s() {\n  int rc = cc, ro = co;\n", n.toString());
      int pCount = 0;
      boolean seenEpsilon = false;

      for (ARTGrammarInstanceCat p : n.getProductions()) {
        pCount++;
        artText.printf("\n  /* Nonterminal %s, alternate %d */\n  cc = rc; co = ro; oracleSet(%d);", n, pCount, pCount);

        int braceCount = 0;
        for (ARTGrammarInstance i = p.getChild(); i != null; i = i.getSibling()) {
          if (i instanceof ARTGrammarInstanceSlot) continue;

          if (i instanceof ARTGrammarInstanceNonterminal) {
            artText.printf("\n  if (parse_%s()) {", i.getPayload());
            braceCount++;
          } else if (i instanceof ARTGrammarInstanceTerminal) {
            artText.printf("\n  if (match(\"%s\")) {", ARTText.toLiteralString(((ARTGrammarElementTerminal) i.getPayload()).getId().toString()));
            braceCount++;
          } else if (i instanceof ARTGrammarInstanceEpsilon) {
            artText.printf("\n  /* epsilon */ ");
            seenEpsilon = true;
          } else
            artText.print("!!! Unknown Instance tree node !!!");
        }
        artText.printf("return true; ");

        for (int bc = 0; bc < braceCount; bc++)
          artText.print("}");
        artText.println();

      }
      if (!seenEpsilon) artText.printf("\n  return false;\n");
      artText.printf("}\n\n");
    }

    // Write semantics functions
    for (ARTGrammarElementNonterminal n : artGrammar.getNonterminals()) {
      artText.printf("  class Attribute_%s {", n.toString());
      boolean first = true;
      for (ARTGrammarElementAttribute a : n.getAttributes()) {
        artText.printf("%s\n    %s %s;", (first ? "" : ","), a.getType(), a.getId());
        first = false;
      }

      artText.printf("\n  }\n\n");
      artText.printf("  void semantics_%s(Attribute_%s %s) {\n    switch(oracle[co++]) {", n.toString(), n.toString(), n.toString());
      int pCount = 0;
      for (ARTGrammarInstanceCat p : n.getProductions()) {
        pCount++;
        artText.printf("\n      case %d: {", pCount);

        for (ARTGrammarInstance i = p.getChild(); i != null; i = i.getSibling())
          if (i instanceof ARTGrammarInstanceNonterminal) {
            artText.printf("\n        Attribute_%s %s = new Attribute_%s();", i.getPayload().toString(), i.nameOrInstanceString(), i.getPayload().toString());
          }
        for (ARTGrammarInstance i = p.getChild(); i != null; i = i.getSibling())
          if (i instanceof ARTGrammarInstanceSlot) {
            if (i.getChild() != null) artText.print("  " + i.getChild().getChild());
          } else if (i instanceof ARTGrammarInstanceNonterminal) {
            artText.printf("\n        // Instance %s", i.nameOrInstanceString());

            artText.printf("\n        semantics_%s(%s);", i.getPayload(), i.nameOrInstanceString());
          } else if (i instanceof ARTGrammarInstanceTerminal) {
            artText.printf("\n        match(\"%s\");", ARTText.toLiteralString(((ARTGrammarElementTerminal) i.getPayload()).getId().toString()));
          } else if (i instanceof ARTGrammarInstanceEpsilon)
            artText.printf("\n        /* epsilon */");
          else
            artText.print("!!! Unknown Instance tree node !!!");

        artText.print("\n        break; }\n");
      }
      artText.printf("\n    }\n  }\n\n");
    }

    // Write tree functions
    for (ARTGrammarElementNonterminal n : artGrammar.getNonterminals()) {
      artText.printf("  TreeNode tree_%s() {\n    TreeNode leftNode = null, rightNode = null;\n" + "    switch(oracle[co++]) {", n.toString());
      int pCount = 0;
      for (ARTGrammarInstanceCat p : n.getProductions()) {
        pCount++;
        artText.printf("\n        case %d:", pCount);

        boolean first = true;
        for (ARTGrammarInstance i = p.getChild(); i != null; i = i.getSibling()) {
          if (i instanceof ARTGrammarInstanceSlot) continue;

          artText.print("\n           ");

          if (first) {
            artText.print("leftNode = ");
            first = false;
          }

          if (i instanceof ARTGrammarInstanceNonterminal)
            artText.printf("rightNode = new TreeNode(\"%s\", tree_%s(), rightNode, TreeKind.NONTERMINAL, GIFTKind.%s);", i.getPayload(), i.getPayload(),
                foldKind(i));
          else if (i instanceof ARTGrammarInstanceTerminal)
            artText.printf("rightNode = new TreeNode(\"%s\", null, rightNode, TreeKind.TERMINAL, GIFTKind.%s); match(\"%s\");",
                ARTText.toLiteralString(((ARTGrammarElementTerminal) i.getPayload()).getId().toString()), foldKind(i),
                ARTText.toLiteralString(((ARTGrammarElementTerminal) i.getPayload()).getId().toString()));
          else if (i instanceof ARTGrammarInstanceEpsilon)
            artText.printf("rightNode = new TreeNode(\"#\", null, rightNode, TreeKind.EPSILON, GIFTKind.%s);", foldKind(i));
          else
            artText.print("!!! Unknown Instance tree node !!!");
        }
        artText.print("\n        break;\n");

      }
      artText.printf("\n    }\n  return leftNode;\n" + "}\n\n");
    }

    //@formatter:off
    artText.printf("void parse(String filename) throws FileNotFoundException {\n" +
        "  input = readInput(filename);\n" +
        "\n" +
        "  System.out.printf(\"Input: '%%s'\\n\", input);\n" +
        "  cc = co = 0; builtIn_WHITESPACE();\n" +
        "  if (!(parse_%s() && input.charAt(cc) == '\\0')) {System.out.print(\"Rejected\\n\"); return; }\n" +
        "\n" +
        "  System.out.print(\"Accepted\\n\");\n" +
        "  System.out.print(\"Oracle:\"); for (int i = 0; i < co; i++) System.out.printf(\" %%d\", oracle[i]); System.out.printf(\"\\n\");\n" +
        "  System.out.print(\"\\nSemantics phase\\n\"); cc = 0; co = 0; builtIn_WHITESPACE(); Attribute_%s %s = new Attribute_%s(); semantics_%s(%s);\n" +
        "  System.out.print(\"\\nTree construction phase\\n\"); cc = 0; co = 0; builtIn_WHITESPACE();\n" +
        "  TreeNode dt = new TreeNode(\"%s\", tree_%s(), null, TreeKind.NONTERMINAL, GIFTKind.NONE);\n" +
        "  dt.dot(\"dt.dot\");" +
        "  System.out.print(\"\\nDerivation term\\n\"); dt.printTerm(0);\n" +
        "  System.out.print(\"\\n\\nDerivation tree\\n\"); dt.printTree(0);\n" +
        "  TreeNode cloneRoot = dt.clone(null, null);\n" +
        "    cloneRoot.dot(\"clone.dot\");\n" +
        "\n" +
        "    // System.out.print(\"\\nCloned derivation tree\\n\");\n" +
        "    // cloneRoot.printTree(0);\n" +
        "    TreeNode rdtEpsilon = dt.evaluateTIF(null, null, true);\n" +
        "    rdtEpsilon.dot(\"rdtEpsilon.dot\");\n" +
        "\n" +
        "    //System.out.print(\"\\nRDTEpsilon fold tree\\n\");\n" +
        "    //rdtEpsilon.printTree(0);\n" +
        "    rdtEpsilon.foldunderEpsilon();\n" +
        "    rdtEpsilon.dot(\"rdtEpsilonFold.dot\");\n" +
        "\n" +
        "    //System.out.print(\"\\nAnnotated RDTEpsilon tree\\n\");\n" +
        "    //rdtEpsilon.printTree(0);\n" +
        "    rdt = rdtEpsilon.evaluateTIF(null, null, true);\n" +
        "    rdt.dot(\"rdt.dot\");\n" +
        "\n" +
        "    System.out.print(\"\\nRewritten Derivation term\\n\"); rdt.printTerm(0);\n" +
        "    System.out.print(\"\\n\\nRewritten Derivation Tree\\n\");\n" +
        "    rdt.printTree(0);\n" +
        "    postParse(rdt);\n" +
        "\n" +
        "" +
        "}\n" +
        "\n" +
        "public static void main(String[] args) throws FileNotFoundException{\n" +
        "    if (args.length < 1) {\n" +
        "      System.err.println(\"No input file name supplied\");\n" +
        "      System.exit(1);\n" +
        "    } else\n"+
        "      new ARTGeneratedParser().parse(args[0]);\n" +
        "  }\n", artGrammar.getDefaultStartNonterminal().toString(), artGrammar.getDefaultStartNonterminal().toString(), artGrammar.getDefaultStartNonterminal().toString(), artGrammar.getDefaultStartNonterminal().toString(), artGrammar.getDefaultStartNonterminal().toString(), artGrammar.getDefaultStartNonterminal().toString(), artGrammar.getDefaultStartNonterminal().toString(), artGrammar.getDefaultStartNonterminal().toString());
    artText.print("}\n");
    //@formatter:on
  }
}