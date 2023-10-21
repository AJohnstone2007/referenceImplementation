package com.sun.glass.ui.monocle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
class InputDeviceRegistry {
protected ObservableSet<InputDevice> devices =
FXCollections.observableSet();
ObservableSet<InputDevice> getInputDevices() {
return devices;
}
}
