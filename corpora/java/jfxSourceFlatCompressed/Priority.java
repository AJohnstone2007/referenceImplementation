package javafx.scene.layout;
public enum Priority {
ALWAYS,
SOMETIMES,
NEVER;
public static Priority max(Priority a, Priority b) {
if (a == ALWAYS || b == ALWAYS) {
return ALWAYS;
} else if (a == SOMETIMES || b == SOMETIMES) {
return SOMETIMES;
} else {
return NEVER;
}
}
public static Priority min(Priority a, Priority b) {
if (a == NEVER || b == NEVER) {
return NEVER;
} else if (a == SOMETIMES || b == SOMETIMES) {
return SOMETIMES;
} else {
return ALWAYS;
}
}
}
