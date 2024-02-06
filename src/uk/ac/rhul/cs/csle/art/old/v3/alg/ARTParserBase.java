package uk.ac.rhul.cs.csle.art.old.v3.alg;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Date;

import uk.ac.rhul.cs.csle.art.old.core.OLDDirectives;
import uk.ac.rhul.cs.csle.art.old.util.bitset.ARTBitSet;
import uk.ac.rhul.cs.csle.art.old.util.histogram.ARTHistogram;
import uk.ac.rhul.cs.csle.art.old.util.slotarray.ARTSlotArray;
import uk.ac.rhul.cs.csle.art.old.util.text.ARTText;
import uk.ac.rhul.cs.csle.art.old.util.text.ARTTextHandlerConsole;
import uk.ac.rhul.cs.csle.art.old.util.text.ARTTextHandlerFile;
import uk.ac.rhul.cs.csle.art.old.util.text.ARTTextLevel;
import uk.ac.rhul.cs.csle.art.old.v3.alg.gll.support.ARTGLLAttributeBlock;
import uk.ac.rhul.cs.csle.art.old.v3.alg.gll.support.ARTGLLRDTHandle;
import uk.ac.rhul.cs.csle.art.old.v3.alg.gll.support.ARTGLLRDTVertex;
import uk.ac.rhul.cs.csle.art.old.v3.lex.ARTLexerV3;
import uk.ac.rhul.cs.csle.art.old.v3.manager.grammar.ARTGrammar;
import uk.ac.rhul.cs.csle.art.old.v3.manager.mode.ARTModeGrammarKind;

public abstract class ARTParserBase {
  /**
   * Debug message control
   */
  protected int artTrace;
  protected int artParseTimeLimit = 5000;
  public boolean isTimedOut = false;
  /**
   * Defaut options for generated parsers
   */
  public final OLDDirectives artDirectives;
  /**
   * local text handlers - initialised by ART generated constructor
   */
  public ARTText artText;
  public ARTText artTraceText;
  /**
   * Grammar alphabet enumeration variable elements - these are loaded with the actual values by the generated parser
   */
  public static int ARTL_EOS;
  public static int ARTL_EPSILON;
  public static int ARTL_ANNOTATION;
  public static int ARTL_DUMMY;

  /**
   * Label kind values
   */
  public static final int ARTK_ILLEGAL = 0;
  public static final int ARTK_EOS = 1;
  public static final int ARTK_EPSILON = 2;
  public static final int ARTK_BUILTIN_TERMINAL = 3;
  public static final int ARTK_CHARACTER_TERMINAL = 4;
  public static final int ARTK_CHARACTER_RANGE_TERMINAL = 5;
  public static final int ARTK_CASE_SENSITIVE_TERMINAL = 6;
  public static final int ARTK_CASE_INSENSITIVE_TERMINAL = 7;
  public static final int ARTK_NONTERMINAL = 8;
  public static final int ARTK_INTERMEDIATE = 9;
  public static final int ARTK_END_OF_RULE = 10;
  public static final int ARTK_DO_FIRST = 11;
  public static final int ARTK_OPTIONAL = 12;
  public static final int ARTK_POSITIVE_CLOSURE = 13;
  public static final int ARTK_KLEENE_CLOSURE = 14;

  public static final int ARTFOLD_NONE = 0;
  public static final int ARTFOLD_UNDER = 1;
  public static final int ARTFOLD_OVER = 2;
  public static final int ARTFOLD_TEAR = 3;
  // public static final int ARTFOLD_ROOT = 4;

  public final int artRenderKindIllegal = 0;
  public final int artRenderKindSPPF = 1;
  public final int artRenderKindSPPFFull = 2;
  public final int artRenderKindDerivation = 3;
  public final int artRenderKindDerivationFull = 4;
  public final int artRenderKindGSS = 5;

  /**
   * Parser global variables
   */

