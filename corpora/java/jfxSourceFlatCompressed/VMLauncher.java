package com.oracle.dalvik;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import android.util.Log;
public class VMLauncher extends Thread {
private static final String TAG = "VMLauncher";
private static String sJavaHome;
private String[] args;
public static void initialize(String javaHome) {
if (javaHome != null) {
sJavaHome = javaHome;
loadNativeLibraries();
} else {
throw new NullPointerException();
}
}
public static String getJvmArch() {
String rawarch = System.getProperty("os.arch");
if (rawarch != null && rawarch.contains("86")) {
return "i386";
}
return "arm";
}
private static void loadNativeLibraries() {
try {
System.load(sJavaHome + "/lib/" + getJvmArch() + "/minimal/libjvm.so");
} catch(UnsatisfiedLinkError e) {
System.load(sJavaHome + "/lib/" + getJvmArch() + "/client/libjvm.so");
}
System.load(sJavaHome + "/lib/" + getJvmArch() + "/jli/libjli.so");
}
private static String getCmdLine() {
StringBuilder builder = new StringBuilder();
try {
Reader reader = new FileReader("/proc/self/cmdline");
int c = 0;
while ((c = reader.read()) > 0) {
builder.append((char)c);
}
reader.close();
} catch (Exception e) {
builder = new StringBuilder("dalvik.package");
}
return builder.toString();
}
private static void listDirToStandardOut(String dirpath) {
File dir = new File(dirpath);
try {
for (File file : dir.listFiles()) {
if (file.isDirectory()) {
listDirToStandardOut(file.getAbsolutePath());
}
}
} catch (Exception e) {
Log.e(TAG, "Exception listing dir " + dir);
}
}
private VMLauncher(String[] args) {
super("VMLauncher");
setDaemon(true);
this.args = args;
}
public void run()
{
launchJVM(this.args);
}
private static void startJavaInBackground(String[] args)
{
new VMLauncher(args).start();
}
public static void runOnDebugPort(Integer debugPort,
String[] args)
{
ArrayList<String> localArrayList = new ArrayList();
localArrayList.add(getCmdLine());
if (debugPort.intValue() > 0) {
localArrayList.add("-Xdebug");
localArrayList.add("-agentlib:jdwp=server=y,suspend=y,transport=dt_socket,address=" + debugPort);
}
for (String arg : args) {
localArrayList.add(arg);
}
String[] processedArgs = localArrayList.toArray(new String[0]);
for (String arg : processedArgs) {
Log.v(TAG, "Processed JVM arg : " + arg);
}
startJavaInBackground(processedArgs);
}
private static native int launchJVM(String[] args);
}