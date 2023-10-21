package com.sun.javafx.runtime;
public class VersionInfo {
private static final String BUILD_TIMESTAMP = "@BUILD_TIMESTAMP@";
private static final String HUDSON_JOB_NAME = "@HUDSON_JOB_NAME@";
private static final String HUDSON_BUILD_NUMBER = "@HUDSON_BUILD_NUMBER@";
private static final String PROMOTED_BUILD_NUMBER = "@PROMOTED_BUILD_NUMBER@";
private static final String RELEASE_VERSION = "@RELEASE_VERSION@";
private static final String RELEASE_SUFFIX = "@RELEASE_SUFFIX@";
private static final String VERSION;
private static final String RUNTIME_VERSION;
static {
String tmpVersion = RELEASE_VERSION;
tmpVersion += RELEASE_SUFFIX;
VERSION = tmpVersion;
tmpVersion += "+" + PROMOTED_BUILD_NUMBER;
if (getHudsonJobName().length() == 0) {
tmpVersion += "-" + BUILD_TIMESTAMP;
}
RUNTIME_VERSION = tmpVersion;
}
public static synchronized void setupSystemProperties() {
if (System.getProperty("javafx.version") == null) {
System.setProperty("javafx.version", getVersion());
System.setProperty("javafx.runtime.version", getRuntimeVersion());
}
}
public static String getBuildTimestamp() {
return BUILD_TIMESTAMP;
}
public static String getHudsonJobName() {
if (HUDSON_JOB_NAME.equals("not_hudson")) {
return "";
}
return HUDSON_JOB_NAME;
}
public static String getHudsonBuildNumber() {
return HUDSON_BUILD_NUMBER;
}
public static String getReleaseMilestone() {
String str = RELEASE_SUFFIX;
if (str.startsWith("-")) {
str = str.substring(1);
}
return str;
}
public static String getVersion() {
return VERSION;
}
public static String getRuntimeVersion() {
return RUNTIME_VERSION;
}
}
