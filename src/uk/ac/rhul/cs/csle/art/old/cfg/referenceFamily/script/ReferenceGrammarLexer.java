package uk.ac.rhul.cs.csle.art.old.cfg.referenceFamily.script;

import java.io.FileNotFoundException;

import uk.ac.rhul.cs.csle.art.old.core.ARTUncheckedException;
import uk.ac.rhul.cs.csle.art.old.term.*;
import uk.ac.rhul.cs.csle.art.old.util.bitset.ARTBitSet;
import uk.ac.rhul.cs.csle.art.old.util.text.*;
import uk.ac.rhul.cs.csle.art.old.v3.alg.gll.support.*;
import uk.ac.rhul.cs.csle.art.old.v3.lex.*;
import uk.ac.rhul.cs.csle.art.old.v3.manager.*;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.*;
import uk.ac.rhul.cs.csle.art.old.v3.manager.mode.*;
/*******************************************************************************
*
* ReferenceGrammarLexer.java
*
*******************************************************************************/
@SuppressWarnings("fallthrough") public class ReferenceGrammarLexer extends ARTLexerV3 {
public void artLexicaliseBuiltinInstances() {
  artBuiltin_ID();
  artLexicaliseTest(ReferenceGrammarParser.ARTTB_ID);
  artBuiltin_INTEGER();
  artLexicaliseTest(ReferenceGrammarParser.ARTTB_INTEGER);
  artBuiltin_REAL();
  artLexicaliseTest(ReferenceGrammarParser.ARTTB_REAL);
  artBuiltin_STRING_DQ();
  artLexicaliseTest(ReferenceGrammarParser.ARTTB_STRING_DQ);
  artBuiltin_STRING_PLAIN_SQ();
  artLexicaliseTest(ReferenceGrammarParser.ARTTB_STRING_PLAIN_SQ);
}

public void artLexicalisePreparseWhitespaceInstances() {
  artBuiltin_COMMENT_LINE_C();
  artBuiltin_COMMENT_NEST_ART();
  artBuiltin_SIMPLE_WHITESPACE();
}

};
