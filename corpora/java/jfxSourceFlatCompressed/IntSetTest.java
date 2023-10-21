package test.com.sun.glass.ui.monocle;
import com.sun.glass.ui.monocle.IntSetShim;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
@RunWith(Parameterized.class)
public class IntSetTest {
private Integer[] array;
@Parameterized.Parameters
public static Collection<Object[]> data() {
Integer[][] sets = {
{ 1 },
{ 1, 2 },
{ 1, 2, 3},
{ 1, 1 },
{ 1, 1, 1 },
{ 1, 1, 2 },
};
return Arrays.asList(sets).stream()
.map(d -> new Object[] { d })
.collect(Collectors.toList());
}
public IntSetTest(Integer[] array) {
this.array = array;
}
private int[] getIntSetAsArray(IntSetShim s) {
int[] a = new int[s.size()];
for (int i = 0; i < s.size(); i++) {
a[i] = s.get(i);
}
Arrays.sort(a);
return a;
}
private int[] getHashSetAsArray(Set<Integer> set) {
return set.stream().sorted().mapToInt(x -> x).toArray();
}
private void assertSet(Set<Integer> expected, IntSetShim actual) {
Assert.assertArrayEquals(
"Expected: " + expected + ", found " + actual,
getHashSetAsArray(expected),
getIntSetAsArray(actual));
}
@Test
public void testAddInOrderRemoveInOrder() {
IntSetShim set = new IntSetShim();
Set<Integer> hashSet = new HashSet<>();
assertSet(hashSet, set);
for (int i = 0; i < array.length; i++) {
set.addInt(array[i]);
hashSet.add(array[i]);
assertSet(hashSet, set);
}
for (int i = 0; i < array.length; i++) {
set.removeInt(array[i]);
hashSet.remove(array[i]);
assertSet(hashSet, set);
}
}
@Test
public void testAddInOrderRemoveInReverse() {
IntSetShim set = new IntSetShim();
Set<Integer> hashSet = new HashSet<>();
assertSet(hashSet, set);
for (int i = 0; i < array.length; i++) {
set.addInt(array[i]);
hashSet.add(array[i]);
assertSet(hashSet, set);
}
for (int i = array.length - 1; i >= 0; i--) {
set.removeInt(array[i]);
hashSet.remove(array[i]);
assertSet(hashSet, set);
}
}
@Test
public void testAddInReverseRemoveInOrder() {
IntSetShim set = new IntSetShim();
Set<Integer> hashSet = new HashSet<>();
assertSet(hashSet, set);
for (int i = array.length - 1; i >= 0; i--) {
set.addInt(array[i]);
hashSet.add(array[i]);
assertSet(hashSet, set);
}
for (int i = 0; i < array.length; i++) {
set.removeInt(array[i]);
hashSet.remove(array[i]);
assertSet(hashSet, set);
}
}
@Test
public void testAddInReverseRemoveInReverse() {
IntSetShim set = new IntSetShim();
Set<Integer> hashSet = new HashSet<>();
assertSet(hashSet, set);
for (int i = array.length - 1; i >= 0; i--) {
set.addInt(array[i]);
hashSet.add(array[i]);
assertSet(hashSet, set);
}
for (int i = array.length - 1; i >= 0; i--) {
set.removeInt(array[i]);
hashSet.remove(array[i]);
assertSet(hashSet, set);
}
}
}
