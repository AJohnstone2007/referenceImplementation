package com.sun.javafx.font.directwrite;
class JFXTextAnalysisSink extends IUnknown {
JFXTextAnalysisSink(long ptr) {
super(ptr);
}
boolean Next() {
return OS.Next(ptr);
}
int GetStart() {
return OS.GetStart(ptr);
}
int GetLength() {
return OS.GetLength(ptr);
}
DWRITE_SCRIPT_ANALYSIS GetAnalysis() {
return OS.GetAnalysis(ptr);
}
}
