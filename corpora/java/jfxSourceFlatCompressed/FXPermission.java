package javafx.util;
import java.security.BasicPermission;
public final class FXPermission extends BasicPermission {
private static final long serialVersionUID = 2890556410764946054L;
public FXPermission(String name) {
super(name);
}
}
