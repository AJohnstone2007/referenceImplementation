package com.sun.javafx.logging.jfr;
import jdk.jfr.Category;
import jdk.jfr.Description;
import jdk.jfr.Enabled;
import jdk.jfr.Event;
import jdk.jfr.Label;
import jdk.jfr.Name;
import jdk.jfr.StackTrace;
@Name("javafx.PulsePhase")
@Label("JavaFX Pulse Phase")
@Category("JavaFX")
@Description("Describes a phase in JavaFX pulse processing")
@StackTrace(false)
@Enabled(false)
public final class JFRPulsePhaseEvent extends Event {
@PulseId
@Label("Pulse Id")
private int pulseId;
@Label("Phase Name")
private String phaseName;
public int getPulseId() {
return pulseId;
}
public void setPulseId(int pulseId) {
this.pulseId = pulseId;
}
public String getPhaseName() {
return phaseName;
}
public void setPhaseName(String phaseName) {
this.phaseName = phaseName;
}
}
