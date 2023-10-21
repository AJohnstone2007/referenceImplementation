package com.sun.glass.ui.monocle;
public class TouchFilterShim implements TouchFilter {
public boolean filter(TouchState ts) {
throw new RuntimeException("not implemented");
}
public boolean flush(TouchState ts) {
throw new RuntimeException("not implemented");
}
public int getPriority() {
throw new RuntimeException("not implemented");
}
public static class TranslateFilter extends TouchFilterShim {
@Override
public boolean filter(TouchState state) {
for (int i = 0; i < state.getPointCount(); i++) {
state.getPoint(i).x += 8;
state.getPoint(i).y -= 5;
}
return false;
}
@Override
public int getPriority() {
return 50;
}
@Override
public boolean flush(TouchState state) {
return false;
}
}
public static class OverrideIDFilter extends TouchFilterShim {
@Override
public boolean filter(TouchState state) {
for (int i = 0; i < state.getPointCount(); i++) {
state.getPoint(i).id = 5;
}
return false;
}
@Override
public int getPriority() {
return -50;
}
@Override
public boolean flush(TouchState state) {
return false;
}
}
public static class NoMultiplesOfTenOnXFilter extends TouchFilterShim {
@Override
public boolean filter(TouchState state) {
for (int i = 0; i < state.getPointCount(); i++) {
if (state.getPoint(i).x % 10 == 0) {
return true;
}
}
return false;
}
@Override
public int getPriority() {
return 60;
}
@Override
public boolean flush(TouchState state) {
return false;
}
}
public static class LoggingFilter extends TouchFilterShim {
@Override
public boolean filter(TouchState state) {
for (int i = 0; i < state.getPointCount(); i++) {
TestLogShim.format("Touch point id=%d at %d,%d",
state.getPoint(i).id,
state.getPoint(i).x,
state.getPoint(i).y);
}
return false;
}
@Override
public int getPriority() {
return -100;
}
@Override
public boolean flush(TouchState state) {
return false;
}
}
public static class FlushingFilter extends TouchFilterShim {
int i = 3;
@Override
public boolean filter(TouchState state) {
return false;
}
@Override
public int getPriority() {
return 90;
}
@Override
public boolean flush(TouchState state) {
if (i > 0) {
i --;
state.clear();
TouchState.Point p = state.addPoint(null);
p.x = 205 + i * 100;
p.y = 100;
p.id = -1;
return true;
} else {
return false;
}
}
}
}
