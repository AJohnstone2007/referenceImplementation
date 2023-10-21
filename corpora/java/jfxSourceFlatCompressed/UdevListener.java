package com.sun.glass.ui.monocle;
import java.util.Map;
interface UdevListener {
void udevEvent(String action, Map<String, String> event);
}
