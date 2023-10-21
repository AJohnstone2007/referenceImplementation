package com.sun.prism.es2;
class GLGPUInfo {
final String vendor;
final String model;
GLGPUInfo(String vendor, String model) {
this.vendor = vendor;
this.model = model;
}
boolean matches(GLGPUInfo gi) {
boolean result = true;
if (gi.vendor != null) {
result = vendor.startsWith(gi.vendor);
}
if (gi.model != null) {
result = model.contains(gi.model);
}
return result;
}
@Override public String toString() {
return "GLGPUInfo [vendor = " + vendor + ", model = " + model + "]";
}
}
