package test.com.sun.javafx.runtime;
import com.sun.javafx.runtime.VersionInfo;
import org.junit.Test;
import static org.junit.Assert.*;
public class VersionInfoTest {
private static class Version {
private String vnum = "";
private String suffix = "";
private String build = "";
private String opt = "";
private Version(String version) {
int plusIdx = version.indexOf("+");
int firstDashIdx = version.indexOf("-");
if (plusIdx < 0) {
if (firstDashIdx >= 0) {
vnum = version.substring(0, firstDashIdx);
suffix = version.substring(firstDashIdx+1);
} else {
vnum = version;
}
} else {
if (firstDashIdx < 0) {
vnum = version.substring(0, plusIdx);
build = version.substring(plusIdx+1);
} else {
if (firstDashIdx < plusIdx) {
vnum = version.substring(0, firstDashIdx);
suffix = version.substring(firstDashIdx+1, plusIdx);
String rest = version.substring(plusIdx+1);
int nextDashIndex = rest.indexOf("-");
if (nextDashIndex < 0) {
build = rest;
} else {
build = rest.substring(0, nextDashIndex);
opt = rest.substring(nextDashIndex+1);
}
} else {
vnum = version.substring(0, plusIdx);
build = version.substring(plusIdx+1, firstDashIdx);
opt = version.substring(firstDashIdx+1);
}
}
}
}
}
@Test
public void testMajorVersion() {
String version = VersionInfo.getVersion();
assertTrue(version.startsWith("19"));
String runtimeVersion = VersionInfo.getRuntimeVersion();
assertTrue(runtimeVersion.startsWith(version));
}
@Test
public void testBuildNumber() {
String version = VersionInfo.getVersion();
assertFalse(version.contains("+"));
Version v = new Version(version);
assertEquals("", v.build);
assertEquals("", v.opt);
String runtimeVersion = VersionInfo.getRuntimeVersion();
assertTrue(runtimeVersion.contains("+"));
v = new Version(runtimeVersion);
assertTrue(v.build.length() > 0);
int buildNum = Integer.parseInt(v.build);
assertTrue(buildNum >= 0);
}
@Test
public void testNoFcs() {
String version = VersionInfo.getVersion();
assertFalse(version.contains("fcs"));
String runtimeVersion = VersionInfo.getRuntimeVersion();
assertFalse(runtimeVersion.contains("fcs"));
}
@Test
public void testSuffixOpt() {
String runtimeVersion = VersionInfo.getRuntimeVersion();
int internalIndex = runtimeVersion.indexOf("-internal");
boolean isInternal = internalIndex > 0;
Version v = new Version(runtimeVersion);
if (isInternal) {
assertEquals("internal", v.suffix);
assertTrue(v.opt.length() > 0);
} else {
assertFalse("internal".equals(v.suffix));
}
}
@Test
public void testNonPublic() {
String runtimeVersion = VersionInfo.getRuntimeVersion();
Version v = new Version(runtimeVersion);
String milestone = VersionInfo.getReleaseMilestone();
String timestamp = VersionInfo.getBuildTimestamp();
String hudsonJob = VersionInfo.getHudsonJobName();
assertEquals(milestone, v.suffix);
if (hudsonJob.length() == 0) {
assertEquals(timestamp, v.opt);
assertEquals("internal", v.suffix);
} else {
assertFalse("internal".equals(v.suffix));
}
}
}
