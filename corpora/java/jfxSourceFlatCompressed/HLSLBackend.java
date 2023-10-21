package com.sun.scenario.effect.compiler.backend.hw;
import java.util.Map;
import com.sun.scenario.effect.compiler.JSLParser;
import com.sun.scenario.effect.compiler.model.BaseType;
import com.sun.scenario.effect.compiler.model.Function;
import com.sun.scenario.effect.compiler.model.Qualifier;
import com.sun.scenario.effect.compiler.model.Type;
import com.sun.scenario.effect.compiler.model.Variable;
import com.sun.scenario.effect.compiler.tree.Expr;
import com.sun.scenario.effect.compiler.tree.FuncDef;
import com.sun.scenario.effect.compiler.tree.JSLVisitor;
import com.sun.scenario.effect.compiler.tree.VarDecl;
public class HLSLBackend extends SLBackend {
public HLSLBackend(JSLParser parser, JSLVisitor visitor) {
super(parser, visitor);
}
private static final Map<String, String> QUAL_MAP = Map.of(
"const", "",
"param", "");
private static final Map<String, String> TYPE_MAP = Map.ofEntries(
Map.entry("void", "void"),
Map.entry("float", "float"),
Map.entry("float2", "float2"),
Map.entry("float3", "float3"),
Map.entry("float4", "float4"),
Map.entry("int", "int"),
Map.entry("int2", "int2"),
Map.entry("int3", "int3"),
Map.entry("int4", "int4"),
Map.entry("bool", "bool"),
Map.entry("bool2", "bool2"),
Map.entry("bool3", "bool3"),
Map.entry("bool4", "bool4"),
Map.entry("sampler", "sampler2D"),
Map.entry("lsampler", "sampler2D"),
Map.entry("fsampler", "sampler2D"));
private static final Map<String, String> VAR_MAP = Map.of();
private static final Map<String, String> FUNC_MAP = Map.of(
"sample", "tex2D",
"fract", "frac",
"mix", "lerp",
"mod", "fmod",
"intcast", "int",
"any", "any",
"length", "length");
@Override
protected String getType(Type t) {
return TYPE_MAP.get(t.toString());
}
@Override
protected String getQualifier(Qualifier q) {
return QUAL_MAP.get(q.toString());
}
@Override
protected String getVar(String v) {
String s = VAR_MAP.get(v);
return (s != null) ? s : v;
}
@Override
protected String getFuncName(String f) {
String s = FUNC_MAP.get(f);
return (s != null) ? s : f;
}
@Override
public void visitFuncDef(FuncDef d) {
Function func = d.getFunction();
if (func.getName().equals("main")) {
output(getType(func.getReturnType()) + " " + func.getName() + "(");
for (int i = 0; i < 2; i++) {
output("in float2 pos" + i + " : TEXCOORD" + i + ",\n");
}
output("in float2 pixcoord : VPOS,\n");
output("in float4 jsl_vertexColor : COLOR0,\n");
output("out float4 color : COLOR0");
output(") ");
scan(d.getStmt());
} else {
super.visitFuncDef(d);
}
}
@Override
public void visitVarDecl(VarDecl d) {
Variable var = d.getVariable();
Type type = var.getType();
Qualifier qual = var.getQualifier();
if (qual == Qualifier.PARAM && type.getBaseType() == BaseType.INT) {
String t;
switch (type) {
case INT:
t = "float";
break;
case INT2:
t = "float2";
break;
case INT3:
t = "float3";
break;
case INT4:
t = "float4";
break;
default:
throw new InternalError();
}
output(t + " " + var.getName());
} else if (qual == Qualifier.CONST) {
output("#define " + var.getName());
} else {
output(getType(type) + " " + var.getName());
}
Expr init = d.getInit();
if (init != null) {
if (qual == Qualifier.CONST) {
output(" (");
scan(init);
output(")");
} else {
output(" = ");
scan(init);
}
}
if (var.isArray()) {
output("[" + var.getArraySize() + "]");
}
if (qual == Qualifier.PARAM) {
char c = (type.getBaseType() == BaseType.SAMPLER) ? 's' : 'c';
output(" : register(" + c + var.getReg() + ")");
}
if (qual == Qualifier.CONST) {
output("\n");
} else {
output(";\n");
}
}
}
