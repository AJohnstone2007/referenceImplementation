package uk.ac.rhul.cs.csle.art.old.core;
public class Version {
  public static int major() {return 4;}
  public static int minor() {return 4;}
  public static int build() {return 11;}
  public static String status() {return "AMBER";}
  public static String timeStamp() {return "2023-10-06 03:39:44";}
  public static String version() { return major()+"."+minor()+"."+build()+"."+status() +  " " + timeStamp(); };
}
