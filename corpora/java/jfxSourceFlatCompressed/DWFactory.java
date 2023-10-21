package com.sun.javafx.font.directwrite;
import com.sun.javafx.font.PrismFontFactory;
import com.sun.javafx.font.PrismFontFile;
import com.sun.javafx.text.GlyphLayout;
import com.sun.prism.GraphicsPipeline;
public class DWFactory extends PrismFontFactory {
private static IDWriteFactory DWRITE_FACTORY = null;
private static IDWriteFontCollection FONT_COLLECTION = null;
private static IWICImagingFactory WIC_FACTORY = null;
private static ID2D1Factory D2D_FACTORY = null;
private static Thread d2dThread;
public static PrismFontFactory getFactory() {
if (getDWriteFactory() == null) {
return null;
}
return new DWFactory();
}
private DWFactory() {
}
@Override
protected PrismFontFile createFontFile(String name, String filename,
int fIndex, boolean register,
boolean embedded, boolean copy,
boolean tracked) throws Exception {
return new DWFontFile(name, filename, fIndex, register,
embedded, copy, tracked);
}
@Override public GlyphLayout createGlyphLayout() {
return new DWGlyphLayout();
}
@Override
protected boolean registerEmbeddedFont(String path) {
IDWriteFactory factory = DWFactory.getDWriteFactory();
IDWriteFontFile fontFile = factory.CreateFontFileReference(path);
if (fontFile == null) return false;
boolean[] isSupportedFontType = new boolean[1];
int[] fontFileType = new int[1];
int[] fontFaceType = new int[1];
int[] numberOfFaces = new int[1];
int hr = fontFile.Analyze(isSupportedFontType, fontFileType, fontFaceType, numberOfFaces);
fontFile.Release();
if (hr != OS.S_OK) return false;
return isSupportedFontType[0];
}
static IDWriteFactory getDWriteFactory() {
if (DWRITE_FACTORY == null) {
DWRITE_FACTORY = OS.DWriteCreateFactory(OS.DWRITE_FACTORY_TYPE_SHARED);
}
return DWRITE_FACTORY;
}
static IDWriteFontCollection getFontCollection() {
if (FONT_COLLECTION == null) {
FONT_COLLECTION = getDWriteFactory().GetSystemFontCollection(false);
}
return FONT_COLLECTION;
}
private static void checkThread() {
Thread current = Thread.currentThread();
if (d2dThread == null) {
d2dThread = current;
}
if (d2dThread != current) {
throw new IllegalStateException(
"This operation is not permitted on the current thread ["
+ current.getName() + "]");
}
}
static synchronized IWICImagingFactory getWICFactory() {
checkThread();
if (WIC_FACTORY == null) {
if (!OS.CoInitializeEx(OS.COINIT_APARTMENTTHREADED | OS.COINIT_DISABLE_OLE1DDE)) {
return null;
}
WIC_FACTORY = OS.WICCreateImagingFactory();
if (WIC_FACTORY == null) {
return null;
}
GraphicsPipeline.getPipeline().addDisposeHook(() -> {
checkThread();
WIC_FACTORY.Release();
OS.CoUninitialize();
WIC_FACTORY = null;
});
}
return WIC_FACTORY;
}
static synchronized ID2D1Factory getD2DFactory() {
checkThread();
if (D2D_FACTORY == null) {
D2D_FACTORY = OS.D2D1CreateFactory(OS.D2D1_FACTORY_TYPE_SINGLE_THREADED);
}
return D2D_FACTORY;
}
}