  // We essentially have three kinds of parsers, distonguished by the manner in
  // which they express information about the grammar
  public ARTGrammar artGrammar; // Interpreted parsers with the word Instance in their name access a fully
                                // evaluated ARTGrammar object
  public ARTSlotArray artSlotArray; // Interpreted parsers with the words SlotArray in their name access a fully
                                    // evaluated SlotArray object
  // Generated parsers have the grammar rules embedded within them but still need
  // information

  public ARTLexerV3 artLexer;
  public String[] artLabelStrings; // These strings are used by some lexers to find keywords. Slot element sare set
                                   // to ""

  public String[] artLabelInternalStrings; // These strings have the full 'dotted' slot

  public boolean[] artTerminalRequiresWhiteSpace;
  public boolean[] artTerminalCaseInsensitive;
  public int artFirstTerminalLabel;
  public int artFirstUnusedLabel;

  public int artDefaultStartSymbolLabel;
  public int artStartSymbolLabel;
  public String artBuildDirectives;

  public String[] artAnnotations;
  public int[] artPreSlots;
  public int[] artPostSlots;
  public int[] artInstanceOfs;
  public int[] artKindOfs;
  public int[] artUserNameOfs;
  public int[] artSlotInstanceOfs;
  public int artSetExtent;
  public int[] artlhsL;
  public boolean artFIFODescriptors;
  // protected boolean[] artNonterminalsDeclaredAsTerminals;
  protected uk.ac.rhul.cs.csle.art.old.util.bitset.ARTBitSet[] artHigher;
  protected ARTBitSet[] artLonger;
  protected ARTBitSet[] artShorter;

  // Outcome and timing variables set by each run of the parse function
  public ARTModeGrammarKind artGrammarKind = ARTModeGrammarKind.UNKNOWN;
  public boolean artInadmissable = false; // set when, say, a BNF parser is called with an EBNF grammar
  public boolean artIsInLanguage;
  public long artStartTime;
  public long artSetupCompleteTime = 0;
  public long artLexCompleteTime = 0;
  public long artParseCompleteTime = 0;
  public long artDerivationSelectCompleteTime = 0;
  public long artAttributeEvaluateCompleteTime = 0;
  public long arteSOSInterpretCompleteTime = 0;
  public long artParseStartMemory = 0, artParseEndMemory = 0;

  // Statistics
  public String artSpecificationName = "Specification name not set";
  public String artParserKind = "Kind not set";
  public long artSPPFNodeCardinality;
  public long artSPPFNodeFinds;
  public long artSPPFPackedNodeCardinality;
  public long artSPPFPackedNodeFinds;
  public long artGSSNodeCardinality;
  public long artGSSNodeFinds;
  public long artGSSEdgeCardinality;
  public long artGSSEdgeFinds;
  public long artPopElementCardinality;
  public long artPopElementFinds;
  public long artDescriptorCardinality;
  public long artDescriptorFinds;
  public long artTestRepeatElementCardinality;
  public long artTestRepeatElementFinds;
  public long artClusterElementCardinality;
  public long artClusterElementFinds;
  public ARTHistogram artSPPFNodeHistogram;
  public ARTHistogram artSPPFPackedNodeHistogram;
  public ARTHistogram artGSSNodeHistogram;
  public ARTHistogram artGSSEdgeHistogram;
  public ARTHistogram artPopElementHistogram;
  public ARTHistogram artDescriptorHistogram;
  public ARTHistogram artTestRepeatElementHistogram;
  public ARTHistogram artClusterElementHistogram;
  public ARTHistogram artOverallHistogram;
  public long artSPPFEdges;
  public long artSPPFEpsilonNodes;
  public long artSPPFTerminalNodes;
  public long artSPPFNonterminalNodes;
  public long artSPPFIntermediateNodes;
  public long artSPPFOtherNodes;
  public long artSPPFAmbiguityNodes;
  public long artPoppingDescriptors;
  public long artNonpoppingDescriptors;
  public long artPrimaryPops;
  public long artContingentPops;
  public long artHashCollisions;
  public long artHashtableResizes;

