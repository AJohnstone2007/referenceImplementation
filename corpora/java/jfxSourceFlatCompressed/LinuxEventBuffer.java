package com.sun.glass.ui.monocle;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
class LinuxEventBuffer {
interface EventStruct {
int getTypeIndex();
int getCodeIndex();
int getValueIndex();
int getSize();
}
class EventStruct32Bit implements EventStruct {
public int getTypeIndex() { return 8; }
public int getCodeIndex() { return 10; }
public int getValueIndex() { return 12; }
public int getSize() { return 16; }
}
class EventStruct64Bit implements EventStruct {
public int getTypeIndex() { return 16; }
public int getCodeIndex() { return 18; }
public int getValueIndex() { return 20; }
public int getSize() { return 24; }
}
private static final int EVENT_BUFFER_SIZE = 1000;
private final ByteBuffer bb;
private final EventStruct eventStruct;
private int positionOfLastSync;
private int currentPosition;
private int mark;
LinuxEventBuffer(int osArchBits) {
eventStruct = osArchBits == 64 ? new EventStruct64Bit() : new EventStruct32Bit();
bb = ByteBuffer.allocate(eventStruct.getSize() * EVENT_BUFFER_SIZE);
bb.order(ByteOrder.nativeOrder());
}
int getEventSize() {
return eventStruct.getSize();
}
synchronized boolean put(ByteBuffer event) throws
InterruptedException {
boolean isSync = event.getShort(eventStruct.getTypeIndex()) == 0
&& event.getInt(eventStruct.getValueIndex()) == 0;
while (bb.limit() - bb.position() < event.limit()) {
if (MonocleSettings.settings.traceEventsVerbose) {
MonocleTrace.traceEvent(
"Event buffer %s is full, waiting for some space to become available",
bb);
}
wait();
}
if (isSync) {
positionOfLastSync = bb.position();
}
bb.put(event);
if (MonocleSettings.settings.traceEventsVerbose) {
int index = bb.position() - eventStruct.getSize();
MonocleTrace.traceEvent("Read %s [index=%d]",
getEventDescription(index), index);
}
return isSync;
}
synchronized void startIteration() {
currentPosition = 0;
mark = 0;
if (MonocleSettings.settings.traceEventsVerbose) {
MonocleTrace.traceEvent("Processing %s [index=%d]", getEventDescription(), currentPosition);
}
}
synchronized void compact() {
positionOfLastSync -= currentPosition;
int newLimit = bb.position();
bb.position(currentPosition);
bb.limit(newLimit);
bb.compact();
if (MonocleSettings.settings.traceEventsVerbose) {
MonocleTrace.traceEvent("Compacted event buffer %s", bb);
}
notifyAll();
}
synchronized short getEventType() {
return bb.getShort(currentPosition + eventStruct.getTypeIndex());
}
short getEventCode() {
return bb.getShort(currentPosition + eventStruct.getCodeIndex());
}
synchronized int getEventValue() {
return bb.getInt(currentPosition + eventStruct.getValueIndex());
}
synchronized String getEventDescription() {
return getEventDescription(currentPosition);
}
private synchronized String getEventDescription(int position) {
short type = bb.getShort(position + eventStruct.getTypeIndex());
short code = bb.getShort(position + eventStruct.getCodeIndex());
int value = bb.getInt(position + eventStruct.getValueIndex());
String typeStr = LinuxInput.typeToString(type);
return typeStr + " " + LinuxInput.codeToString(typeStr, code) + " " + value;
}
synchronized void nextEvent() {
if (currentPosition > positionOfLastSync) {
throw new IllegalStateException("Cannot advance past the last" +
" EV_SYN EV_SYN_REPORT 0");
}
currentPosition += eventStruct.getSize();
if (MonocleSettings.settings.traceEventsVerbose && hasNextEvent()) {
MonocleTrace.traceEvent("Processing %s [index=%d]",
getEventDescription(), currentPosition);
}
}
synchronized void mark() {
mark = currentPosition;
}
synchronized void reset() {
currentPosition = mark;
}
synchronized boolean hasNextEvent() {
return currentPosition <= positionOfLastSync;
}
synchronized boolean hasData() {
return bb.position() != 0;
}
}
