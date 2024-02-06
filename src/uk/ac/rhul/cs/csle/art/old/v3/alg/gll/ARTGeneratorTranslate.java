package uk.ac.rhul.cs.csle.art.old.v3.alg.gll;

import uk.ac.rhul.cs.csle.art.old.util.text.ARTText;

public abstract class ARTGeneratorTranslate {
  int indentLevel = 0;

  abstract String targetLanguageName();

  protected ARTText text;

  ARTText getText() {
    return text;
  }

  /* names */
  String booleanName() {
    return null;
  }

  String trueName() {
    return null;
  }

  String falseName() {
    return null;
  }

  String integerName() {
    return null;
  }

  String stringName() {
    return null;
  }

  String nullName() {
    return null;
  }

  String inputAccessToken() {
    return null;
  }

  String inputAccessLeftExtent() {
    return null;
  }

  String inputAccessFirstSuccessorReference() {
    return null;
  }

  /* text formatting */
  void newLine() {
  }

  void indent() {
  }

  void indentSet(int i) {
    indentLevel = i;
  }

  void indentUp() {
    indentLevel++;
  }

  void indentDown() {
    if (indentLevel > 0) indentLevel--;
  }

  void comment(String s) {
  }

  /* control flow */
  void ifInSet(String set, String var) {
  }

  void ifNotInSet(String set, String var) {
  }

  void ifNot(String var) {
  }

  void ifNull(String var) {
  }

  void ifFunction(String var) {
  }

  void ifTestRepeat(String var, String p1, String p2, String p3) {
  }

  void whileTrue(String label) {
  }

  void forSuccessorPair() {
  }

  String inputSuccessorReference() {
    return null;
  }

  String inputSuccessorReferenceToken() {
    return null;
  }

  String inputSuccessorReferenceLeftExtent() {
    return null;
  }

  void exception(String id) {
  }

  void blockOpen() {
  }

  void blockClose() {
  }

  void caseOpen(String id) {
  }

  void caseBranchOpen(String id, boolean flowThrough) {
  }

  void caseBranchClose(String id) {
  }

  void caseDefault() {
  }

  void caseClose(String id, boolean flowThrough) {
  }

  void jump(String id) {
  }

  void jumpDynamic(String id) {
  }

  void jumpState(String id) {
  }

  void jumpFragment(String id) {
  }

  void label(String label) {
  }

  void ret() {
  }

  void brk() {
  }

  /* file handling */
  void fileOpen(String filename, String nameSpace) {
  }

  void fileClose(String filename, String nameSpace) {
  }

  /* declarations and assignments */
  void classOpen(String id) {
  }

  void classOpen(String id, String superClassName) {
  }

  void classOpen(String id, String superClassName, String interfaceName) {

  }

  void classClose(String id) {
  }

  void enumerationOpen(String id) {
  }

  void enumerationElement(String id) {
  }

  void enumerationClose(String id) {
  }

  void constructorOpen(String id) {
  }

  void constructorOpen(String id, String p1Type, String p1) {
  }

  void constructorOpen(String id, String p1Type, String p1, String p2Type, String p2) {
  }

  void constructorOpenRef(String id, String p1Type, String p1) {
  }

  void constructorClose(String id) {
  }

  void functionVoidForward(String id) {
  }

  void functionVoidOpen(String id) {
  }

  void functionVoidOpen(String id, String p1Type, String p1) {
  }

  void functionVoidOpenForward(String id, String p1Type, String p1) {
  }

  void functionVoidOpen(String id, String p1Type, String p1, String p2Type, String p2) {
  }

  void functionVoidOpenRef(String id, String p1Type, String p1) {
  }

  void functionClose(String id) {
  }

  void functionCall(String id) {
  }

  void functionCall(String id, String p1) {
  }

  void functionCall(String id, String p1, String p2) {
  }

  void functionCall(String id, String p1, String p2, String p3) {
  }

  void functionCall(String id, String p1, String p2, String p3, String p4) {
  }

  void functionAssignCall(String variable, String id) {
  }

  void functionAssignCall(String variable, String id, String p1) {
  }

  void functionAssignCall(String variable, String id, String p1, String p2) {
  }

  void functionAssignCall(String variable, String id, String p1, String p2, String p3) {
  }

  void functionAssignCall(String variable, String id, String p1, String p2, String p3, String p4) {
  }

  void assign(String id, String value) {
  }

  void assignString(String id, String value) {
  }

  void assignAppendNull(String id, String value) {
  }

  void declareBoolean(String id) {
  }

  void declareBoolean(String id, String value) {
  }

  void declareBooleanArray(String id) {
  }

  void allocateBooleanArray(String id, String extent) {
  }

  void assignBooleanArrayElement(String id, String index, String value) {
  }

  void allocateBitSetArray(String id, String extent) {
  }

  void assignBitSetArrayElement(String id, String index, String value, String max) {
  }

  void declareInteger(String id) {
  }

  void declareInteger(String id, String value) {
  }

  void declareIntegerArray(String id) {
  }

  void allocateIntegerArray(String id, String extent) {
  }

  void assignIntegerArrayElement(String id, String index, String value) {
  }

  void declareEnumeration(String enumId, String id) {
  }

  void declareEnumeration(String enumId, String id, String value) {
  }

  void declareEnumerationArray(String enumId, String id) {
  }

  void allocateEnumerationArray(String enumId, String id, String extent) {
  }

  void assignEnumerationArrayElement(String id, String index, String value) {
  }

  void declareString(String id) {
  }

  void declareString(String id, String value) {
  }

  void declareStringArray(String id, String extent) {
  }

  void allocateStringArray(String id, String extent) {
  }

  void assignStringArrayElement(String id, String index, String value) {
  }

  /* Lexer builtin support */
  void lexerBuiltInInstance(String builtinId, String terminalId) {
  }

  void lexerWhitespaceBuiltinInstance(String id) {
  }

  void lexerWhitespaceCharInstance(String id) {
  }

}
