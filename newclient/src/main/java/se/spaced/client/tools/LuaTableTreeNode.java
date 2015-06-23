package se.spaced.client.tools;

import com.google.common.collect.Maps;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.KahluaTableIterator;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.Comparator;
import java.util.Map;

public class LuaTableTreeNode extends DefaultMutableTreeNode {
	private boolean areChildrenDefined = false;
	private final Map<Object, Object> objects;
	private final String name;

	public LuaTableTreeNode(String name, KahluaTable table) {
		this.name = name;
		objects = Maps.newTreeMap(new Comparator<Object>() {
			@Override
			public int compare(Object o, Object o2) {
				if (o instanceof Double && o2 instanceof Double) {
					return ((Double) o).compareTo((Double) o2);
				}
				return o.toString().compareTo(o2.toString());
			}
		});
		KahluaTableIterator iterator = table.iterator();
		while (iterator.advance()) {
			objects.put(iterator.getKey(), iterator.getValue());
		}
	}

	public boolean isLeaf() {
		return objects.isEmpty();
	}

	public int getChildCount() {
		if (!areChildrenDefined) {
			defineChildNodes();
		}
		return (super.getChildCount());
	}

	private void defineChildNodes() {
		// You must set the flag before defining children if you
		// use "add" for the new children. Otherwise you get an infinite
		// recursive loop, since add results in a call to getChildCount.
		// However, you could use "insert" in such a case.
		areChildrenDefined = true;
		for (Map.Entry<Object, Object> entry : objects.entrySet()) {
			Object value = entry.getValue();
			if (value instanceof KahluaTable) {
				add(new LuaTableTreeNode(entry.getKey().toString() + ":" + entry.getValue(), (KahluaTable) value));
			} else {
				add(new DefaultMutableTreeNode(entry.getKey() + ":" + entry.getValue()));
			}

		}
	}

	@Override
	public String toString() {
		return name;
	}
}
