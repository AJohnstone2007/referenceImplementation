package uk.ac.rhul.cs.csle.art;
public class ARTVersion {
  public static int major() {return 5;}
  public static int minor() {return 0;}
  public static int build() {return 33;}
  public static String timeStamp() {return "2024-04-13 05:19:55";}
  public static String version() { return major()+"."+minor()+"."+build() + " " + timeStamp(); };
}
