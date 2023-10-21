package ensemble.samples.language.fxml;
import java.util.HashMap;
import java.util.Map;
public class Authenticator {
private static final Map<String, String> MAP = new HashMap<String, String>();
static {
MAP.put("demo", "demo");
}
public static boolean validate(String user, String password){
String validUserPassword = MAP.get(user);
return validUserPassword != null && validUserPassword.equals(password);
}
}
