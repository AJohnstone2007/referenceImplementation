package uk.ac.rhul.cs.csle.art;
public class ARTVersion {
  public static int major() {return 5;}
  public static int minor() {return 0;}
  public static int build() {return 20;}
  public static String timeStamp() {return "2024-03-02 21:05:51";}
  public static String version() { return major()+"."+minor()+"."+build() + " " + timeStamp(); };
}
