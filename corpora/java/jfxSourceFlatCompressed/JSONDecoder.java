package javafx.scene.web;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
class JSONDecoder {
private final JS2JavaBridge owner;
public JSONDecoder(JS2JavaBridge owner) {
this.owner = owner;
}
public Object decode(String string) {
return decode(new StringCharacterIterator(string));
}
private Object decode(CharacterIterator it) {
char ch = getSignificant(it);
switch (ch) {
case '"': {
String s = decodeString(it);
if (s != null && !s.isEmpty()) {
String prefix = s.substring(0, 1);
String value = s.substring(1);
if ("o".equals(prefix)) {
Object o = owner.getJavaObjectForjsId(value);
if (o != null) {
return o;
} else {
return new JSObjectIosImpl(owner, value);
}
} else if ("s".equals(prefix)) {
return value;
}
}
return s;
}
case '[':
return decodeArray(it);
case '{':
return decodeObject(it);
case 'n':
if (!"null".equals(getString(it, 4))) {
throw new IllegalArgumentException("decoding error (null)");
}
return null;
case 't':
if (!"true".equals(getString(it, 4))) {
throw new IllegalArgumentException("decoding error (true)");
}
return Boolean.TRUE;
case 'f':
if (!"false".equals(getString(it, 5))) {
throw new IllegalArgumentException("decoding error (false)");
}
return Boolean.FALSE;
}
StringBuilder sb = new StringBuilder();
while ("+-0123456789.Ee".indexOf(ch) >= 0) {
sb.append(ch);
ch = it.next();
}
String sNum = sb.toString();
if (sNum.indexOf('.') >= 0 || sNum.indexOf('E') >= 0 || sNum.indexOf('e') >= 0) {
return Double.valueOf(sNum);
} else {
long val = Long.parseLong(sNum);
if ((val <= Integer.MAX_VALUE) && (Integer.MIN_VALUE <= val)) {
return Integer.valueOf((int) val);
} else {
return Double.valueOf(val);
}
}
}
private char getSignificant(CharacterIterator it) {
char ch = it.current();
while (Character.isWhitespace(ch) && ch != CharacterIterator.DONE) {
ch = it.next();
}
return ch;
}
private String getString(CharacterIterator it, int len) {
char[] buffer = new char[len];
for (int i=0; i<len; i++) {
buffer[i] = it.current();
it.next();
}
return new String(buffer);
}
private String decodeString(CharacterIterator it) {
StringBuilder sb = new StringBuilder();
for (char ch = it.next(); ch != '"'; ch=it.next()) {
if (ch == CharacterIterator.DONE) {
throw new IllegalArgumentException("Unterminated string");
} else if (ch == '\\') {
switch (ch = it.next()) {
case '"':
case '\\':
case '/':
sb.append(ch);
break;
case 'b':
sb.append('\b');
break;
case 'f':
sb.append('\f');
break;
case 'n':
sb.append('\n');
break;
case 'r':
sb.append('\r');
break;
case 't':
sb.append('\t');
break;
case 'u':
int n = 0;
for (int i=0; i<4; i++) {
n = (n << 4) + dehex(it.next());
}
sb.append((char)n);
break;
default:
throw new IllegalArgumentException("Illegal escape sequence");
}
} else {
sb.append(ch);
}
}
it.next();
return sb.toString();
}
private int dehex(char ch) {
if (ch >= '0' && ch <= '9') {
return ch - '0';
}
if (ch >= 'a' && ch <= 'f') {
return ch - 'a' + 10;
}
if (ch >= 'A' && ch <= 'F') {
return ch - 'A' + 10;
}
throw new IllegalArgumentException("Wrong unicode value");
}
private Object[] decodeArray(CharacterIterator it) {
ArrayList arr = new ArrayList();
it.next();
char ch = getSignificant(it);
while (ch != ']') {
Object obj = decode(it);
arr.add(obj);
ch = getSignificant(it);
switch (ch) {
case ',':
it.next();
break;
case ']':
break;
default:
throw new IllegalArgumentException("Array decoding error (expect ']' or ',')");
}
}
it.next();
return arr.toArray();
}
private Object decodeObject(CharacterIterator it) {
Map<String, Object> map = new HashMap<String, Object>();
it.next();
char ch = getSignificant(it);
while (ch != '}') {
if (getSignificant(it) != '"') {
throw new IllegalArgumentException("Object decoding error (key should be a string)");
}
String key = decodeString(it);
if (getSignificant(it) != ':') {
throw new IllegalArgumentException("Object decoding error (expect ':')");
}
it.next();
Object value = decode(it);
map.put(key, value);
ch = getSignificant(it);
switch (ch) {
case ',':
it.next();
break;
case '}':
break;
default:
throw new IllegalArgumentException("Object decoding error (expect '}' or ',')");
}
}
it.next();
return map;
}
}
