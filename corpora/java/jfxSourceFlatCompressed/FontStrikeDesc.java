package com.sun.javafx.font;
import com.sun.javafx.geom.transform.BaseTransform;
public class FontStrikeDesc {
float[] matrix;
float size;
int aaMode;
public FontStrikeDesc(float fontSize, BaseTransform transform, int aaMode) {
BaseTransform tx2d = transform;
size = fontSize;
this.aaMode = aaMode;
matrix = new float[4];
matrix[0] = (float)tx2d.getMxx();
matrix[1] = (float)tx2d.getMxy();
matrix[2] = (float)tx2d.getMyx();
matrix[3] = (float)tx2d.getMyy();
}
private int hash;
@Override
public int hashCode() {
if (hash == 0) {
hash =
aaMode+
Float.floatToIntBits(size)+
Float.floatToIntBits((float)matrix[0])+
Float.floatToIntBits((float)matrix[1])+
Float.floatToIntBits((float)matrix[2])+
Float.floatToIntBits((float)matrix[3]);
}
return hash;
}
@Override
public boolean equals(Object o) {
FontStrikeDesc other = (FontStrikeDesc)o;
return
this.aaMode == other.aaMode &&
this.matrix[0] == other.matrix[0] &&
this.matrix[1] == other.matrix[1] &&
this.matrix[2] == other.matrix[2] &&
this.matrix[3] == other.matrix[3] &&
this.size == other.size;
}
}
