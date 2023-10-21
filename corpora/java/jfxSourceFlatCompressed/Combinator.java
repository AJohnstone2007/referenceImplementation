package com.sun.javafx.css;
public enum Combinator {
CHILD {
public String toString() { return ">"; }
},
DESCENDANT {
public String toString() { return " "; }
}
}
