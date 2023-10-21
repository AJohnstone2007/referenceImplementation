package com.sun.glass.ui.monocle;
public class GLException extends Exception {
GLException(int errorCode, String message) {
super("0x" + Integer.toHexString(errorCode) + ": " + message);
}
}
