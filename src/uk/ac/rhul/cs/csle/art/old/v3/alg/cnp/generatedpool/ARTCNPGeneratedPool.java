package uk.ac.rhul.cs.csle.art.old.v3.alg.cnp.generatedpool;
// package uk.ac.rhul.cs.csle.art.v3.alg.cnp.generatedpool;
//
// import java.time.ZonedDateTime;
//
// import uk.ac.rhul.cs.csle.art.util.ARTException;
// import uk.ac.rhul.cs.csle.art.util.pool.ARTPool;
// import uk.ac.rhul.cs.csle.art.util.text.ARTText;
// import uk.ac.rhul.cs.csle.art.util.text.ARTTextHandlerConsole;
// import uk.ac.rhul.cs.csle.art.v3.alg.ARTParserBase;
// import uk.ac.rhul.cs.csle.art.v3.lex.ARTLexerV3;
//
// public abstract class ARTCNPGeneratedPool extends ARTParserBase {
//
// protected boolean artGeneratedFromBNF = true;
//
// protected ARTPool pool;
// protected int startSymbol;
// protected int epsilonSlot;
// protected int[][] productionFirstSlots;
// protected int[] slotRightSymbols;
// protected int[] slotProductionL;
// protected int[] prefixLengths;
// protected int[] prefixSlotMap;
// protected int[] startProductions;
// protected boolean[] isEpsilonSlotOrZeroSlot;
// protected boolean[][] sets;
// protected boolean[][] slotFirstSets;
// protected boolean[][] nonterminalFollowSets;
//
// // variables from theCNP algorithm
// protected int[] input;
// protected int m;// Length of the input
// protected int cU;
// protected int cI;
// protected int artSlot;
// protected int descriptorFinds;
// protected int bsrFinds; // Count accesses to upsilon, NOT including scan of upsilon for start candiates in acceptance testing
// protected int crfKeyFinds;
// protected int crfLeafFinds;
//
// // Structure of a bsr set element: 4_0
// protected final int bsrElementSlotOffset = 0;
// protected final int bsrElementIOffset = 1;
// protected final int bsrElementJOffset = 2;
// protected final int bsrElementKOffset = 3;
//
// protected String bsrElementToString(int base) {
// if (base == 0) return "null";
// return "<" + pool.poolGet(base) + ", " + pool.poolGet(base + 1) + ", " + pool.poolGet(base + 2) + ", " + pool.poolGet(base + 3) + ">";
// }
//
// protected int upsilon; // A set of bsrElements
//
// // Structure of a descriptor: 3_0
// protected final int descriptorSlotOffset = 0;
// protected final int descriptorKOffset = 1;
// protected final int descriptorIOffset = 2;
//
// protected int U; // A set of descriptors
//
// // structure of a descriptor list element: the base address of a descriptor, and the base address of the next list element
// protected final int descriptorListElementDescriptorOffset = 0;
// protected final int descriptorListElementNextOffset = 1;
//
// protected int R; // A list of references to descriptors
//
// // structure of a CRF leaf node
// protected final int CRFleafSlotOffset = 0;
// protected final int CRFleafHOffset = 1;
//
// // Structure of a CRF node which is a map from (nonterminal X h) -> (popSetPartition x leafSet) which is a 2_2
// // These nodes encode both the Call-Return Forest and the P set
// protected final int CRFNodeNonterminalOffset = 0;
// protected final int CRFNodeHOffset = 1;
// protected final int CRFNodePopSetPartitionOffset = 2;
// protected final int CRFNodeLeafSetOffset = 3;
//
// protected int CRF; // The Call-Return Forest
//
// // Hash table sizes
// protected final int largePrime = 99999989;
// protected final int upsilonBucketCount = 500000;
// protected final int UBucketCount = 500000;
// protected final int CRFBucketCount = 500000;
// protected final int CRFPopSetPartitionBucketCount = 100;
// protected final int CRFleafSetBucketCount = 100;
//
// // Make and return an array of integers
// protected int[] intArray(int... firstSlotList) {
// return firstSlotList;
// }
//
// // Make a boolean array of length size and setelements adresses by subsequent arguments to true
// boolean[] boolArray(int size, int... trues) {
// boolean[] ret = new boolean[size];
// for (int i = 0; i < trues.length; i++)
// ret[i] = true;
// return ret;
// }
//
// String setToString(boolean[] set) {
// String ret = "{ ";
// for (int i = 0; i < set.length; i++)
// if (set[i]) ret += artLabelStrings[i] + " ";
// return ret + "}";
// }
//
// // ntAdd() needs a sequence of production first slots for each nonterminal: access via generated functions
// // productionFirstSlots[X], slotFirstSets[X], nonterminalFollowSets[X]
// protected void ntAdd(int N, int i) throws ARTException {
// if (artTrace) artTraceText.println("ntAdd(" + artLabelStrings[N] + ", " + i + ")");
// for (int p = 0; productionFirstSlots[N][p] != 0; p++) {
// int firstSlot = productionFirstSlots[N][p]; // Add first slot in production, not the production label itself - already computed
// if (testSelect(input[i], N, slotFirstSets[firstSlot], nonterminalFollowSets[N])) dscAdd(firstSlot, i, i);
// }
// }
//
// // testSelect() needs first(alpha) and follow(X)
// // pass in first(alpha) instead of alpha and follow(X) instead of X
// protected boolean testSelect(int b, int X, boolean[] firstAlpha, boolean[] followX) {
// if (artTrace)
// artTraceText.println("testSelect(" + artLabelStrings[b] + ", " + artLabelStrings[X] + ", " + setToString(firstAlpha) + ", " + setToString(followX) + ")");
// return firstAlpha[b] || firstAlpha[epsilonSlot] && followX[b];
// }
//
// // dscAdd() is independent of slotArray
// // no changes required
// protected void dscAdd(int slot, int k, int i) {
// if (artTrace) artTraceText.println("dscAdd(" + artLabelStrings[slot] + ", " + k + ", " + i + ")");
//
// descriptorFinds++;
// int added = pool.mapFind_3_0(U, slot, k, i);
// if (!pool.found) {
// int newListElement = pool.poolAllocate(2);
// pool.poolPut(newListElement + descriptorListElementDescriptorOffset, added);
// pool.poolPut(newListElement + descriptorListElementNextOffset, R);
// R = newListElement;
// }
// }
//
// // rtn() is independent of slotArray
// // no changes required
// protected void rtn(int X, int k, int j) throws ARTException {
// if (artTrace) artTraceText.println("rtn(" + artLabelStrings[X] + ", " + k + ", " + j + ")");
//
// int CRFNode = pool.mapFind_2_2(CRF, X, k); // This must already exist
// crfKeyFinds++;
//
// int popSetPartition = pool.poolGet(CRFNode + CRFNodePopSetPartitionOffset);
// int leafSet = pool.poolGet(CRFNode + CRFNodeLeafSetOffset);
//
// pool.mapFind_1_0(popSetPartition, j);
// if (!pool.found) {
// for (int v = pool.mapIteratorFirst1(leafSet); v != 0; v = pool.mapIteratorNext1()) {
// int slot = pool.poolGet(v + CRFleafSlotOffset);
// int H = pool.poolGet(v + CRFleafHOffset);
//
// dscAdd(slot, H, j);
// bsrAdd(slot, H, k, j);
// }
// }
// }
//
// protected void call(int L, int i, int j) throws ARTException {
// if (artTrace) artTraceText.println("call(" + artLabelStrings[L] + ", " + i + ", " + j + ")");
//
// int X = slotRightSymbols[L - 1];
// // int u = pool.mapFind2_2(CRF, L, i);
// crfLeafFinds++;
//
// // This is normalisation of the leaf element, it keeps the leaf cardinality to its real value. We shalln't use this in HashPool
// // BUT MAYBE WE SHOULD!
// // if (CRFleaves.containsKey(u))
// // u = CRFleaves.get(u);
// // else
// // CRFleaves.put(u, u);
//
// int crfNode = pool.mapFind_2_2(CRF, X, j); // Add (X,j) to CRF
// crfKeyFinds++;
// if (!pool.found) {
// pool.poolPut(crfNode + CRFNodeLeafSetOffset, pool.mapMake(CRFleafSetBucketCount)); // Allocate the leaf set for the new CRF node (X,j)
// pool.poolPut(crfNode + CRFNodePopSetPartitionOffset, pool.mapMake(CRFPopSetPartitionBucketCount)); // Allocate the pop set partition for the
// // new CRF node (X,j)
// pool.mapFind_2_2(pool.poolGet(crfNode + CRFNodeLeafSetOffset), L, i); // Add (L,i) to the leaf set
// // protected void ntAdd(int N, int i, int[] firstSlots, boolean[][] firstSets, boolean[] follow_N) throws ARTException {
// ntAdd(X, j);
// } else {
// int leafSet = pool.poolGet(crfNode + CRFNodeLeafSetOffset);
// pool.mapFind_2_2(leafSet, L, i); // Add (L,i) to the leaf set
// if (!pool.found) { // If the leaf set for (X, j) does not contain (L, i)
// int popSetPartition = pool.poolGet(crfNode + CRFNodePopSetPartitionOffset);
// for (int hh = pool.mapIteratorFirst1(popSetPartition); hh != 0; hh = pool.mapIteratorNext1()) {
// int h = pool.poolGet(hh);
// if (artTrace) artTraceText.println("Contingent PP actions for cluster node " + crfNode + " index = " + h);
// dscAdd(L, i, h);
// bsrAdd(L, i, j, h);
// }
// }
// }
// }
//
// // bsrAdd() needs isEpsilonSlotOrZeroSlot(slot)
// protected void bsrAdd(int slot, int i, int k, int j) throws ARTException {
// if (artTrace) artTraceText.print("bsrAdd(" + artLabelStrings[slot] + ", " + i + ", " + k + ", " + j + ")");
// int added = 0;
// if (isEpsilonSlotOrZeroSlot[slot]) {
// added = pool.mapFind_4_0(upsilon, slotProductionL[slot], i, k, j);
// bsrFinds++;
// } else if (prefixLengths[slot] > 1) {
// added = pool.mapFind_4_0(upsilon, prefixSlotMap[slot], i, k, j);
// bsrFinds++;
// }
// if (artTrace) artTraceText.println(" added " + bsrElementToString(added));
// }
//
// // public void artLog(String inputFilename) throws FileNotFoundException {
// // PrintStream logStream = new PrintStream("log.csv");
// //
// // int left = inputFilename.lastIndexOf('.') + 1;
// // int right = Math.min(inputFilename.length(), left + 3);
// //
// // String inputFiletype = inputFilename.substring(left, right);
// //
// // int pathSeparatorIndex = 0;
// // if (inputFilename.lastIndexOf('/') != -1)
// // pathSeparatorIndex = inputFilename.lastIndexOf('/') + 1;
// // else if (inputFilename.lastIndexOf('\\') != -1) pathSeparatorIndex = inputFilename.lastIndexOf('\\') + 1;
// // String shortInputFilename = inputFilename.substring(pathSeparatorIndex);
// //
// // String status = "good";
// // if (inputFiletype.equals("acc") || inputFiletype.equals("rej")) {
// // if ((inputFiletype.equals("acc") && !artIsInLanguage) || (inputFiletype.equals("rej") && artIsInLanguage)) status = "bad";
// // } else
// // status = "--";
// //
// // String msg = String.format("%s,%s,%s,%s,%s,%s,%s,%d,,, Parse: %f,,,", "CNPInterpretSlotArrayPool", "BNF", "start", shortInputFilename,
// // artIsInLanguage ? "accept" : "reject", status, new Date(), m, artParseCompleteTime / 1.0E9);
// // if (U != 0) {
// // msg += String.format(",descriptor,%d,%s", descriptorFinds, pool.mapCardinality(U));
// // msg += String.format(",,,,BSRSet,%d,%s", bsrFinds, pool.mapCardinality(upsilon));
// // msg += String.format(",CRF cluster node,%d,%s", crfKeyFinds, pool.mapCardinality(CRF));
// // msg += String.format(",CRF leaf node,%d,%s", crfLeafFinds, "0"); // construct the union over the CRF leaf node sets);
// // int CRFEdges = 0;
// // for (int c = pool.iteratorFirst1(CRF); c != 0; c = pool.iteratorNext1())
// // CRFEdges += pool.mapCardinality(pool.poolGet(c + CRFNodeLeafSetOffset));
// // msg += String.format(",CRF edges,%s", CRFEdges);
// // }
// // logStream.println(msg);
// // logStream.close();
// //
// // // Now render the SPPF
// // // upsilon.toDot("CNPBSRSPPF.dot", m);
// // }
// //
// @Override
// public void artParse(String stringInput) throws ARTException {
// if (artTrace) {
// // traceText = new ARTText(new ARTTextHandlerFile("CNPGeneratedTrace.txt"));
// artTraceText = new ARTText(new ARTTextHandlerConsole());
// artTraceText.println("CNP trace " + ZonedDateTime.now());
// }
//
// artIsInLanguage = false;
//
// if (!artGeneratedFromBNF) {
// if (artTrace) artTraceText.println(this.getClass() + " called on EBNF grammar aborting");
// artInadmissable = true;
// return;
// }
//
// input = ARTLexerV3.lexicaliseToArrayOfIntForCNP(stringInput, 0, artLabelStrings, epsilonSlot);
//
// if (input == null) {
// if (artTrace) artTraceText.println("Lexical reject");
// System.out.println("Lexical reject");
// return;
// } else
// // System.out.println("Lexed: " + input)
// ;
//
// pool = new ARTPool(20, 1024); // 1024 x 1Mlocation blocks: at 32-bit integers that is 4G of memory when fully allocated
//
// artRestartClock();
// m = input == null ? 0 : input.length - 1;
// if (artTrace) {
// artTraceText.println("Parsing " + m + " tokens");
// for (int i = 0; i < input.length; i++)
// artTraceText.print(artLabelStrings[input[i]] + " ");
// artTraceText.println();
// }
// descriptorFinds = bsrFinds = crfKeyFinds = crfLeafFinds = 0;
// upsilon = pool.mapMake(upsilonBucketCount);
//
// U = pool.mapMake(UBucketCount);
// R = 0; // Set the list base to 'null'
// CRF = pool.mapMake(CRFBucketCount);
// int crfNode = pool.mapFind_2_2(CRF, startSymbol, 0);
// pool.poolPut(crfNode + CRFNodeLeafSetOffset, pool.mapMake(CRFleafSetBucketCount)); // Allocate the leaf set for the new CRF node (X,j)
// pool.poolPut(crfNode + CRFNodePopSetPartitionOffset, pool.mapMake(CRFPopSetPartitionBucketCount)); // Allocate the pop set partition for the
//
// crfKeyFinds++;
// ntAdd(startSymbol, 0);
//
// while (true) {
// if (R == 0) {
// artParseCompleteTime = artReadClock();
//
// artIsInLanguage = false;
//
// if (artTrace) artTraceText.println("Acceptance testing against start symbol " + artLabelStrings[startSymbol] + " with right extent " + m);
//
// for (int pi = 0; startProductions[pi] != 0; pi++) { // i is the production number of the start Symbol
// int p = startProductions[pi];
// for (int u = pool.mapIteratorFirst1(upsilon); u != 0; u = pool.mapIteratorNext1()) { // iterate over pool elements
// artIsInLanguage |= pool.poolGet(u + bsrElementSlotOffset) == p && pool.poolGet(u + bsrElementIOffset) == 0
// && pool.poolGet(u + bsrElementKOffset) == m;
// if (artTrace) artTraceText.println("Test " + p + ": " + artLabelStrings[p] + " against " + pool.poolGet(u + bsrElementSlotOffset) + ":"
// + bsrElementToString(u) + " yields " + artIsInLanguage);
// }
// }
//
// if (artTrace) {
// artTraceText.println("Final descriptor set (U): " + U);
// artTraceText.print("Final BSR set (upsilon): ");
// for (int i = pool.mapIteratorFirst1(upsilon); i != 0; i = pool.mapIteratorNext1())
// artTraceText.print(bsrElementToString(i));
// artTraceText.println();
// artTraceText.println("Final CRF: " + CRF);
// artTraceText.println((artIsInLanguage ? "Accept" : "Reject") + " in " + artParseCompleteTime * 1E-6 + "ms");
// artTraceText.close();
// }
//
// System.out.println("CNPGeneratedPool " + (artIsInLanguage ? "accept" : "reject") + " in " + artParseCompleteTime * 1E-6 + "ms");
//
// if (artTrace) artTraceText.close();
// return;
// } else {
// // Unload a descriptor
// int descriptor = pool.poolGet(R + descriptorListElementDescriptorOffset);
// R = pool.poolGet(R + descriptorListElementNextOffset);
// if (artTrace) artTraceText.println("\nProcessing descriptor " + descriptor + " R is now " + R);
// artSlot = pool.poolGet(descriptor + descriptorSlotOffset);
// cU = pool.poolGet(descriptor + descriptorKOffset);
// cI = pool.poolGet(descriptor + descriptorIOffset);
//
// artCNPParseBody(); // interpret or template if overridden
// }
// }
// }
//
// protected abstract void artCNPParseBody() throws ARTException;
// }
