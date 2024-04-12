package uk.ac.rhul.cs.csle.art;
public class ARTVersion {
  public static int major() {return 5;}
  public static int minor() {return 0;}
  public static int build() {return 31;}
  public static String timeStamp() {return "2024-04-12 07:43:52";}
  public static String version() { return major()+"."+minor()+"."+build() + " " + timeStamp(); };
}
