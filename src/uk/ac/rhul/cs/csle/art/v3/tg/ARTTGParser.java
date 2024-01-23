package uk.ac.rhul.cs.csle.art.v3.tg;

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
* ARTTGParser.java
*
*******************************************************************************/
import java.io.File; import java.io.FileNotFoundException; import java.io.PrintWriter;
@SuppressWarnings("fallthrough") public class ARTTGParser extends ARTGLLParserHashPool {
private static boolean[] ARTSet1;
private static boolean[] ARTSet2;
private static boolean[] ARTSet3;
private static boolean[] ARTSet4;
private static boolean[] ARTSet5;
private static boolean[] ARTSet6;
private static boolean[] ARTSet7;
private static boolean[] ARTSet8;
private static boolean[] ARTSet9;
private static boolean[] ARTSet10;
private static boolean[] ARTSet11;
private static boolean[] ARTSet12;
private static boolean[] ARTSet13;
private static boolean[] ARTSet14;
private static boolean[] ARTSet15;
private static boolean[] ARTSet16;
private static boolean[] ARTSet17;
private static boolean[] ARTSet18;
private static boolean[] ARTSet19;
private static boolean[] ARTSet20;
private static boolean[] ARTSet21;
private static boolean[] ARTSet22;
private static boolean[] ARTSet23;
private static boolean[] ARTSet24;
private static boolean[] ARTSet25;
private static boolean[] ARTSet26;
private static boolean[] ARTSet27;
private static boolean[] ARTSet28;
private static boolean[] ARTSet29;

/* Start of artLabel enumeration */
public static final int ARTX_EOS = 0;
public static final int ARTTB_ID = 1;
public static final int ARTTB_STRING_BRACE = 2;
public static final int ARTTB_STRING_DOLLAR = 3;
public static final int ARTTS_accept = 4;
public static final int ARTTS_reject = 5;
public static final int ARTX_EPSILON = 6;
public static final int ARTL_ART_ID = 7;
public static final int ARTL_ART_acceptOpt = 8;
public static final int ARTL_ART_optionOpt = 9;
public static final int ARTL_ART_rejectOpt = 10;
public static final int ARTL_ART_stringBrace = 11;
public static final int ARTL_ART_stringDollar = 12;
public static final int ARTL_ART_strings = 13;
public static final int ARTL_ART_test = 14;
public static final int ARTL_ART_tests = 15;
public static final int ARTL_ART_text = 16;
public static final int ARTL_ART_ID_33 = 17;
public static final int ARTL_ART_ID_34 = 18;
public static final int ARTL_ART_ID_35 = 19;
public static final int ARTL_ART_ID_36 = 20;
public static final int ARTL_ART_acceptOpt_55 = 21;
public static final int ARTL_ART_acceptOpt_56 = 22;
public static final int ARTL_ART_acceptOpt_57 = 23;
public static final int ARTL_ART_acceptOpt_58 = 24;
public static final int ARTL_ART_acceptOpt_61 = 25;
public static final int ARTL_ART_acceptOpt_62 = 26;
public static final int ARTL_ART_acceptOpt_63 = 27;
public static final int ARTL_ART_acceptOpt_64 = 28;
public static final int ARTL_ART_acceptOpt_65 = 29;
public static final int ARTL_ART_acceptOpt_66 = 30;
public static final int ARTL_ART_optionOpt_39 = 31;
public static final int ARTL_ART_optionOpt_40 = 32;
public static final int ARTL_ART_optionOpt_41 = 33;
public static final int ARTL_ART_optionOpt_42 = 34;
public static final int ARTL_ART_optionOpt_45 = 35;
public static final int ARTL_ART_optionOpt_46 = 36;
public static final int ARTL_ART_optionOpt_47 = 37;
public static final int ARTL_ART_optionOpt_48 = 38;
public static final int ARTL_ART_rejectOpt_67 = 39;
public static final int ARTL_ART_rejectOpt_68 = 40;
public static final int ARTL_ART_rejectOpt_69 = 41;
public static final int ARTL_ART_rejectOpt_70 = 42;
public static final int ARTL_ART_rejectOpt_73 = 43;
public static final int ARTL_ART_rejectOpt_74 = 44;
public static final int ARTL_ART_rejectOpt_75 = 45;
public static final int ARTL_ART_rejectOpt_76 = 46;
public static final int ARTL_ART_rejectOpt_77 = 47;
public static final int ARTL_ART_rejectOpt_78 = 48;
public static final int ARTL_ART_stringBrace_79 = 49;
public static final int ARTL_ART_stringBrace_80 = 50;
public static final int ARTL_ART_stringBrace_81 = 51;
public static final int ARTL_ART_stringBrace_82 = 52;
public static final int ARTL_ART_stringDollar_49 = 53;
public static final int ARTL_ART_stringDollar_50 = 54;
public static final int ARTL_ART_stringDollar_51 = 55;
public static final int ARTL_ART_stringDollar_52 = 56;
public static final int ARTL_ART_strings_85 = 57;
public static final int ARTL_ART_strings_86 = 58;
public static final int ARTL_ART_strings_87 = 59;
public static final int ARTL_ART_strings_88 = 60;
public static final int ARTL_ART_strings_89 = 61;
public static final int ARTL_ART_strings_90 = 62;
public static final int ARTL_ART_strings_91 = 63;
public static final int ARTL_ART_strings_92 = 64;
public static final int ARTL_ART_strings_95 = 65;
public static final int ARTL_ART_strings_96 = 66;
public static final int ARTL_ART_strings_97 = 67;
public static final int ARTL_ART_strings_98 = 68;
public static final int ARTL_ART_strings_99 = 69;
public static final int ARTL_ART_strings_100 = 70;
public static final int ARTL_ART_test_17 = 71;
public static final int ARTL_ART_test_18 = 72;
public static final int ARTL_ART_test_19 = 73;
public static final int ARTL_ART_test_20 = 74;
public static final int ARTL_ART_test_21 = 75;
public static final int ARTL_ART_test_22 = 76;
public static final int ARTL_ART_test_23 = 77;
public static final int ARTL_ART_test_24 = 78;
public static final int ARTL_ART_test_27 = 79;
public static final int ARTL_ART_test_28 = 80;
public static final int ARTL_ART_test_31 = 81;
public static final int ARTL_ART_test_32 = 82;
public static final int ARTL_ART_tests_7 = 83;
public static final int ARTL_ART_tests_8 = 84;
public static final int ARTL_ART_tests_9 = 85;
public static final int ARTL_ART_tests_10 = 86;
public static final int ARTL_ART_tests_11 = 87;
public static final int ARTL_ART_tests_12 = 88;
public static final int ARTL_ART_tests_13 = 89;
public static final int ARTL_ART_tests_14 = 90;
public static final int ARTL_ART_tests_15 = 91;
public static final int ARTL_ART_tests_16 = 92;
public static final int ARTL_ART_text_1 = 93;
public static final int ARTL_ART_text_2 = 94;
public static final int ARTL_ART_text_5 = 95;
public static final int ARTL_ART_text_6 = 96;
public static final int ARTX_DESPATCH = 97;
public static final int ARTX_DUMMY = 98;
public static final int ARTX_LABEL_EXTENT = 99;
/* End of artLabel enumeration */

/* Start of artName enumeration */
public static final int ARTNAME_NONE = 0;
public static final int ARTNAME_EXTENT = 1;
/* End of artName enumeration */
public void ARTPF_ART_ID() {
  switch (artCurrentRestartLabel) {
      /* Nonterminal ID production descriptor loads*/
    case ARTL_ART_ID: 
      if (ARTSet2[artInputPairBuffer[artCurrentInputPairReference]]) 
        artFindDescriptor(ARTL_ART_ID_34, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      /* Nonterminal ID: match production*/
    case ARTL_ART_ID_34: 
      /* Cat/unary template start */
      /* Terminal template start */
      artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference + 1]][artInputPairBuffer[artCurrentInputPairReference]];
      artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTB_ID, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
      artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
      artCurrentSPPFNode = artFindSPPF(ARTL_ART_ID_36, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
      /* Terminal template end */
      /* Cat/unary template end */
      if (!ARTSet3[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
  }
}

public void ARTPF_ART_acceptOpt() {
  switch (artCurrentRestartLabel) {
      /* Nonterminal acceptOpt production descriptor loads*/
    case ARTL_ART_acceptOpt: 
      if (ARTSet7[artInputPairBuffer[artCurrentInputPairReference]]) 
        artFindDescriptor(ARTL_ART_acceptOpt_56, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
      if (ARTSet6[artInputPairBuffer[artCurrentInputPairReference]]) 
        artFindDescriptor(ARTL_ART_acceptOpt_64, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      /* Nonterminal acceptOpt: match production*/
    case ARTL_ART_acceptOpt_56: 
      /* Cat/unary template start */
      /* Terminal template start */
      artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference + 1]][artInputPairBuffer[artCurrentInputPairReference]];
      artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTS_accept, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
      artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
      artCurrentSPPFNode = artFindSPPF(ARTL_ART_acceptOpt_58, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
      /* Terminal template end */
      if (!ARTSet9[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      /* Nonterminal template start */
      artCurrentGSSNode = artFindGSS(ARTL_ART_acceptOpt_62, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTL_ART_strings; return; }
    case ARTL_ART_acceptOpt_62: 
      /* Nonterminal template end */
      /* Cat/unary template end */
      if (!ARTSet6[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
      /* Nonterminal acceptOpt: match production*/
    case ARTL_ART_acceptOpt_64: 
      /* Cat/unary template start */
      /* Epsilon template start */
      artCurrentSPPFRightChildNode = artFindSPPFEpsilon(artCurrentInputPairIndex);
      artCurrentSPPFNode = artFindSPPF(ARTL_ART_acceptOpt_66, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
      /* Epsilon template end */
      /* Cat/unary template end */
      if (!ARTSet6[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
  }
}

public void ARTPF_ART_optionOpt() {
  switch (artCurrentRestartLabel) {
      /* Nonterminal optionOpt production descriptor loads*/
    case ARTL_ART_optionOpt: 
      if (ARTSet12[artInputPairBuffer[artCurrentInputPairReference]]) 
        artFindDescriptor(ARTL_ART_optionOpt_40, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
      if (ARTSet11[artInputPairBuffer[artCurrentInputPairReference]]) 
        artFindDescriptor(ARTL_ART_optionOpt_46, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      /* Nonterminal optionOpt: match production*/
    case ARTL_ART_optionOpt_40: 
      /* Cat/unary template start */
      /* Nonterminal template start */
      artCurrentGSSNode = artFindGSS(ARTL_ART_optionOpt_42, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTL_ART_stringBrace; return; }
    case ARTL_ART_optionOpt_42: 
      /* Nonterminal template end */
      /* Cat/unary template end */
      if (!ARTSet11[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
      /* Nonterminal optionOpt: match production*/
    case ARTL_ART_optionOpt_46: 
      /* Cat/unary template start */
      /* Epsilon template start */
      artCurrentSPPFRightChildNode = artFindSPPFEpsilon(artCurrentInputPairIndex);
      artCurrentSPPFNode = artFindSPPF(ARTL_ART_optionOpt_48, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
      /* Epsilon template end */
      /* Cat/unary template end */
      if (!ARTSet11[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
  }
}

public void ARTPF_ART_rejectOpt() {
  switch (artCurrentRestartLabel) {
      /* Nonterminal rejectOpt production descriptor loads*/
    case ARTL_ART_rejectOpt: 
      if (ARTSet15[artInputPairBuffer[artCurrentInputPairReference]]) 
        artFindDescriptor(ARTL_ART_rejectOpt_68, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
      if (ARTSet14[artInputPairBuffer[artCurrentInputPairReference]]) 
        artFindDescriptor(ARTL_ART_rejectOpt_76, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      /* Nonterminal rejectOpt: match production*/
    case ARTL_ART_rejectOpt_68: 
      /* Cat/unary template start */
      /* Terminal template start */
      artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference + 1]][artInputPairBuffer[artCurrentInputPairReference]];
      artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTS_reject, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
      artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
      artCurrentSPPFNode = artFindSPPF(ARTL_ART_rejectOpt_70, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
      /* Terminal template end */
      if (!ARTSet16[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      /* Nonterminal template start */
      artCurrentGSSNode = artFindGSS(ARTL_ART_rejectOpt_74, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTL_ART_strings; return; }
    case ARTL_ART_rejectOpt_74: 
      /* Nonterminal template end */
      /* Cat/unary template end */
      if (!ARTSet14[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
      /* Nonterminal rejectOpt: match production*/
    case ARTL_ART_rejectOpt_76: 
      /* Cat/unary template start */
      /* Epsilon template start */
      artCurrentSPPFRightChildNode = artFindSPPFEpsilon(artCurrentInputPairIndex);
      artCurrentSPPFNode = artFindSPPF(ARTL_ART_rejectOpt_78, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
      /* Epsilon template end */
      /* Cat/unary template end */
      if (!ARTSet14[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
  }
}

public void ARTPF_ART_stringBrace() {
  switch (artCurrentRestartLabel) {
      /* Nonterminal stringBrace production descriptor loads*/
    case ARTL_ART_stringBrace: 
      if (ARTSet17[artInputPairBuffer[artCurrentInputPairReference]]) 
        artFindDescriptor(ARTL_ART_stringBrace_80, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      /* Nonterminal stringBrace: match production*/
    case ARTL_ART_stringBrace_80: 
      /* Cat/unary template start */
      /* Terminal template start */
      artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference + 1]][artInputPairBuffer[artCurrentInputPairReference]];
      artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTB_STRING_BRACE, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
      artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
      artCurrentSPPFNode = artFindSPPF(ARTL_ART_stringBrace_82, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
      /* Terminal template end */
      /* Cat/unary template end */
      if (!ARTSet11[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
  }
}

public void ARTPF_ART_stringDollar() {
  switch (artCurrentRestartLabel) {
      /* Nonterminal stringDollar production descriptor loads*/
    case ARTL_ART_stringDollar: 
      if (ARTSet18[artInputPairBuffer[artCurrentInputPairReference]]) 
        artFindDescriptor(ARTL_ART_stringDollar_50, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      /* Nonterminal stringDollar: match production*/
    case ARTL_ART_stringDollar_50: 
      /* Cat/unary template start */
      /* Terminal template start */
      artCurrentInputPairReference = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference + 1]][artInputPairBuffer[artCurrentInputPairReference]];
      artCurrentSPPFRightChildNode = artFindSPPFTerminal(ARTTB_STRING_DOLLAR, artCurrentInputPairIndex, artInputPairBuffer[artCurrentInputPairReference + 1]);
      artCurrentInputPairIndex = artInputPairBuffer[artCurrentInputPairReference + 1];
      artCurrentSPPFNode = artFindSPPF(ARTL_ART_stringDollar_52, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
      /* Terminal template end */
      /* Cat/unary template end */
      if (!ARTSet19[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
  }
}

public void ARTPF_ART_strings() {
  switch (artCurrentRestartLabel) {
      /* Nonterminal strings production descriptor loads*/
    case ARTL_ART_strings: 
      if (ARTSet6[artInputPairBuffer[artCurrentInputPairReference]]) 
        artFindDescriptor(ARTL_ART_strings_86, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
      if (ARTSet11[artInputPairBuffer[artCurrentInputPairReference]]) 
        artFindDescriptor(ARTL_ART_strings_90, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
      if (ARTSet11[artInputPairBuffer[artCurrentInputPairReference]]) 
        artFindDescriptor(ARTL_ART_strings_98, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      /* Nonterminal strings: match production*/
    case ARTL_ART_strings_86: 
      /* Cat/unary template start */
      /* Epsilon template start */
      artCurrentSPPFRightChildNode = artFindSPPFEpsilon(artCurrentInputPairIndex);
      artCurrentSPPFNode = artFindSPPF(ARTL_ART_strings_88, artCurrentSPPFNode, artCurrentSPPFRightChildNode);
      /* Epsilon template end */
      /* Cat/unary template end */
      if (!ARTSet6[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
      /* Nonterminal strings: match production*/
    case ARTL_ART_strings_90: 
      /* Cat/unary template start */
      /* Nonterminal template start */
      artCurrentGSSNode = artFindGSS(ARTL_ART_strings_92, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTL_ART_stringDollar; return; }
    case ARTL_ART_strings_92: 
      /* Nonterminal template end */
      if (!ARTSet9[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      /* Nonterminal template start */
      artCurrentGSSNode = artFindGSS(ARTL_ART_strings_96, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTL_ART_strings; return; }
    case ARTL_ART_strings_96: 
      /* Nonterminal template end */
      /* Cat/unary template end */
      if (!ARTSet6[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
      /* Nonterminal strings: match production*/
    case ARTL_ART_strings_98: 
      /* Cat/unary template start */
      /* Nonterminal template start */
      artCurrentGSSNode = artFindGSS(ARTL_ART_strings_100, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTL_ART_stringDollar; return; }
    case ARTL_ART_strings_100: 
      /* Nonterminal template end */
      /* Cat/unary template end */
      if (!ARTSet6[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
  }
}

public void ARTPF_ART_test() {
  switch (artCurrentRestartLabel) {
      /* Nonterminal test production descriptor loads*/
    case ARTL_ART_test: 
      if (ARTSet21[artInputPairBuffer[artCurrentInputPairReference]]) 
        artFindDescriptor(ARTL_ART_test_18, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      /* Nonterminal test: match production*/
    case ARTL_ART_test_18: 
      /* Cat/unary template start */
      /* Nonterminal template start */
      artCurrentGSSNode = artFindGSS(ARTL_ART_test_20, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTL_ART_ID; return; }
    case ARTL_ART_test_20: 
      /* Nonterminal template end */
      if (!ARTSet3[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      /* Nonterminal template start */
      artCurrentGSSNode = artFindGSS(ARTL_ART_test_22, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTL_ART_optionOpt; return; }
    case ARTL_ART_test_22: 
      /* Nonterminal template end */
      if (!ARTSet11[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      /* Nonterminal template start */
      artCurrentGSSNode = artFindGSS(ARTL_ART_test_24, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTL_ART_stringDollar; return; }
    case ARTL_ART_test_24: 
      /* Nonterminal template end */
      if (!ARTSet23[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      /* Nonterminal template start */
      artCurrentGSSNode = artFindGSS(ARTL_ART_test_28, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTL_ART_acceptOpt; return; }
    case ARTL_ART_test_28: 
      /* Nonterminal template end */
      if (!ARTSet6[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      /* Nonterminal template start */
      artCurrentGSSNode = artFindGSS(ARTL_ART_test_32, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTL_ART_rejectOpt; return; }
    case ARTL_ART_test_32: 
      /* Nonterminal template end */
      /* Cat/unary template end */
      if (!ARTSet14[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
  }
}

public void ARTPF_ART_tests() {
  switch (artCurrentRestartLabel) {
      /* Nonterminal tests production descriptor loads*/
    case ARTL_ART_tests: 
      if (ARTSet27[artInputPairBuffer[artCurrentInputPairReference]]) 
        artFindDescriptor(ARTL_ART_tests_8, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
      if (ARTSet27[artInputPairBuffer[artCurrentInputPairReference]]) 
        artFindDescriptor(ARTL_ART_tests_14, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      /* Nonterminal tests: match production*/
    case ARTL_ART_tests_8: 
      /* Cat/unary template start */
      /* Nonterminal template start */
      artCurrentGSSNode = artFindGSS(ARTL_ART_tests_10, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTL_ART_test; return; }
    case ARTL_ART_tests_10: 
      /* Nonterminal template end */
      if (!ARTSet29[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      /* Nonterminal template start */
      artCurrentGSSNode = artFindGSS(ARTL_ART_tests_12, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTL_ART_tests; return; }
    case ARTL_ART_tests_12: 
      /* Nonterminal template end */
      /* Cat/unary template end */
      if (!ARTSet28[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
      /* Nonterminal tests: match production*/
    case ARTL_ART_tests_14: 
      /* Cat/unary template start */
      /* Nonterminal template start */
      artCurrentGSSNode = artFindGSS(ARTL_ART_tests_16, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTL_ART_test; return; }
    case ARTL_ART_tests_16: 
      /* Nonterminal template end */
      /* Cat/unary template end */
      if (!ARTSet28[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
  }
}

public void ARTPF_ART_text() {
  switch (artCurrentRestartLabel) {
      /* Nonterminal text production descriptor loads*/
    case ARTL_ART_text: 
      if (ARTSet29[artInputPairBuffer[artCurrentInputPairReference]]) 
        artFindDescriptor(ARTL_ART_text_2, artCurrentGSSNode, artCurrentInputPairIndex, artDummySPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      /* Nonterminal text: match production*/
    case ARTL_ART_text_2: 
      /* Cat/unary template start */
      /* Nonterminal template start */
      artCurrentGSSNode = artFindGSS(ARTL_ART_text_6, artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTL_ART_tests; return; }
    case ARTL_ART_text_6: 
      /* Nonterminal template end */
      /* Cat/unary template end */
      if (!ARTSet28[artInputPairBuffer[artCurrentInputPairReference]]) { artCurrentRestartLabel = ARTX_DESPATCH; return; }
      artPop(artCurrentGSSNode, artCurrentInputPairIndex, artCurrentSPPFNode);
      { artCurrentRestartLabel = ARTX_DESPATCH /* Top level pop */; return; }
  }
}

public void artParseBody(int artStartLabel) {
  artSetupCompleteTime = artReadClock();
  artSpecificationName = "ARTTGParser.art";
  artStartSymbolLabel = artStartLabel;
  artIsInLanguage = false;
  artTokenExtent = 17;
  artLexicaliseForV3GLL(artInputString, null);
  artLexCompleteTime = artReadClock();
  artDummySPPFNode = artFindSPPFInitial(ARTL_DUMMY, 0, 0);
  artCurrentSPPFNode = artDummySPPFNode;
  artRootGSSNode = artFindGSS(ARTL_EOS, 0, 0, 0);
  artCurrentGSSNode = artRootGSSNode;
  artCurrentRestartLabel = artStartSymbolLabel;
  artCurrentInputPairIndex = 0;
  artCurrentInputPairReference = 0;
  while (true)
    switch (artlhsL[artCurrentRestartLabel]) {
      case ARTL_ART_ID: 
        ARTPF_ART_ID();
        break;
      case ARTL_ART_acceptOpt: 
        ARTPF_ART_acceptOpt();
        break;
      case ARTL_ART_optionOpt: 
        ARTPF_ART_optionOpt();
        break;
      case ARTL_ART_rejectOpt: 
        ARTPF_ART_rejectOpt();
        break;
      case ARTL_ART_stringBrace: 
        ARTPF_ART_stringBrace();
        break;
      case ARTL_ART_stringDollar: 
        ARTPF_ART_stringDollar();
        break;
      case ARTL_ART_strings: 
        ARTPF_ART_strings();
        break;
      case ARTL_ART_test: 
        ARTPF_ART_test();
        break;
      case ARTL_ART_tests: 
        ARTPF_ART_tests();
        break;
      case ARTL_ART_text: 
        ARTPF_ART_text();
        break;
      case ARTX_DESPATCH: 
        if (artNoDescriptors()) { 
          artCheckAcceptance();
          artParseCompleteTime = artReadClock();
          artParseEndMemory = artMemoryUsed();
          return;
         }
        artUnloadDescriptor();
    }
}

public void ARTSet1initialise() {
  ARTSet1 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet1, 0, artSetExtent, false);
}

public void ARTSet11initialise() {
  ARTSet11 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet11, 0, artSetExtent, false);
  ARTSet11[ARTTB_STRING_DOLLAR] = true;
  ARTSet11[ARTL_ART_stringDollar] = true;
}

public void ARTSet28initialise() {
  ARTSet28 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet28, 0, artSetExtent, false);
  ARTSet28[ARTX_EOS] = true;
}

public void ARTSet21initialise() {
  ARTSet21 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet21, 0, artSetExtent, false);
  ARTSet21[ARTTB_ID] = true;
  ARTSet21[ARTL_ART_ID] = true;
}

public void ARTSet14initialise() {
  ARTSet14 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet14, 0, artSetExtent, false);
  ARTSet14[ARTX_EOS] = true;
  ARTSet14[ARTTB_ID] = true;
  ARTSet14[ARTL_ART_ID] = true;
  ARTSet14[ARTL_ART_test] = true;
  ARTSet14[ARTL_ART_tests] = true;
}

public void ARTSet29initialise() {
  ARTSet29 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet29, 0, artSetExtent, false);
  ARTSet29[ARTTB_ID] = true;
  ARTSet29[ARTL_ART_ID] = true;
  ARTSet29[ARTL_ART_test] = true;
  ARTSet29[ARTL_ART_tests] = true;
}

public void ARTSet27initialise() {
  ARTSet27 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet27, 0, artSetExtent, false);
  ARTSet27[ARTTB_ID] = true;
  ARTSet27[ARTL_ART_ID] = true;
  ARTSet27[ARTL_ART_test] = true;
}

public void ARTSet3initialise() {
  ARTSet3 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet3, 0, artSetExtent, false);
  ARTSet3[ARTTB_STRING_BRACE] = true;
  ARTSet3[ARTTB_STRING_DOLLAR] = true;
  ARTSet3[ARTL_ART_optionOpt] = true;
  ARTSet3[ARTL_ART_stringBrace] = true;
  ARTSet3[ARTL_ART_stringDollar] = true;
}

public void ARTSet10initialise() {
  ARTSet10 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet10, 0, artSetExtent, false);
  ARTSet10[ARTTB_STRING_BRACE] = true;
  ARTSet10[ARTL_ART_stringBrace] = true;
}

public void ARTSet15initialise() {
  ARTSet15 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet15, 0, artSetExtent, false);
  ARTSet15[ARTTS_reject] = true;
}

public void ARTSet18initialise() {
  ARTSet18 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet18, 0, artSetExtent, false);
  ARTSet18[ARTTB_STRING_DOLLAR] = true;
}

public void ARTSet2initialise() {
  ARTSet2 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet2, 0, artSetExtent, false);
  ARTSet2[ARTTB_ID] = true;
}

public void ARTSet5initialise() {
  ARTSet5 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet5, 0, artSetExtent, false);
  ARTSet5[ARTTS_accept] = true;
}

public void ARTSet13initialise() {
  ARTSet13 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet13, 0, artSetExtent, false);
  ARTSet13[ARTTS_reject] = true;
}

public void ARTSet22initialise() {
  ARTSet22 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet22, 0, artSetExtent, false);
  ARTSet22[ARTTB_STRING_BRACE] = true;
  ARTSet22[ARTL_ART_optionOpt] = true;
  ARTSet22[ARTL_ART_stringBrace] = true;
}

public void ARTSet12initialise() {
  ARTSet12 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet12, 0, artSetExtent, false);
  ARTSet12[ARTTB_STRING_BRACE] = true;
  ARTSet12[ARTL_ART_stringBrace] = true;
}

public void ARTSet23initialise() {
  ARTSet23 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet23, 0, artSetExtent, false);
  ARTSet23[ARTX_EOS] = true;
  ARTSet23[ARTTB_ID] = true;
  ARTSet23[ARTTS_accept] = true;
  ARTSet23[ARTTS_reject] = true;
  ARTSet23[ARTL_ART_ID] = true;
  ARTSet23[ARTL_ART_acceptOpt] = true;
  ARTSet23[ARTL_ART_rejectOpt] = true;
  ARTSet23[ARTL_ART_test] = true;
  ARTSet23[ARTL_ART_tests] = true;
}

public void ARTSet8initialise() {
  ARTSet8 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet8, 0, artSetExtent, false);
  ARTSet8[ARTTB_STRING_DOLLAR] = true;
  ARTSet8[ARTL_ART_stringDollar] = true;
  ARTSet8[ARTL_ART_strings] = true;
}

public void ARTSet24initialise() {
  ARTSet24 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet24, 0, artSetExtent, false);
  ARTSet24[ARTTS_accept] = true;
  ARTSet24[ARTTS_reject] = true;
  ARTSet24[ARTL_ART_acceptOpt] = true;
  ARTSet24[ARTL_ART_rejectOpt] = true;
}

public void ARTSet26initialise() {
  ARTSet26 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet26, 0, artSetExtent, false);
  ARTSet26[ARTTS_reject] = true;
  ARTSet26[ARTL_ART_rejectOpt] = true;
}

public void ARTSet7initialise() {
  ARTSet7 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet7, 0, artSetExtent, false);
  ARTSet7[ARTTS_accept] = true;
}

public void ARTSet17initialise() {
  ARTSet17 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet17, 0, artSetExtent, false);
  ARTSet17[ARTTB_STRING_BRACE] = true;
}

public void ARTSet16initialise() {
  ARTSet16 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet16, 0, artSetExtent, false);
  ARTSet16[ARTX_EOS] = true;
  ARTSet16[ARTTB_ID] = true;
  ARTSet16[ARTTB_STRING_DOLLAR] = true;
  ARTSet16[ARTL_ART_ID] = true;
  ARTSet16[ARTL_ART_stringDollar] = true;
  ARTSet16[ARTL_ART_strings] = true;
  ARTSet16[ARTL_ART_test] = true;
  ARTSet16[ARTL_ART_tests] = true;
}

public void ARTSet6initialise() {
  ARTSet6 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet6, 0, artSetExtent, false);
  ARTSet6[ARTX_EOS] = true;
  ARTSet6[ARTTB_ID] = true;
  ARTSet6[ARTTS_reject] = true;
  ARTSet6[ARTL_ART_ID] = true;
  ARTSet6[ARTL_ART_rejectOpt] = true;
  ARTSet6[ARTL_ART_test] = true;
  ARTSet6[ARTL_ART_tests] = true;
}

public void ARTSet25initialise() {
  ARTSet25 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet25, 0, artSetExtent, false);
  ARTSet25[ARTTS_accept] = true;
  ARTSet25[ARTL_ART_acceptOpt] = true;
}

public void ARTSet19initialise() {
  ARTSet19 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet19, 0, artSetExtent, false);
  ARTSet19[ARTX_EOS] = true;
  ARTSet19[ARTTB_ID] = true;
  ARTSet19[ARTTB_STRING_DOLLAR] = true;
  ARTSet19[ARTTS_accept] = true;
  ARTSet19[ARTTS_reject] = true;
  ARTSet19[ARTL_ART_ID] = true;
  ARTSet19[ARTL_ART_acceptOpt] = true;
  ARTSet19[ARTL_ART_rejectOpt] = true;
  ARTSet19[ARTL_ART_stringDollar] = true;
  ARTSet19[ARTL_ART_strings] = true;
  ARTSet19[ARTL_ART_test] = true;
  ARTSet19[ARTL_ART_tests] = true;
}

public void ARTSet4initialise() {
  ARTSet4 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet4, 0, artSetExtent, false);
}

public void ARTSet20initialise() {
  ARTSet20 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet20, 0, artSetExtent, false);
  ARTSet20[ARTTB_STRING_DOLLAR] = true;
  ARTSet20[ARTL_ART_stringDollar] = true;
}

public void ARTSet9initialise() {
  ARTSet9 = new boolean[artSetExtent];
  artInitialiseBooleanArray(ARTSet9, 0, artSetExtent, false);
  ARTSet9[ARTX_EOS] = true;
  ARTSet9[ARTTB_ID] = true;
  ARTSet9[ARTTB_STRING_DOLLAR] = true;
  ARTSet9[ARTTS_reject] = true;
  ARTSet9[ARTL_ART_ID] = true;
  ARTSet9[ARTL_ART_rejectOpt] = true;
  ARTSet9[ARTL_ART_stringDollar] = true;
  ARTSet9[ARTL_ART_strings] = true;
  ARTSet9[ARTL_ART_test] = true;
  ARTSet9[ARTL_ART_tests] = true;
}

public void artSetInitialise() {
  ARTSet1initialise();
  ARTSet11initialise();
  ARTSet28initialise();
  ARTSet21initialise();
  ARTSet14initialise();
  ARTSet29initialise();
  ARTSet27initialise();
  ARTSet3initialise();
  ARTSet10initialise();
  ARTSet15initialise();
  ARTSet18initialise();
  ARTSet2initialise();
  ARTSet5initialise();
  ARTSet13initialise();
  ARTSet22initialise();
  ARTSet12initialise();
  ARTSet23initialise();
  ARTSet8initialise();
  ARTSet24initialise();
  ARTSet26initialise();
  ARTSet7initialise();
  ARTSet17initialise();
  ARTSet16initialise();
  ARTSet6initialise();
  ARTSet25initialise();
  ARTSet19initialise();
  ARTSet4initialise();
  ARTSet20initialise();
  ARTSet9initialise();
}

public void artTableInitialiser_ART_ID() {
  artLabelInternalStrings[ARTL_ART_ID] = "ID";
  artLabelStrings[ARTL_ART_ID] = "ID";
  artKindOfs[ARTL_ART_ID] = ARTK_NONTERMINAL;
  artLabelInternalStrings[ARTL_ART_ID_34] = "ID ::= . &ID  ";
  artLabelStrings[ARTL_ART_ID_34] = "";
  artlhsL[ARTL_ART_ID_34] = ARTL_ART_ID;
  artKindOfs[ARTL_ART_ID_34] = ARTK_INTERMEDIATE;
  artPopD[ARTL_ART_ID_34] = true;
  artLabelInternalStrings[ARTL_ART_ID_35] = "ID ::= &ID  ";
  artLabelStrings[ARTL_ART_ID_35] = "";
  artlhsL[ARTL_ART_ID_35] = ARTL_ART_ID;
  artPopD[ARTL_ART_ID_35] = true;
  artLabelInternalStrings[ARTL_ART_ID_36] = "ID ::= &ID  .";
  artLabelStrings[ARTL_ART_ID_36] = "";
  artlhsL[ARTL_ART_ID_36] = ARTL_ART_ID;
  artKindOfs[ARTL_ART_ID_36] = ARTK_INTERMEDIATE;
  arteoRL[ARTL_ART_ID_36] = true;
  arteoR_pL[ARTL_ART_ID_36] = true;
  artPopD[ARTL_ART_ID_36] = true;
}

public void artTableInitialiser_ART_acceptOpt() {
  artLabelInternalStrings[ARTL_ART_acceptOpt] = "acceptOpt";
  artLabelStrings[ARTL_ART_acceptOpt] = "acceptOpt";
  artKindOfs[ARTL_ART_acceptOpt] = ARTK_NONTERMINAL;
  artLabelInternalStrings[ARTL_ART_acceptOpt_56] = "acceptOpt ::= . 'accept'  strings ";
  artLabelStrings[ARTL_ART_acceptOpt_56] = "";
  artlhsL[ARTL_ART_acceptOpt_56] = ARTL_ART_acceptOpt;
  artKindOfs[ARTL_ART_acceptOpt_56] = ARTK_INTERMEDIATE;
  artLabelInternalStrings[ARTL_ART_acceptOpt_57] = "acceptOpt ::= 'accept'  strings ";
  artLabelStrings[ARTL_ART_acceptOpt_57] = "";
  artlhsL[ARTL_ART_acceptOpt_57] = ARTL_ART_acceptOpt;
  artLabelInternalStrings[ARTL_ART_acceptOpt_58] = "acceptOpt ::= 'accept'  . strings ";
  artLabelStrings[ARTL_ART_acceptOpt_58] = "";
  artlhsL[ARTL_ART_acceptOpt_58] = ARTL_ART_acceptOpt;
  artKindOfs[ARTL_ART_acceptOpt_58] = ARTK_INTERMEDIATE;
  artfiRL[ARTL_ART_acceptOpt_58] = true;
  artLabelInternalStrings[ARTL_ART_acceptOpt_62] = "acceptOpt ::= 'accept'  strings .";
  artLabelStrings[ARTL_ART_acceptOpt_62] = "";
  artlhsL[ARTL_ART_acceptOpt_62] = ARTL_ART_acceptOpt;
  artSlotInstanceOfs[ARTL_ART_acceptOpt_62] = ARTL_ART_strings;
  artKindOfs[ARTL_ART_acceptOpt_62] = ARTK_INTERMEDIATE;
  arteoRL[ARTL_ART_acceptOpt_62] = true;
  arteoR_pL[ARTL_ART_acceptOpt_62] = true;
  artPopD[ARTL_ART_acceptOpt_62] = true;
  artLabelInternalStrings[ARTL_ART_acceptOpt_64] = "acceptOpt ::= . # ";
  artLabelStrings[ARTL_ART_acceptOpt_64] = "";
  artlhsL[ARTL_ART_acceptOpt_64] = ARTL_ART_acceptOpt;
  artKindOfs[ARTL_ART_acceptOpt_64] = ARTK_INTERMEDIATE;
  artPopD[ARTL_ART_acceptOpt_64] = true;
  artLabelInternalStrings[ARTL_ART_acceptOpt_66] = "acceptOpt ::= # .";
  artLabelStrings[ARTL_ART_acceptOpt_66] = "";
  artlhsL[ARTL_ART_acceptOpt_66] = ARTL_ART_acceptOpt;
  artKindOfs[ARTL_ART_acceptOpt_66] = ARTK_INTERMEDIATE;
  arteoRL[ARTL_ART_acceptOpt_66] = true;
  arteoR_pL[ARTL_ART_acceptOpt_66] = true;
  artPopD[ARTL_ART_acceptOpt_66] = true;
}

public void artTableInitialiser_ART_optionOpt() {
  artLabelInternalStrings[ARTL_ART_optionOpt] = "optionOpt";
  artLabelStrings[ARTL_ART_optionOpt] = "optionOpt";
  artKindOfs[ARTL_ART_optionOpt] = ARTK_NONTERMINAL;
  artLabelInternalStrings[ARTL_ART_optionOpt_40] = "optionOpt ::= . stringBrace ";
  artLabelStrings[ARTL_ART_optionOpt_40] = "";
  artlhsL[ARTL_ART_optionOpt_40] = ARTL_ART_optionOpt;
  artKindOfs[ARTL_ART_optionOpt_40] = ARTK_INTERMEDIATE;
  artLabelInternalStrings[ARTL_ART_optionOpt_42] = "optionOpt ::= stringBrace .";
  artLabelStrings[ARTL_ART_optionOpt_42] = "";
  artlhsL[ARTL_ART_optionOpt_42] = ARTL_ART_optionOpt;
  artSlotInstanceOfs[ARTL_ART_optionOpt_42] = ARTL_ART_stringBrace;
  artKindOfs[ARTL_ART_optionOpt_42] = ARTK_INTERMEDIATE;
  arteoRL[ARTL_ART_optionOpt_42] = true;
  arteoR_pL[ARTL_ART_optionOpt_42] = true;
  artPopD[ARTL_ART_optionOpt_42] = true;
  artLabelInternalStrings[ARTL_ART_optionOpt_46] = "optionOpt ::= . # ";
  artLabelStrings[ARTL_ART_optionOpt_46] = "";
  artlhsL[ARTL_ART_optionOpt_46] = ARTL_ART_optionOpt;
  artKindOfs[ARTL_ART_optionOpt_46] = ARTK_INTERMEDIATE;
  artPopD[ARTL_ART_optionOpt_46] = true;
  artLabelInternalStrings[ARTL_ART_optionOpt_48] = "optionOpt ::= # .";
  artLabelStrings[ARTL_ART_optionOpt_48] = "";
  artlhsL[ARTL_ART_optionOpt_48] = ARTL_ART_optionOpt;
  artKindOfs[ARTL_ART_optionOpt_48] = ARTK_INTERMEDIATE;
  arteoRL[ARTL_ART_optionOpt_48] = true;
  arteoR_pL[ARTL_ART_optionOpt_48] = true;
  artPopD[ARTL_ART_optionOpt_48] = true;
}

public void artTableInitialiser_ART_rejectOpt() {
  artLabelInternalStrings[ARTL_ART_rejectOpt] = "rejectOpt";
  artLabelStrings[ARTL_ART_rejectOpt] = "rejectOpt";
  artKindOfs[ARTL_ART_rejectOpt] = ARTK_NONTERMINAL;
  artLabelInternalStrings[ARTL_ART_rejectOpt_68] = "rejectOpt ::= . 'reject'  strings ";
  artLabelStrings[ARTL_ART_rejectOpt_68] = "";
  artlhsL[ARTL_ART_rejectOpt_68] = ARTL_ART_rejectOpt;
  artKindOfs[ARTL_ART_rejectOpt_68] = ARTK_INTERMEDIATE;
  artLabelInternalStrings[ARTL_ART_rejectOpt_69] = "rejectOpt ::= 'reject'  strings ";
  artLabelStrings[ARTL_ART_rejectOpt_69] = "";
  artlhsL[ARTL_ART_rejectOpt_69] = ARTL_ART_rejectOpt;
  artLabelInternalStrings[ARTL_ART_rejectOpt_70] = "rejectOpt ::= 'reject'  . strings ";
  artLabelStrings[ARTL_ART_rejectOpt_70] = "";
  artlhsL[ARTL_ART_rejectOpt_70] = ARTL_ART_rejectOpt;
  artKindOfs[ARTL_ART_rejectOpt_70] = ARTK_INTERMEDIATE;
  artfiRL[ARTL_ART_rejectOpt_70] = true;
  artLabelInternalStrings[ARTL_ART_rejectOpt_74] = "rejectOpt ::= 'reject'  strings .";
  artLabelStrings[ARTL_ART_rejectOpt_74] = "";
  artlhsL[ARTL_ART_rejectOpt_74] = ARTL_ART_rejectOpt;
  artSlotInstanceOfs[ARTL_ART_rejectOpt_74] = ARTL_ART_strings;
  artKindOfs[ARTL_ART_rejectOpt_74] = ARTK_INTERMEDIATE;
  arteoRL[ARTL_ART_rejectOpt_74] = true;
  arteoR_pL[ARTL_ART_rejectOpt_74] = true;
  artPopD[ARTL_ART_rejectOpt_74] = true;
  artLabelInternalStrings[ARTL_ART_rejectOpt_76] = "rejectOpt ::= . # ";
  artLabelStrings[ARTL_ART_rejectOpt_76] = "";
  artlhsL[ARTL_ART_rejectOpt_76] = ARTL_ART_rejectOpt;
  artKindOfs[ARTL_ART_rejectOpt_76] = ARTK_INTERMEDIATE;
  artPopD[ARTL_ART_rejectOpt_76] = true;
  artLabelInternalStrings[ARTL_ART_rejectOpt_78] = "rejectOpt ::= # .";
  artLabelStrings[ARTL_ART_rejectOpt_78] = "";
  artlhsL[ARTL_ART_rejectOpt_78] = ARTL_ART_rejectOpt;
  artKindOfs[ARTL_ART_rejectOpt_78] = ARTK_INTERMEDIATE;
  arteoRL[ARTL_ART_rejectOpt_78] = true;
  arteoR_pL[ARTL_ART_rejectOpt_78] = true;
  artPopD[ARTL_ART_rejectOpt_78] = true;
}

public void artTableInitialiser_ART_stringBrace() {
  artLabelInternalStrings[ARTL_ART_stringBrace] = "stringBrace";
  artLabelStrings[ARTL_ART_stringBrace] = "stringBrace";
  artKindOfs[ARTL_ART_stringBrace] = ARTK_NONTERMINAL;
  artLabelInternalStrings[ARTL_ART_stringBrace_80] = "stringBrace ::= . &STRING_BRACE  ";
  artLabelStrings[ARTL_ART_stringBrace_80] = "";
  artlhsL[ARTL_ART_stringBrace_80] = ARTL_ART_stringBrace;
  artKindOfs[ARTL_ART_stringBrace_80] = ARTK_INTERMEDIATE;
  artPopD[ARTL_ART_stringBrace_80] = true;
  artLabelInternalStrings[ARTL_ART_stringBrace_81] = "stringBrace ::= &STRING_BRACE  ";
  artLabelStrings[ARTL_ART_stringBrace_81] = "";
  artlhsL[ARTL_ART_stringBrace_81] = ARTL_ART_stringBrace;
  artPopD[ARTL_ART_stringBrace_81] = true;
  artLabelInternalStrings[ARTL_ART_stringBrace_82] = "stringBrace ::= &STRING_BRACE  .";
  artLabelStrings[ARTL_ART_stringBrace_82] = "";
  artlhsL[ARTL_ART_stringBrace_82] = ARTL_ART_stringBrace;
  artKindOfs[ARTL_ART_stringBrace_82] = ARTK_INTERMEDIATE;
  arteoRL[ARTL_ART_stringBrace_82] = true;
  arteoR_pL[ARTL_ART_stringBrace_82] = true;
  artPopD[ARTL_ART_stringBrace_82] = true;
}

public void artTableInitialiser_ART_stringDollar() {
  artLabelInternalStrings[ARTL_ART_stringDollar] = "stringDollar";
  artLabelStrings[ARTL_ART_stringDollar] = "stringDollar";
  artKindOfs[ARTL_ART_stringDollar] = ARTK_NONTERMINAL;
  artLabelInternalStrings[ARTL_ART_stringDollar_50] = "stringDollar ::= . &STRING_DOLLAR  ";
  artLabelStrings[ARTL_ART_stringDollar_50] = "";
  artlhsL[ARTL_ART_stringDollar_50] = ARTL_ART_stringDollar;
  artKindOfs[ARTL_ART_stringDollar_50] = ARTK_INTERMEDIATE;
  artPopD[ARTL_ART_stringDollar_50] = true;
  artLabelInternalStrings[ARTL_ART_stringDollar_51] = "stringDollar ::= &STRING_DOLLAR  ";
  artLabelStrings[ARTL_ART_stringDollar_51] = "";
  artlhsL[ARTL_ART_stringDollar_51] = ARTL_ART_stringDollar;
  artPopD[ARTL_ART_stringDollar_51] = true;
  artLabelInternalStrings[ARTL_ART_stringDollar_52] = "stringDollar ::= &STRING_DOLLAR  .";
  artLabelStrings[ARTL_ART_stringDollar_52] = "";
  artlhsL[ARTL_ART_stringDollar_52] = ARTL_ART_stringDollar;
  artKindOfs[ARTL_ART_stringDollar_52] = ARTK_INTERMEDIATE;
  arteoRL[ARTL_ART_stringDollar_52] = true;
  arteoR_pL[ARTL_ART_stringDollar_52] = true;
  artPopD[ARTL_ART_stringDollar_52] = true;
}

public void artTableInitialiser_ART_strings() {
  artLabelInternalStrings[ARTL_ART_strings] = "strings";
  artLabelStrings[ARTL_ART_strings] = "strings";
  artKindOfs[ARTL_ART_strings] = ARTK_NONTERMINAL;
  artLabelInternalStrings[ARTL_ART_strings_86] = "strings ::= . # ";
  artLabelStrings[ARTL_ART_strings_86] = "";
  artlhsL[ARTL_ART_strings_86] = ARTL_ART_strings;
  artKindOfs[ARTL_ART_strings_86] = ARTK_INTERMEDIATE;
  artPopD[ARTL_ART_strings_86] = true;
  artLabelInternalStrings[ARTL_ART_strings_88] = "strings ::= # .";
  artLabelStrings[ARTL_ART_strings_88] = "";
  artlhsL[ARTL_ART_strings_88] = ARTL_ART_strings;
  artKindOfs[ARTL_ART_strings_88] = ARTK_INTERMEDIATE;
  arteoRL[ARTL_ART_strings_88] = true;
  arteoR_pL[ARTL_ART_strings_88] = true;
  artPopD[ARTL_ART_strings_88] = true;
  artLabelInternalStrings[ARTL_ART_strings_90] = "strings ::= . stringDollar strings ";
  artLabelStrings[ARTL_ART_strings_90] = "";
  artlhsL[ARTL_ART_strings_90] = ARTL_ART_strings;
  artKindOfs[ARTL_ART_strings_90] = ARTK_INTERMEDIATE;
  artLabelInternalStrings[ARTL_ART_strings_92] = "strings ::= stringDollar . strings ";
  artLabelStrings[ARTL_ART_strings_92] = "";
  artlhsL[ARTL_ART_strings_92] = ARTL_ART_strings;
  artSlotInstanceOfs[ARTL_ART_strings_92] = ARTL_ART_stringDollar;
  artKindOfs[ARTL_ART_strings_92] = ARTK_INTERMEDIATE;
  artfiRL[ARTL_ART_strings_92] = true;
  artLabelInternalStrings[ARTL_ART_strings_96] = "strings ::= stringDollar strings .";
  artLabelStrings[ARTL_ART_strings_96] = "";
  artlhsL[ARTL_ART_strings_96] = ARTL_ART_strings;
  artSlotInstanceOfs[ARTL_ART_strings_96] = ARTL_ART_strings;
  artKindOfs[ARTL_ART_strings_96] = ARTK_INTERMEDIATE;
  arteoRL[ARTL_ART_strings_96] = true;
  arteoR_pL[ARTL_ART_strings_96] = true;
  artPopD[ARTL_ART_strings_96] = true;
  artLabelInternalStrings[ARTL_ART_strings_98] = "strings ::= . stringDollar ";
  artLabelStrings[ARTL_ART_strings_98] = "";
  artlhsL[ARTL_ART_strings_98] = ARTL_ART_strings;
  artKindOfs[ARTL_ART_strings_98] = ARTK_INTERMEDIATE;
  artLabelInternalStrings[ARTL_ART_strings_100] = "strings ::= stringDollar .";
  artLabelStrings[ARTL_ART_strings_100] = "";
  artlhsL[ARTL_ART_strings_100] = ARTL_ART_strings;
  artSlotInstanceOfs[ARTL_ART_strings_100] = ARTL_ART_stringDollar;
  artKindOfs[ARTL_ART_strings_100] = ARTK_INTERMEDIATE;
  arteoRL[ARTL_ART_strings_100] = true;
  arteoR_pL[ARTL_ART_strings_100] = true;
  artPopD[ARTL_ART_strings_100] = true;
}

public void artTableInitialiser_ART_test() {
  artLabelInternalStrings[ARTL_ART_test] = "test";
  artLabelStrings[ARTL_ART_test] = "test";
  artKindOfs[ARTL_ART_test] = ARTK_NONTERMINAL;
  artLabelInternalStrings[ARTL_ART_test_18] = "test ::= . ID optionOpt stringDollar acceptOpt rejectOpt ";
  artLabelStrings[ARTL_ART_test_18] = "";
  artlhsL[ARTL_ART_test_18] = ARTL_ART_test;
  artKindOfs[ARTL_ART_test_18] = ARTK_INTERMEDIATE;
  artLabelInternalStrings[ARTL_ART_test_20] = "test ::= ID . optionOpt stringDollar acceptOpt rejectOpt ";
  artLabelStrings[ARTL_ART_test_20] = "";
  artlhsL[ARTL_ART_test_20] = ARTL_ART_test;
  artSlotInstanceOfs[ARTL_ART_test_20] = ARTL_ART_ID;
  artKindOfs[ARTL_ART_test_20] = ARTK_INTERMEDIATE;
  artfiRL[ARTL_ART_test_20] = true;
  artLabelInternalStrings[ARTL_ART_test_22] = "test ::= ID optionOpt . stringDollar acceptOpt rejectOpt ";
  artLabelStrings[ARTL_ART_test_22] = "";
  artlhsL[ARTL_ART_test_22] = ARTL_ART_test;
  artSlotInstanceOfs[ARTL_ART_test_22] = ARTL_ART_optionOpt;
  artKindOfs[ARTL_ART_test_22] = ARTK_INTERMEDIATE;
  artLabelInternalStrings[ARTL_ART_test_24] = "test ::= ID optionOpt stringDollar . acceptOpt rejectOpt ";
  artLabelStrings[ARTL_ART_test_24] = "";
  artlhsL[ARTL_ART_test_24] = ARTL_ART_test;
  artSlotInstanceOfs[ARTL_ART_test_24] = ARTL_ART_stringDollar;
  artKindOfs[ARTL_ART_test_24] = ARTK_INTERMEDIATE;
  artLabelInternalStrings[ARTL_ART_test_28] = "test ::= ID optionOpt stringDollar acceptOpt . rejectOpt ";
  artLabelStrings[ARTL_ART_test_28] = "";
  artlhsL[ARTL_ART_test_28] = ARTL_ART_test;
  artSlotInstanceOfs[ARTL_ART_test_28] = ARTL_ART_acceptOpt;
  artKindOfs[ARTL_ART_test_28] = ARTK_INTERMEDIATE;
  artLabelInternalStrings[ARTL_ART_test_32] = "test ::= ID optionOpt stringDollar acceptOpt rejectOpt .";
  artLabelStrings[ARTL_ART_test_32] = "";
  artlhsL[ARTL_ART_test_32] = ARTL_ART_test;
  artSlotInstanceOfs[ARTL_ART_test_32] = ARTL_ART_rejectOpt;
  artKindOfs[ARTL_ART_test_32] = ARTK_INTERMEDIATE;
  arteoRL[ARTL_ART_test_32] = true;
  arteoR_pL[ARTL_ART_test_32] = true;
  artPopD[ARTL_ART_test_32] = true;
}

public void artTableInitialiser_ART_tests() {
  artLabelInternalStrings[ARTL_ART_tests] = "tests";
  artLabelStrings[ARTL_ART_tests] = "tests";
  artKindOfs[ARTL_ART_tests] = ARTK_NONTERMINAL;
  artLabelInternalStrings[ARTL_ART_tests_8] = "tests ::= . test tests ";
  artLabelStrings[ARTL_ART_tests_8] = "";
  artlhsL[ARTL_ART_tests_8] = ARTL_ART_tests;
  artKindOfs[ARTL_ART_tests_8] = ARTK_INTERMEDIATE;
  artLabelInternalStrings[ARTL_ART_tests_10] = "tests ::= test . tests ";
  artLabelStrings[ARTL_ART_tests_10] = "";
  artlhsL[ARTL_ART_tests_10] = ARTL_ART_tests;
  artSlotInstanceOfs[ARTL_ART_tests_10] = ARTL_ART_test;
  artKindOfs[ARTL_ART_tests_10] = ARTK_INTERMEDIATE;
  artfiRL[ARTL_ART_tests_10] = true;
  artLabelInternalStrings[ARTL_ART_tests_12] = "tests ::= test tests .";
  artLabelStrings[ARTL_ART_tests_12] = "";
  artlhsL[ARTL_ART_tests_12] = ARTL_ART_tests;
  artSlotInstanceOfs[ARTL_ART_tests_12] = ARTL_ART_tests;
  artKindOfs[ARTL_ART_tests_12] = ARTK_INTERMEDIATE;
  arteoRL[ARTL_ART_tests_12] = true;
  arteoR_pL[ARTL_ART_tests_12] = true;
  artPopD[ARTL_ART_tests_12] = true;
  artLabelInternalStrings[ARTL_ART_tests_14] = "tests ::= . test ";
  artLabelStrings[ARTL_ART_tests_14] = "";
  artlhsL[ARTL_ART_tests_14] = ARTL_ART_tests;
  artKindOfs[ARTL_ART_tests_14] = ARTK_INTERMEDIATE;
  artLabelInternalStrings[ARTL_ART_tests_16] = "tests ::= test .";
  artLabelStrings[ARTL_ART_tests_16] = "";
  artlhsL[ARTL_ART_tests_16] = ARTL_ART_tests;
  artSlotInstanceOfs[ARTL_ART_tests_16] = ARTL_ART_test;
  artKindOfs[ARTL_ART_tests_16] = ARTK_INTERMEDIATE;
  arteoRL[ARTL_ART_tests_16] = true;
  arteoR_pL[ARTL_ART_tests_16] = true;
  artPopD[ARTL_ART_tests_16] = true;
}

public void artTableInitialiser_ART_text() {
  artLabelInternalStrings[ARTL_ART_text] = "text";
  artLabelStrings[ARTL_ART_text] = "text";
  artKindOfs[ARTL_ART_text] = ARTK_NONTERMINAL;
  artLabelInternalStrings[ARTL_ART_text_2] = "text ::= . tests ";
  artLabelStrings[ARTL_ART_text_2] = "";
  artlhsL[ARTL_ART_text_2] = ARTL_ART_text;
  artKindOfs[ARTL_ART_text_2] = ARTK_INTERMEDIATE;
  artLabelInternalStrings[ARTL_ART_text_6] = "text ::= tests .";
  artLabelStrings[ARTL_ART_text_6] = "";
  artlhsL[ARTL_ART_text_6] = ARTL_ART_text;
  artSlotInstanceOfs[ARTL_ART_text_6] = ARTL_ART_tests;
  artKindOfs[ARTL_ART_text_6] = ARTK_INTERMEDIATE;
  arteoRL[ARTL_ART_text_6] = true;
  arteoR_pL[ARTL_ART_text_6] = true;
  artPopD[ARTL_ART_text_6] = true;
}

public void artTableInitialise() {
  artLabelInternalStrings = new String[ARTX_LABEL_EXTENT + 1];
  artLabelStrings = new String[ARTX_LABEL_EXTENT + 1];
  artLabelInternalStrings[ARTL_EOS] = "ART$";
  artLabelStrings[ARTL_EOS] = " EOS $";
  artLabelInternalStrings[ARTX_DESPATCH] = "ARTX_DESPATCH";
  artLabelStrings[ARTX_DESPATCH] = " DESPATCH";
  artLabelInternalStrings[ARTL_DUMMY] = "ARTL_DUMMY";
  artLabelStrings[ARTL_DUMMY] = " DUMMY";
  artLabelInternalStrings[ARTX_LABEL_EXTENT] = "!!ILLEGAL!!";
  artLabelStrings[ARTX_LABEL_EXTENT] = " ILLEGAL";
  artLabelStrings[ARTL_EPSILON] = "#";
  artLabelInternalStrings[ARTL_EPSILON] = "#";

  artTerminalRequiresWhiteSpace = new boolean[ARTL_EPSILON];
  artInitialiseBooleanArray(artTerminalRequiresWhiteSpace, 0, ARTL_EPSILON, false);

  artTerminalCaseInsensitive = new boolean[ARTL_EPSILON];
  artInitialiseBooleanArray(artTerminalCaseInsensitive, 0, ARTL_EPSILON, false);

  artlhsL = new int[ARTX_LABEL_EXTENT];
  artInitialiseIntegerArray(artlhsL, 0, ARTX_LABEL_EXTENT);
  artlhsL[ARTX_DESPATCH] = ARTX_DESPATCH;

  artKindOfs = new int[ARTX_LABEL_EXTENT + 1];
  artKindOfs[ARTL_EOS] = ARTK_EOS;
  artKindOfs[ARTL_EPSILON] = ARTK_EPSILON;

  artHigher = new ARTBitSet[ARTX_LABEL_EXTENT + 1];

  artLonger = new ARTBitSet[ARTX_LABEL_EXTENT + 1];

  artShorter = new ARTBitSet[ARTX_LABEL_EXTENT + 1];

  artPreSlots = new int[ARTX_LABEL_EXTENT];
  artInitialiseIntegerArray(artPreSlots, 0, ARTX_LABEL_EXTENT);

  artPostSlots = new int[ARTX_LABEL_EXTENT];
  artInitialiseIntegerArray(artPostSlots, 0, ARTX_LABEL_EXTENT);

  artInstanceOfs = new int[ARTX_LABEL_EXTENT];
  artInitialiseIntegerArray(artInstanceOfs, 0, ARTX_LABEL_EXTENT);

  artSlotInstanceOfs = new int[ARTX_LABEL_EXTENT];
  artInitialiseIntegerArray(artSlotInstanceOfs, 0, ARTX_LABEL_EXTENT);

  artUserNameOfs = new int[ARTX_LABEL_EXTENT + 1];

  artGathers = new int[ARTX_LABEL_EXTENT];
  artInitialiseIntegerArray(artGathers, 0, ARTX_LABEL_EXTENT);

  artFolds = new int[ARTX_LABEL_EXTENT];
  artInitialiseIntegerArray(artFolds, 0, ARTX_LABEL_EXTENT, 0);

  artpL = new int[ARTX_LABEL_EXTENT];
  artInitialiseIntegerArray(artpL, 0, ARTX_LABEL_EXTENT);

  artaL = new int[ARTX_LABEL_EXTENT];
  artInitialiseIntegerArray(artaL, 0, ARTX_LABEL_EXTENT);

  artcolonL = new int[ARTX_LABEL_EXTENT];
  artInitialiseIntegerArray(artcolonL, 0, ARTX_LABEL_EXTENT);

  arteoOPL = new boolean[ARTX_LABEL_EXTENT];
  artInitialiseBooleanArray(arteoOPL, 0, ARTX_LABEL_EXTENT, false);

  artfiRL = new boolean[ARTX_LABEL_EXTENT];
  artInitialiseBooleanArray(artfiRL, 0, ARTX_LABEL_EXTENT, false);

  artfiPCL = new boolean[ARTX_LABEL_EXTENT];
  artInitialiseBooleanArray(artfiPCL, 0, ARTX_LABEL_EXTENT, false);

  arteoRL = new boolean[ARTX_LABEL_EXTENT];
  artInitialiseBooleanArray(arteoRL, 0, ARTX_LABEL_EXTENT, false);

  arteoR_pL = new boolean[ARTX_LABEL_EXTENT];
  artInitialiseBooleanArray(arteoR_pL, 0, ARTX_LABEL_EXTENT, false);

  artPopD = new boolean[ARTX_LABEL_EXTENT];
  artInitialiseBooleanArray(artPopD, 0, ARTX_LABEL_EXTENT, false);

  artLabelStrings[ARTTB_ID] = "ID";
  artLabelInternalStrings[ARTTB_ID] = "&ID";
  artKindOfs[ARTTB_ID] = ARTK_BUILTIN_TERMINAL;
  artTerminalRequiresWhiteSpace[ARTTB_ID] = true;
  artLabelStrings[ARTTB_STRING_BRACE] = "STRING_BRACE";
  artLabelInternalStrings[ARTTB_STRING_BRACE] = "&STRING_BRACE";
  artKindOfs[ARTTB_STRING_BRACE] = ARTK_BUILTIN_TERMINAL;
  artTerminalRequiresWhiteSpace[ARTTB_STRING_BRACE] = true;
  artLabelStrings[ARTTB_STRING_DOLLAR] = "STRING_DOLLAR";
  artLabelInternalStrings[ARTTB_STRING_DOLLAR] = "&STRING_DOLLAR";
  artKindOfs[ARTTB_STRING_DOLLAR] = ARTK_BUILTIN_TERMINAL;
  artTerminalRequiresWhiteSpace[ARTTB_STRING_DOLLAR] = true;
  artLabelStrings[ARTTS_accept] = "accept";
  artLabelInternalStrings[ARTTS_accept] = "'accept'";
  artKindOfs[ARTTS_accept] = ARTK_CASE_SENSITIVE_TERMINAL;
  artTerminalRequiresWhiteSpace[ARTTS_accept] = true;
  artLabelStrings[ARTTS_reject] = "reject";
  artLabelInternalStrings[ARTTS_reject] = "'reject'";
  artKindOfs[ARTTS_reject] = ARTK_CASE_SENSITIVE_TERMINAL;
  artTerminalRequiresWhiteSpace[ARTTS_reject] = true;
  artTableInitialiser_ART_ID();
  artTableInitialiser_ART_acceptOpt();
  artTableInitialiser_ART_optionOpt();
  artTableInitialiser_ART_rejectOpt();
  artTableInitialiser_ART_stringBrace();
  artTableInitialiser_ART_stringDollar();
  artTableInitialiser_ART_strings();
  artTableInitialiser_ART_test();
  artTableInitialiser_ART_tests();
  artTableInitialiser_ART_text();
}

public ARTTGParser(ARTLexerV3 artLexer) {
  this(null, artLexer);
}

public ARTTGParser(ARTGrammar artGrammar, ARTLexerV3 artLexer) {
  super(artGrammar, artLexer);
  artParserKind = "GLL Gen";
  artFirstTerminalLabel = ARTTS_accept;
  artFirstUnusedLabel = ARTX_LABEL_EXTENT + 1;
  artSetExtent = 17;
  ARTL_EOS = ARTX_EOS;
  ARTL_EPSILON = ARTX_EPSILON;
  ARTL_DUMMY = ARTX_DUMMY;
  artGrammarKind = ARTModeGrammarKind.BNF;
  artDefaultStartSymbolLabel = ARTL_ART_text;
  artBuildDirectives = "ARTDirectives [inputs=[], inputFilenames=[], directives={suppressPopGuard=false, tweLexicalisations=false, algorithmMode=gllGeneratorPool, tweLongest=false, tweSegments=false, sppfShortest=false, termWrite=false, tweCounts=false, clusteredGSS=false, twePrint=false, rewriteDisable=false, tweAmbiguityClasses=false, sppfAmbiguityAnalysis=false, rewriteConfiguration=false, outputDirectory=., inputCounts=false, twePriority=false, treeShow=false, tweRecursive=false, rewritePostorder=false, rewriteContractum=true, parseCounts=false, predictivePops=false, suppressProductionGuard=false, sppfDead=false, twePrintFull=false, input=0, tweExtents=false, suppressSemantics=false, despatchMode=fragment, treePrintLevel=3, sppfShowFull=false, treePrint=false, sppfChooseCounts=false, log=0, tweDump=false, sppfCycleDetect=false, sppfCountSentences=false, parserName=ARTTGParser, rewriteResume=true, inputPrint=false, lexerName=ARTTGLexer, trace=false, tweTokenWrite=false, tweDead=false, tweShortest=false, rewritePure=true, tweSelectOne=false, smlCycleBreak=false, termPrint=false, suppressTestRepeat=false, rewritePreorder=false, sppfAmbiguityAnalysisFull=false, tweFromSPPF=false, actionSuppress=false, tweLexicalisationsQuick=false, sppfPriority=false, sppfShow=false, rewriteOneStep=false, namespace=uk.ac.rhul.cs.csle.art.v3.tg, sppfSelectOne=false, FIFODescriptors=false, sppfOrderedLongest=false, verbosity=0, sppfLongest=false, gssShow=false}]";
  artFIFODescriptors = false;
  artSetInitialise();
  artTableInitialise();
}

int stringCount; boolean vv3;
public static class ARTAT_ART_ID extends ARTGLLAttributeBlock {
  public int rightExtent;
  public int leftExtent;
  public String lexeme;
  public String v;
  public String toString() {
    String ret = "";
  ret += " rightExtent=" + rightExtent;
  ret += " leftExtent=" + leftExtent;
  ret += " lexeme=" + lexeme;
  ret += " v=" + v;
    return ret + " ";
}
}

public static class ARTAT_ART_acceptOpt extends ARTGLLAttributeBlock {
  public String base;
  public int count;
  ARTGLLRDTHandle strings1;
  public String toString() {
    String ret = "";
  ret += " base=" + base;
  ret += " count=" + count;
    return ret + " ";
}
}

public static class ARTAT_ART_optionOpt extends ARTGLLAttributeBlock {
  public String v;
  ARTGLLRDTHandle stringBrace1;
  public String toString() {
    String ret = "";
  ret += " v=" + v;
    return ret + " ";
}
}

public static class ARTAT_ART_rejectOpt extends ARTGLLAttributeBlock {
  public String base;
  public int count;
  ARTGLLRDTHandle strings1;
  public String toString() {
    String ret = "";
  ret += " base=" + base;
  ret += " count=" + count;
    return ret + " ";
}
}

public static class ARTAT_ART_stringBrace extends ARTGLLAttributeBlock {
  public int rightExtent;
  public int leftExtent;
  public String lexeme;
  public String v;
  public String toString() {
    String ret = "";
  ret += " rightExtent=" + rightExtent;
  ret += " leftExtent=" + leftExtent;
  ret += " lexeme=" + lexeme;
  ret += " v=" + v;
    return ret + " ";
}
}

public static class ARTAT_ART_stringDollar extends ARTGLLAttributeBlock {
  public int rightExtent;
  public int leftExtent;
  public String lexeme;
  public String v;
  public String toString() {
    String ret = "";
  ret += " rightExtent=" + rightExtent;
  ret += " leftExtent=" + leftExtent;
  ret += " lexeme=" + lexeme;
  ret += " v=" + v;
    return ret + " ";
}
}

public static class ARTAT_ART_strings extends ARTGLLAttributeBlock {
  public String fileName;
  public boolean accepting;
  ARTGLLRDTHandle stringDollar1;
  ARTGLLRDTHandle strings1;
  public String toString() {
    String ret = "";
  ret += " fileName=" + fileName;
  ret += " accepting=" + accepting;
    return ret + " ";
}
}

public static class ARTAT_ART_text extends ARTGLLAttributeBlock {
  public boolean vv3;
  public String toString() {
    String ret = "";
  ret += " vv3=" + vv3;
    return ret + " ";
}
}

public void ARTRD_ID(int artElement, ARTGLLRDTVertex artParent, boolean artWriteable, ARTAT_ART_ID ID)  {
ARTGLLRDTVertex artNewParent; boolean artNewWriteable = true;
    for (int artPackedNode = artSPPFNodePackedNodeList(artElement); artPackedNode != 0; artPackedNode = artSPPFPackedNodePackedNodeList(artPackedNode)) {
      if (artSPPFPackedNodeSelected(artPackedNode)) {
        switch (artSPPFPackedNodeLabel(artPackedNode)) {
    /*ID ::= &ID  .*/
    case ARTL_ART_ID_36: 
            ARTRD_ID(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable, ID);
      artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
      ID.lexeme = artLexeme(ID.leftExtent, ID.rightExtent); ID.v = artLexemeAsID(ID.leftExtent, ID.rightExtent); 
      break;
        default: ; }}}
}

public void ARTRD_acceptOpt(int artElement, ARTGLLRDTVertex artParent, boolean artWriteable, ARTAT_ART_acceptOpt acceptOpt, ARTAT_ART_strings strings1)  {
ARTGLLRDTVertex artNewParent; boolean artNewWriteable = true;
    for (int artPackedNode = artSPPFNodePackedNodeList(artElement); artPackedNode != 0; artPackedNode = artSPPFPackedNodePackedNodeList(artPackedNode)) {
      if (artSPPFPackedNodeSelected(artPackedNode)) {
        switch (artSPPFPackedNodeLabel(artPackedNode)) {
    /*acceptOpt ::= 'accept'  strings .*/
    case ARTL_ART_acceptOpt_62: 
      strings1 = new ARTAT_ART_strings();
            artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeLeftChild(artPackedNode)), null));
       strings1.fileName = acceptOpt.base + ".acc"; strings1.accepting = true; 
      artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), strings1));
      ARTRD_strings(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable, strings1, null, null);
            break;
    /*acceptOpt ::= # .*/
    case ARTL_ART_acceptOpt_66: 
      strings1 = new ARTAT_ART_strings();
            ARTRD_acceptOpt(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable, acceptOpt, strings1);
      artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
            break;
        default: ; }}}
}

public void ARTRD_optionOpt(int artElement, ARTGLLRDTVertex artParent, boolean artWriteable, ARTAT_ART_optionOpt optionOpt, ARTAT_ART_stringBrace stringBrace1)  {
ARTGLLRDTVertex artNewParent; boolean artNewWriteable = true;
    for (int artPackedNode = artSPPFNodePackedNodeList(artElement); artPackedNode != 0; artPackedNode = artSPPFPackedNodePackedNodeList(artPackedNode)) {
      if (artSPPFPackedNodeSelected(artPackedNode)) {
        switch (artSPPFPackedNodeLabel(artPackedNode)) {
    /*optionOpt ::= stringBrace .*/
    case ARTL_ART_optionOpt_42: 
      stringBrace1 = new ARTAT_ART_stringBrace();
            ARTRD_optionOpt(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable, optionOpt, stringBrace1);
      stringBrace1.rightExtent = artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode));
      stringBrace1.leftExtent = artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode));
      artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), stringBrace1));
      ARTRD_stringBrace(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable, stringBrace1);
       optionOpt.v = stringBrace1.v; 
      break;
    /*optionOpt ::= # .*/
    case ARTL_ART_optionOpt_48: 
      stringBrace1 = new ARTAT_ART_stringBrace();
            ARTRD_optionOpt(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable, optionOpt, stringBrace1);
      artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
            break;
        default: ; }}}
}

public void ARTRD_rejectOpt(int artElement, ARTGLLRDTVertex artParent, boolean artWriteable, ARTAT_ART_rejectOpt rejectOpt, ARTAT_ART_strings strings1)  {
ARTGLLRDTVertex artNewParent; boolean artNewWriteable = true;
    for (int artPackedNode = artSPPFNodePackedNodeList(artElement); artPackedNode != 0; artPackedNode = artSPPFPackedNodePackedNodeList(artPackedNode)) {
      if (artSPPFPackedNodeSelected(artPackedNode)) {
        switch (artSPPFPackedNodeLabel(artPackedNode)) {
    /*rejectOpt ::= 'reject'  strings .*/
    case ARTL_ART_rejectOpt_74: 
      strings1 = new ARTAT_ART_strings();
            artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeLeftChild(artPackedNode)), null));
       strings1.fileName = rejectOpt.base + ".rej"; strings1.accepting = false; 
      artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), strings1));
      ARTRD_strings(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable, strings1, null, null);
            break;
    /*rejectOpt ::= # .*/
    case ARTL_ART_rejectOpt_78: 
      strings1 = new ARTAT_ART_strings();
            ARTRD_rejectOpt(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable, rejectOpt, strings1);
      artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
            break;
        default: ; }}}
}

public void ARTRD_stringBrace(int artElement, ARTGLLRDTVertex artParent, boolean artWriteable, ARTAT_ART_stringBrace stringBrace)  {
ARTGLLRDTVertex artNewParent; boolean artNewWriteable = true;
    for (int artPackedNode = artSPPFNodePackedNodeList(artElement); artPackedNode != 0; artPackedNode = artSPPFPackedNodePackedNodeList(artPackedNode)) {
      if (artSPPFPackedNodeSelected(artPackedNode)) {
        switch (artSPPFPackedNodeLabel(artPackedNode)) {
    /*stringBrace ::= &STRING_BRACE  .*/
    case ARTL_ART_stringBrace_82: 
            ARTRD_stringBrace(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable, stringBrace);
      artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
      stringBrace.lexeme = artLexeme(stringBrace.leftExtent, stringBrace.rightExtent); stringBrace.v = artLexemeAsString(stringBrace.leftExtent, stringBrace.rightExtent); 
      break;
        default: ; }}}
}

public void ARTRD_stringDollar(int artElement, ARTGLLRDTVertex artParent, boolean artWriteable, ARTAT_ART_stringDollar stringDollar)  {
ARTGLLRDTVertex artNewParent; boolean artNewWriteable = true;
    for (int artPackedNode = artSPPFNodePackedNodeList(artElement); artPackedNode != 0; artPackedNode = artSPPFPackedNodePackedNodeList(artPackedNode)) {
      if (artSPPFPackedNodeSelected(artPackedNode)) {
        switch (artSPPFPackedNodeLabel(artPackedNode)) {
    /*stringDollar ::= &STRING_DOLLAR  .*/
    case ARTL_ART_stringDollar_52: 
            ARTRD_stringDollar(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable, stringDollar);
      artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
      stringDollar.lexeme = artLexeme(stringDollar.leftExtent, stringDollar.rightExtent); stringDollar.v = artLexemeAsRawString(stringDollar.leftExtent, stringDollar.rightExtent); 
      break;
        default: ; }}}
}

public void ARTRD_strings(int artElement, ARTGLLRDTVertex artParent, boolean artWriteable, ARTAT_ART_strings strings, ARTAT_ART_stringDollar stringDollar1, ARTAT_ART_strings strings1)  {
ARTGLLRDTVertex artNewParent; boolean artNewWriteable = true;
    for (int artPackedNode = artSPPFNodePackedNodeList(artElement); artPackedNode != 0; artPackedNode = artSPPFPackedNodePackedNodeList(artPackedNode)) {
      if (artSPPFPackedNodeSelected(artPackedNode)) {
        switch (artSPPFPackedNodeLabel(artPackedNode)) {
    /*strings ::= # .*/
    case ARTL_ART_strings_88: 
      stringDollar1 = new ARTAT_ART_stringDollar();
      strings1 = new ARTAT_ART_strings();
            ARTRD_strings(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable, strings, stringDollar1, strings1);
      artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
            break;
    /*strings ::= stringDollar strings .*/
    case ARTL_ART_strings_96: 
      stringDollar1 = new ARTAT_ART_stringDollar();
      strings1 = new ARTAT_ART_strings();
            stringDollar1.rightExtent = artSPPFNodeRightExtent(artSPPFPackedNodeLeftChild(artPackedNode));
      stringDollar1.leftExtent = artSPPFNodeLeftExtent(artSPPFPackedNodeLeftChild(artPackedNode));
      artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeLeftChild(artPackedNode)), stringDollar1));
      ARTRD_stringDollar(artSPPFPackedNodeLeftChild(artPackedNode), artNewParent, artNewWriteable, stringDollar1);
       System.err	.println("New string file '" + strings.fileName + "_" + stringCount /* + ": string '" + stringDollar1.v + "'" */); 
    try { PrintWriter out = new PrintWriter(new File(strings.fileName + "_" + stringCount++));
    out.printf("%s", stringDollar1.v);
System.out.println("    tryString(\"" + stringDollar1.v + "\", " + strings.accepting + ", grammar, parser, lexer);");
    out.close(); }catch (FileNotFoundException e) { e.printStackTrace(); }
    strings1.fileName = strings.fileName;
    strings1.accepting = strings.accepting;
  
      artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), strings1));
      ARTRD_strings(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable, strings1, null, null);
            break;
    /*strings ::= stringDollar .*/
    case ARTL_ART_strings_100: 
      stringDollar1 = new ARTAT_ART_stringDollar();
      strings1 = new ARTAT_ART_strings();
            ARTRD_strings(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable, strings, stringDollar1, strings1);
      stringDollar1.rightExtent = artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode));
      stringDollar1.leftExtent = artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode));
      artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), stringDollar1));
      ARTRD_stringDollar(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable, stringDollar1);
       System.err.println("New string file '" + strings.fileName + "_" + stringCount /* + ": string '" + stringDollar1.v + "'"*/); 
    try { PrintWriter out = new PrintWriter(new File(strings.fileName + "_" + stringCount++));
    out.printf("%s", stringDollar1.v);
System.out.println("    tryString(\"" + stringDollar1.v + "\", " + strings.accepting + ", grammar, parser, lexer);");
    out.close(); }catch (FileNotFoundException e) { e.printStackTrace(); }
  
      break;
        default: ; }}}
}

public void ARTRD_test(int artElement, ARTGLLRDTVertex artParent, boolean artWriteable, ARTAT_ART_ID ID1, ARTAT_ART_acceptOpt acceptOpt1, ARTAT_ART_optionOpt optionOpt1, ARTAT_ART_rejectOpt rejectOpt1, ARTAT_ART_stringDollar stringDollar1)  {
ARTGLLRDTVertex artNewParent; boolean artNewWriteable = true;
    for (int artPackedNode = artSPPFNodePackedNodeList(artElement); artPackedNode != 0; artPackedNode = artSPPFPackedNodePackedNodeList(artPackedNode)) {
      if (artSPPFPackedNodeSelected(artPackedNode)) {
        switch (artSPPFPackedNodeLabel(artPackedNode)) {
    /*test ::= ID optionOpt . stringDollar acceptOpt rejectOpt */
    case ARTL_ART_test_22: 
      ID1.rightExtent = artSPPFNodeRightExtent(artSPPFPackedNodeLeftChild(artPackedNode));
      ID1.leftExtent = artSPPFNodeLeftExtent(artSPPFPackedNodeLeftChild(artPackedNode));
      artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeLeftChild(artPackedNode)), ID1));
      ARTRD_ID(artSPPFPackedNodeLeftChild(artPackedNode), artNewParent, artNewWriteable, ID1);
            artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), optionOpt1));
      ARTRD_optionOpt(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable, optionOpt1, null);
            break;
    /*test ::= ID optionOpt stringDollar . acceptOpt rejectOpt */
    case ARTL_ART_test_24: 
      ARTRD_test(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable, ID1, acceptOpt1, optionOpt1, rejectOpt1, stringDollar1);
      stringDollar1.rightExtent = artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode));
      stringDollar1.leftExtent = artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode));
      artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), stringDollar1));
      ARTRD_stringDollar(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable, stringDollar1);
       
  StringBuilder stringBuilder = new StringBuilder(stringDollar1.v + " ");
  StringBuilder startSymbol = new StringBuilder();

  int cc;
  for (cc = 0; cc < stringDollar1.v.length(); cc++) {
    if (stringDollar1.v.charAt(cc) == '_' || Character.isLetter(stringDollar1.v.charAt(cc)))
      break; 
    if (stringDollar1.v.charAt(cc) == '(' && stringDollar1.v.charAt(cc+1) == '*') 
      do 
        cc++;
      while(!(stringDollar1.v.charAt(cc-1) == '*' && stringDollar1.v.charAt(cc) == ')'));
   }

  for (; cc < stringDollar1.v.length(); cc++) {
    if (!(stringDollar1.v.charAt(cc) == '_' || Character.isLetter(stringDollar1.v.charAt(cc))))
      break; 
    startSymbol.append(stringDollar1.v.charAt(cc));
   }

  try { PrintWriter out = new PrintWriter(new File(ID1.v + ".art"));
  if (vv3) {
//    out.println("module M");
  } else {
    out.println("(* " + ID1.v + ".art *)");
    out.println("M()(" + startSymbol.toString() + ")");
  }

  if (vv3) {
    for (int i = 1; i < stringBuilder.length(); i++)
      if (stringBuilder.charAt(i) == ';' && !(stringBuilder.charAt(i-1) == '\'' && stringBuilder.charAt(i+1) == '\'')){
        stringBuilder.replace(i, i+1, " "); 
      }
  }

System.out.println("    grammar = new Grammar(\"" + ID1.v + "\", \"" + stringBuilder.toString() + "\");");
  out.printf("%s", stringBuilder.toString());
  out.close(); }catch (FileNotFoundException e) { e.printStackTrace(); }
  System.err.println("New" + (vv3 ? " vv3" : " v2") + " test file '" + ID1.v + "' with options '" + optionOpt1.v /* + "': grammar '" + stringDollar1.v + "' mapped to '" + stringBuilder.toString() + "'"*/); 
  stringCount = 1;


 acceptOpt1.base = ID1.v; 
      break;
    /*test ::= ID optionOpt stringDollar acceptOpt . rejectOpt */
    case ARTL_ART_test_28: 
      ARTRD_test(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable, ID1, acceptOpt1, optionOpt1, rejectOpt1, stringDollar1);
      artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), acceptOpt1));
      ARTRD_acceptOpt(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable, acceptOpt1, null);
       rejectOpt1.base = ID1.v; 
      break;
    /*test ::= ID optionOpt stringDollar acceptOpt rejectOpt .*/
    case ARTL_ART_test_32: 
      ID1 = new ARTAT_ART_ID();
      acceptOpt1 = new ARTAT_ART_acceptOpt();
      optionOpt1 = new ARTAT_ART_optionOpt();
      rejectOpt1 = new ARTAT_ART_rejectOpt();
      stringDollar1 = new ARTAT_ART_stringDollar();
            ARTRD_test(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable, ID1, acceptOpt1, optionOpt1, rejectOpt1, stringDollar1);
      artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), rejectOpt1));
      ARTRD_rejectOpt(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable, rejectOpt1, null);
            break;
        default: ; }}}
}

