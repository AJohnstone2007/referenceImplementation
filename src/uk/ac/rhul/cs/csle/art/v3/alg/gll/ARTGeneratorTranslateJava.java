package uk.ac.rhul.cs.csle.art.v3.alg.gll;

import uk.ac.rhul.cs.csle.art.util.text.ARTText;

public class ARTGeneratorTranslateJava extends ARTGeneratorTranslate {

  public ARTGeneratorTranslateJava(ARTText text) {
    super();
    this.text = text;
  }

  @Override
  String targetLanguageName() {
    return "Java";
  }

  String className;

  @Override
  String booleanName() {
    return "boolean";
  }

  @Override
  String trueName() {
    return "true";
  }

  @Override
  String falseName() {
    return "false";
  }

  @Override
  String integerName() {
    return "int";
  }

  @Override
  String stringName() {
    return "String";
  }

  @Override
  String nullName() {
    return "null";
  }

  @Override
  String inputAccessToken() {
    return "artInputPairBuffer[artCurrentInputPairReference]";
  }

  @Override
  String inputAccessLeftExtent() {
    return "artInputPairBuffer[artCurrentInputPairReference + 1]";
  }

  @Override
  String inputAccessFirstSuccessorReference() {
    return "artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference + 1]][artInputPairBuffer[artCurrentInputPairReference]]";
  }

  @Override
  void fileOpen(String filename, String nameSpace) {
    if (nameSpace.length() != 0) text.printf("package %s;\n\n", nameSpace);

    text.printf("import uk.ac.rhul.cs.csle.art.core.ARTUncheckedException;\n" + "import java.io.FileNotFoundException;\n"
        + "import uk.ac.rhul.cs.csle.art.v3.alg.gll.support.*;\n" + "import uk.ac.rhul.cs.csle.art.v3.lex.*;\n"
        + "import uk.ac.rhul.cs.csle.art.v3.manager.*;\n" + "import uk.ac.rhul.cs.csle.art.v3.manager.grammar.*;\n"
        + "import uk.ac.rhul.cs.csle.art.v3.manager.mode.*;\n" + "import uk.ac.rhul.cs.csle.art.util.text.*;\n" + "import uk.ac.rhul.cs.csle.art.term.*;\n"
        + "import uk.ac.rhul.cs.csle.art.util.bitset.ARTBitSet;\n" + "/*******************************************************************************\n"
        + "*\n" + "* %s\n" + "*\n" + "*******************************************************************************/\n", filename);
  }

  @Override
  void fileClose(String filename, String nameSpace) {
    text.close();
  }

  @Override
  void newLine() {
    text.printf("\n");
  }

  @Override
  void indent() {
    for (int i = 0; i < indentLevel; i++)
      text.printf("  ");
  }

  @Override
  void comment(String s) {
    text.printf("/*%s*/\n", s);
  }

  @Override
  void blockOpen() {
    text.printf("{ ");
  }

  @Override
  void blockClose() {
    text.printf(" }\n");
  }

  @Override
  void ifInSet(String set, String var) {
    indent();
    text.printf("if (%s[%s]) ", set, var);
  }

  @Override
  void ifNotInSet(String set, String var) {
    indent();
    text.printf("if (!%s[%s]) ", set, var);
  }

  @Override
  void ifNot(String var) {
    indent();
    text.printf("if (!%s) ", var);
  }

  @Override
  void ifNull(String var) {
    indent();
    text.printf("if (%s == null) ", var);
  }

  @Override
  void ifFunction(String var) {
    indent();
    text.printf("if (%s()) ", var);
  }

  @Override
  void ifTestRepeat(String var, String p1, String p2, String p3) {
    indent();
    text.printf("if (artTestRepeat(%s, %s, %s, %s)) ", var, p1, p2, p3);
  }

  @Override
  void jump(String id) {
    indent();
    text.printf("goto %s;\n", id);
  }

  @Override
  void jumpDynamic(String id) {
    indent();
    text.printf("goto *%s;\n", id);
  }

  @Override
  void jumpState(String id) {
    text.printf("{ artCurrentRestartLabel = %s; break; }\n", id);
  }

  @Override
  void jumpFragment(String id) {
    text.printf("{ artCurrentRestartLabel = %s; return; }\n", id);
  }

  @Override
  void ret() {
    indent();
    text.printf("return;\n");
  }

  @Override
  void brk() {
    indent();
    text.printf("break;\n");
  }

  @Override
  void exception(String id) {
    indent();
    text.printf("printf(\"\\nException: %s\\n\"); exit(1);\n", id);
  }

  @Override
  void whileTrue(String label) {
    indent();
    if (label != null) text.printf("%s: ", label);
    text.printf("while (true)\n");
    indentUp();
  }

  @Override
  void forSuccessorPair() {
    indent();
    text.printf(
        "for (int artI = artInputSuccessorIndex[artInputPairBuffer[artCurrentInputPairReference+1]][artInputPairBuffer[artCurrentInputPairReference]]; artInputSuccessorBuffer[artI] != -1; artI++) ");
    indentUp();
  }

