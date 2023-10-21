package test.com.sun.javafx.binding;
import static org.junit.Assert.*;
import com.sun.javafx.logging.PlatformLogger.Level;
import com.sun.javafx.binding.Logging;
import com.sun.javafx.binding.Logging.ErrorLogger.ErrorLogRecord;
import com.sun.javafx.binding.Logging.ErrorLogger;
public class ErrorLoggingUtiltity {
private static ErrorLogger errorLogger = Logging.getLogger();
public static void reset() {
errorLogger.setErrorLogRecord(null);
}
public static boolean isEmpty() {
return errorLogger.getErrorLogRecord() == null;
}
public static void checkFine(Class<?> expectedException) {
check(Level.FINE, expectedException);
}
public static void checkWarning(Class<?> expectedException) {
check(Level.WARNING, expectedException);
}
public static void check(Level expectedLevel, Class<?> expectedException) {
ErrorLogRecord errorLogRecord = errorLogger.getErrorLogRecord();
assertNotNull(errorLogRecord);
assertEquals(expectedLevel, errorLogRecord.getLevel());
assertTrue(expectedException.isAssignableFrom(errorLogRecord.getThrown().getClass()));
reset();
}
}
