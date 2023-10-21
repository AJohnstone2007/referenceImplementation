package com.sun.glass.ui.monocle;
class LookaheadTouchFilter implements TouchFilter {
private TouchState previousState = new TouchState();
private TouchState tmpState = new TouchState();
private boolean assignIDs;
private enum FilterState {
CLEAN,
PENDING_UNMODIFIABLE,
PENDING_MODIFIABLE
}
private FilterState filterState = FilterState.CLEAN;
LookaheadTouchFilter(boolean assignIDs) {
this.assignIDs = assignIDs;
}
@Override
public boolean filter(TouchState state) {
state.sortPointsByID();
switch (filterState) {
case CLEAN:
state.copyTo(previousState);
filterState = FilterState.PENDING_UNMODIFIABLE;
return true;
case PENDING_UNMODIFIABLE:
state.copyTo(tmpState);
previousState.copyTo(state);
tmpState.copyTo(previousState);
if (state.canBeFoldedWith(previousState, assignIDs)) {
filterState = FilterState.PENDING_MODIFIABLE;
}
return false;
case PENDING_MODIFIABLE:
if (state.canBeFoldedWith(previousState, assignIDs)) {
state.copyTo(previousState);
return true;
} else {
state.copyTo(tmpState);
previousState.copyTo(state);
tmpState.copyTo(previousState);
filterState = FilterState.PENDING_UNMODIFIABLE;
return false;
}
default:
return false;
}
}
@Override
public boolean flush(TouchState state) {
switch (filterState) {
case PENDING_MODIFIABLE:
case PENDING_UNMODIFIABLE:
previousState.copyTo(state);
filterState = FilterState.CLEAN;
return true;
default:
return false;
}
}
@Override
public int getPriority() {
return PRIORITY_PRE_ID + 1;
}
@Override
public String toString() {
return "Lookahead[previousState="
+ previousState
+ ",filterState=" + filterState
+ "]";
}
}
