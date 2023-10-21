package com.sun.glass.ui.monocle;
interface TouchFilter {
static final int PRIORITY_PRE_ID = 100;
static final int PRIORITY_ID = 0;
static final int PRIORITY_POST_ID = -100;
boolean filter(TouchState state);
boolean flush(TouchState state);
int getPriority();
}
