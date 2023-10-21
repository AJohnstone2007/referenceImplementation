package com.sun.javafx.binding;
import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableDoubleValue;
public final class DoubleConstant implements ObservableDoubleValue {
private final double value;
private DoubleConstant(double value) {
this.value = value;
}
public static DoubleConstant valueOf(double value) {
return new DoubleConstant(value);
}
@Override
public double get() {
return value;
}
@Override
public Double getValue() {
return value;
}
@Override
public void addListener(InvalidationListener observer) {
}
@Override
public void addListener(ChangeListener<? super Number> listener) {
}
@Override
public void removeListener(InvalidationListener observer) {
}
@Override
public void removeListener(ChangeListener<? super Number> listener) {
}
@Override
public int intValue() {
return (int) value;
}
@Override
public long longValue() {
return (long) value;
}
@Override
public float floatValue() {
return (float) value;
}
@Override
public double doubleValue() {
return value;
}
}
