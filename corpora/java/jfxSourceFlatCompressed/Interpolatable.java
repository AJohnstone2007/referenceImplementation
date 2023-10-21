package javafx.animation;
@FunctionalInterface
public interface Interpolatable<T> {
public T interpolate(T endValue, double t);
}
