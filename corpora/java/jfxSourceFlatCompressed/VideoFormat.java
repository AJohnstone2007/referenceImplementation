package com.sun.media.jfxmedia.control;
import java.lang.annotation.Native;
import java.util.HashMap;
import java.util.Map;
public enum VideoFormat {
ARGB(FormatTypes.FORMAT_TYPE_ARGB),
BGRA_PRE(FormatTypes.FORMAT_TYPE_BGRA_PRE),
YCbCr_420p(FormatTypes.FORMAT_TYPE_YCBCR_420P),
YCbCr_422(FormatTypes.FORMAT_TYPE_YCBCR_422);
private int nativeType;
private static final Map<Integer, VideoFormat> lookupMap = new HashMap<Integer, VideoFormat>();
static {
for (VideoFormat fmt : VideoFormat.values()) {
lookupMap.put(fmt.getNativeType(), fmt);
}
}
private VideoFormat(int ntype) {
nativeType = ntype;
}
public int getNativeType() {
return nativeType;
}
public boolean isRGB() {
return this == ARGB || this == BGRA_PRE;
}
public boolean isEqualTo(int ntype) {
return nativeType == ntype;
}
public static VideoFormat formatForType(int ntype) {
return lookupMap.get(Integer.valueOf(ntype));
}
public static class FormatTypes {
@Native public static final int FORMAT_TYPE_ARGB = 1;
@Native public static final int FORMAT_TYPE_BGRA_PRE = 2;
@Native public static final int FORMAT_TYPE_YCBCR_420P = 100;
@Native public static final int FORMAT_TYPE_YCBCR_422 = 101;
}
}