public void ARTRD_tests(int artElement, ARTGLLRDTVertex artParent, boolean artWriteable)  {
ARTGLLRDTVertex artNewParent; boolean artNewWriteable = true;
    for (int artPackedNode = artSPPFNodePackedNodeList(artElement); artPackedNode != 0; artPackedNode = artSPPFPackedNodePackedNodeList(artPackedNode)) {
      if (artSPPFPackedNodeSelected(artPackedNode)) {
        switch (artSPPFPackedNodeLabel(artPackedNode)) {
    /*tests ::= test tests .*/
    case ARTL_ART_tests_12: 
            artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeLeftChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeLeftChild(artPackedNode)), null));
      ARTRD_test(artSPPFPackedNodeLeftChild(artPackedNode), artNewParent, artNewWriteable, null, null, null, null, null);
            artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
      ARTRD_tests(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable);
            break;
    /*tests ::= test .*/
    case ARTL_ART_tests_16: 
            ARTRD_tests(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable);
      artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
      ARTRD_test(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable, null, null, null, null, null);
            break;
        default: ; }}}
}

public void ARTRD_text(int artElement, ARTGLLRDTVertex artParent, boolean artWriteable, ARTAT_ART_text text)  {
ARTGLLRDTVertex artNewParent; boolean artNewWriteable = true;
    for (int artPackedNode = artSPPFNodePackedNodeList(artElement); artPackedNode != 0; artPackedNode = artSPPFPackedNodePackedNodeList(artPackedNode)) {
      if (artSPPFPackedNodeSelected(artPackedNode)) {
        switch (artSPPFPackedNodeLabel(artPackedNode)) {
    /*text ::= tests .*/
    case ARTL_ART_text_6: 
      vv3 = text.vv3;
      ARTRD_text(artSPPFPackedNodeLeftChild(artPackedNode), artParent, artWriteable, text);
      artNewWriteable = true; artNewParent = artParent.addChild(artNextFreeNodeNumber++, new ARTGLLRDTPayload(artRDT, artSPPFNodeLeftExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeRightExtent(artSPPFPackedNodeRightChild(artPackedNode)), artSPPFNodeLabel(artSPPFPackedNodeRightChild(artPackedNode)), null));
      ARTRD_tests(artSPPFPackedNodeRightChild(artPackedNode), artNewParent, artNewWriteable);
            break;
        default: ; }}}
}

