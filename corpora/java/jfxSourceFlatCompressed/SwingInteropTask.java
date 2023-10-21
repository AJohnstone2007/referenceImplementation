package ensemble.samples.language.swing;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javafx.concurrent.Task;
public class SwingInteropTask extends Task<Process> {
private Process proc = null;
public SwingInteropTask() {
}
@Override
protected Process call() throws Exception {
String home = System.getProperty("java.home");
String java = home + File.separator + "bin" + File.separator + "java";
String classpath = System.getProperty("java.class.path");
String className = SwingInterop.class.getCanonicalName();
List<String> command = new ArrayList<>();
command.add(java);
command.add("-cp");
command.add(classpath);
command.add(className);
ProcessBuilder pb = new ProcessBuilder(command);
proc = pb.start();
proc.waitFor();
return proc;
}
@Override
protected void cancelled() {
if (proc != null) {
proc.destroy();
}
}
}
