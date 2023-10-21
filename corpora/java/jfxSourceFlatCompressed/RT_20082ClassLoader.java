package test.javafx.fxml;
public class RT_20082ClassLoader extends ClassLoader {
public static int loadCount = 0;
public RT_20082ClassLoader() {
super(getSystemClassLoader());
}
@Override
protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
loadCount++;
return super.loadClass(name, resolve);
}
@Override
public Class<?> findClass(String name) throws ClassNotFoundException {
return super.findClass(name);
}
}
