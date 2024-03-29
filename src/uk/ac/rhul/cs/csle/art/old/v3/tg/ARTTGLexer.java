package uk.ac.rhul.cs.csle.art.old.v3.tg;

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
* ARTTGLexer.java
*
*******************************************************************************/
@SuppressWarnings("fallthrough") public class ARTTGLexer extends ARTLexerV3 {
public void artLexicaliseBuiltinInstances() {
  artBuiltin_ID();
  artLexicaliseTest(ARTTGParser.ARTTB_ID);
  artBuiltin_STRING_BRACE();
  artLexicaliseTest(ARTTGParser.ARTTB_STRING_BRACE);
  artBuiltin_STRING_DOLLAR();
  artLexicaliseTest(ARTTGParser.ARTTB_STRING_DOLLAR);
}

public void artLexicalisePreparseWhitespaceInstances() {
  artBuiltin_COMMENT_NEST_ART();
  artBuiltin_SIMPLE_WHITESPACE();
}

};
