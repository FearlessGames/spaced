package se.spaced.client.view;

import se.spaced.client.model.Prop;

public interface ActivePropProvider {
	boolean hasActiveProp();

	Prop getActiveProp();

	void setActiveProp(Prop prop);
}