  /**
   * Constructors
   */
  static ARTModeGrammarKind artGrammarKind(ARTGrammar artGrammar) {
    ARTModeGrammarKind artGrammarKind = ARTModeGrammarKind.UNKNOWN;
    if (artGrammar.isFBNF()) artGrammarKind = ARTModeGrammarKind.FBNF;
    if (artGrammar.isEBNF()) artGrammarKind = ARTModeGrammarKind.EBNF;
    if (!artGrammar.isFBNF() && !artGrammar.isEBNF()) artGrammarKind = ARTModeGrammarKind.BNF;
    return artGrammarKind;
  }

  public ARTParserBase(ARTGrammar artGrammar, ARTLexerV3 artLexer, ARTModeGrammarKind artGrammarKind) {
    this.artGrammar = artGrammar;
    if (artGrammar != null)
      artDirectives = artGrammar.artDirectives;
    else
      artDirectives = new OLDDirectives();
    artText = new ARTText(new ARTTextHandlerConsole());
    this.artLexer = artLexer;
    if (artLexer != null) artLexer.artSetParser(this);
    if (artGrammar != null)
      this.artGrammarKind = artGrammar.getGrammarKind();
    else
      this.artGrammarKind = artGrammarKind;
  }

  public ARTParserBase(ARTGrammar artGrammar, ARTLexerV3 artLexer) {
    this(artGrammar, artLexer, ARTModeGrammarKind.UNKNOWN);
  }

  public ARTParserBase(ARTGrammar artGrammar) {
    this(artGrammar, null, ARTModeGrammarKind.UNKNOWN);
  }

  public ARTParserBase() {
    this(null, null, ARTModeGrammarKind.UNKNOWN);
  }

  public ARTParserBase(ARTSlotArray artSlotArray) {
    this(null, null, artSlotArray.artGrammarKind);
    this.artSlotArray = artSlotArray;
  }

  /**
   * Table initialisation
   */
  public void artInitialiseIntegerArray(int[] array, int lo, int hi) {
    for (int tmp = lo; tmp < hi; tmp++)
      array[tmp] = tmp;
  }

  public void artInitialiseIntegerArray(int[] array, int lo, int hi, int value) {
    for (int tmp = lo; tmp < hi; tmp++)
      array[tmp] = value;
  }

  public void artInitialiseStringArray(String[] array, int lo, int hi, String value) {
    for (int tmp = lo; tmp < hi; tmp++)
      array[tmp] = value;
  }

  public void artInitialiseBooleanArray(boolean[] array, int lo, int hi, boolean value) {
    for (int tmp = lo; tmp < hi; tmp++)
      array[tmp] = value;
  }

  public void artSetLexer(ARTLexerV3 artGLLLexer) {
    artLexer = artGLLLexer;
  }

  public void setSlotArray(ARTSlotArray slotArray) {
    this.artSlotArray = slotArray;
  }

  /**
   * Timing methods
   */
  public void artRestartClock() {
    artStartTime = System.currentTimeMillis();
  }

  public long artReadClock() {
    return System.currentTimeMillis() - artStartTime;
  }

  public double artTimeAsSeconds(long time) {
    double ret = time / 1.0E3;
    if (ret < 1.0E-9) ret = 0.0;
    return ret;
  }

  public String artGetTimes() {
    return String.format("%s, Setup, %.3f, Lex, %.3f, Parse, %.3f, Derivation select, %.3f, Attribute evaluate, %.3f, eSOS interpret, %.3f", artParserKind,
        artTimeAsSeconds(artSetupCompleteTime), artTimeAsSeconds(artLexCompleteTime), artTimeAsSeconds(artParseCompleteTime),
        artTimeAsSeconds(artDerivationSelectCompleteTime), artTimeAsSeconds(artAttributeEvaluateCompleteTime), artTimeAsSeconds(arteSOSInterpretCompleteTime));
  }