public void artEvaluate(ARTGLLRDTHandle artElement, ARTGLLAttributeBlock artAttributes, ARTGLLRDTVertex artParent, Boolean artWriteable)  {
  switch (artSPPFNodeLabel(artElement.element)) {
    case ARTL_ART_ID:  ARTRD_ID(artElement.element, artParent, artWriteable, (ARTAT_ART_ID) artAttributes); break;
    case ARTL_ART_acceptOpt:  ARTRD_acceptOpt(artElement.element, artParent, artWriteable, (ARTAT_ART_acceptOpt) artAttributes, null); break;
    case ARTL_ART_optionOpt:  ARTRD_optionOpt(artElement.element, artParent, artWriteable, (ARTAT_ART_optionOpt) artAttributes, null); break;
    case ARTL_ART_rejectOpt:  ARTRD_rejectOpt(artElement.element, artParent, artWriteable, (ARTAT_ART_rejectOpt) artAttributes, null); break;
    case ARTL_ART_stringBrace:  ARTRD_stringBrace(artElement.element, artParent, artWriteable, (ARTAT_ART_stringBrace) artAttributes); break;
    case ARTL_ART_stringDollar:  ARTRD_stringDollar(artElement.element, artParent, artWriteable, (ARTAT_ART_stringDollar) artAttributes); break;
    case ARTL_ART_strings:  ARTRD_strings(artElement.element, artParent, artWriteable, (ARTAT_ART_strings) artAttributes, null, null); break;
    case ARTL_ART_test: ARTRD_test(artElement.element, artParent, artWriteable, null, null, null, null, null); break;
    case ARTL_ART_tests: ARTRD_tests(artElement.element, artParent, artWriteable); break;
    case ARTL_ART_text:  ARTRD_text(artElement.element, artParent, artWriteable, (ARTAT_ART_text) artAttributes); break;
  }
}

};
