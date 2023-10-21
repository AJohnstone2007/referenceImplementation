package javafx.scene.control;
import java.time.LocalDate;
import java.time.LocalTime;
public abstract class SpinnerValueFactoryShim<T> extends SpinnerValueFactory<T> {
public static void LocalDate_setMin(SpinnerValueFactory<LocalDate> ld, LocalDate d) {
SpinnerValueFactory.LocalDateSpinnerValueFactory lds = (SpinnerValueFactory.LocalDateSpinnerValueFactory)ld;
lds.setMin(d);
}
public static LocalDate LocalDate_getMin(SpinnerValueFactory<LocalDate> ld) {
SpinnerValueFactory.LocalDateSpinnerValueFactory lds = (SpinnerValueFactory.LocalDateSpinnerValueFactory)ld;
return lds.getMin();
}
public static void LocalDate_setMax(SpinnerValueFactory<LocalDate> ld, LocalDate d) {
SpinnerValueFactory.LocalDateSpinnerValueFactory lds = (SpinnerValueFactory.LocalDateSpinnerValueFactory)ld;
lds.setMax(d);
}
public static LocalDate LocalDate_getMax(SpinnerValueFactory<LocalDate> ld) {
SpinnerValueFactory.LocalDateSpinnerValueFactory lds = (SpinnerValueFactory.LocalDateSpinnerValueFactory)ld;
return lds.getMax();
}
public static void LocalTime_setMin(SpinnerValueFactory<LocalTime> ld, LocalTime d) {
SpinnerValueFactory.LocalTimeSpinnerValueFactory lts = (SpinnerValueFactory.LocalTimeSpinnerValueFactory)ld;
lts.setMin(d);
}
public static LocalTime LocalTime_getMin(SpinnerValueFactory<LocalTime> ld) {
SpinnerValueFactory.LocalTimeSpinnerValueFactory lts = (SpinnerValueFactory.LocalTimeSpinnerValueFactory)ld;
return lts.getMin();
}
public static void LocalTime_setMax(SpinnerValueFactory<LocalTime> ld, LocalTime d) {
SpinnerValueFactory.LocalTimeSpinnerValueFactory lts = (SpinnerValueFactory.LocalTimeSpinnerValueFactory)ld;
lts.setMax(d);
}
public static LocalTime LocalTime_getMax(SpinnerValueFactory<LocalTime> ld) {
SpinnerValueFactory.LocalTimeSpinnerValueFactory lts = (SpinnerValueFactory.LocalTimeSpinnerValueFactory)ld;
return lts.getMax();
}
}
