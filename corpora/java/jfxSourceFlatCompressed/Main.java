package com.javafx.main;
public class Main {
static {
System.err.println("com.javafx.main.Main unexpectedly initialized");
if (true) {
throw new InternalError("class unexpectedly initialized");
}
}
public static void main(String[] args) {
}
}
