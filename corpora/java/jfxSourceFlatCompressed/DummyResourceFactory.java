package com.sun.prism.null3d;
import com.sun.glass.ui.Screen;
import com.sun.prism.MediaFrame;
import com.sun.prism.Mesh;
import com.sun.prism.MeshView;
import com.sun.prism.PhongMaterial;
import com.sun.prism.PixelFormat;
import com.sun.prism.Presentable;
import com.sun.prism.PresentableState;
import com.sun.prism.RTTexture;
import com.sun.prism.Texture;
import com.sun.prism.Texture.Usage;
import com.sun.prism.Texture.WrapMode;
import com.sun.prism.impl.TextureResourcePool;
import com.sun.prism.impl.ps.BaseShaderFactory;
import com.sun.prism.ps.Shader;
import java.io.InputStream;
import java.util.Map;
class DummyResourceFactory extends BaseShaderFactory {
private final DummyContext context;
DummyResourceFactory(Screen screen) {
this.context = new DummyContext(screen, this);
}
DummyContext getContext() {
return context;
}
public TextureResourcePool getTextureResourcePool() {
return DummyTexturePool.instance;
}
@Override
public DummyTexture createTexture(PixelFormat format,
Usage usagehint, WrapMode wrapMode,
int w, int h)
{
return new DummyTexture(context, format, wrapMode, w, h);
}
@Override
public DummyTexture createTexture(PixelFormat format, Usage usagehint,
WrapMode wrapMode, int w, int h, boolean useMipmap) {
return createTexture(format, usagehint, wrapMode, w, h);
}
public int getRTTWidth(int w, WrapMode wrapMode) {
return w;
}
public int getRTTHeight(int h, WrapMode wrapMode) {
return h;
}
@Override
public boolean isCompatibleTexture(Texture tex) {
return tex instanceof DummyTexture;
}
@Override
public RTTexture createRTTexture(int width, int height, WrapMode wrapMode) {
return createRTTexture(width, height, wrapMode, false);
}
@Override
public RTTexture createRTTexture(int width, int height, WrapMode wrapMode, boolean msaa) {
return new DummyRTTexture(context, wrapMode, width, height);
}
public Presentable createPresentable(PresentableState pState) {
DummyRTTexture rtt = new DummyRTTexture(context, WrapMode.CLAMP_NOT_NEEDED, pState.getWidth(), pState.getHeight());
return new DummySwapChain(context, pState, rtt);
}
public Shader createShader(InputStream pixelShaderCode,
Map<String, Integer> samplers,
Map<String, Integer> params,
int maxTexCoordIndex,
boolean isPixcoordUsed,
boolean isPerVertexColorUsed)
{
return new DummyShader(context, params);
}
public Shader createStockShader(String name) {
return new DummyShader(context, name);
}
@Override
public void dispose() {
}
@Override
public boolean isFormatSupported(PixelFormat format) {
return true;
}
public int getMaximumTextureSize() {
return 65536;
}
public Texture createTexture(MediaFrame frame) {
return new DummyTexture(context, frame.getPixelFormat(), WrapMode.CLAMP_TO_EDGE,
frame.getWidth(), frame.getHeight());
}
public PhongMaterial createPhongMaterial() {
throw new UnsupportedOperationException("Not supported yet.");
}
public MeshView createMeshView(Mesh mesh) {
throw new UnsupportedOperationException("Not supported yet.");
}
public Mesh createMesh() {
throw new UnsupportedOperationException("Not supported yet.");
}
}
