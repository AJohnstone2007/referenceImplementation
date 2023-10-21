package com.sun.javafx.sg.prism;
import com.sun.prism.Image;
import com.sun.prism.Material;
import com.sun.prism.PhongMaterial;
import com.sun.prism.ResourceFactory;
import com.sun.prism.TextureMap;
import com.sun.prism.paint.Color;
public class NGPhongMaterial {
private static final Image WHITE_1X1 = Image.fromIntArgbPreData(new int[]{0xffffffff}, 1, 1);
private PhongMaterial material;
private Color diffuseColor;
private boolean diffuseColorDirty = true;
private TextureMap diffuseMap = new TextureMap(PhongMaterial.MapType.DIFFUSE);
private Color specularColor;
private boolean specularColorDirty = true;
private float specularPower;
private boolean specularPowerDirty = true;
private TextureMap specularMap = new TextureMap(PhongMaterial.MapType.SPECULAR);
private TextureMap bumpMap = new TextureMap(PhongMaterial.MapType.BUMP);
private TextureMap selfIllumMap = new TextureMap(PhongMaterial.MapType.SELF_ILLUM);
Material createMaterial(ResourceFactory f) {
if (material != null && !material.isValid()) {
disposeMaterial();
}
if (material == null) {
material = f.createPhongMaterial();
}
validate(f);
return material;
}
private void disposeMaterial() {
diffuseColorDirty = true;
specularColorDirty = true;
specularPowerDirty = true;
diffuseMap.setDirty(true);
specularMap.setDirty(true);
bumpMap.setDirty(true);
selfIllumMap.setDirty(true);
material.dispose();
material = null;
}
private void validate(ResourceFactory f) {
if (diffuseColorDirty) {
if (diffuseColor != null) {
material.setDiffuseColor(
diffuseColor.getRed(), diffuseColor.getGreen(),
diffuseColor.getBlue(), diffuseColor.getAlpha());
} else {
material.setDiffuseColor(0, 0, 0, 0);
}
diffuseColorDirty = false;
}
if (diffuseMap.isDirty()) {
if (diffuseMap.getImage() == null) {
diffuseMap.setImage(WHITE_1X1);
}
material.setTextureMap(diffuseMap);
}
if (bumpMap.isDirty()) {
material.setTextureMap(bumpMap);
}
if (selfIllumMap.isDirty()) {
material.setTextureMap(selfIllumMap);
}
if (specularMap.isDirty()) {
material.setTextureMap(specularMap);
}
if (specularColorDirty || specularPowerDirty) {
if (specularColor != null) {
float r = specularColor.getRed();
float g = specularColor.getGreen();
float b = specularColor.getBlue();
material.setSpecularColor(true, r, g, b, specularPower);
} else {
material.setSpecularColor(false, 1, 1, 1, specularPower);
}
specularColorDirty = false;
specularPowerDirty = false;
}
}
public void setDiffuseColor(Object diffuseColor) {
this.diffuseColor = (Color)diffuseColor;
diffuseColorDirty = true;
}
public void setSpecularColor(Object specularColor) {
this.specularColor = (Color)specularColor;
specularColorDirty = true;
}
public void setSpecularPower(float specularPower) {
if (specularPower < 0.001f) {
specularPower = 0.001f;
}
this.specularPower = specularPower;
specularPowerDirty = true;
}
public void setDiffuseMap(Object diffuseMap) {
this.diffuseMap.setImage((Image)diffuseMap);
this.diffuseMap.setDirty(true);
}
public void setSpecularMap(Object specularMap) {
this.specularMap.setImage((Image)specularMap);
this.specularMap.setDirty(true);
}
public void setBumpMap(Object bumpMap) {
this.bumpMap.setImage((Image)bumpMap);
this.bumpMap.setDirty(true);
}
public void setSelfIllumMap(Object selfIllumMap) {
this.selfIllumMap.setImage((Image)selfIllumMap);
this.selfIllumMap.setDirty(true);
}
Color test_getDiffuseColor() {
return diffuseColor;
}
}
