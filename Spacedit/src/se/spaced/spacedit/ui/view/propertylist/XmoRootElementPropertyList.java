package se.spaced.spacedit.ui.view.propertylist;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.ardortech.math.SpacedVector3;
import se.spaced.spacedit.xmo.model.WrappedXmoRoot;

import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.Container;
import java.awt.GridLayout;

public class XmoRootElementPropertyList implements PropertyListBuilder {
	private final WrappedXmoRoot xmoRoot;
	private final Logger log = LoggerFactory.getLogger(getClass());

	public XmoRootElementPropertyList(WrappedXmoRoot xmoRoot) {
		this.xmoRoot = xmoRoot;
	}


	@Override
	public Container build() {
		Container container = new Container();
		container.setLayout(new GridLayout(0, 1));

		addNameTextField(container);
		addSizeSliders(container);

		return container;
	}

	private void addNameTextField(Container container) {
		final JTextField nameTextField = new JTextField(xmoRoot.getName());
		nameTextField.getDocument().addDocumentListener(new TextChangeListener() {
			@Override
			public void onTextChange() {
				xmoRoot.setName(nameTextField.getText());
			}
		});

		container.add(new JLabel("Name:"));
		container.add(nameTextField);
	}

	private void addSizeSliders(Container container) {
		SpacedVector3 scale = xmoRoot.getSize();
		final JSpinner scaleX = new JSpinner(new SpinnerNumberModel(scale.getX(), 0, 100, 0.1));
		final JSpinner scaleY = new JSpinner(new SpinnerNumberModel(scale.getY(), 0, 100, 0.1));
		final JSpinner scaleZ = new JSpinner(new SpinnerNumberModel(scale.getZ(), 0, 100, 0.1));


		ChangeListener scaleChangeListener = new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				xmoRoot.updateSize((Double) scaleX.getValue(), (Double) scaleY.getValue(), (Double) scaleZ.getValue());
			}
		};

		scaleX.addChangeListener(scaleChangeListener);
		scaleY.addChangeListener(scaleChangeListener);
		scaleZ.addChangeListener(scaleChangeListener);

		container.add(new JLabel("SIZE:X"));
		container.add(scaleX);
		container.add(new JLabel("SIZE:Y"));
		container.add(scaleY);
		container.add(new JLabel("SIZE:Z"));
		container.add(scaleZ);
	}

}