package com.sun.javafx.css.parser;
import javafx.css.Stylesheet;
import java.io.File;
import java.io.IOException;
public final class Css2Bin {
public static void main(String args[]) throws Exception {
if ( args.length < 1 ) throw new IllegalArgumentException("expected file name as argument");
try {
String ifname = args[0];
String ofname = (args.length > 1) ?
args[1] : ifname.substring(0, ifname.lastIndexOf('.')+1).concat("bss");
convertToBinary(ifname, ofname);
} catch (Exception e) {
System.err.println(e.toString());
e.printStackTrace(System.err);
System.exit(-1);
}
}
public static void convertToBinary(String ifname, String ofname) throws IOException {
if (ifname == null || ofname == null) {
throw new IllegalArgumentException("parameters cannot be null");
}
if (ifname.equals(ofname)) {
throw new IllegalArgumentException("input file and output file cannot be the same");
}
final File source = new File(ifname);
final File destination = new File(ofname);
Stylesheet.convertToBinary(source, destination);
}
}
