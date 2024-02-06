package uk.ac.rhul.cs.csle.art.old.core;

import uk.ac.rhul.cs.csle.art.old.cfg.referenceFamily.grammar.Grammar;
import uk.ac.rhul.cs.csle.art.old.cfg.referenceFamily.lexer.LexerLM;
import uk.ac.rhul.cs.csle.art.old.term.ITerms;
import uk.ac.rhul.cs.csle.art.old.term.ITermsLowLevelAPI;

public class ARTV4 {

  public ARTV4(String specification) {
    ITerms iTerms = new ITermsLowLevelAPI();
    int scriptTerm = iTerms.findTerm(new ARTV4ScriptTerm().scriptTermString);

    Grammar grammar = new Grammar("ART V4 script", iTerms, scriptTerm);

    LexerLM lexer = new LexerLM();
    lexer.lex(specification, grammar.lexicalKindsArray(), grammar.lexicalStringsArray(), grammar.whitespacesArray());
    if (lexer.tokens == null) {
      System.err.println("ART script lexical error");
      System.exit(1);
    }

    // Use the GLLBL (baseline) parser to parse the specification against ARTV4Script.art
    ARTV4ScriptParser parser = new ARTV4ScriptParser();
    parser.inputString = specification;
    parser.accepted = false;
    parser.grammar = grammar;
    parser.input = lexer.tokens;
    parser.positions = lexer.positions;
    parser.parse();

    int derivationTerm = parser.accepted ? parser.derivationTerm() : 0;

    System.out.println("Derivation term:\n" + iTerms.toString(derivationTerm, false, -1, null));

    System.out.println(derivationTerm == scriptTerm ? "ART V4 scriptTerm matches" : "ART V4 scriptTerm differs");
  }
}
