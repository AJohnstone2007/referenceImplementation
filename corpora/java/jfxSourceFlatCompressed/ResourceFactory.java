package com.sun.prism;
import com.sun.prism.Texture.WrapMode;
import com.sun.prism.impl.TextureResourcePool;
import com.sun.prism.shape.ShapeRep;
public interface ResourceFactory extends GraphicsResource {
public boolean isDisposed();
public boolean isDeviceReady();
public TextureResourcePool getTextureResourcePool();
public Texture createTexture(Image image,
Texture.Usage usageHint,
Texture.WrapMode wrapMode);
public Texture createTexture(Image image, Texture.Usage usageHint,
Texture.WrapMode wrapMode, boolean useMipmap);
public Texture createTexture(PixelFormat formatHint,
Texture.Usage usageHint,
Texture.WrapMode wrapMode,
int w, int h);
public Texture createTexture(PixelFormat formatHint, Texture.Usage usageHint,
Texture.WrapMode wrapMode, int w, int h, boolean useMipmap);
public Texture createTexture(MediaFrame frame);
public Texture getCachedTexture(Image image, Texture.WrapMode wrapMode);
public Texture getCachedTexture(Image image, Texture.WrapMode wrapMode, boolean useMipmap);
public boolean isFormatSupported(PixelFormat format);
public boolean isWrapModeSupported(WrapMode mode);
public int getMaximumTextureSize();
public int getRTTWidth(int w, Texture.WrapMode wrapMode);
public int getRTTHeight(int h, Texture.WrapMode wrapMode);
public Texture createMaskTexture(int width, int height, Texture.WrapMode wrapMode);
public Texture createFloatTexture(int width, int height);
public RTTexture createRTTexture(int width, int height, Texture.WrapMode wrapMode);
public RTTexture createRTTexture(int width, int height, Texture.WrapMode wrapMode, boolean msaa);
public boolean isCompatibleTexture(Texture tex);
public Presentable createPresentable(PresentableState pState);
public ShapeRep createPathRep();
public ShapeRep createRoundRectRep();
public ShapeRep createEllipseRep();
public ShapeRep createArcRep();
public void addFactoryListener(ResourceFactoryListener l);
public void removeFactoryListener(ResourceFactoryListener l);
public void setRegionTexture(Texture texture);
public Texture getRegionTexture();
public void setGlyphTexture(Texture texture);
public Texture getGlyphTexture();
public boolean isSuperShaderAllowed();
public PhongMaterial createPhongMaterial();
public MeshView createMeshView(Mesh mesh);
public Mesh createMesh();
}
