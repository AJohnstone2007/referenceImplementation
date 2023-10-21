package com.javafx.experiments.importers.maya.values;
import java.util.List;
public interface MComponentList extends MData {
public static class Component {
private String name;
private int startIndex;
private int endIndex;
public String name() { return name; }
public int startIndex() { return startIndex; }
public int endIndex() { return endIndex; }
public Component(String name, int startIndex, int endIndex) {
this.name = name;
this.startIndex = startIndex;
this.endIndex = endIndex;
}
public static Component parse(String str) {
String name = null;
int startIndex = 0;
int endIndex = 0;
int bracket = str.indexOf("[");
int endBracket = str.indexOf("]");
if (bracket < 0) {
name = str;
startIndex = -1;
} else {
name = str.substring(0, bracket);
if (str.charAt(bracket + 1) == '*') {
startIndex = -1;
endIndex = -1;
} else {
int i = bracket + 1;
for (; i < endBracket; i++) {
if (str.charAt(i) == ':')
break;
startIndex *= 10;
startIndex += str.charAt(i) - '0';
}
if (str.charAt(i) == ':') {
i++;
for (; i < endBracket; i++) {
endIndex *= 10;
endIndex += str.charAt(i) - '0';
}
} else {
endIndex = startIndex;
}
}
}
return new Component(name, startIndex, endIndex);
}
public String toString() {
StringBuffer buf = new StringBuffer();
buf.append(name);
buf.append("[");
if (startIndex < 0) {
buf.append("*");
} else {
buf.append(startIndex);
if (endIndex > startIndex) {
buf.append(":");
buf.append(endIndex);
}
}
buf.append("]");
return buf.toString();
}
}
public void set(List<Component> value);
public List<Component> get();
}
