package test.com.sun.javafx.event;
public abstract class Operation {
private final int constant;
private Operation(final int constant) {
this.constant = constant;
}
public abstract int applyTo(int value);
public static Operation add(final int constant) {
return new Operation(constant) {
@Override
public int applyTo(final int value) {
return value + constant;
}
};
}
public static Operation sub(final int constant) {
return new Operation(constant) {
@Override
public int applyTo(final int value) {
return value - constant;
}
};
}
public static Operation mul(final int constant) {
return new Operation(constant) {
@Override
public int applyTo(final int value) {
return value * constant;
}
};
}
public static Operation div(final int constant) {
return new Operation(constant) {
@Override
public int applyTo(final int value) {
return value / constant;
}
};
}
}
