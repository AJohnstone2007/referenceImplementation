package ensemble.samples.language.fxml;
import java.util.HashMap;
import java.util.Map;
public class User {
private static final Map<String, User> USERS = new HashMap<String, User>();
public static User of(String id) {
User user = USERS.get(id);
if (user == null) {
user = new User(id);
USERS.put(id, user);
}
return user;
}
private User(String id) {
this.id = id;
}
private String id;
public String getId() {
return id;
}
private String email = "";
private String phone = "";
private boolean subscribed;
private String address = "";
public String getEmail() {
return email;
}
public void setEmail(String email) {
this.email = email;
}
public String getPhone() {
return phone;
}
public void setPhone(String phone) {
this.phone = phone;
}
public boolean isSubscribed() {
return subscribed;
}
public void setSubscribed(boolean subscribed) {
this.subscribed = subscribed;
}
public String getAddress() {
return address;
}
public void setAddress(String address) {
this.address = address;
}
}
