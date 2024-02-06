package uk.ac.rhul.cs.csle.art.old.cfg.lex;

import java.util.HashSet;
import java.util.Set;

import uk.ac.rhul.cs.csle.art.old.cfg.CFGDFA;
import uk.ac.rhul.cs.csle.art.old.cfg.CFGNFA;
import uk.ac.rhul.cs.csle.art.old.cfg.Lexer;
import uk.ac.rhul.cs.csle.art.old.core.ARTUncheckedException;
import uk.ac.rhul.cs.csle.art.old.core.OLDModule;
import uk.ac.rhul.cs.csle.art.old.term.ITerms;
import uk.ac.rhul.cs.csle.art.old.term.TermTraverserText;
import uk.ac.rhul.cs.csle.art.old.util.graph.ARTAbstractVertex;
import uk.ac.rhul.cs.csle.art.old.util.graph.ARTGraphEdge;
import uk.ac.rhul.cs.csle.art.old.util.graph.ARTGraphVertex;
import uk.ac.rhul.cs.csle.art.old.v3.lex.ARTLexerV3;

/*
 *
 *  Build a DFA based lexer from the paraterminal rules in a module
 *
 */
public class ARTLexDFA extends Lexer {
  final ITerms iTerms;
  OLDModule module;
  final TermTraverserText tt;

  CFGNFA nfa;// , nfaReverse, nfaMinimised;
  CFGDFA dfa;// , dfaReverse, dfaMinimised;
  ARTGraphVertex root, mostRecentVertex;
  Integer stateNumber = 0;
  Set<Integer> emptySet = new HashSet<>();

  public ARTLexDFA(OLDModule module, ITerms iTerms) {
    this.module = module;
    this.iTerms = module.iTerms;
    this.tt = module.tt;
    if (module.getParaterminals().isEmpty()) throw new ARTUncheckedException("!lexDFA called on module with no paraterminals");

    nfa = new CFGNFA("Lexer NFA");
    dfa = new CFGDFA("Lexer DFA");

    root = mostRecentVertex = nfa.addVertex(++stateNumber, "start");
    nfa.getRoots().add(root);
    for (Integer pt : module.getParaterminals().keySet()) {

      if (module.getCfgRules().get(pt) == null)
        System.out.println("Warning from lexer NFA builder: paraterminal " + module.tt.toString(pt) + " has no rules - skipping");
      else {
        System.out.println("Extending NFA using paraterminal " + module.tt.toString(pt));
        ARTGraphVertex finalVertex = buildNFARec(root, pt);
        System.out.println("Marking accepting state " + finalVertex.getKey() + " for paraterminal " + module.tt.toString(pt));
        // nfa.addAccepting(finalVertex, pt);
      }
    }

    nfa.printDot("artLexerNFA.dot");
    nfa.subsetConstruction(dfa);
    // System.err.println("Outer DFA roots: " + dfa.getRoots());
    dfa.updateAcceptingStates(nfa);
    dfa.printDot("artLexerDFA.dot");

    System.out.println("NFA vertex count: " + nfa.vertexCount());
    System.out.println("DFA vertex count: " + dfa.vertexCount());
  }

