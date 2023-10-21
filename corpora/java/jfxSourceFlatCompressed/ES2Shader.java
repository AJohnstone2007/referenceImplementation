package com.sun.prism.es2;
import com.sun.prism.impl.BaseGraphicsResource;
import com.sun.prism.impl.Disposer;
import com.sun.prism.ps.Shader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
public class ES2Shader extends BaseGraphicsResource implements Shader {
private static class Uniform {
private int location;
private Object values;
}
private int programID;
private final ES2Context context;
private final Map<String, Uniform> uniforms = new HashMap<String, Uniform>();
private final int maxTexCoordIndex;
private final boolean isPixcoordUsed;
private boolean valid;
private float[] currentMatrix;
private ES2Shader(ES2Context context, int programID,
int vertexShaderID, int[] fragmentShaderID,
Map<String, Integer> samplers,
int maxTexCoordIndex, boolean isPixcoordUsed)
throws RuntimeException {
super(new ES2ShaderDisposerRecord(context,
vertexShaderID,
fragmentShaderID,
programID));
this.context = context;
this.programID = programID;
this.maxTexCoordIndex = maxTexCoordIndex;
this.isPixcoordUsed = isPixcoordUsed;
this.valid = (programID != 0);
if (valid && samplers != null) {
int currentProgram = context.getShaderProgram();
context.setShaderProgram(programID);
for (String key : samplers.keySet()) {
setConstant(key, samplers.get(key));
}
context.setShaderProgram(currentProgram);
}
}
static ES2Shader createFromSource(ES2Context context,
String vert, String[] frag,
Map<String, Integer> samplers,
Map<String, Integer> attributes,
int maxTexCoordIndex,
boolean isPixcoordUsed) {
GLContext glCtx = context.getGLContext();
if (!glCtx.isShaderCompilerSupported()) {
throw new RuntimeException("Shader compiler not available on this device");
}
if (vert == null || frag == null || frag.length == 0) {
throw new RuntimeException(
"Both vertexShaderSource and fragmentShaderSource "
+ "must be specified");
}
int vertexShaderID = glCtx.compileShader(vert, true);
if (vertexShaderID == 0) {
throw new RuntimeException("Error creating vertex shader");
}
int[] fragmentShaderID = new int[frag.length];
for (int i = 0; i < frag.length; i++) {
fragmentShaderID[i] = glCtx.compileShader(frag[i], false);
if (fragmentShaderID[i] == 0) {
glCtx.deleteShader(vertexShaderID);
throw new RuntimeException("Error creating fragment shader");
}
}
String[] attrs = new String[attributes.size()];
int[] indexs = new int[attrs.length];
int i = 0;
for (String attr : attributes.keySet()) {
attrs[i] = attr;
indexs[i] = attributes.get(attr);
i++;
}
int programID = glCtx.createProgram(vertexShaderID, fragmentShaderID,
attrs, indexs);
if (programID == 0) {
throw new RuntimeException("Error creating shader program");
}
return new ES2Shader(context,
programID, vertexShaderID, fragmentShaderID,
samplers, maxTexCoordIndex, isPixcoordUsed);
}
static ES2Shader createFromSource(ES2Context context,
String vert, InputStream frag,
Map<String, Integer> samplers,
Map<String, Integer> attributes,
int maxTexCoordIndex,
boolean isPixcoordUsed) {
String[] fragmentShaderSource = new String[] {readStreamIntoString(frag)};
return createFromSource(context, vert, fragmentShaderSource, samplers,
attributes, maxTexCoordIndex, isPixcoordUsed);
}
static String readStreamIntoString(InputStream in) {
StringBuffer sb = new StringBuffer(1024);
BufferedReader reader = new BufferedReader(new InputStreamReader(in));
try {
char[] chars = new char[1024];
int numRead = 0;
while ((numRead = reader.read(chars)) > -1) {
sb.append(String.valueOf(chars, 0, numRead));
}
} catch (IOException e) {
throw new RuntimeException("Error reading shader stream");
} finally {
try {
reader.close();
} catch (IOException e) {
throw new RuntimeException("Error closing reader");
}
}
return sb.toString();
}
public int getProgramObject() {
return programID;
}
public int getMaxTexCoordIndex() {
return maxTexCoordIndex;
}
public boolean isPixcoordUsed() {
return isPixcoordUsed;
}
private Uniform getUniform(String name) {
Uniform uniform = uniforms.get(name);
if (uniform == null) {
int loc = context.getGLContext().getUniformLocation(programID, name);
uniform = new Uniform();
uniform.location = loc;
uniforms.put(name, uniform);
}
return uniform;
}
@Override
public void enable() throws RuntimeException {
context.updateShaderProgram(programID);
}
@Override
public void disable() throws RuntimeException {
context.updateShaderProgram(0);
}
@Override
public boolean isValid() {
return valid;
}
@Override
public void setConstant(String name, int i0)
throws RuntimeException {
Uniform uniform = getUniform(name);
if (uniform.location == -1) {
return;
}
if (uniform.values == null) {
uniform.values = new int[1];
}
int[] values = (int[]) uniform.values;
if (values[0] != i0) {
values[0] = i0;
context.getGLContext().uniform1i(uniform.location, i0);
}
}
@Override
public void setConstant(String name, int i0, int i1)
throws RuntimeException {
Uniform uniform = getUniform(name);
if (uniform.location == -1) {
return;
}
if (uniform.values == null) {
uniform.values = new int[2];
}
int[] values = (int[]) uniform.values;
if (values[0] != i0 || values[1] != i1) {
values[0] = i0;
values[1] = i1;
context.getGLContext().uniform2i(uniform.location, i0, i1);
}
}
@Override
public void setConstant(String name, int i0, int i1, int i2)
throws RuntimeException {
Uniform uniform = getUniform(name);
if (uniform.location == -1) {
return;
}
if (uniform.values == null) {
uniform.values = new int[3];
}
int[] values = (int[]) uniform.values;
if (values[0] != i0 || values[1] != i1 || values[2] != i2) {
values[0] = i0;
values[1] = i1;
values[2] = i2;
context.getGLContext().uniform3i(uniform.location, i0, i1, i2);
}
}
@Override
public void setConstant(String name, int i0, int i1, int i2, int i3)
throws RuntimeException {
Uniform uniform = getUniform(name);
if (uniform.location == -1) {
return;
}
if (uniform.values == null) {
uniform.values = new int[4];
}
int[] values = (int[]) uniform.values;
if (values[0] != i0 || values[1] != i1 || values[2] != i2 || values[3] != i3) {
values[0] = i0;
values[1] = i1;
values[2] = i2;
values[3] = i3;
context.getGLContext().uniform4i(uniform.location, i0, i1, i2, i3);
}
}
@Override
public void setConstant(String name, float f0)
throws RuntimeException {
Uniform uniform = getUniform(name);
if (uniform.location == -1) {
return;
}
if (uniform.values == null) {
uniform.values = new float[1];
}
float[] values = (float[]) uniform.values;
if (values[0] != f0) {
values[0] = f0;
context.getGLContext().uniform1f(uniform.location, f0);
}
}
@Override
public void setConstant(String name, float f0, float f1)
throws RuntimeException {
Uniform uniform = getUniform(name);
if (uniform.location == -1) {
return;
}
if (uniform.values == null) {
uniform.values = new float[2];
}
float[] values = (float[]) uniform.values;
if (values[0] != f0 || values[1] != f1) {
values[0] = f0;
values[1] = f1;
context.getGLContext().uniform2f(uniform.location, f0, f1);
}
}
@Override
public void setConstant(String name, float f0, float f1, float f2)
throws RuntimeException {
Uniform uniform = getUniform(name);
if (uniform.location == -1) {
return;
}
if (uniform.values == null) {
uniform.values = new float[3];
}
float[] values = (float[]) uniform.values;
if (values[0] != f0 || values[1] != f1 || values[2] != f2) {
values[0] = f0;
values[1] = f1;
values[2] = f2;
context.getGLContext().uniform3f(uniform.location, f0, f1, f2);
}
}
@Override
public void setConstant(String name, float f0, float f1, float f2, float f3)
throws RuntimeException {
Uniform uniform = getUniform(name);
if (uniform.location == -1) {
return;
}
if (uniform.values == null) {
uniform.values = new float[4];
}
float[] values = (float[]) uniform.values;
if (values[0] != f0 || values[1] != f1 || values[2] != f2 || values[3] != f3) {
values[0] = f0;
values[1] = f1;
values[2] = f2;
values[3] = f3;
context.getGLContext().uniform4f(uniform.location, f0, f1, f2, f3);
}
}
@Override
public void setConstants(String name, IntBuffer buf, int off, int count)
throws RuntimeException {
int loc = getUniform(name).location;
if (loc == -1) {
return;
}
context.getGLContext().uniform4iv(loc, count, buf);
}
@Override
public void setConstants(String name, FloatBuffer buf, int off, int count)
throws RuntimeException {
int loc = getUniform(name).location;
if (loc == -1) {
return;
}
context.getGLContext().uniform4fv(loc, count, buf);
}
public void setMatrix(String name, float buf[]) throws RuntimeException {
int loc = getUniform(name).location;
if (loc == -1) {
return;
}
if (currentMatrix == null) {
currentMatrix = new float[GLContext.NUM_MATRIX_ELEMENTS];
}
if (!Arrays.equals(currentMatrix, buf)) {
context.getGLContext().uniformMatrix4fv(loc, false, buf);
System.arraycopy(buf, 0, currentMatrix, 0, buf.length);
}
}
@Override
public void dispose() throws RuntimeException {
if (programID != 0) {
disposerRecord.dispose();
programID = 0;
}
valid = false;
}
private static class ES2ShaderDisposerRecord implements Disposer.Record {
private final ES2Context context;
private int vertexShaderID;
private int[] fragmentShaderID;
private int programID;
private ES2ShaderDisposerRecord(ES2Context context,
int vertexShaderID,
int[] fragmentShaderID,
int programID) {
this.context = context;
this.vertexShaderID = vertexShaderID;
this.fragmentShaderID = fragmentShaderID;
this.programID = programID;
}
@Override
public void dispose() {
if (programID != 0) {
context.getGLContext().disposeShaders(programID,
vertexShaderID, fragmentShaderID);
programID = vertexShaderID = 0;
fragmentShaderID = null;
}
}
}
}
