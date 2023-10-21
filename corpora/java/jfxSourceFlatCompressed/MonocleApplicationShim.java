package com.sun.glass.ui.monocle;
public class MonocleApplicationShim {
public static int _getKeyCodeForChar(char c) {
return MonocleApplication.getKeyCodeForChar(c);
}
}
