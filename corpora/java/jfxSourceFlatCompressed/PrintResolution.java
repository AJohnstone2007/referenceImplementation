package javafx.print;
public final class PrintResolution {
private int cfRes;
private int fRes;
PrintResolution(int crossFeedResolution, int feedResolution)
throws IllegalArgumentException
{
if (crossFeedResolution <= 0 || feedResolution <= 0) {
throw new IllegalArgumentException("Values must be positive");
}
cfRes = crossFeedResolution;
fRes = feedResolution;
}
public int getCrossFeedResolution() {
return cfRes;
}
public int getFeedResolution() {
return fRes;
}
@Override
public boolean equals(Object o) {
try {
PrintResolution other = (PrintResolution)o;
return this.cfRes == other.cfRes && this.fRes == other.fRes;
} catch (Exception e) {
return false;
}
}
@Override
public int hashCode() {
return cfRes << 16 | fRes;
}
@Override
public String toString() {
return "Feed res=" + fRes + "dpi. Cross Feed res=" + cfRes + "dpi.";
}
}