  @Override
  String inputSuccessorReference() {
    return "artInputSuccessorBuffer[artI]";
  };

  @Override
  String inputSuccessorReferenceToken() {
    return "artInputPairBuffer[artInputSuccessorBuffer[artI]]";
  };

  @Override
  String inputSuccessorReferenceLeftExtent() {
    return "artInputPairBuffer[artInputSuccessorBuffer[artI]+1]";
  };

  @Override
  void caseOpen(String id) {
    indent();
    text.printf("switch (%s) {\n", id);
    indentUp();
  }

  @Override
  void caseBranchOpen(String id, boolean flowThrough) {
    if (flowThrough) indentDown();
    indent();
    text.printf("case %s: \n", id);
    indentUp();
  }

  @Override
  void caseBranchClose(String id) {
    indent();
    text.printf("continue artSelectState;\n");
    indentDown();
  }

  @Override
  void caseDefault() {
    indent();
    text.printf("default: ");
    indentUp();
  }

  @Override
  void caseClose(String id, boolean flowThrough) {
    if (flowThrough) indentDown();
    indentDown();
    indent();
    text.printf("}\n");
  }

  @Override
  void classOpen(String id) {
    className = id;
    text.printf("public class %s {\n", id);
  }

  @Override
  void classOpen(String id, String superClassName) {
    className = id;
    text.printf("@SuppressWarnings(\"fallthrough\") public class %s extends %s {\n", id, superClassName);
  }

  @Override
  void classOpen(String id, String superClassName, String interfaceName) {
    className = id;
    text.printf("public class %s extends %s implements %s {\n", id, superClassName, interfaceName);
  }

  @Override
  void classClose(String id) {
    text.printf("};\n");
  }

  int elementNumber;

  @Override
  void enumerationOpen(String id) {
    indent();
    elementNumber = 0;
    text.printf("/* Start of %s enumeration */\n", id);
  }

  @Override
  void enumerationElement(String id) {
    indent();
    // System.out.printf("%s = %d;\n", id, elementNumber);
    text.printf("public static final int %s = %d;\n", id, elementNumber++);
  }

  @Override
  void enumerationClose(String id) {
    indent();
    text.printf("/* End of %s enumeration */\n", id);
  }

  @Override
  void constructorOpen(String id) {
    indent();
    text.printf("public %s() {\n", id);
    indentUp();
  }

  @Override
  void constructorOpen(String id, String p1Type, String p1) {
    indent();
    text.printf("public %s(%s %s) {\n", id, p1Type, p1);
    indentUp();
  }

  @Override
  void constructorOpen(String id, String p1Type, String p1, String p2Type, String p2) {
    indent();
    text.printf("public %s(%s %s, %s %s) {\n", id, p1Type, p1, p2Type, p2);
    indentUp();
  }

  @Override
  void constructorOpenRef(String id, String p1Type, String p1) {
    indent();
    text.printf("public %s(%s %s) {\n", id, p1Type, p1);
    indentUp();
  }

  @Override
  void constructorClose(String id) {
    indentDown();
    indent();
    text.printf("}\n");
  }

  @Override
  void functionVoidForward(String id) {
  }

  @Override
  void functionVoidOpen(String id) {
    indent();
    text.printf("public void %s() {\n", id);
    indentUp();
  }

  @Override
  void functionVoidOpen(String id, String p1Type, String p1) {
    indent();
    text.printf("public void %s(%s %s) {\n", id, p1Type, p1);
    indentUp();
  }

  @Override
  void functionVoidOpen(String id, String p1Type, String p1, String p2Type, String p2) {
    indent();
    text.printf("public void %s(%s %s, %s %s) {\n", id, p1Type, p1, p2Type, p2);
    indentUp();
  }

  @Override
  void functionVoidOpenRef(String id, String p1Type, String p1) {
    indent();
    text.printf("public void %s(%s %s) {\n", id, p1Type, p1);
    indentUp();
  }

  @Override
  void functionClose(String id) {
    indentDown();
    indent();
    text.printf("}\n");
  }

  @Override
  void functionCall(String id) {
    indent();
    text.printf("%s();\n", id);
  }

  @Override
  void functionCall(String id, String p1) {
    indent();
    text.printf("%s(%s);\n", id, p1);
  }

  @Override
  void functionCall(String id, String p1, String p2) {
    indent();
    text.printf("%s(%s, %s);\n", id, p1, p2);
  }

  @Override
  void functionCall(String id, String p1, String p2, String p3) {
    indent();
    text.printf("%s(%s, %s, %s);\n", id, p1, p2, p3);
  }

  @Override
  void functionCall(String id, String p1, String p2, String p3, String p4) {
    indent();
    text.printf("%s(%s, %s, %s, %s);\n", id, p1, p2, p3, p4);
  }

  @Override
  void functionAssignCall(String var, String id) {
    indent();
    text.printf("%s = %s();\n", var, id);
  }

