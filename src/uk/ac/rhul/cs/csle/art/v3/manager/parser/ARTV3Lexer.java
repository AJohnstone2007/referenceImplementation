package uk.ac.rhul.cs.csle.art.v3.manager.parser;

import java.io.FileNotFoundException;

import uk.ac.rhul.cs.csle.art.core.ARTUncheckedException;
import uk.ac.rhul.cs.csle.art.term.*;
import uk.ac.rhul.cs.csle.art.util.bitset.ARTBitSet;
import uk.ac.rhul.cs.csle.art.util.text.*;
import uk.ac.rhul.cs.csle.art.v3.alg.gll.support.*;
import uk.ac.rhul.cs.csle.art.v3.lex.*;
import uk.ac.rhul.cs.csle.art.v3.manager.*;
import uk.ac.rhul.cs.csle.art.v3.manager.grammar.*;
import uk.ac.rhul.cs.csle.art.v3.manager.mode.*;
/*******************************************************************************
*
* ARTV3Lexer.java
*
*******************************************************************************/
@SuppressWarnings("fallthrough") public class ARTV3Lexer extends ARTLexerV3 {
public void artLexicaliseBuiltinInstances() {
  artBuiltin_CHAR_BQ();
  artLexicaliseTest(ARTV3Parser.ARTTB_CHAR_BQ);
  artBuiltin_ID();
  artLexicaliseTest(ARTV3Parser.ARTTB_ID);
  artBuiltin_INTEGER();
  artLexicaliseTest(ARTV3Parser.ARTTB_INTEGER);
  artBuiltin_REAL();
  artLexicaliseTest(ARTV3Parser.ARTTB_REAL);
  artBuiltin_STRING_BRACE_NEST();
  artLexicaliseTest(ARTV3Parser.ARTTB_STRING_BRACE_NEST);
  artBuiltin_STRING_DOLLAR();
  artLexicaliseTest(ARTV3Parser.ARTTB_STRING_DOLLAR);
  artBuiltin_STRING_DQ();
  artLexicaliseTest(ARTV3Parser.ARTTB_STRING_DQ);
  artBuiltin_STRING_PLAIN_SQ();
  artLexicaliseTest(ARTV3Parser.ARTTB_STRING_PLAIN_SQ);
}

public void artLexicalisePreparseWhitespaceInstances() {
  artBuiltin_COMMENT_LINE_C();
  artBuiltin_COMMENT_NEST_ART();
  artBuiltin_SIMPLE_WHITESPACE();
}

};
