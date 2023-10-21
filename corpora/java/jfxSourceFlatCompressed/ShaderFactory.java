package com.sun.prism.ps;
import com.sun.prism.ResourceFactory;
import java.io.InputStream;
import java.util.Map;
public interface ShaderFactory extends ResourceFactory {
public Shader createShader(InputStream pixelShaderCode,
Map<String, Integer> samplers,
Map<String, Integer> params,
int maxTexCoordIndex,
boolean isPixcoordUsed,
boolean isPerVertexColorUsed);
public Shader createStockShader(String name);
}
