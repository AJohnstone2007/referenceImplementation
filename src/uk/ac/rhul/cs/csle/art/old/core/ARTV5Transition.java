package uk.ac.rhul.cs.csle.art.old.core;

import uk.ac.rhul.cs.csle.art.old.cfg.referenceFamily.grammar.Grammar;
import uk.ac.rhul.cs.csle.art.old.cfg.referenceFamily.lexer.LexerLM;
import uk.ac.rhul.cs.csle.art.old.term.ITerms;
import uk.ac.rhul.cs.csle.art.old.term.ITermsLowLevelAPI;

public class ARTV5Transition {

  public ARTV5Transition(String specification) {
    ITerms iTerms = new ITermsLowLevelAPI();
    int scriptTerm = iTerms.findTerm(new ARTV5TransitionScriptTerm().scriptTermString);

    Grammar grammar = new Grammar("ART V5Transition script", iTerms, scriptTerm);

    LexerLM lexer = new LexerLM();
    lexer.lex(specification, grammar.lexicalKindsArray(), grammar.lexicalStringsArray(), grammar.whitespacesArray());
    if (lexer.tokens == null) {
      System.err.println("ART script lexical error");
      System.exit(1);
    }

    // Use the GLLBL (baseline) parser to parse the specification against ARTV4Script.art
    ARTV5TransitionScriptParser parser = new ARTV5TransitionScriptParser();
    parser.inputString = specification;
    parser.accepted = false;
    parser.grammar = grammar;
    parser.input = lexer.tokens;
    parser.positions = lexer.positions;
    parser.parse();

    int derivationTerm = parser.accepted ? parser.derivationTerm() : 0;

    System.out.println("Derivation term:\n" + iTerms.toString(derivationTerm, false, -1, null));

    System.out.println(derivationTerm == scriptTerm ? "ART V5Transition scriptTerm matches" : "ART V5Transition scriptTerm differs");
  }
}
