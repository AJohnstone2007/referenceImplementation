package com.sun.javafx.application;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.application.Application;
import javafx.application.Application.Parameters;
public class ParametersImpl extends Parameters {
private List<String> rawArgs = new ArrayList<String>();
private Map<String, String> namedParams = new HashMap<String, String>();
private List<String> unnamedParams = new ArrayList<String>();
private List<String> readonlyRawArgs = null;
private Map<String, String> readonlyNamedParams = null;
private List<String> readonlyUnnamedParams = null;
private static Map<Application, Parameters> params =
new HashMap<Application, Parameters>();
public ParametersImpl() {
}
public ParametersImpl(List<String> args) {
if (args != null) {
init(args);
}
}
public ParametersImpl(String[] args) {
if (args != null) {
init(Arrays.asList(args));
}
}
public ParametersImpl(Map params, String[] arguments) {
init(params, arguments);
}
private void init(List<String>args) {
for (String arg: args) {
if (arg != null) {
rawArgs.add(arg);
}
}
computeNamedParams();
computeUnnamedParams();
}
private void init(Map params, String[] arguments) {
for (Object e : params.entrySet()) {
Object key = ((Map.Entry)e).getKey();
if (validKey(key)) {
Object value = params.get(key);
if (value instanceof String) {
namedParams.put((String)key, (String)value);
}
}
}
computeRawArgs();
if (arguments != null) {
for (String arg : arguments) {
unnamedParams.add(arg);
rawArgs.add(arg);
}
}
}
private boolean validFirstChar(char c) {
return Character.isLetter(c) || c == '_';
}
private boolean validKey(Object key) {
if (key instanceof String) {
String keyStr = (String)key;
if (keyStr.length() > 0 && keyStr.indexOf('=') < 0) {
return validFirstChar(keyStr.charAt(0));
}
}
return false;
}
private boolean isNamedParam(String arg) {
if (arg.startsWith("--")) {
return (arg.indexOf('=') > 2 && validFirstChar(arg.charAt(2)));
} else {
return false;
}
}
private void computeUnnamedParams() {
for (String arg : rawArgs) {
if (!isNamedParam(arg)) {
unnamedParams.add(arg);
}
}
}
private void computeNamedParams() {
for (String arg : rawArgs) {
if (isNamedParam(arg)) {
final int eqIdx = arg.indexOf('=');
String key = arg.substring(2, eqIdx);
String value = arg.substring(eqIdx + 1);
namedParams.put(key, value);
}
}
}
private void computeRawArgs() {
ArrayList<String> keys = new ArrayList<String>();
keys.addAll(namedParams.keySet());
Collections.sort(keys);
for (String key : keys) {
rawArgs.add("--" + key + "=" + namedParams.get(key));
}
}
@Override public List<String> getRaw() {
if (readonlyRawArgs == null) {
readonlyRawArgs = Collections.unmodifiableList(rawArgs);
}
return readonlyRawArgs;
}
@Override public Map<String, String> getNamed() {
if (readonlyNamedParams == null) {
readonlyNamedParams = Collections.unmodifiableMap(namedParams);
}
return readonlyNamedParams;
}
@Override public List<String> getUnnamed() {
if (readonlyUnnamedParams == null) {
readonlyUnnamedParams = Collections.unmodifiableList(unnamedParams);
}
return readonlyUnnamedParams;
}
public static Parameters getParameters(Application app) {
Parameters p = params.get(app);
return p;
}
public static void registerParameters(Application app, Parameters p) {
params.put(app, p);
}
}
