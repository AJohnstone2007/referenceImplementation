package com.sun.prism.es2;
class ES2Light {
float x, y, z = 0;
float r, g, b, w = 1;
float ca, la, qa, isAttenuated;
float maxRange;
float dirX, dirY, dirZ;
float innerAngle, outerAngle, falloff;
ES2Light(float x, float y, float z, float r, float g, float b, float w, float ca, float la, float qa,
float isAttenuated, float maxRange, float dirX, float dirY, float dirZ,
float innerAngle, float outerAngle, float falloff) {
this.x = x;
this.y = y;
this.z = z;
this.r = r;
this.g = g;
this.b = b;
this.w = w;
this.ca = ca;
this.la = la;
this.qa = qa;
this.isAttenuated = isAttenuated;
this.maxRange = maxRange;
this.dirX = dirX;
this.dirY = dirY;
this.dirZ = dirZ;
this.innerAngle = innerAngle;
this.outerAngle = outerAngle;
this.falloff = falloff;
}
boolean isPointLight() {
return falloff == 0 && outerAngle == 180 && isAttenuated > 0.5;
}
boolean isDirectionalLight() {
return isAttenuated < 0.5;
}
}
