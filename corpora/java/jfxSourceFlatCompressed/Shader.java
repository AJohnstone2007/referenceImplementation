package com.sun.prism.ps;
import com.sun.prism.GraphicsResource;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
public interface Shader extends GraphicsResource {
public void enable();
public void disable();
public boolean isValid();
public void setConstant(String name, int i0);
public void setConstant(String name, int i0, int i1);
public void setConstant(String name, int i0, int i1, int i2);
public void setConstant(String name, int i0, int i1, int i2, int i3);
public void setConstants(String name, IntBuffer buf, int off, int count);
public void setConstant(String name, float f0);
public void setConstant(String name, float f0, float f1);
public void setConstant(String name, float f0, float f1, float f2);
public void setConstant(String name, float f0, float f1, float f2, float f3);
public void setConstants(String name, FloatBuffer buf, int off, int count);
}
