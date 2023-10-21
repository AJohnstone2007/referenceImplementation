package com.sun.scenario.effect.compiler.backend.sw.java;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import com.sun.scenario.effect.compiler.JSLParser;
import com.sun.scenario.effect.compiler.model.BaseType;
import com.sun.scenario.effect.compiler.model.Qualifier;
import com.sun.scenario.effect.compiler.model.Type;
import com.sun.scenario.effect.compiler.model.Variable;
import com.sun.scenario.effect.compiler.tree.FuncDef;
import com.sun.scenario.effect.compiler.tree.JSLVisitor;
import com.sun.scenario.effect.compiler.tree.ProgramUnit;
import com.sun.scenario.effect.compiler.tree.TreeScanner;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;
import static java.nio.charset.StandardCharsets.UTF_8;
public class JSWBackend extends TreeScanner {
private final JSLParser parser;
private final JSLVisitor visitor;
private final String body;
public JSWBackend(JSLParser parser, JSLVisitor visitor, ProgramUnit program) {
resetStatics();
this.parser = parser;
this.visitor = visitor;
JSWTreeScanner scanner = new JSWTreeScanner();
scanner.scan(program);
this.body = scanner.getResult();
}
public final String getGenCode(String effectName,
String peerName,
String genericsName,
String interfaceName)
{
Map<String, Variable> vars = visitor.getSymbolTable().getGlobalVariables();
StringBuilder genericsDecl = new StringBuilder();
StringBuilder interfaceDecl = new StringBuilder();
StringBuilder constants = new StringBuilder();
StringBuilder samplers = new StringBuilder();
StringBuilder cleanup = new StringBuilder();
StringBuilder srcRects = new StringBuilder();
StringBuilder posDecls = new StringBuilder();
StringBuilder pixInitY = new StringBuilder();
StringBuilder pixInitX = new StringBuilder();
StringBuilder posIncrY = new StringBuilder();
StringBuilder posInitY = new StringBuilder();
StringBuilder posIncrX = new StringBuilder();
StringBuilder posInitX = new StringBuilder();
pixInitY.append("float pixcoord_y = (float)dy;\n");
pixInitX.append("float pixcoord_x = (float)dx;\n");
for (Variable v : vars.values()) {
if (v.getQualifier() == Qualifier.CONST && v.getConstValue() == null) {
continue;
}
Type t = v.getType();
BaseType bt = t.getBaseType();
if (v.getQualifier() != null && bt != BaseType.SAMPLER) {
String vtype = bt.toString();
String vname = v.getName();
String accName = v.getAccessorName();
if (v.isArray()) {
String bufType = (bt == BaseType.FLOAT) ?
"FloatBuffer" : "IntBuffer";
String bufName = vname + "_buf";
String arrayName = vname + "_arr";
constants.append(bufType + " " + bufName + " = " + accName + "();\n");
constants.append(vtype + "[] " + arrayName);
constants.append(" = new " + vtype + "[");
constants.append(bufName + ".capacity()];\n");
constants.append(bufName + ".get(" + arrayName + ");\n");
} else {
if (t.isVector()) {
String arrayName = vname + "_arr";
constants.append(vtype + "[] " + arrayName + " = " + accName + "();\n");
constants.append(vtype + " ");
for (int i = 0; i < t.getNumFields(); i++) {
if (i > 0) {
constants.append(", ");
}
constants.append(vname + getSuffix(i) + " = " + arrayName + "[" + i + "]");
}
constants.append(";\n");
} else {
constants.append(vtype + " " + vname);
if (v.getQualifier() == Qualifier.CONST) {
constants.append(" = " + v.getConstValue());
} else {
constants.append(" = " + accName + "()");
}
constants.append(";\n");
}
}
} else if (v.getQualifier() == Qualifier.PARAM && bt == BaseType.SAMPLER) {
int i = v.getReg();
if (t == Type.FSAMPLER) {
samplers.append("FloatMap src" + i + " = (FloatMap)getSamplerData(" + i + ");\n");
samplers.append("int src" + i + "x = 0;\n");
samplers.append("int src" + i + "y = 0;\n");
samplers.append("int src" + i + "w = src" + i + ".getWidth();\n");
samplers.append("int src" + i + "h = src" + i + ".getHeight();\n");
samplers.append("int src" + i + "scan = src" + i + ".getWidth();\n");
samplers.append("float[] " + v.getName() + " = src" + i + ".getData();\n");
samplers.append("float " + v.getName() + "_vals[] = new float[4];\n");
srcRects.append("float[] src" + i + "Rect = new float[] {0,0,1,1};\n");
} else {
if (t == Type.LSAMPLER) {
samplers.append("HeapImage src" + i + " = (HeapImage)inputs[" + i + "].getUntransformedImage();\n");
} else {
samplers.append("HeapImage src" + i + " = (HeapImage)inputs[" + i + "].getTransformedImage(dstBounds);\n");
cleanup.append("inputs[" + i + "].releaseTransformedImage(src" + i + ");\n");
}
samplers.append("int src" + i + "x = 0;\n");
samplers.append("int src" + i + "y = 0;\n");
samplers.append("int src" + i + "w = src" + i + ".getPhysicalWidth();\n");
samplers.append("int src" + i + "h = src" + i + ".getPhysicalHeight();\n");
samplers.append("int src" + i + "scan = src" + i + ".getScanlineStride();\n");
samplers.append("int[] " + v.getName() + " =\n");
samplers.append("    src" + i + ".getPixelArray();\n");
samplers.append("Rectangle src" + i + "Bounds = new Rectangle(");
samplers.append("src" + i + "x, ");
samplers.append("src" + i + "y, ");
samplers.append("src" + i + "w, ");
samplers.append("src" + i + "h);\n");
if (t == Type.LSAMPLER) {
samplers.append("Rectangle src" + i + "InputBounds = inputs[" + i + "].getUntransformedBounds();\n");
samplers.append("BaseTransform src" + i + "Transform = inputs[" + i + "].getTransform();\n");
} else {
samplers.append("Rectangle src" + i + "InputBounds = inputs[" + i + "].getTransformedBounds(dstBounds);\n");
samplers.append("BaseTransform src" + i + "Transform = BaseTransform.IDENTITY_TRANSFORM;\n");
}
samplers.append("setInputBounds(" + i + ", src" + i + "InputBounds);\n");
samplers.append("setInputNativeBounds(" + i + ", src" + i + "Bounds);\n");
if (t == Type.LSAMPLER) {
samplers.append("float " + v.getName() + "_vals[] = new float[4];\n");
}
srcRects.append("float[] src" + i + "Rect = new float[4];\n");
srcRects.append("getTextureCoordinates(" + i + ", src" + i + "Rect,\n");
srcRects.append("                      src" + i + "InputBounds.x, src" + i + "InputBounds.y,\n");
srcRects.append("                      src" + i + "w, src" + i + "h,\n");
srcRects.append("                      dstBounds, src" + i + "Transform);\n");
}
posDecls.append("float inc" + i + "_x = (src" + i + "Rect[2] - src" + i + "Rect[0]) / dstw;\n");
posDecls.append("float inc" + i + "_y = (src" + i + "Rect[3] - src" + i + "Rect[1]) / dsth;\n");
posInitY.append("float pos" + i + "_y = src" + i + "Rect[1] + inc" + i + "_y*0.5f;\n");
posInitX.append("float pos" + i + "_x = src" + i + "Rect[0] + inc" + i + "_x*0.5f;\n");
posIncrX.append("pos" + i + "_x += inc" + i + "_x;\n");
posIncrY.append("pos" + i + "_y += inc" + i + "_y;\n");
}
}
if (genericsName != null) {
genericsDecl.append("<"+genericsName+">");
}
if (interfaceName != null) {
interfaceDecl.append("implements "+interfaceName);
}
STGroup group = new STGroupFile(getClass().getResource("JSWGlue.stg"), UTF_8.displayName(), '$', '$');
ST glue = group.getInstanceOf("glue");
glue.add("effectName", effectName);
glue.add("peerName", peerName);
glue.add("genericsDecl", genericsDecl.toString());
glue.add("interfaceDecl", interfaceDecl.toString());
glue.add("usercode", usercode.toString());
glue.add("samplers", samplers.toString());
glue.add("cleanup", cleanup.toString());
glue.add("srcRects", srcRects.toString());
glue.add("constants", constants.toString());
glue.add("posDecls", posDecls.toString());
glue.add("pixInitY", pixInitY.toString());
glue.add("pixInitX", pixInitX.toString());
glue.add("posIncrY", posIncrY.toString());
glue.add("posInitY", posInitY.toString());
glue.add("posIncrX", posIncrX.toString());
glue.add("posInitX", posInitX.toString());
glue.add("body", body);
return glue.render();
}
private static char[] fields = {'x', 'y', 'z', 'w'};
public static String getSuffix(int i) {
return "_" + fields[i];
}
static int getFieldIndex(char field) {
switch (field) {
case 'r':
case 'x':
return 0;
case 'g':
case 'y':
return 1;
case 'b':
case 'z':
return 2;
case 'a':
case 'w':
return 3;
default:
throw new InternalError();
}
}
private static Map<String, FuncDef> funcDefs = new HashMap<String, FuncDef>();
static void putFuncDef(FuncDef def) {
funcDefs.put(def.getFunction().getName(), def);
}
static FuncDef getFuncDef(String name) {
return funcDefs.get(name);
}
private static Set<String> resultVars = new HashSet<String>();
static boolean isResultVarDeclared(String vname) {
return resultVars.contains(vname);
}
static void declareResultVar(String vname) {
resultVars.add(vname);
}
private static StringBuilder usercode = new StringBuilder();
static void addGlueBlock(String block) {
usercode.append(block);
}
private static void resetStatics() {
funcDefs.clear();
resultVars.clear();
usercode = new StringBuilder();
}
}
