package se.spaced.spacedit.ui.view.propertylist;

import com.ardor3d.math.type.ReadOnlyQuaternion;
import com.ardor3d.math.type.ReadOnlyVector3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.spaced.spacedit.xmo.model.WrappedXmoContainerNode;

import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.Container;
import java.awt.GridLayout;

public class XmoContainerPropertyList implements PropertyListBuilder {
	private final WrappedXmoContainerNode xmoContainerNode;
	private final Logger log = LoggerFactory.getLogger(getClass());

	public XmoContainerPropertyList(WrappedXmoContainerNode xmoContainerNode) {
		this.xmoContainerNode = xmoContainerNode;
	}

	@Override
	public Container build() {
		Container container = new Container();
		container.setLayout(new GridLayout(0, 1));

		addScaleSliders(container);
		addRotationSliders(container);
		return container;
	}

	private void addScaleSliders(Container container) {
		ReadOnlyVector3 scale = xmoContainerNode.getScale();

		final JSpinner scaleX = new JSpinner(new SpinnerNumberModel(scale.getX(), 0, 100, 0.1));
		final JSpinner scaleY = new JSpinner(new SpinnerNumberModel(scale.getY(), 0, 100, 0.1));
		final JSpinner scaleZ = new JSpinner(new SpinnerNumberModel(scale.getZ(), 0, 100, 0.1));

		ChangeListener scaleChangeListener = new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				xmoContainerNode.updateScale((Double) scaleX.getValue(), (Double) scaleY.getValue(), (Double) scaleZ.getValue());
			}
		};

		scaleX.addChangeListener(scaleChangeListener);
		scaleY.addChangeListener(scaleChangeListener);
		scaleZ.addChangeListener(scaleChangeListener);

		container.add(new JLabel("SCALE:X"));
		container.add(scaleX);
		container.add(new JLabel("SCALE:Y"));
		container.add(scaleY);
		container.add(new JLabel("SCALE:Z"));
		container.add(scaleZ);
	}

	private void addRotationSliders(Container container) {
		ReadOnlyQuaternion rotation = xmoContainerNode.getRotation();

		final JSpinner rotateX = new JSpinner(new SpinnerNumberModel(rotation.getX(), 0, Math.PI * 2, 0.1));
		final JSpinner rotateY = new JSpinner(new SpinnerNumberModel(rotation.getY(), 0, Math.PI * 2, 0.1));
		final JSpinner rotateZ = new JSpinner(new SpinnerNumberModel(rotation.getZ(), 0, Math.PI * 2, 0.1));
		final JSpinner rotateW = new JSpinner(new SpinnerNumberModel(rotation.getW(), 0, Math.PI * 2, 0.1));


		ChangeListener changeListener = new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				xmoContainerNode.updateRotation((Double) rotateX.getValue(), (Double) rotateY.getValue(),
						(Double) rotateZ.getValue(), (Double) rotateW.getValue());
			}
		};

		rotateX.addChangeListener(changeListener);
		rotateY.addChangeListener(changeListener);
		rotateZ.addChangeListener(changeListener);
		rotateW.addChangeListener(changeListener);

		container.add(new JLabel("ROT:X"));
		container.add(rotateX);
		container.add(new JLabel("ROT:Y"));
		container.add(rotateY);
		container.add(new JLabel("ROT:Z"));
		container.add(rotateZ);
		container.add(new JLabel("ROT:W"));
		container.add(rotateW);

	}

}