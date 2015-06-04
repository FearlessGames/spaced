package se.spaced.shared.tools.ui;

import javax.swing.AbstractListModel;
import javax.swing.JList;
import javax.swing.JTextField;
import javax.swing.ListModel;
import java.util.ArrayList;
import java.util.List;

public class FilteredJList<T> extends JList {
	private final TextField filterField;
	private final FilterModel<T> filterModel;

	public FilteredJList() {
		super();
		filterModel = new FilterModel<T>();
		super.setModel(filterModel);
		filterField = new TextField();

		filterField.addTextChangeListener(new TextField.TextChanged() {
			@Override
			public void onChange(TextField sender, String text) {
				filterModel.refilter();
			}
		});

	}

	@Override
	@SuppressWarnings("unchecked")
	public T getSelectedValue() {
		return (T) super.getSelectedValue();
	}


	public void setSelectedItem(T anObject) {
		super.setSelectedValue(anObject, true);
	}

	@Override
	public void setModel(ListModel m) {
		throw new IllegalArgumentException("Not allowed to change the model on filterlist");
	}

	public void addItem(T t) {
		filterModel.addElement(t);
	}

	public JTextField getFilterField() {
		return filterField;
	}

	public void removeAllItems() {
		filterModel.clear();
	}

	public void removeItem(T selectedValue) {
		filterModel.removeElement(selectedValue);
	}

	private class FilterModel<T> extends AbstractListModel {
		List<T> items;
		List<T> filterItems;

		private FilterModel() {
			super();
			items = new ArrayList<T>();
			filterItems = new ArrayList<T>();
		}

		public void removeElement(T element) {
			items.remove(element);
			refilter();
		}

		@Override
		public Object getElementAt(int index) {
			if (filterItems.isEmpty()) {
				return null;
			}
			if (index < filterItems.size()) {
				return filterItems.get(index);
			} else {
				return null;
			}
		}

		@Override
		public int getSize() {
			return filterItems.size();
		}

		public void addElement(T t) {
			items.add(t);
			refilter();

		}

		private void refilter() {
			filterItems.clear();
			String term = getFilterField().getText();
			for (T item : items) {
				if (item.toString().indexOf(term, 0) != -1) {
					filterItems.add(item);
				}
			}
			fireContentsChanged(this, 0, getSize());
		}

		public void clear() {
			items.clear();
			refilter();
		}
	}
}
