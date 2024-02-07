package uk.ac.rhul.cs.csle.art;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalTime;

public class ARTVersionUpdate {

  public static void main(String[] args) throws FileNotFoundException {
    int newBuild = ARTVersion.build() + 1;
    String timeStamp = LocalDate.now() + " " + LocalTime.now();
    timeStamp = timeStamp.substring(0, timeStamp.indexOf('.'));

    System.out.printf("Updating from %s: new build %d%n", ARTVersion.version(), newBuild, timeStamp);
    PrintWriter pw = new PrintWriter("ARTVersion.java.new");

    pw.printf("package uk.ac.rhul.cs.csle.art;%n" + "public class ARTVersion {%n" + "  public static int major() {return %d;}%n"
        + "  public static int minor() {return %d;}%n" + "  public static int build() {return %d;}%n" + "  public static String timeStamp() {return \"%s\";}%n"
        + "  public static String version() { return major()+\".\"+minor()+\".\"+build() + \" \" + timeStamp(); };%n" + "}%n", ARTVersion.major(),
        ARTVersion.minor(), newBuild, timeStamp);
    pw.close();

    pw = new PrintWriter("manifest.local.new");
    pw.printf("Specification-Vendor: Center for Software Language Engineering, RHUL%n" + "Specification-Title: ART%n" + "Specification-Version: 5%n"
        + "Implementation-Vendor: Center for Software Language Engineering, RHUL%n" + "Implementation-Title: ART%n" + "Implementation-Version: %d.%d.%d%n"
        + "Implementation-Build-Date: %s%n" + "Main-Class: uk.ac.rhul.cs.csle.art.ART%n", ARTVersion.major(), ARTVersion.minor(), newBuild, timeStamp);
    pw.close();
  }
}
