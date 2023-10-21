package test.com.sun.javafx.test;
public class DoubleComparator extends ValueComparator {
private double delta;
public DoubleComparator(double delta) {
this.delta = delta;
}
@Override
public boolean equals(Object expected, Object actual) {
if (expected == actual) {
return true;
}
if (!(expected instanceof Number)) {
return false;
}
if (!(actual instanceof Number)) {
return false;
}
double bExpectend = ((Number) expected).doubleValue();
double bActual = ((Number) actual).doubleValue();
if (Math.abs(bExpectend - bActual) > delta) {
return false;
}
return true;
}
}
