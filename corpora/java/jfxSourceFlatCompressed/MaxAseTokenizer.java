package com.javafx.experiments.importers.max;
import java.io.*;
import javafx.geometry.Point3D;
public class MaxAseTokenizer {
static public class Callback {
static public class ParamList {
byte[][] args;
int len[], argc, current = 1;
public ParamList(byte[][] args, int[] len, int argc) {
this.args = args;
this.len = len;
this.argc = argc;
}
public float nextFloat() {
if (argc == current) return 0;
current++;
return MaxAseTokenizer.parseFloat(args[current-1], len[current-1]);
}
public int nextInt() {
if (argc == current) return 0;
current++;
return MaxAseTokenizer.parseInt(args[current-1], len[current-1]);
}
public String nextString() {
if (argc == current) return null;
current++;
return MaxAseTokenizer.parseString(args[current-1], len[current-1]);
}
Point3D nextVector() {
return new Point3D(nextFloat(), nextFloat(), nextFloat());
}
}
static boolean equals(byte data[], int len, byte cData[]) {
if (len != cData.length) return false;
for (int i=0; i!=len; ++i)
if (data[i] != cData[i])
return false;
return true;
}
public static int parseInt(byte data[], int l) {
return MaxAseTokenizer.parseInt(data, l);
}
public static float parseFloat(byte data[], int l) {
return MaxAseTokenizer.parseFloat(data, l);
}
public static String parseString(byte data[], int l) {
return MaxAseTokenizer.parseString(data, l);
}
void value(byte args[][], int len[], int argc) {
onValue(parseString(args[0], len[0]), new ParamList(args, len, argc));
}
Callback object(byte args[][], int len[], int argc) {
return onObject(parseString(args[0], len[0]), new ParamList(args, len, argc));
}
void onValue(String name, ParamList list) {}
Callback onObject(String name, ParamList list) { return this; }
}
static public class CallbackNOP extends Callback {
static public final CallbackNOP instance = new CallbackNOP();
void value(byte args[][], int len[], int argc) {}
Callback object(byte args[][], int len[], int argc) { return this; }
}
public static void parse(InputStream stream, Callback callback) throws IOException {
new ParserImpl(stream).parse(callback);
}
static private class ParserImpl {
final InputStream stream;
final byte buffer1K[] = new byte[1024];
private byte line[][] = new byte[32][];
private int lineLen[] = new int[32];
private int bufferBytes = 0, bufferPos = 0;
private boolean hasData = true;
ParserImpl(InputStream inStream) {
stream = inStream;
for (int i=0; i!=line.length; ++i)
line[i] = new byte[64];
}
final byte CLOSE = 0x7D;
final byte OPEN = 0x7B;
private void parse(Callback callback) throws IOException {
while (hasData) {
int argc = parseLine();
if (argc < 0) return;
if (lineLen[0] == 1 && line[0][0] == CLOSE)
return;
if (lineLen[argc-1] == 1 && line[argc-1][0] == OPEN) {
parse(callback.object(line, lineLen, argc));
} else {
callback.value(line, lineLen, argc);
}
}
}
private int parseLine() throws IOException {
byte lLine[][] = line;
byte cLine[] = lLine[0];
boolean inString = false;
for (int args = 0, len = 0;;) {
if (bufferPos == bufferBytes) {
bufferBytes = stream.read(buffer1K);
if (bufferBytes < 0) {
hasData = false;
return 0;
}
bufferPos = 0;
}
for (int i = bufferPos; i != bufferBytes; ++i) {
byte b = buffer1K[i];
switch (b) {
case 0x0D: break;
case 0x0A:
bufferPos = i + 1;
lineLen[args] = len;
return len>0 ? args+1 : args;
case 0x22:
inString = !inString;
break;
case 0x20: case 0x9:
if (!inString) {
if (len != 0) {
if (++args >= lLine.length)
lLine = growTokens();
lineLen[args - 1] = len;
cLine = lLine[args];
len = 0;
}
break;
}
default:
if (cLine.length == len)
cLine = growToken(args);
cLine[len++] = b;
}
}
bufferPos = bufferBytes;
}
}
private byte[][] growTokens() {
int lineLen2[] = new int[line.length*2];
System.arraycopy(lineLen, 0, lineLen2, 0, line.length);
lineLen = lineLen2;
byte line2[][] = new byte[line.length*2][];
System.arraycopy(line, 0, line2, 0, line.length);
return line = line2;
}
private byte[] growToken(int i) {
byte line2[] = new byte[line[i].length*2];
System.arraycopy(line[i], 0, line2, 0, line[i].length);
return line[i] = line2;
}
}
public static int parseInt(byte data[], int l) {
int result = 0, sign = 1, i = 0;
if (l>0 && data[0]=='-') {
sign = -1;
i = 1;
}
for (; i!=l; ++i) {
byte ch = data[i];
if (ch >= 0x30 && ch <= 0x39) {
result = result*10 + (int)ch - 0x30;
} else break;
}
return result * sign;
}
public static float parseFloat(byte data[], int l) {
float result = 0, sign = 1;
int i = 0; byte ch = 0;
if (l>0 && data[0]=='-') {
sign = -1;
i = 1;
}
for (;i!=l; ++i) {
ch = data[i];
if (ch >= 0x30 && ch <= 0x39) {
result = result*10 + (int)ch - 0x30;
} else break;
}
if (i!=l && ch == '.') {
float m = 1.f;
for (++i; i!=l; ++i) {
ch = data[i];
if (ch >= 0x30 && ch <= 0x39) {
m *= 1.f/10;
result += m*((int)ch - 0x30);
} else break;
}
}
return result * sign;
}
public static String parseString(byte data[], int l) {
return new String(data, 0, l);
}
}
