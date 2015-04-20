package se.spaced.client.view;

import se.spaced.client.model.Prop;

public interface PropSelectionListener {
	void activePropChanged(Prop prop, Prop oldProp);
}
