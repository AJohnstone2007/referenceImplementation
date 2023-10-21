package com.sun.javafx.binding;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.StringExpression;
import javafx.beans.value.ChangeListener;
public class StringConstant extends StringExpression {
private final String value;
private StringConstant(String value) {
this.value = value;
}
public static StringConstant valueOf(String value) {
return new StringConstant(value);
}
@Override
public String get() {
return value;
}
@Override
public String getValue() {
return value;
}
@Override
public void addListener(InvalidationListener observer) {
}
@Override
public void addListener(ChangeListener<? super String> observer) {
}
@Override
public void removeListener(InvalidationListener observer) {
}
@Override
public void removeListener(ChangeListener<? super String> observer) {
}
}