  /**
   * Public parser access - many of these are expected to be overridden. They have not been made abstract so as to reduce the burden on derived classes
   *
   * @
   */
  public void artParse(String inputString) {
    artParse(inputString, artDefaultStartSymbolLabel, null);
  }

  public void artParse(String inputString, ARTGLLAttributeBlock startAttributes) {
    artParse(inputString, artDefaultStartSymbolLabel, startAttributes);
  }

  public void artParse(String inputString, int nonterminalLabel, ARTGLLAttributeBlock startAttributes) {
  }

  public void artComputeParseCounts() {
  }

  public int artGetNonterminalLabel(String nonterminalName) {
    for (int s = 0; s < artFirstUnusedLabel; s++)
      if (artKindOfs[s] == ARTK_NONTERMINAL && artLabelStrings[s].equals(nonterminalName)) return s;

    return ARTL_EOS;
  }

  public void artDerivationSelectAll() {
  }

  public void artSPPFSelectOne() {
  }

  public boolean artDerivationSelectNext() {
    return false;
  }

  public boolean computeIsAmbiguous(String str) {
    return true;
  }

  public void artChoose() {
  }

  public void artDisambiguateOrderedLongest() {
  }

  public ARTLexerV3 artSPPFCollectTWESet(boolean postUseTerminals) {
    return null;
  }

  public void artEvaluator() {
    artEvaluator(null);
  };

  public void artEvaluator(ARTGLLAttributeBlock startAttributes) {
  }

  protected void artEvaluate(ARTGLLRDTHandle element, ARTGLLAttributeBlock startAttributes) {
    artEvaluate(element, startAttributes, new ARTGLLRDTVertex(null, null), false);
  }

  protected void artEvaluate(ARTGLLRDTHandle element, ARTGLLAttributeBlock artAttributes, ARTGLLRDTVertex parent, Boolean writeable) {
  }

  protected void artWriteSPPF(ARTText text, int renderKind, ARTLexerV3 tweSet) {
  }

  public void artWriteSPPF(String filename, int artRenderKind) {
    artRenderSPPF(filename, artRenderKind, null);
  }

  public void artRenderSPPF(String filename, int artRenderKind, ARTLexerV3 tweSet) {
    ARTText text = new ARTText(new ARTTextHandlerFile(filename));
    text.println("digraph \"Graph from GLL parser\"{");
    text.println("graph[ordering=out]");
    text.println("node[fontname=Helvetica fontsize=9 shape=box height=0 width=0 margin=0.04 color=gray]");
    text.println("edge[arrowsize=0.1 color=gray]");
    artWriteSPPF(text, artRenderKind, tweSet);
    text.println("}");
    text.close();
  }

  protected boolean artNotBNF() {
    if (artGrammar == null) {
      if (artSlotArray.artGrammarKind != ARTModeGrammarKind.BNF) {
        artInadmissable = true;
        return true;
      }
    } else if (artGrammar.getGrammarKind() != ARTModeGrammarKind.BNF) {
      artInadmissable = true;
      return true;
    }
    return false;
  }

  public void artLog(String inputFilename, Boolean console) {
    try {
      PrintStream logStream = new PrintStream("log.csv");

      int left = inputFilename.lastIndexOf('.') + 1;
      int right = Math.min(inputFilename.length(), left + 3);

      String inputFiletype = inputFilename.substring(left, right);

      int pathSeparatorIndex = 0;
      if (inputFilename.lastIndexOf('/') != -1)
        pathSeparatorIndex = inputFilename.lastIndexOf('/') + 1;
      else if (inputFilename.lastIndexOf('\\') != -1) pathSeparatorIndex = inputFilename.lastIndexOf('\\') + 1;
      String shortInputFilename = inputFilename.substring(pathSeparatorIndex);

      String status = "--";
      if (inputFiletype.equals("acc") || inputFiletype.equals("rej")) {
        status = "bad";
        if ((inputFiletype.equals("acc") && artIsInLanguage) || (inputFiletype.equals("rej") && !artIsInLanguage)) status = "good";
      }
      if (artInadmissable) status = "inadmissable";

      int localInputLength = artLexer == null ? 0 : artLexer.artInputLength - 1;
      if (localInputLength <= 0) localInputLength = 1;

      String msg = String.format("%s,%s,%s,%s,%s,%s,%d,%s", this.getClass().getSimpleName(), artGrammarKind.toString(), shortInputFilename,
          artIsInLanguage ? "accept" : "reject", status, new Date(), localInputLength, artGetTimes());

      msg += artLogStats();
      // msg += String.format(",%s", artBuildOptions);

      if (console)
        System.out.println(msg);
      else
        logStream.println(msg);

      logStream.close();

      if (inputFilename.indexOf("deliberatelyCrash") != -1) artText.printf(ARTTextLevel.FATAL, "Exiting");
    } catch (FileNotFoundException e) {
      System.out.println("Unable to write to 'log.csv'");
    }
  }

