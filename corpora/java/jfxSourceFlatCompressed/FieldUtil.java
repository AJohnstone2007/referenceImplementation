package com.sun.javafx.reflect;
import java.lang.reflect.Field;
public final class FieldUtil {
private FieldUtil() {
}
public static Field getField(Class<?> cls, String name)
throws NoSuchFieldException {
ReflectUtil.checkPackageAccess(cls);
return cls.getField(name);
}
}
