package myapp4.pkg5;
public class POJO {
private final String name;
private double val;
private RefClass obj;
public final String getName() {
return name;
}
public final void setVal(double val) {
this.val = val;
}
public final double getVal() {
return val;
}
public final void setObj(RefClass obj) {
this.obj = obj;
}
public final RefClass getObj() {
return obj;
}
public POJO(String name, double val, RefClass obj) {
this.name = name;
this.val = val;
this.obj = obj;
}
}