  private ARTGraphVertex buildNFARec(ARTGraphVertex src, Integer term) {

    ARTGraphVertex ret;

    if (iTerms.hasSymbol(term, "cfgRHS") || iTerms.hasSymbol(term, "cfgAlt")) {
      int[] children = iTerms.getTermChildren(term);

      ARTGraphVertex head = nfa.addVertex(++stateNumber, "alt head");
      nfa.addEdge(src, head);
      ARTGraphVertex tail = nfa.addVertex(++stateNumber, "alt tail");

      for (int i = 0; i < children.length; i++) {
        ARTGraphVertex tmp = nfa.addVertex(++stateNumber, "alt branch");
        nfa.addEdge(head, tmp);
        tmp = buildNFARec(tmp, children[i]);
        nfa.addEdge(tmp, tail);
      }

      return tail;
    }

    if (iTerms.hasSymbol(term, "cfgCat")) {
      ARTGraphVertex base = src;
      int[] children = iTerms.getTermChildren(term);

      for (int i = 0; i < children.length; i++) {
        if (iTerms.hasSymbol(children[i], "cfgSlot")) continue;
        ARTGraphVertex tmp = buildNFARec(base, children[i]);
        if (i < children.length - 2) {
          base = nfa.addVertex(++stateNumber, "cat splice");
          nfa.addEdge(tmp, base);
        } else
          base = tmp;
      }
      return base;
    }

    if (iTerms.hasSymbol(term, "cfgNonterminal")) {
      for (Integer l : module.getCfgRules().get(term)) {
        // System.out.println("Extending NFA using rule " + iTerms.toString(l));
        ARTGraphVertex head = nfa.addVertex(++stateNumber, iTerms.getTermSymbolString(iTerms.getTermChildren(term)[0]) + " head");
        nfa.addEdge(src, head);

        ret = buildNFARec(head, l);
        ARTGraphVertex tail = nfa.addVertex(++stateNumber, "");
        nfa.addEdge(ret, tail);

        return tail;
      }
    }

    if (iTerms.hasSymbol(term, "cfgCharacterTerminal")) {
      String characterTerminal = iTerms.getTermSymbolString(iTerms.getTermChildren(term)[0]);

      nfa.addEdge(src, ret = nfa.addVertex(++stateNumber, "terminal"), characterTerminal);
      return ret;
    }

    if (iTerms.hasSymbol(term, "cfgCharacterRangeTerminal")) {
      String characterTerminal = iTerms.getTermSymbolString(iTerms.getTermChildren(term)[0]) + ".."
          + iTerms.getTermSymbolString(iTerms.getTermChildren(term)[1]);

      nfa.addEdge(src, ret = nfa.addVertex(++stateNumber, "range"), characterTerminal);
      return ret;
    }

    if (iTerms.hasSymbol(term, "cfgEpsilon")) {
      nfa.addEdge(src, ret = nfa.addVertex(++stateNumber, "epsilon"));
      return ret;
    }

    if (iTerms.hasSymbol(term, "cfgSlot")) {
      nfa.addEdge(src, ret = nfa.addVertex(++stateNumber, "slot"));
      return ret;
    }

    if (iTerms.hasSymbol(term, "cfgDoFirst")) {
      return buildNFARec(src, iTerms.getTermChildren(term)[0]);
    }

    if (iTerms.hasSymbol(term, "cfgPositiveClosure") || iTerms.hasSymbol(term, "cfgKleeneClosure")) {
      ARTGraphVertex head = nfa.addVertex(++stateNumber, (iTerms.hasSymbol(term, "cfgPositiveClosure") ? "Positive" : "Kleene") + " head");
      nfa.addEdge(src, head);

      ret = nfa.addVertex(++stateNumber, "closure tail");

      if (iTerms.hasSymbol(term, "cfgKleeneClosure")) nfa.addEdge(head, ret);

      ARTGraphVertex body = nfa.addVertex(++stateNumber, "Closure body");
      nfa.addEdge(head, body);

      ARTGraphVertex loop = buildNFARec(body, iTerms.getTermChildren(term)[0]);

      nfa.addEdge(loop, body);

      nfa.addEdge(loop, ret);

      return ret;
    }

    if (iTerms.hasSymbol(term, "cfgOptional")) {
      ARTGraphVertex head = nfa.addVertex(++stateNumber, "Optional head");
      nfa.addEdge(src, head);

      ret = nfa.addVertex(++stateNumber, "Optional tail");

      nfa.addEdge(head, ret);

      ARTGraphVertex body = nfa.addVertex(++stateNumber, "Optional body");
      nfa.addEdge(head, body);

      ARTGraphVertex loop = buildNFARec(body, iTerms.getTermChildren(term)[0]);

      nfa.addEdge(loop, ret);

      return ret;
    }

    throw new ARTUncheckedException("lexer NFA builder found unexpected term " + iTerms.toString(term));

  }

  public boolean recogniseViaMap(String input) {
    ARTLexerV3 twe = new ARTLexerV3(); // This suprising looking declaration gives us access to a 'blank' TWE set framework

    int rightExtent = 0;
    final ARTLexerV3 tweSet = new ARTLexerV3();

    System.out.println();
    int leftExtent = 0;
    for (ARTAbstractVertex r : dfa.getRoots()) {
      ARTGraphVertex v = (ARTGraphVertex) r;

      step: while (true) {
        // System.out.println(i + ": S" + v.getPayload());
        @SuppressWarnings("unchecked")
        Set<ARTGraphVertex> dfaLabel = (Set<ARTGraphVertex>) v.getKey();
        for (ARTGraphVertex nfaV : dfaLabel)
          if (nfa.getAcceptingStates().containsKey(nfaV)) {
            // for (int t : nfa.getAcceptingStates().get(nfaV)) {
            // System.out.println("Accept " + 0 + ", " + rightExtent + " " + tt.toString(t));
            // twe.tweSetUpdateExactMakeRightSet(t, leftExtent, rightExtent);
            // }
          }

        for (ARTGraphEdge e : v.getOutEdges()) {
          // System.out.println("Checking edge " + e.getSrc().getPayload() + " -> " + e.getDst().getPayload() + " " + e.getPayload());
          String edgeLabel = (String) e.getPayload();
          if (input.charAt(rightExtent) == edgeLabel.charAt(1)) {
            // System.out.println("Transition");
            v = e.getDst();
            if (++rightExtent >= input.length()) {
              System.out.println("End of string");
              break step;
            }
            continue step;
          }
        }
        System.out.println("Edges exhausted without transition");
        break step;
      }
    }

    return true;
  }

}
