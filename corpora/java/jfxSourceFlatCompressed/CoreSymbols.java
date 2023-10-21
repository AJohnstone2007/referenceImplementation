package com.sun.scenario.effect.compiler.model;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import static com.sun.scenario.effect.compiler.model.Precision.*;
import static com.sun.scenario.effect.compiler.model.Type.*;
public class CoreSymbols {
private static Set<Variable> vars = new HashSet<Variable>();
private static Set<Function> funcs = new HashSet<Function>();
static Set<Variable> getAllVariables() {
return vars;
}
static Set<Function> getAllFunctions() {
return funcs;
}
public static Function getFunction(String name, List<Type> ptypes) {
return SymbolTable.getFunctionForSignature(funcs, name, ptypes);
}
static {
declareVariable("pos0", FLOAT2, null, true);
declareVariable("pos1", FLOAT2, null, true);
declareVariable("pixcoord", FLOAT2, null, true);
declareVariable("jsl_vertexColor", FLOAT4, LOWP, true);
declareVariable("color", FLOAT4, LOWP, false);
declareFunction(FLOAT4, "sample", SAMPLER, "s", FLOAT2, "loc");
declareFunction(FLOAT4, "sample", LSAMPLER, "s", FLOAT2, "loc");
declareFunction(FLOAT4, "sample", FSAMPLER, "s", FLOAT2, "loc");
declareFunction(INT, "intcast", FLOAT, "x");
declareOverloadsBool("any");
declareOverloadsMinMax("min");
declareOverloadsMinMax("max");
declareOverloadsClamp();
declareOverloadsSmoothstep();
declareOverloadsSimple("abs");
declareOverloadsSimple("floor");
declareOverloadsSimple("ceil");
declareOverloadsSimple("fract");
declareOverloadsSimple("sign");
declareOverloadsSimple("sqrt");
declareOverloadsSimple("sin");
declareOverloadsSimple("cos");
declareOverloadsSimple("tan");
declareOverloadsSimple2("pow");
declareOverloadsMinMax("mod");
declareOverloadsFloat2("dot");
declareOverloadsFloat2("distance");
declareOverloadsFloat("length");
declareOverloadsMix();
declareOverloadsSimple("normalize");
declareOverloadsSimple("ddx");
declareOverloadsSimple("ddy");
}
private static void declareVariable(String name, Type type,
Precision precision,
boolean readonly)
{
Qualifier qual = readonly ? Qualifier.CONST : null;
vars.add(new Variable(name, type, qual, precision, -1, -1, null, false));
}
private static void declareFunction(Type returnType,
String name,
Object... params)
{
List<Param> paramList = new ArrayList<Param>();
if (params.length % 2 != 0) {
throw new InternalError("Params array length must be even");
}
for (int i = 0; i < params.length; i+=2) {
if (!(params[i+0] instanceof Type) ||
!(params[i+1] instanceof String))
{
throw new InternalError("Params must be specified as (Type,String) pairs");
}
paramList.add(new Param((String)params[i+1], (Type)params[i]));
}
funcs.add(new Function(name, returnType, paramList));
}
private static void declareOverloadsSimple(String name) {
declareFunction(FLOAT, name, FLOAT, "x");
declareFunction(FLOAT2, name, FLOAT2, "x");
declareFunction(FLOAT3, name, FLOAT3, "x");
declareFunction(FLOAT4, name, FLOAT4, "x");
}
private static void declareOverloadsSimple2(String name) {
declareFunction(FLOAT, name, FLOAT, "x", FLOAT, "y");
declareFunction(FLOAT2, name, FLOAT2, "x", FLOAT2, "y");
declareFunction(FLOAT3, name, FLOAT3, "x", FLOAT3, "y");
declareFunction(FLOAT4, name, FLOAT4, "x", FLOAT4, "y");
}
private static void declareOverloadsMinMax(String name) {
declareFunction(FLOAT, name, FLOAT, "x", FLOAT, "y");
declareFunction(FLOAT2, name, FLOAT2, "x", FLOAT2, "y");
declareFunction(FLOAT3, name, FLOAT3, "x", FLOAT3, "y");
declareFunction(FLOAT4, name, FLOAT4, "x", FLOAT4, "y");
declareFunction(FLOAT2, name, FLOAT2, "x", FLOAT, "y");
declareFunction(FLOAT3, name, FLOAT3, "x", FLOAT, "y");
declareFunction(FLOAT4, name, FLOAT4, "x", FLOAT, "y");
}
private static void declareOverloadsClamp() {
final String name = "clamp";
declareFunction(FLOAT, name, FLOAT, "val", FLOAT, "min", FLOAT, "max");
declareFunction(FLOAT2, name, FLOAT2, "val", FLOAT2, "min", FLOAT2, "max");
declareFunction(FLOAT3, name, FLOAT3, "val", FLOAT3, "min", FLOAT3, "max");
declareFunction(FLOAT4, name, FLOAT4, "val", FLOAT4, "min", FLOAT4, "max");
declareFunction(FLOAT2, name, FLOAT2, "val", FLOAT, "min", FLOAT, "max");
declareFunction(FLOAT3, name, FLOAT3, "val", FLOAT, "min", FLOAT, "max");
declareFunction(FLOAT4, name, FLOAT4, "val", FLOAT, "min", FLOAT, "max");
}
private static void declareOverloadsSmoothstep() {
final String name = "smoothstep";
declareFunction(FLOAT, name, FLOAT, "min", FLOAT, "max", FLOAT, "val");
declareFunction(FLOAT2, name, FLOAT2, "min", FLOAT2, "max", FLOAT2, "val");
declareFunction(FLOAT3, name, FLOAT3, "min", FLOAT3, "max", FLOAT3, "val");
declareFunction(FLOAT4, name, FLOAT4, "min", FLOAT4, "max", FLOAT4, "val");
declareFunction(FLOAT2, name, FLOAT, "min", FLOAT, "max", FLOAT2, "val");
declareFunction(FLOAT3, name, FLOAT, "min", FLOAT, "max", FLOAT3, "val");
declareFunction(FLOAT4, name, FLOAT, "min", FLOAT, "max", FLOAT4, "val");
}
private static void declareOverloadsMix() {
final String name = "mix";
declareFunction(FLOAT, name, FLOAT, "x", FLOAT, "y", FLOAT, "a");
declareFunction(FLOAT2, name, FLOAT2, "x", FLOAT2, "y", FLOAT2, "a");
declareFunction(FLOAT3, name, FLOAT3, "x", FLOAT3, "y", FLOAT3, "a");
declareFunction(FLOAT4, name, FLOAT4, "x", FLOAT4, "y", FLOAT4, "a");
declareFunction(FLOAT2, name, FLOAT2, "x", FLOAT2, "y", FLOAT, "a");
declareFunction(FLOAT3, name, FLOAT3, "x", FLOAT3, "y", FLOAT, "a");
declareFunction(FLOAT4, name, FLOAT4, "x", FLOAT4, "y", FLOAT, "a");
}
private static void declareOverloadsBool(String name) {
declareFunction(BOOL, name, BOOL2, "x");
declareFunction(BOOL, name, BOOL3, "x");
declareFunction(BOOL, name, BOOL4, "x");
}
private static void declareOverloadsFloat(String name) {
declareFunction(FLOAT, name, FLOAT, "x");
declareFunction(FLOAT, name, FLOAT2, "x");
declareFunction(FLOAT, name, FLOAT3, "x");
declareFunction(FLOAT, name, FLOAT4, "x");
}
private static void declareOverloadsFloat2(String name) {
declareFunction(FLOAT, name, FLOAT, "x", FLOAT, "y");
declareFunction(FLOAT, name, FLOAT2, "x", FLOAT2, "y");
declareFunction(FLOAT, name, FLOAT3, "x", FLOAT3, "y");
declareFunction(FLOAT, name, FLOAT4, "x", FLOAT4, "y");
}
}
