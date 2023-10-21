package com.sun.scenario.effect.light;
import com.sun.scenario.effect.Color4f;
public class DistantLight extends Light {
private float azimuth;
private float elevation;
public DistantLight() {
this(0f, 0f, Color4f.WHITE);
}
public DistantLight(float azimuth, float elevation, Color4f color) {
super(Type.DISTANT, color);
this.azimuth = azimuth;
this.elevation = elevation;
}
public float getAzimuth() {
return azimuth;
}
public void setAzimuth(float azimuth) {
this.azimuth = azimuth;
}
public float getElevation() {
return elevation;
}
public void setElevation(float elevation) {
this.elevation = elevation;
}
@Override
public float[] getNormalizedLightPosition() {
double a = Math.toRadians(azimuth);
double e = Math.toRadians(elevation);
float x = (float)(Math.cos(a) * Math.cos(e));
float y = (float)(Math.sin(a) * Math.cos(e));
float z = (float)(Math.sin(e));
float len = (float)Math.sqrt(x*x + y*y + z*z);
if (len == 0f) len = 1f;
float[] pos = new float[] {x/len, y/len, z/len};
return pos;
}
}
