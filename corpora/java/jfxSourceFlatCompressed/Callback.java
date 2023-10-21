package javafx.util;
@FunctionalInterface
public interface Callback<P,R> {
public R call(P param);
}
