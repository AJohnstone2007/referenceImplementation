package com.sun.javafx.font.directwrite;
class IDWriteGlyphRunAnalysis extends IUnknown {
IDWriteGlyphRunAnalysis(long ptr) {
super(ptr);
}
byte[] CreateAlphaTexture(int textureType, RECT textureBounds) {
return OS.CreateAlphaTexture(ptr, textureType, textureBounds);
}
RECT GetAlphaTextureBounds(int textureType) {
return OS.GetAlphaTextureBounds(ptr, textureType);
}
}
