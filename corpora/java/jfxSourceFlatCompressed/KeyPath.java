package com.sun.javafx.fxml.expression;
import java.io.IOException;
import java.io.PushbackReader;
import java.io.StringReader;
import java.util.AbstractList;
import java.util.ArrayList;
public class KeyPath extends AbstractList<String> {
private ArrayList<String> elements;
public KeyPath(ArrayList<String> elements) {
if (elements == null) {
throw new NullPointerException();
}
this.elements = elements;
}
@Override
public String get(int index) {
return elements.get(index);
}
@Override
public int size() {
return elements.size();
}
@Override
public String toString() {
StringBuilder stringBuilder = new StringBuilder();
for (int i = 0, n = elements.size(); i < n; i++) {
String element = elements.get(i);
if (Character.isDigit(element.charAt(0))) {
stringBuilder.append("[");
stringBuilder.append(element);
stringBuilder.append("]");
} else {
if (i > 0) {
stringBuilder.append(".");
}
stringBuilder.append(element);
}
}
return stringBuilder.toString();
}
public static KeyPath parse(String value) {
KeyPath keyPath;
try {
PushbackReader reader = new PushbackReader(new StringReader(value));
try {
keyPath = parse(reader);
} finally {
reader.close();
}
} catch (IOException exception) {
throw new RuntimeException(exception);
}
return keyPath;
}
protected static KeyPath parse(PushbackReader reader) throws IOException {
ArrayList<String> elements = new ArrayList<String>();
int c = reader.read();
while (c != -1 && (Character.isJavaIdentifierStart(c) || c == '[')) {
StringBuilder keyBuilder = new StringBuilder();
boolean bracketed = (c == '[');
if (bracketed) {
c = reader.read();
boolean quoted = (c == '"' || c == '\'');
char quote;
if (quoted) {
quote = (char)c;
c = reader.read();
} else {
quote = Character.UNASSIGNED;
}
while (c != -1
&& bracketed) {
if (Character.isISOControl(c)) {
throw new IllegalArgumentException("Illegal identifier character.");
}
if (!quoted
&& !Character.isDigit(c)) {
throw new IllegalArgumentException("Illegal character in index value.");
}
keyBuilder.append((char)c);
c = reader.read();
if (quoted) {
quoted = c != quote;
if (!quoted) {
c = reader.read();
}
}
bracketed = c != ']';
}
if (quoted) {
throw new IllegalArgumentException("Unterminated quoted identifier.");
}
if (bracketed) {
throw new IllegalArgumentException("Unterminated bracketed identifier.");
}
c = reader.read();
} else {
while(c != -1 && (c != '.' && c != '[' && Character.isJavaIdentifierPart(c))) {
keyBuilder.append((char)c);
c = reader.read();
}
}
if (c == '.') {
c = reader.read();
if (c == -1) {
throw new IllegalArgumentException("Illegal terminator character.");
}
}
if (keyBuilder.length() == 0) {
throw new IllegalArgumentException("Missing identifier.");
}
elements.add(keyBuilder.toString());
}
if (elements.size() == 0) {
throw new IllegalArgumentException("Invalid path.");
}
if (c != -1) {
reader.unread(c);
}
return new KeyPath(elements);
}
}
