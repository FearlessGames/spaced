package se.spaced.spacedit.ui.view.propertylist;

import se.ardortech.math.SpacedVector3;
import se.spaced.spacedit.xmo.model.WrappedXmoMetaNode;

import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.Container;
import java.awt.GridLayout;

public class XmoMetaElementPropertyList implements PropertyListBuilder {
	private final WrappedXmoMetaNode metaNode;

	public XmoMetaElementPropertyList(WrappedXmoMetaNode metaNode) {
		this.metaNode = metaNode;
	}

	@Override
	public Container build() {
		Container container = new Container();
		container.setLayout(new GridLayout(0, 1));

		addSizeSliders(container);

		return container;
	}

	private void addSizeSliders(Container container) {
		SpacedVector3 position = metaNode.getPosition();
		final JSpinner posX = new JSpinner(new SpinnerNumberModel(position.getX(), 0, 100, 0.1));
		final JSpinner posY = new JSpinner(new SpinnerNumberModel(position.getY(), 0, 100, 0.1));
		final JSpinner posZ = new JSpinner(new SpinnerNumberModel(position.getZ(), 0, 100, 0.1));

		ChangeListener scaleChangeListener = new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				metaNode.updateLocation((Double) posX.getValue(), (Double) posY.getValue(), (Double) posZ.getValue());
			}
		};

		posX.addChangeListener(scaleChangeListener);
		posY.addChangeListener(scaleChangeListener);
		posZ.addChangeListener(scaleChangeListener);

		container.add(new JLabel("LOC:X"));
		container.add(posX);
		container.add(new JLabel("LOC:Y"));
		container.add(posY);
		container.add(new JLabel("LOC:Z"));
		container.add(posZ);
	}

}