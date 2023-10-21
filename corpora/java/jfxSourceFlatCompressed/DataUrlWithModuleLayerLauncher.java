package myapp7;
import java.io.File;
import java.lang.module.Configuration;
import java.lang.module.ModuleFinder;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
public class DataUrlWithModuleLayerLauncher {
public static void main(String[] args) throws Exception {
new Thread() {
{
setDaemon(true);
}
@Override
public void run() {
try {
Thread.sleep(15000);
} catch (InterruptedException ex) {
}
System.exit(DataUrlWithModuleLayer.ERROR_TIMEOUT);
}
}.start();
List<Path> modulePaths = new ArrayList<>();
for(String workerPath: System.getProperty("module.path").split(File.pathSeparator)) {
modulePaths.add(Paths.get(workerPath));
}
ModuleFinder finder = ModuleFinder.of(modulePaths.toArray(new Path[0]));
ModuleLayer parent = ModuleLayer.boot();
Configuration cf = parent.configuration().resolve(finder, ModuleFinder.of(), Set.of("mymod"));
ClassLoader scl = ClassLoader.getSystemClassLoader();
ModuleLayer layer = parent.defineModulesWithOneLoader(cf, scl);
ClassLoader moduleClassLoader = layer.findLoader("mymod");
Class appClass = moduleClassLoader.loadClass("javafx.application.Application");
Class testClass = moduleClassLoader.loadClass("myapp7.DataUrlWithModuleLayer");
Method launchMethod = appClass.getMethod("launch", Class.class, String[].class);
launchMethod.invoke(null, new Object[]{testClass, args});
System.exit(DataUrlWithModuleLayer.ERROR_UNEXPECTED_EXIT);
}
}
