package javafx.scene.web;
import java.lang.reflect.Array;
class JSONEncoder {
private final JS2JavaBridge owner;
public JSONEncoder(JS2JavaBridge owner) {
this.owner = owner;
}
private static char[] hexChars = "0123456789abcdef".toCharArray();
private static void encodeString(StringBuilder sb, String s) {
sb.append('"');
for (int i=0; i<s.length(); i++) {
char ch = s.charAt(i);
switch (ch) {
case '\"':
sb.append("\\\"");
break;
case '\\':
sb.append("\\\\");
break;
case '/':
sb.append("\\/");
break;
case '\b':
sb.append("\\b");
break;
case '\f':
sb.append("\\f");
break;
case '\n':
sb.append("\\n");
break;
case '\r':
sb.append("\\r");
break;
case '\t':
sb.append("\\t");
break;
default:
if (Character.isLetterOrDigit(ch)) {
sb.append(ch);
} else {
sb.append("\\u");
sb.append(hexChars[(ch & 0xf000) >> 12]);
sb.append(hexChars[(ch & 0x0f00) >> 8]);
sb.append(hexChars[(ch & 0x00f0) >> 4]);
sb.append(hexChars[(ch & 0x000f)]);
}
break;
}
}
sb.append('"');
}
public void encode(StringBuilder sb, Object object) {
if (object == null) {
sb.append("null");
} else if (object instanceof String || object instanceof Character) {
encodeString(sb, object.toString());
} else if (object instanceof Number || object instanceof Boolean) {
sb.append(object.toString());
} else if (object.getClass().isArray()) {
sb.append("[");
int length = Array.getLength(object);
for (int i = 0; i < length; i++) {
if (i>0) {
sb.append(",");
}
encode(sb, Array.get(object, i));
}
sb.append("]");
} else if (object instanceof JSObjectIosImpl) {
JSObjectIosImpl jsArg = (JSObjectIosImpl) object;
sb.append(jsArg.toScript().toString());
} else {
encodeJavaObject(object, sb);
}
}
private boolean encodedJavaObject(Object object, StringBuilder sb) {
String jsId = owner.getjsIdForJavaObject(object);
if (jsId != null) {
sb.append(owner.getJavaBridge()).append(".exportedJSObjects[").append(jsId).append("]");
return true;
}
return false;
}
private void encodeJavaObject(Object object, StringBuilder sb) {
if (!encodedJavaObject(object, sb)) {
owner.exportObject("anyname",object);
if (!encodedJavaObject(object, sb)) {
ExportedJavaObject jsObj = owner.createExportedJavaObject(object);
sb.append(jsObj.getJSDecl());
}
}
}
}
