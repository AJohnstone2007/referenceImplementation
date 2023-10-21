package com.javafx.experiments.importers.maya.parser;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import com.javafx.experiments.importers.maya.MEnv;
import com.javafx.experiments.importers.maya.MNode;
import com.javafx.experiments.importers.maya.MPath;
import com.javafx.experiments.importers.maya.MayaImporter;
import com.javafx.experiments.importers.maya.values.MData;
import com.javafx.experiments.importers.maya.values.MPointer;
public class MParser {
private MEnv env;
private URL inputSource;
private MNode selectedNode;
private boolean inPlaybackScriptNode = false;
private Set<String> refs = new HashSet<>();
public MParser(MEnv env) {
this.env = env;
}
public void parse(URL url) throws IOException {
if (url == null) {
throw new IOException("Null URL");
}
this.inputSource = url;
URLConnection conn = url.openConnection();
try (InputStream input = conn.getInputStream()) {
parse(input);
}
}
private int lineNo;
public void parse(InputStream inputStream) throws IOException {
BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
String line;
lineNo = 0;
List<String> command = new ArrayList<>();
while ((line = reader.readLine()) != null) {
++lineNo;
Tokenizer tokenizer = new Tokenizer(line);
while (tokenizer.hasMoreTokens()) {
String tok = tokenizer.nextToken();
if (tok.startsWith("//")) {
break;
}
if (tok.endsWith(";")) {
String tmp = tok.substring(0, tok.length() - 1);
if (tmp.length() > 0) {
command.add(tmp);
}
if (command.size() > 0) {
execute(command);
command.clear();
}
} else {
command.add(tok);
}
}
}
}
class Tokenizer {
private String line;
private int pos;
private boolean escaping = false;
Tokenizer(String line) {
this.line = line;
}
public boolean hasMoreTokens() {
while (pos < line.length()) {
if (Character.isWhitespace(line.charAt(pos))) {
++pos;
} else {
return true;
}
}
return false;
}
public String nextToken() {
while (pos < line.length() &&
Character.isWhitespace(line.charAt(pos))) {
++pos;
}
int startPos = pos;
boolean firstChar = true;
boolean insideString = false;
while (true) {
if (pos == line.length()) {
if ((pos - startPos) == 0) {
return null;
} else {
return line.substring(startPos, pos);
}
}
char ch = line.charAt(pos);
if (Character.isWhitespace(ch)) {
if (!insideString) {
return line.substring(startPos, pos);
}
} else if (ch == '\"') {
if (firstChar) {
insideString = true;
++startPos;
} else if (insideString && !escaping) {
++pos;
return line.substring(startPos, pos - 1);
}
escaping = false;
} else if (ch == '\\') {
escaping = true;
}
firstChar = false;
++pos;
}
}
}
private RuntimeException error(String error) {
return new RuntimeException(error + ", current command " + curCommand + ", file " + inputSource + ", line " + lineNo);
}
private void execute(List<String> commandArgs) {
String command = commandArgs.remove(0);
curCommand = command;
curArgs = commandArgs;
switch (command) {
case "file":
doFile();
break;
case "requires":
doRequires();
break;
case "createNode":
doCreateNode();
break;
case "setAttr":
doSetAttr();
break;
case "addAttr":
doAddAttr();
break;
case "parent":
doParent();
break;
case "connectAttr":
doConnectAttr();
break;
case "disconnectAttr":
doDisconnectAttr();
break;
case "select":
doSelect();
break;
case "currentUnit":
doCurrentUnit();
break;
case "fileInfo":
doFileInfo();
break;
default:
System.out.println("Unrecognized command " + command);
break;
}
}
private String curCommand;
private List<String> curArgs;
private String nextArg() {
if (curArgs.size() > 0) {
return curArgs.remove(0);
}
throw error("No more arguments for command \"" + curCommand + "\"");
}
private boolean moreArgs() {
return curArgs.size() > 0;
}
private void doAddAttr() {
if (selectedNode == null) {
return;
}
String longName = null;
String shortName = null;
String dataType = null;
String attrType = null;
boolean isArray = false;
String dataValue = null;
String attrParent = null;
for (int i = 0; i < curArgs.size(); i++) {
String arg = curArgs.get(i);
switch (arg) {
case "-at":
case "-attributeType":
attrType = curArgs.get(++i);
break;
case "-p":
attrParent = curArgs.get(++i);
break;
case "-dt":
case "-dataType":
dataType = curArgs.get(++i);
break;
case "-sn":
case "-shortName":
shortName = curArgs.get(++i);
break;
case "-ln":
case "-longName":
longName = curArgs.get(++i);
break;
case "-dv":
case "-datavValue":
dataValue = curArgs.get(++i);
break;
}
}
String type = dataType != null ? dataType : attrType;
switch (type) {
case "stringArray":
type = "string[]";
break;
case "doubleArray":
type = "double[]";
break;
case "Int32Array":
type = "int[]";
break;
case "fltMatrix":
type = "matrix";
break;
case "message":
type = "Message";
break;
case "doubleAngle":
type = "double";
break;
case "doubleLinear":
type = "double";
break;
default:
break;
}
selectedNode.addAttr(longName, shortName, type, isArray, attrParent);
if (dataValue != null) {
List<String> list = new LinkedList<>();
list.add(dataValue);
MData data = selectedNode.getAttr(shortName);
if (data != null) {
data.parse(list);
}
}
}
private void doConnectAttr() {
String src = nextArg();
String dst = nextArg();
if (src.startsWith(":")) {
src = src.substring(1);
}
MPath srcPath = new MPath(env, src);
MPath dstPath = new MPath(env, dst);
MData srcData = srcPath.apply();
MData dstData = dstPath.apply();
env.connectAttr(src, dst);
if (srcData instanceof MPointer) {
((MPointer) srcData).setTarget(dstPath);
}
if (dstData instanceof MPointer) {
((MPointer) dstData).setTarget(srcPath);
}
}
private void doCreateNode() {
String name = null;
String parent = null;
String type = null;
boolean shared = false;
boolean select = true;
while (moreArgs()) {
String tok = nextArg();
switch (tok) {
case "-name":
case "-n":
name = nextArg();
break;
case "-parent":
case "-p":
parent = nextArg();
break;
case "-shared":
case "-s":
shared = true;
break;
case "-skipSelect":
case "-ss":
select = false;
break;
default:
if (type != null) {
throw error("Node type (" + type + ") already specified, can't specify new one (" + tok + ")");
}
type = tok;
break;
}
}
if (type == null) {
throw error("Node type not specified");
}
if ("script".equals(type)) {
inPlaybackScriptNode = true;
return;
}
if (shared && env.findNode(name) != null) {
return;
}
MNode parentNode = null;
if (parent != null) {
parentNode = env.findNode(parent);
}
MNode node = env.createNode(type, name);
if (node != null) {
if (parentNode != null) {
node.setParentNode(parentNode);
}
env.addNode(node);
if (select) {
selectedNode = node;
}
} else {
selectedNode = null;
}
}
private void doCurrentUnit() {
}
private void doDisconnectAttr() {
}
private void doFile() {
String fileName = null;
while (moreArgs()) {
fileName = nextArg();
}
if (fileName != null && !refs.contains(fileName)) {
refs.add(fileName);
try {
new MParser(env).parse(new URL(inputSource, fileName));
} catch (Exception e) {
throw new RuntimeException(e);
}
}
}
private void doFileInfo() {
}
private void doParent() {
String instance = null;
String source = null;
for (int i = 0; i < curArgs.size(); i++) {
String arg = curArgs.get(i);
if (!arg.startsWith("-")) {
source = arg;
} else if (arg.equals("-add")) {
instance = curArgs.get(++i);
}
}
if (source != null && instance != null) {
MPath spath = new MPath(env, source);
MPath ipath = new MPath(env, instance);
MNode inode = ipath.getTargetNode();
if (inode != null) {
inode.parent(spath.getTargetNode());
}
}
}
private void doRequires() {
}
private void doSelect() {
String path = null;
for (int i = 0; i < curArgs.size(); i++) {
String arg = curArgs.get(i);
if (!arg.startsWith("-")) {
path = arg;
break;
}
}
if (path != null) {
MPath mpath = new MPath(env, path);
selectedNode = mpath.getTargetNode();
}
}
private void doSetAttr() {
String target = null;
List<String> value = new ArrayList();
@SuppressWarnings("UnusedDeclaration") String type = null;
int size = -1;
for (int i = 0; i < curArgs.size(); i++) {
String arg = curArgs.get(i);
if (arg.equals("-s") || arg.equals("-size")) {
try {
size = Integer.parseInt(curArgs.get(++i));
} catch (Exception ignored) {
ignored.printStackTrace(System.err);
}
} else if (arg.equals("-type") || arg.equals("-typ")) {
type = curArgs.get(++i);
} else if (target == null && (arg.equals("-l") || arg.equals("-lock"))) {
++i;
} else if (target == null && (arg.equals("-k") || arg.equals("-keyable"))) {
++i;
} else if (target == null && (arg.equals("-ch") || arg.equals("-capacityHint"))) {
++i;
} else if (target == null) {
target = arg;
} else {
value.add(arg);
}
}
if (inPlaybackScriptNode) {
if (".b".equals(target)) {
inPlaybackScriptNode = false;
try {
String playBackOptions = value.get(0);
StringTokenizer izer = new StringTokenizer(playBackOptions, " ");
izer.nextToken();
float min = 0;
float max = 0;
while (izer.hasMoreTokens()) {
String key = izer.nextToken();
String val = "";
if (key.startsWith("-")) {
val = izer.nextToken();
}
switch (key) {
case "-min":
min = Float.parseFloat(val.trim());
break;
case "-max":
max = Float.parseFloat(val.trim());
break;
case "-ast":
break;
case "-aet":
break;
}
}
env.setPlaybackRange(min, max);
} catch (Exception e) {
e.printStackTrace(System.err);
}
}
return;
}
if (selectedNode == null) {
return;
}
MData data = selectedNode.getAttr(target.substring(1));
if (data == null) {
return;
}
if (size > 0) {
data.setSize(size);
}
if (value.isEmpty()) {
return;
}
try {
data.parse(value);
} catch (Exception e) {
if (MayaImporter.DEBUG) {
e.printStackTrace(System.err);
}
}
}
}
