package com.sun.glass.ui.monocle;
interface LinuxInputProcessor {
void processEvents(LinuxInputDevice device);
static class Logger implements LinuxInputProcessor {
@Override
public void processEvents(LinuxInputDevice device) {
LinuxEventBuffer buffer = device.getBuffer();
while (buffer.hasNextEvent()) {
System.out.format("%1$ts.%1$tL %2$s: %3$s\n",
new java.util.Date(),
device, buffer.getEventDescription());
buffer.nextEvent();
}
}
}
}
