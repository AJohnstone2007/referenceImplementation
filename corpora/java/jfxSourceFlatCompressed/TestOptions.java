package com.sun.javafx.webkit.drt;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
class TestOptions {
private final Map<String, String> testOptions = new HashMap();
private static final String BEGIN_STRING = "webkit-test-runner [ ";
private static final String END_STRING = " ]";
TestOptions(final String path) {
if (path.startsWith("https://") || path.startsWith("http://")) {
return;
}
final String testPath = path.replaceFirst("file://", "");
try (BufferedReader br = new BufferedReader(new FileReader(testPath))) {
final String options = br.readLine();
int beginLocation = options.indexOf(BEGIN_STRING);
if (beginLocation < 0)
return;
int endLocation = options.indexOf(END_STRING, beginLocation);
if (endLocation < 0)
return;
final String pairStrings[] = options.substring(beginLocation + BEGIN_STRING.length(), endLocation).split("[ ,]+");
for (final String pair : pairStrings) {
final String splited[] = pair.split("=", 2);
testOptions.put(splited[0], splited[1]);
}
} catch(Exception e) {
System.err.println("Exception received:" + e);
e.printStackTrace();
}
}
public Map<String, String> getOptions() {
return testOptions;
}
}
