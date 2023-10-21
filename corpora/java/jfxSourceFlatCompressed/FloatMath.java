package com.sun.marlin;
public final class FloatMath implements MarlinConst {
static final boolean CHECK_OVERFLOW = true;
static final boolean CHECK_NAN = true;
private FloatMath() {
}
public static int max(final int a, final int b) {
return (a >= b) ? a : b;
}
public static int min(final int a, final int b) {
return (a <= b) ? a : b;
}
public static int ceil_int(final float a) {
final int intpart = (int) a;
if (a <= intpart
|| (CHECK_OVERFLOW && intpart == Integer.MAX_VALUE)
|| CHECK_NAN && Float.isNaN(a)) {
return intpart;
}
return intpart + 1;
}
public static int ceil_int(final double a) {
final int intpart = (int) a;
if (a <= intpart
|| (CHECK_OVERFLOW && intpart == Integer.MAX_VALUE)
|| CHECK_NAN && Double.isNaN(a)) {
return intpart;
}
return intpart + 1;
}
public static int floor_int(final float a) {
final int intpart = (int) a;
if (a >= intpart
|| (CHECK_OVERFLOW && intpart == Integer.MIN_VALUE)
|| CHECK_NAN && Float.isNaN(a)) {
return intpart;
}
return intpart - 1;
}
public static int floor_int(final double a) {
final int intpart = (int) a;
if (a >= intpart
|| (CHECK_OVERFLOW && intpart == Integer.MIN_VALUE)
|| CHECK_NAN && Double.isNaN(a)) {
return intpart;
}
return intpart - 1;
}
}
