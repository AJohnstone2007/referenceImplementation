package com.sun.prism.d3d;
import com.sun.prism.impl.BufferUtil;
import com.sun.prism.ps.Shader;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Map;
final class D3DShader extends D3DResource implements Shader {
private static IntBuffer itmp;
private static FloatBuffer ftmp;
private final Map<String, Integer> registers;
private boolean valid;
D3DShader(D3DContext context, long pData, Map<String, Integer> registers) {
super(new D3DRecord(context, pData));
this.valid = (pData != 0L);
this.registers = registers;
}
static native long init(long pCtx, ByteBuffer buf,
int maxTexCoordIndex, boolean isPixcoordUsed, boolean isPerVertexColorUsed);
private static native int enable(long pCtx, long pData);
private static native int disable(long pCtx, long pData);
private static native int setConstantsF(long pCtx, long pData, int register,
FloatBuffer buf, int off,
int count);
private static native int setConstantsI(long pCtx, long pData, int register,
IntBuffer buf, int off,
int count);
private static native int nGetRegister(long pCtx, long pData, String name);
@Override
public void enable() {
int res = enable(d3dResRecord.getContext().getContextHandle(),
d3dResRecord.getResource());
valid &= res >= 0;
D3DContext.validate(res);
}
@Override
public void disable() {
int res = disable(d3dResRecord.getContext().getContextHandle(),
d3dResRecord.getResource());
valid &= res >= 0;
D3DContext.validate(res);
}
private static void checkTmpIntBuf() {
if (itmp == null) {
itmp = BufferUtil.newIntBuffer(4);
}
itmp.clear();
}
@Override
public void setConstant(String name, int i0) {
setConstant(name, (float)i0);
}
@Override
public void setConstant(String name, int i0, int i1) {
setConstant(name, (float)i0, (float)i1);
}
@Override
public void setConstant(String name, int i0, int i1, int i2) {
setConstant(name, (float)i0, (float)i1, (float)i2);
}
@Override
public void setConstant(String name, int i0, int i1, int i2, int i3) {
setConstant(name, (float)i0, (float)i1, (float)i2, (float)i3);
}
@Override
public void setConstants(String name, IntBuffer buf, int off, int count) {
throw new InternalError("Not yet implemented");
}
private static void checkTmpFloatBuf() {
if (ftmp == null) {
ftmp = BufferUtil.newFloatBuffer(4);
}
ftmp.clear();
}
@Override
public void setConstant(String name, float f0) {
checkTmpFloatBuf();
ftmp.put(f0);
setConstants(name, ftmp, 0, 1);
}
@Override
public void setConstant(String name, float f0, float f1) {
checkTmpFloatBuf();
ftmp.put(f0);
ftmp.put(f1);
setConstants(name, ftmp, 0, 1);
}
@Override
public void setConstant(String name, float f0, float f1, float f2) {
checkTmpFloatBuf();
ftmp.put(f0);
ftmp.put(f1);
ftmp.put(f2);
setConstants(name, ftmp, 0, 1);
}
@Override
public void setConstant(String name, float f0, float f1, float f2, float f3) {
checkTmpFloatBuf();
ftmp.put(f0);
ftmp.put(f1);
ftmp.put(f2);
ftmp.put(f3);
setConstants(name, ftmp, 0, 1);
}
@Override
public void setConstants(String name, FloatBuffer buf, int off, int count) {
int res = setConstantsF(d3dResRecord.getContext().getContextHandle(),
d3dResRecord.getResource(),
getRegister(name), buf, off, count);
valid &= res >= 0;
D3DContext.validate(res);
}
private int getRegister(String name) {
Integer reg = registers.get(name);
if (reg == null) {
int nRegister = nGetRegister(
d3dResRecord.getContext().getContextHandle(),
d3dResRecord.getResource(), name);
if (nRegister < 0) {
throw new IllegalArgumentException("Register not found for: " +
name);
}
registers.put(name, nRegister);
return nRegister;
}
return reg;
}
@Override
public boolean isValid() {
return valid;
}
@Override
public void dispose() {
super.dispose();
valid = false;
}
}
