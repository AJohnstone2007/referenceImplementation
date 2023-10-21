package com.sun.scenario.effect.compiler.backend.sw.me;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.sun.scenario.effect.compiler.model.CoreSymbols;
import com.sun.scenario.effect.compiler.model.FuncImpl;
import com.sun.scenario.effect.compiler.model.Function;
import com.sun.scenario.effect.compiler.model.Type;
import com.sun.scenario.effect.compiler.tree.Expr;
import com.sun.scenario.effect.compiler.tree.VariableExpr;
import static com.sun.scenario.effect.compiler.backend.sw.me.MEBackend.*;
import static com.sun.scenario.effect.compiler.model.Type.*;
class MEFuncImpls {
private static Map<Function, FuncImpl> funcs = new HashMap<Function, FuncImpl>();
static FuncImpl get(Function func) {
return funcs.get(func);
}
static {
declareFunctionSample(SAMPLER);
declareFunctionSample(LSAMPLER);
declareFunctionSample(FSAMPLER);
declareFunctionIntCast();
declareOverloadsMinMax("min", "((x_tmp$1 < y_tmp$2) ? x_tmp$1 : y_tmp$2)");
declareOverloadsMinMax("max", "((x_tmp$1 > y_tmp$2) ? x_tmp$1 : y_tmp$2)");
declareOverloadsClamp();
declareOverloadsSmoothstep();
declareOverloadsSimple("abs", "abs(x_tmp$1)");
declareOverloadsSimple("floor", "floor(x_tmp$1)");
declareOverloadsSimple("ceil", "ceil(x_tmp$1)");
declareOverloadsSimple("fract", "(x_tmp$1 - floor(x_tmp$1))");
declareOverloadsSimple("sign", "((x_tmp$1 < 0.f) ? -1.f : (x_tmp$1 > 0.f) ? 1.f : 0.f)");
declareOverloadsSimple("sqrt", "sqrt(x_tmp$1)");
declareOverloadsSimple("sin", "sin(x_tmp$1)");
declareOverloadsSimple("cos", "cos(x_tmp$1)");
declareOverloadsSimple("tan", "tan(x_tmp$1)");
declareOverloadsSimple2("pow", "pow(x_tmp$1, y_tmp$2)");
declareOverloadsMinMax("mod", "(x_tmp$1 % y_tmp$2)");
declareOverloadsDot();
declareOverloadsDistance();
declareOverloadsMix();
declareOverloadsNormalize();
declareOverloadsSimple("ddx", "<ddx() not implemented for sw backends>");
declareOverloadsSimple("ddy", "<ddy() not implemented for sw backends>");
}
private static void declareFunction(FuncImpl impl,
String name, Type... ptypes)
{
Function f = CoreSymbols.getFunction(name, Arrays.asList(ptypes));
if (f == null) {
throw new InternalError("Core function not found (have you declared the function in CoreSymbols?)");
}
funcs.put(f, impl);
}
private static void declareFunctionSample(final Type type) {
FuncImpl fimpl = new FuncImpl() {
@Override
public String getPreamble(List<Expr> params) {
String s = getSamplerName(params);
String p = getPosName(params);
if (type == LSAMPLER) {
return
"lsample(" + s + ", loc_tmp_x, loc_tmp_y,\n" +
"        " + p + "w, " + p + "h, " + p + "scan,\n" +
"        " + s + "_vals);\n";
} else if (type == FSAMPLER) {
return
"float *" + s + "_arr_tmp = NULL;\n" +
"int iloc_tmp = 0;\n" +
"if (loc_tmp_x >= 0 && loc_tmp_y >= 0) {\n" +
"    int iloc_tmp_x = (int)(loc_tmp_x*" + p + "w);\n" +
"    int iloc_tmp_y = (int)(loc_tmp_y*" + p + "h);\n" +
"    jboolean out =\n" +
"        iloc_tmp_x >= " + p + "w ||\n" +
"        iloc_tmp_y >= " + p + "h;\n" +
"    if (!out) {\n" +
"        "+ s + "_arr_tmp = " + s + ";\n" +
"        iloc_tmp = 4 * (iloc_tmp_y*" + p + "scan + iloc_tmp_x);\n" +
"    }\n" +
"}\n";
} else {
return
"int " + s + "_tmp;\n" +
"if (loc_tmp_x >= 0 && loc_tmp_y >= 0) {\n" +
"    int iloc_tmp_x = (int)(loc_tmp_x*" + p + "w);\n" +
"    int iloc_tmp_y = (int)(loc_tmp_y*" + p + "h);\n" +
"    jboolean out =\n" +
"        iloc_tmp_x >= " + p + "w ||\n" +
"        iloc_tmp_y >= " + p + "h;\n" +
"    " + s + "_tmp = out ? 0 :\n" +
"        " + s + "[iloc_tmp_y*" + p + "scan + iloc_tmp_x];\n" +
"} else {\n" +
"    " + s + "_tmp = 0;\n" +
"}\n";
}
}
public String toString(int i, List<Expr> params) {
String s = getSamplerName(params);
if (type == LSAMPLER) {
return (i < 0 || i > 3) ? null : s + "_vals[" + i + "]";
} else if (type == FSAMPLER) {
String arr = s + "_arr_tmp";
switch (i) {
case 0:
return arr + " == NULL ? 0.f : " + arr + "[iloc_tmp]";
case 1:
return arr + " == NULL ? 0.f : " + arr + "[iloc_tmp+1]";
case 2:
return arr + " == NULL ? 0.f : " + arr + "[iloc_tmp+2]";
case 3:
return arr + " == NULL ? 0.f : " + arr + "[iloc_tmp+3]";
default:
return null;
}
} else {
switch (i) {
case 0:
return "(((" + s + "_tmp >> 16) & 0xff) / 255.f)";
case 1:
return "(((" + s + "_tmp >>  8) & 0xff) / 255.f)";
case 2:
return "(((" + s + "_tmp      ) & 0xff) / 255.f)";
case 3:
return "(((" + s + "_tmp >> 24) & 0xff) / 255.f)";
default:
return null;
}
}
}
private String getSamplerName(List<Expr> params) {
VariableExpr e = (VariableExpr)params.get(0);
return e.getVariable().getName();
}
private String getPosName(List<Expr> params) {
VariableExpr e = (VariableExpr)params.get(0);
return "src" + e.getVariable().getReg();
}
};
declareFunction(fimpl, "sample", type, FLOAT2);
}
private static void declareFunctionIntCast() {
FuncImpl fimpl = new FuncImpl() {
public String toString(int i, List<Expr> params) {
return "((int)x_tmp)";
}
};
declareFunction(fimpl, "intcast", FLOAT);
}
private static void declareOverloadsSimple(String name, final String pattern) {
for (Type type : new Type[] {FLOAT, FLOAT2, FLOAT3, FLOAT4}) {
final boolean useSuffix = (type != FLOAT);
FuncImpl fimpl = new FuncImpl() {
public String toString(int i, List<Expr> params) {
String sfx = useSuffix ? getSuffix(i) : "";
String s = pattern;
s = s.replace("$1", sfx);
return s;
}
};
declareFunction(fimpl, name, type);
}
}
private static void declareOverloadsSimple2(String name, final String pattern) {
for (Type type : new Type[] {FLOAT, FLOAT2, FLOAT3, FLOAT4}) {
final boolean useSuffix = (type != FLOAT);
FuncImpl fimpl = new FuncImpl() {
public String toString(int i, List<Expr> params) {
String sfx = useSuffix ? getSuffix(i) : "";
String s = pattern;
s = s.replace("$1", sfx);
s = s.replace("$2", sfx);
return s;
}
};
declareFunction(fimpl, name, type, type);
}
}
private static void declareOverloadsNormalize() {
final String name = "normalize";
final String pattern = "x_tmp$1 / denom";
for (Type type : new Type[] {FLOAT, FLOAT2, FLOAT3, FLOAT4}) {
int n = type.getNumFields();
final String preamble;
if (n == 1) {
preamble = "float denom = x_tmp;\n";
} else {
String s = "(x_tmp_x * x_tmp_x)";
s += "+\n(x_tmp_y * x_tmp_y)";
if (n > 2) s += "+\n(x_tmp_z * x_tmp_z)";
if (n > 3) s += "+\n(x_tmp_w * x_tmp_w)";
preamble = "float denom = sqrt(" + s + ");\n";
}
final boolean useSuffix = (type != FLOAT);
FuncImpl fimpl = new FuncImpl() {
@Override
public String getPreamble(List<Expr> params) {
return preamble;
}
public String toString(int i, List<Expr> params) {
String sfx = useSuffix ? getSuffix(i) : "";
String s = pattern;
s = s.replace("$1", sfx);
return s;
}
};
declareFunction(fimpl, name, type);
}
}
private static void declareOverloadsDot() {
final String name = "dot";
for (final Type type : new Type[] {FLOAT, FLOAT2, FLOAT3, FLOAT4}) {
int n = type.getNumFields();
String s;
if (n == 1) {
s = "(x_tmp * y_tmp)";
} else {
s = "(x_tmp_x * y_tmp_x)";
s += "+\n(x_tmp_y * y_tmp_y)";
if (n > 2) s += "+\n(x_tmp_z * y_tmp_z)";
if (n > 3) s += "+\n(x_tmp_w * y_tmp_w)";
}
final String str = s;
FuncImpl fimpl = new FuncImpl() {
public String toString(int i, List<Expr> params) {
return str;
}
};
declareFunction(fimpl, name, type, type);
}
}
private static void declareOverloadsDistance() {
final String name = "distance";
for (final Type type : new Type[] {FLOAT, FLOAT2, FLOAT3, FLOAT4}) {
int n = type.getNumFields();
String s;
if (n == 1) {
s = "(x_tmp - y_tmp) * (x_tmp - y_tmp)";
} else {
s = "((x_tmp_x - y_tmp_x) * (x_tmp_x - y_tmp_x))";
s += "+\n((x_tmp_y - y_tmp_y) * (x_tmp_y - y_tmp_y))";
if (n > 2) s += "+\n((x_tmp_z - y_tmp_z) * (x_tmp_z - y_tmp_z))";
if (n > 3) s += "+\n((x_tmp_w - y_tmp_w) * (x_tmp_w - y_tmp_w))";
}
final String str = "sqrt(" + s + ")";
FuncImpl fimpl = new FuncImpl() {
public String toString(int i, List<Expr> params) {
return str;
}
};
declareFunction(fimpl, name, type, type);
}
}
private static void declareOverloadsMinMax(String name, final String pattern) {
for (Type type : new Type[] {FLOAT, FLOAT2, FLOAT3, FLOAT4}) {
final boolean useSuffix = (type != FLOAT);
FuncImpl fimpl = new FuncImpl() {
public String toString(int i, List<Expr> params) {
String sfx = useSuffix ? getSuffix(i) : "";
String s = pattern;
s = s.replace("$1", sfx);
s = s.replace("$2", sfx);
return s;
}
};
declareFunction(fimpl, name, type, type);
if (type == FLOAT) {
continue;
}
fimpl = new FuncImpl() {
public String toString(int i, List<Expr> params) {
String sfx = getSuffix(i);
String s = pattern;
s = s.replace("$1", sfx);
s = s.replace("$2", "");
return s;
}
};
declareFunction(fimpl, name, type, FLOAT);
}
}
private static void declareOverloadsClamp() {
final String name = "clamp";
final String pattern =
"(val_tmp$1 < min_tmp$2) ? min_tmp$2 : \n" +
"(val_tmp$1 > max_tmp$2) ? max_tmp$2 : val_tmp$1";
for (Type type : new Type[] {FLOAT, FLOAT2, FLOAT3, FLOAT4}) {
final boolean useSuffix = (type != FLOAT);
FuncImpl fimpl = new FuncImpl() {
public String toString(int i, List<Expr> params) {
String sfx = useSuffix ? getSuffix(i) : "";
String s = pattern;
s = s.replace("$1", sfx);
s = s.replace("$2", sfx);
return s;
}
};
declareFunction(fimpl, name, type, type, type);
if (type == FLOAT) {
continue;
}
fimpl = new FuncImpl() {
public String toString(int i, List<Expr> params) {
String sfx = getSuffix(i);
String s = pattern;
s = s.replace("$1", sfx);
s = s.replace("$2", "");
return s;
}
};
declareFunction(fimpl, name, type, FLOAT, FLOAT);
}
}
private static void declareOverloadsSmoothstep() {
final String name = "smoothstep";
final String pattern =
"(val_tmp$1 < min_tmp$2) ? 0.0f : \n" +
"(val_tmp$1 > max_tmp$2) ? 1.0f : \n" +
"(val_tmp$1 / (max_tmp$2 - min_tmp$2))";
for (Type type : new Type[] {FLOAT, FLOAT2, FLOAT3, FLOAT4}) {
final boolean useSuffix = (type != FLOAT);
FuncImpl fimpl = new FuncImpl() {
public String toString(int i, List<Expr> params) {
String sfx = useSuffix ? getSuffix(i) : "";
String s = pattern;
s = s.replace("$1", sfx);
s = s.replace("$2", sfx);
return s;
}
};
declareFunction(fimpl, name, type, type, type);
if (type == FLOAT) {
continue;
}
fimpl = new FuncImpl() {
public String toString(int i, List<Expr> params) {
String sfx = getSuffix(i);
String s = pattern;
s = s.replace("$1", sfx);
s = s.replace("$2", "");
return s;
}
};
declareFunction(fimpl, name, FLOAT, FLOAT, type);
}
}
private static void declareOverloadsMix() {
final String name = "mix";
final String pattern =
"(x_tmp$1 * (1.0f - a_tmp$2) + y_tmp$1 * a_tmp$2)";
for (Type type : new Type[] {FLOAT, FLOAT2, FLOAT3, FLOAT4}) {
final boolean useSuffix = (type != FLOAT);
FuncImpl fimpl = new FuncImpl() {
public String toString(int i, List<Expr> params) {
String sfx = useSuffix ? getSuffix(i) : "";
String s = pattern;
s = s.replace("$1", sfx);
s = s.replace("$2", sfx);
return s;
}
};
declareFunction(fimpl, name, type, type, type);
if (type == FLOAT) {
continue;
}
fimpl = new FuncImpl() {
public String toString(int i, List<Expr> params) {
String sfx = getSuffix(i);
String s = pattern;
s = s.replace("$1", sfx);
s = s.replace("$2", "");
return s;
}
};
declareFunction(fimpl, name, type, type, FLOAT);
}
}
}
