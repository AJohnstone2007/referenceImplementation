package com.sun.scenario.effect.compiler.model;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
public class SymbolTable {
private final Map<String, Variable> globalVariableMap = new HashMap<String, Variable>();
private final Map<String, Variable> localVariableMap = new HashMap<String, Variable>();
private final Set<Function> globalFunctionSet = new HashSet<Function>();
private int numSamplers;
private int numParams;
private boolean global = true;
public SymbolTable() {
declareCoreFunctions();
}
private Variable declareParamVariable(String name, Type type) {
return declareVariable(name, type, null, null, -1, null, true);
}
public Variable declareVariable(String name, Type type,
Qualifier qual)
{
return declareVariable(name, type, qual, null);
}
public Variable declareVariable(String name, Type type,
Qualifier qual, Precision precision)
{
return declareVariable(name, type, qual, precision, -1, null, false);
}
public Variable declareVariable(String name, Type type,
Qualifier qual, Precision precision,
int arraySize, Object constValue)
{
return declareVariable(name, type, qual, precision,
arraySize, constValue, false);
}
public Variable declareVariable(String name, Type type,
Qualifier qual, Precision precision,
int arraySize, Object constValue,
boolean isParam)
{
Map<String, Variable> vars = getVariablesForScope();
Variable v = vars.get(name);
if (v != null) {
throw new RuntimeException("Variable '" + name + "' already declared");
}
if (arraySize == 0) {
throw new RuntimeException("Array size cannot be zero");
}
int reg = -1;
if (qual == Qualifier.PARAM) {
if (!global) {
throw new RuntimeException("Param variable can only be declared in global scope");
}
if (type.getBaseType() == BaseType.SAMPLER) {
reg = numSamplers;
numSamplers++;
} else {
reg = numParams;
if (arraySize > 0) {
numParams += arraySize;
} else {
numParams++;
}
}
}
v = new Variable(name, type, qual, precision,
reg, arraySize, constValue, isParam);
vars.put(name, v);
return v;
}
public Function declareFunction(String name, Type returnType, List<Param> params) {
Function f = new Function(name, returnType, params);
if (isFunctionDeclared(f)) {
throw new RuntimeException("Function '" + name + "' already declared");
}
if (name.equals("main") && (params == null || params.isEmpty())) {
if (localVariableMap.isEmpty()) {
for (Variable v : CoreSymbols.getAllVariables()) {
localVariableMap.put(v.getName(), v);
}
}
}
if (params != null) {
for (Param param : params) {
declareParamVariable(param.getName(), param.getType());
}
}
declareUserFunction(f);
return f;
}
public void enterFrame() {
global = false;
localVariableMap.clear();
}
public void exitFrame() {
global = true;
}
private void declareCoreFunctions() {
globalFunctionSet.addAll(CoreSymbols.getAllFunctions());
}
public Map<String, Variable> getGlobalVariables() {
return globalVariableMap;
}
private boolean isFunctionDeclared(Function func) {
return globalFunctionSet.contains(func);
}
private void declareUserFunction(Function func) {
globalFunctionSet.add(func);
}
public Function getFunctionForSignature(String name, List<Type> ptypes) {
return getFunctionForSignature(globalFunctionSet, name, ptypes);
}
static Function getFunctionForSignature(Set<Function> funcs,
String name,
List<Type> ptypes)
{
for (Function f : funcs) {
List<Param> params = f.getParams();
if (name.equals(f.getName()) && params.size() == ptypes.size()) {
boolean match = true;
for (int i = 0; i < params.size(); i++) {
if (params.get(i).getType() != ptypes.get(i)) {
match = false;
break;
}
}
if (match) {
return f;
}
}
}
return null;
}
public Map<String, Variable> getVariablesForScope() {
if (global) {
return globalVariableMap;
} else {
return localVariableMap;
}
}
public int getNumSamplers() {
return numSamplers;
}
}
