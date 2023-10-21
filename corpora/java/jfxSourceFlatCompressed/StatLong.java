package com.sun.marlin.stats;
public class StatLong {
public final String name;
public long count = 0l;
public long sum = 0l;
public long min = Integer.MAX_VALUE;
public long max = Integer.MIN_VALUE;
public StatLong(final String name) {
this.name = name;
}
public void reset() {
count = 0l;
sum = 0l;
min = Integer.MAX_VALUE;
max = Integer.MIN_VALUE;
}
public void add(final int val) {
count++;
sum += val;
if (val < min) {
min = val;
}
if (val > max) {
max = val;
}
}
public void add(final long val) {
count++;
sum += val;
if (val < min) {
min = val;
}
if (val > max) {
max = val;
}
}
@Override
public String toString() {
return toString(new StringBuilder(128)).toString();
}
public final StringBuilder toString(final StringBuilder sb) {
sb.append(name).append('[').append(count);
sb.append("] sum: ").append(sum).append(" avg: ");
sb.append(trimTo3Digits(((double) sum) / count));
sb.append(" [").append(min).append(" | ").append(max).append("]");
return sb;
}
public static double trimTo3Digits(final double value) {
return ((long) (1e3d * value)) / 1e3d;
}
}
