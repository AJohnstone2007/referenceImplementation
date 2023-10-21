package javafx.util;
@FunctionalInterface
public interface BuilderFactory {
public Builder<?> getBuilder(Class<?> type);
}
