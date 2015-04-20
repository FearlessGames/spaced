package se.spaced.spacedit.ui.view.propertylist;

import com.ardor3d.math.type.ReadOnlyQuaternion;
import com.ardor3d.math.type.ReadOnlyVector3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.spaced.spacedit.xmo.XmoManager;
import se.spaced.spacedit.xmo.model.WrappedExtendedMeshObject;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class XmoElementPropertyList implements PropertyListBuilder {
	private final WrappedExtendedMeshObject extendedMeshObject;
	private final XmoManager xmoManager;
	private final Logger log = LoggerFactory.getLogger(getClass());

	public XmoElementPropertyList(WrappedExtendedMeshObject extendedMeshObject, XmoManager xmoManager) {
		this.extendedMeshObject = extendedMeshObject;
		this.xmoManager = xmoManager;
	}

	@Override
	public Container build() {
		Container container = new Container();
		container.setLayout(new GridLayout(0, 1));

		addScaleSliders(container);
		addRotationSliders(container);
		addMaterialTextField(container);
		return container;
	}

	private void addMaterialTextField(Container container) {

		final JTextField materialTextField = new JTextField(extendedMeshObject.getXmoMaterialFile());
		container.add(new JLabel("Xmo Material File:"));
		container.add(materialTextField);
		final JButton setMaterialButton = new JButton("Set Material");
		setMaterialButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				extendedMeshObject.setXmoMaterialFile(materialTextField.getText());
				xmoManager.reloadMaterial(materialTextField.getText());
			}
		});
		container.add(setMaterialButton);
	}

	private void addScaleSliders(Container container) {
		ReadOnlyVector3 scale = extendedMeshObject.getScale();

		final JSpinner scaleX = new JSpinner(new SpinnerNumberModel(scale.getX(), 0, 100, 0.1));
		final JSpinner scaleY = new JSpinner(new SpinnerNumberModel(scale.getY(), 0, 100, 0.1));
		final JSpinner scaleZ = new JSpinner(new SpinnerNumberModel(scale.getZ(), 0, 100, 0.1));

		ChangeListener scaleChangeListener = new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				extendedMeshObject.updateScale((Double) scaleX.getValue(), (Double) scaleY.getValue(), (Double) scaleZ.getValue());
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
		ReadOnlyQuaternion rotation = extendedMeshObject.getRotation();

		final JSpinner rotateX = new JSpinner(new SpinnerNumberModel(rotation.getX(), 0, Math.PI * 2, 0.1));
		final JSpinner rotateY = new JSpinner(new SpinnerNumberModel(rotation.getY(), 0, Math.PI * 2, 0.1));
		final JSpinner rotateZ = new JSpinner(new SpinnerNumberModel(rotation.getZ(), 0, Math.PI * 2, 0.1));
		final JSpinner rotateW = new JSpinner(new SpinnerNumberModel(rotation.getW(), 0, Math.PI * 2, 0.1));


		ChangeListener changeListener = new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				extendedMeshObject.updateRotation((Double) rotateX.getValue(), (Double) rotateY.getValue(),
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



