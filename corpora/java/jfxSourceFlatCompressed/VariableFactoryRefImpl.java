package test.com.sun.javafx.test.binding;
public final class VariableFactoryRefImpl implements VariableFactory {
private final Class<?> variableClass;
public VariableFactoryRefImpl(final Class<?> variableClass) {
this.variableClass = variableClass;
}
@Override
public Object createVariable() {
return ReflectionHelper.newInstance(variableClass);
}
}
