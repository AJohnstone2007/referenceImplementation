package test.com.sun.javafx.test.binding;
public class BindingProxy {
private final Class<?> typeClass;
private final ObservableValueProxy observableValueProxy;
private final WritableValueProxy writableValueProxy;
private final VariableFactory variableFactory;
private final PropertyModelProxy propertyModelProxy;
public BindingProxy(final Class<?> typeClass,
final ObservableValueProxy observableValueProxy,
final WritableValueProxy writableValueProxy,
final VariableFactory variableFactory,
final PropertyModelProxy propertyModelProxy) {
this.typeClass = typeClass;
this.observableValueProxy = observableValueProxy;
this.writableValueProxy = writableValueProxy;
this.variableFactory = variableFactory;
this.propertyModelProxy = propertyModelProxy;
}
public final Class<?> getTypeClass() {
return typeClass;
}
public final ObservableValueProxy getObservableValueProxy() {
return observableValueProxy;
}
public final WritableValueProxy getWritableValueProxy() {
return writableValueProxy;
}
public final VariableFactory getVariableFactory() {
return variableFactory;
}
public final PropertyModelProxy getPropertyModelProxy() {
return propertyModelProxy;
}
}
