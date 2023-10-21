package com.sun.javafx.tk;
import java.util.List;
public interface TKListener {
public void changedTopLevelWindows(List<TKStage> windows);
public void exitedLastNestedLoop();
}
