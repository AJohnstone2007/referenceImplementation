package javafx.scene.control;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalUnit;
public class SpinnerShim {
public static Spinner<LocalDate> getSpinnerLocalDate(
LocalDate min, LocalDate max,
LocalDate initialValue,
long amountToStepBy, TemporalUnit temporalUnit) {
return new Spinner<>(min, max, initialValue, amountToStepBy, temporalUnit);
}
public static Spinner<LocalTime> getSpinnerLocalTime(
LocalTime min, LocalTime max,
LocalTime initialValue,
long amountToStepBy, TemporalUnit temporalUnit) {
return new Spinner<>(min, max, initialValue, amountToStepBy, temporalUnit);
}
}
