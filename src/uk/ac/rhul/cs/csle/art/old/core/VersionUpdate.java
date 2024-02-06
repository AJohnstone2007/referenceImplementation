package uk.ac.rhul.cs.csle.art.old.core;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalTime;

public class VersionUpdate {

  public static void main(String[] args) throws FileNotFoundException {
    String status = "AMBER";
    int newBuild = Version.build() + 1;
    String timeStamp = LocalDate.now() + " " + LocalTime.now();
    timeStamp = timeStamp.substring(0, timeStamp.indexOf('.'));

    if (args.length != 0) status = args[0];
    System.out.printf("Updating from %s: new build %d, new status %s, %s%n", Version.version(), newBuild, status, timeStamp);
    PrintWriter pw = new PrintWriter("Version.java.new");

    pw.printf(
        "package uk.ac.rhul.cs.csle.art.v4.core;%n" + "public class Version {%n" + "  public static int major() {return %d;}%n"
            + "  public static int minor() {return %d;}%n" + "  public static int build() {return %d;}%n" + "  public static String status() {return \"%s\";}%n"
            + "  public static String timeStamp() {return \"%s\";}%n"
            + "  public static String version() { return major()+\".\"+minor()+\".\"+build()+\".\"+status() +  \" \" + timeStamp(); };%n" + "}%n",
        Version.major(), Version.minor(), newBuild, status, timeStamp);
    pw.close();

    pw = new PrintWriter("manifest.local.new");
    pw.printf("Specification-Vendor: Center for Software Language Engineering, RHUL%n" + "Specification-Title: ART%n" + "Specification-Version: 3.0%n"
        + "Implementation-Vendor: Center for Software Language Engineering, RHUL%n" + "Implementation-Title: ART%n" + "Implementation-Version: %d.%d.%d.%s%n"
        + "Main-Class: uk.ac.rhul.cs.csle.art.ART%n", Version.major(), Version.minor(), newBuild, status);
    pw.close();
  }
}
