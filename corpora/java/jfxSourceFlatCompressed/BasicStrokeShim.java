package com.sun.prism;
public class BasicStrokeShim {
public static int join(BasicStroke bs) {
return bs.join;
}
public static void set_join(BasicStroke bs, int join) {
bs.join = join;
}
public static int cap(BasicStroke bs) {
return bs.cap;
}
public static void set_cap(BasicStroke bs, int cap) {
bs.cap = cap;
}
public static float width(BasicStroke bs) {
return bs.width;
}
public static void set_width(BasicStroke bs, float width) {
bs.width = width;
}
}
