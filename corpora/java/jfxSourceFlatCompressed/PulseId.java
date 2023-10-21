package com.sun.javafx.logging.jfr;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import jdk.jfr.Description;
import jdk.jfr.Name;
import jdk.jfr.Relational;
@Relational
@Name("javafx.PulseId")
@Retention(RUNTIME)
@Target(FIELD)
@Description("Binds events with same pulse id together")
public @interface PulseId {
}