  @Override
  void functionAssignCall(String var, String id, String p1) {
    indent();
    text.printf("%s = %s(%s);\n", var, id, p1);
  }

  @Override
  void functionAssignCall(String var, String id, String p1, String p2) {
    indent();
    text.printf("%s = %s(%s, %s);\n", var, id, p1, p2);
  }

  @Override
  void functionAssignCall(String var, String id, String p1, String p2, String p3) {
    indent();
    text.printf("%s = %s(%s, %s, %s);\n", var, id, p1, p2, p3);
  }

  @Override
  void functionAssignCall(String var, String id, String p1, String p2, String p3, String p4) {
    indent();
    text.printf("%s = %s(%s, %s, %s, %s);\n", var, id, p1, p2, p3, p4);
  }

  @Override
  void assign(String id, String value) {
    indent();
    text.printf("%s = %s;\n", id, value);
  }

  @Override
  void assignString(String id, String value) {
    indent();
    text.printf("%s = \"%s\";\n", id, value);
  }

  @Override
  void assignAppendNull(String id, String value) {
    indent();
    text.printf("%s = %s + \"\\0\";\n", id, value);
  }

  @Override
  void declareBoolean(String id) {
    indent();
    text.printf("boolean %s;\n", id);
  }

  @Override
  void declareBoolean(String id, String value) {
    indent();
    text.printf("boolean %s = %s;\n", id, value);
  }

  @Override
  void declareBooleanArray(String id) {
    indent();
    text.printf("private static boolean[] %s;\n", id);
  }

  @Override
  void allocateBooleanArray(String id, String extent) {
    indent();
    text.printf("%s = new boolean[%s];\n", id, extent);
  }

  @Override
  void assignBooleanArrayElement(String id, String index, String value) {
    indent();
    text.printf("%s[%s] = %s;\n", id, index, value);
  }

  @Override
  void allocateBitSetArray(String id, String extent) {
    indent();
    text.printf("%s = new ARTBitSet[%s];\n", id, extent);
  }

  @Override
  void assignBitSetArrayElement(String id, String index, String value, String max) {
    indent();
    text.printf("if (%s[%s] == null) %s[%s] = new ARTBitSet(%s);%n", id, index, id, index, max);
    indent();
    text.printf("%s[%s].set(%s);\n", id, index, value);
  }

  @Override
  void declareInteger(String id) {
    indent();
    text.printf("int %s;\n", id);
  }

  @Override
  void declareInteger(String id, String value) {
    indent();
    text.printf("int %s = %s;\n", id, value);
  }

  @Override
  void declareIntegerArray(String id) {
    indent();
    text.printf("int *%s;\n", id);
  }

  @Override
  void allocateIntegerArray(String id, String extent) {
    indent();
    text.printf("%s = new int[%s];\n", id, extent);
  }

  @Override
  void assignIntegerArrayElement(String id, String index, String value) {
    indent();
    text.printf("%s[%s] = %s;\n", id, index, value);
  }

  @Override
  void declareEnumeration(String enumId, String id) {
    indent();
    text.printf("int %s;\n", id);
  }

  @Override
  void declareEnumeration(String enumId, String id, String value) {
    indent();
    text.printf("int %s = %s;\n", id, value);
  }

  @Override
  void declareEnumerationArray(String enumId, String id) {
    indent();
    text.printf("int [] %s;\n", id);
  }

  @Override
  void allocateEnumerationArray(String enumId, String id, String extent) {
    indent();
    text.printf("%s = new int[%s];\n", id, extent);
  }

  @Override
  void assignEnumerationArrayElement(String id, String index, String value) {
    indent();
    text.printf("%s[%s] = %s;\n", id, index, value);
  }

  @Override
  void declareString(String id) {
    indent();
    text.printf("String %s;\n", id);
  }

  @Override
  void declareString(String id, String value) {
    indent();
    text.printf("String %s = \"%s\";\n", id, value);
  }

  @Override
  void declareStringArray(String id, String extent) {
    indent();
    text.printf("String [] %s;\n", id);
  }

  @Override
  void allocateStringArray(String id, String extent) {
    indent();
    text.printf("%s = new String[%s];\n", id, extent);
  }

  @Override
  void assignStringArrayElement(String id, String index, String value) {
    indent();
    text.printf("%s[%s] = \"%s\";\n", id, index, value);
  }

  @Override
  void lexerBuiltInInstance(String builtinId, String terminalId) {
    indent();
    text.printf("artBuiltin_%s();\n", builtinId);
    indent();
    text.printf("artLexicaliseTest(%s);\n", terminalId);
  }

  @Override
  void lexerWhitespaceBuiltinInstance(String id) {
    indent();
    text.printf("artBuiltin_%s();\n", id);
  }

  @Override
  void lexerWhitespaceCharInstance(String id) {
    indent();
    text.printf("artCharacterStringInputIndex += artCharacterStringInputTest(\'%s\', artCharacterStringInputIndex);\n", id);
  }

  @Override
  void label(String label) {
    indent();
    text.printf("Java error; goto state mode not allowed (%s)", label);
  }

}
