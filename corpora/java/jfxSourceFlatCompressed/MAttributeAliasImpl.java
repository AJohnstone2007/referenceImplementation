package com.javafx.experiments.importers.maya.values.impl;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;
import com.javafx.experiments.importers.maya.types.MAttributeAliasType;
import com.javafx.experiments.importers.maya.values.MAttributeAlias;
public class MAttributeAliasImpl extends MDataImpl implements MAttributeAlias {
Map<String, String> map = new TreeMap();
public MAttributeAliasImpl(MAttributeAliasType type) {
super(type);
}
public Map getMapping() {
return map;
}
public void parse(Iterator<String> values) {
int count = 0;
List<String> list = new ArrayList();
while (values.hasNext()) {
String str = values.next();
int start = str.indexOf("\"");
if (start < 0) {
System.out.println("parse error at: " + str);
continue;
}
str = str.substring(start);
StringTokenizer izer = new StringTokenizer(str, ",");
while (izer.hasMoreTokens()) {
String tok = izer.nextToken();
tok = tok.substring(1, tok.length() - 1);
list.add(tok);
}
}
for (int i = 0; i < list.size(); i += 2) {
map.put(list.get(i), list.get(i + 1));
}
System.out.println("parsed aal: " + map);
}
}
