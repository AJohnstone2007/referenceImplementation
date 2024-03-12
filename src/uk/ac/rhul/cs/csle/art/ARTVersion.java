package uk.ac.rhul.cs.csle.art;
public class ARTVersion {
  public static int major() {return 5;}
  public static int minor() {return 0;}
  public static int build() {return 28;}
  public static String timeStamp() {return "2024-03-12 23:55:41";}
  public static String version() { return major()+"."+minor()+"."+build() + " " + timeStamp(); };
}