  public String artLogStats() {
    String msg = "";
    if (artSPPFNodeHistogram != null) msg += String.format(
        ",descriptor,%d,%d,sppfNode,%d,%d,sppfPackedNode,%d,%d,gssNode,%d,%d,gssEdge,%d,%d,popElement,%d,%d,testRepepeat,%d,%d,clusterElement,%d,%d,"
            + "sppfEpsilonNodes,%d,sppfTerminalNodes,%d,sppfNonterminalNodes,%d,sppfIntermediateNodes,%d,sppfOtherNodes,%d,sppfAmbiguityNodes,%d,"
            + "poppingDescriptors,%d,nonpoppingDescriptors,%d,primaryPops,%d,contingentPops,%d,"
            + "softPageFaults,%d,hardPagefaults,%d,hashCollisions,%d,hashResizes,%d,hash0,%d,hash1,%d,hash2,%d,hash3,%d,hash4,%d,hash5+,%d",

        artDescriptorFinds, artDescriptorHistogram.weightedSumBuckets(), artSPPFNodeFinds, artSPPFNodeHistogram.weightedSumBuckets(), artSPPFPackedNodeFinds,
        artSPPFPackedNodeHistogram.weightedSumBuckets(), artGSSNodeFinds, artGSSNodeHistogram.weightedSumBuckets(), artGSSEdgeFinds,
        artGSSEdgeHistogram.weightedSumBuckets(), artPopElementFinds, artPopElementHistogram.weightedSumBuckets(), artTestRepeatElementFinds,
        artTestRepeatElementHistogram.weightedSumBuckets(), artClusterElementFinds, artClusterElementHistogram.weightedSumBuckets(), artSPPFEpsilonNodes,
        artSPPFTerminalNodes, artSPPFNonterminalNodes, artSPPFIntermediateNodes, artSPPFOtherNodes, artSPPFAmbiguityNodes, artPoppingDescriptors,
        artNonpoppingDescriptors, artPrimaryPops, artContingentPops,

        -1, -1, artHashCollisions, artHashtableResizes, artOverallHistogram.bucketValue(0), artOverallHistogram.bucketValue(1),
        artOverallHistogram.bucketValue(2), artOverallHistogram.bucketValue(3), artOverallHistogram.bucketValue(4), artOverallHistogram.sumBucketsFrom(5));

    msg += String.format(",descriptorH,%s", artDescriptorHistogram);
    msg += String.format(",sppfNodeH,%s", artSPPFNodeHistogram);
    msg += String.format(",sppfPackedNodeH,%s", artSPPFPackedNodeHistogram);
    msg += String.format(",gssNodeH,%s", artGSSNodeHistogram);
    msg += String.format(",gssEdgeH,%s", artGSSEdgeHistogram);
    msg += String.format(",popElementH,%s", artPopElementHistogram);
    msg += String.format(",testRepeatElementH,%s", artTestRepeatElementHistogram);
    return msg;
  }

  public abstract void artWriteRDT(String filename);

  public abstract void artPrintRDT();

  public long artMemoryUsed() {
    System.gc();
    return Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
  }

}