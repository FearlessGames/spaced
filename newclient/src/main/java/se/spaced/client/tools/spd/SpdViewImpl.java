package se.spaced.client.tools.spd;

import com.ardor3d.math.Quaternion;
import com.ardor3d.scenegraph.Node;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import se.ardortech.math.Rotations;
import se.ardortech.math.SpacedVector3;
import se.fearlessgames.common.ui.swing.slidertext.JTextSlider;
import se.spaced.client.model.Prop;
import se.spaced.client.view.ActivePropProvider;
import se.spaced.shared.tools.ui.FilteredJList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

@Singleton
public class SpdViewImpl extends JFrame implements SpdView {
	private static final double DEGREES_TO_RAD = Math.PI / 180;
	private static final double RAD_TO_DEGREES = 1 / DEGREES_TO_RAD;

	private JTextSlider masterScaleSlider;
	private JTextSlider scaleSliderX;
	private JTextSlider scaleSliderY;
	private JTextSlider scaleSliderZ;

	private JTextSlider rotateSliderX;
	private JTextSlider rotateSliderY;

	private JTextSlider rotateSliderZ;
	private JTextSlider positionSliderX;
	private JTextSlider positionSliderY;
	private JTextSlider positionSliderZ;

	private final FilteredJList<String> propList;


	private SpacedVector3 originalActiveLocation = SpacedVector3.ZERO;
	private Presenter presenter;
	private final JPanel sidePanel;
	private final ActivePropProvider activePropProvider;


	@Inject
	public SpdViewImpl(ActivePropProvider activePropProvider) {
		super("Add prop");
		this.activePropProvider = activePropProvider;
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

		JPanel rootPanel = new JPanel(new BorderLayout());

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

		propList = new FilteredJList<String>();
		propList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				presenter.changedXmoFile();
			}
		});
		propList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		JScrollPane listScroll = new JScrollPane(propList);
		listScroll.setPreferredSize(new Dimension(550, 200));
		mainPanel.add(new JLabel("XMO:"));
		mainPanel.add(propList.getFilterField());
		mainPanel.add(listScroll);


		createSliders(mainPanel);

		mainPanel.add(new JLabel("Zone:"));
		String[] zones = {"zone:harborTown", "location:inn"};
		JComboBox zoneList = new JComboBox(zones);
		mainPanel.add(zoneList);

		createButtons(mainPanel);

		rootPanel.add(mainPanel, BorderLayout.CENTER);

		sidePanel = new JPanel();
		sidePanel.setPreferredSize(new Dimension(400, 200));

		rootPanel.add(sidePanel, BorderLayout.WEST);

		getContentPane().add(rootPanel);
		pack();

		this.addWindowFocusListener(new WindowAdapter() {
			@Override
			public void windowGainedFocus(WindowEvent e) {
				presenter.gainedFocus();
			}

			@Override
			public void windowLostFocus(WindowEvent e) {
				presenter.lostFocus();
			}
		});

		this.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentShown(ComponentEvent e) {
				presenter.viewShowing();
			}

			@Override
			public void componentHidden(ComponentEvent e) {
				presenter.viewHiding();
			}
		});
	}


	private void createSliders(JPanel mainPanel) {
		JPanel positionPanel = new JPanel();
		positionPanel.setLayout(new GridLayout(3, 1));

		positionSliderX = new JTextSlider(-10, 10, 0.1, 0);
		positionSliderY = new JTextSlider(-10, 10, 0.1, 0);
		positionSliderZ = new JTextSlider(-10, 10, 0.1, 0);

		createPositionPanel(positionPanel, positionSliderX, "x:");
		createPositionPanel(positionPanel, positionSliderY, "y:");
		createPositionPanel(positionPanel, positionSliderZ, "z:");

		mainPanel.add(new JLabel("Position:"));
		mainPanel.add(positionPanel);


		JPanel rotatePanel = new JPanel();
		rotatePanel.setLayout(new GridLayout(3, 1));

		rotateSliderX = new JTextSlider(-180, 180, 0.1, 0);
		rotateSliderY = new JTextSlider(-180, 180, 0.1, 0);
		rotateSliderZ = new JTextSlider(-180, 180, 0.1, 0);

		createRotatePanel(rotatePanel, rotateSliderX, "yaw:");
		createRotatePanel(rotatePanel, rotateSliderY, "pitch:");
		createRotatePanel(rotatePanel, rotateSliderZ, "roll:");

		mainPanel.add(new JLabel("Rotation:"));
		mainPanel.add(rotatePanel);

		JPanel scalePanel = new JPanel(new GridLayout(4, 2));
		masterScaleSlider = new JTextSlider(0.01, 10, 0.01, 1);
		scaleSliderX = new JTextSlider(0.01, 2, 0.01, 1);
		scaleSliderY = new JTextSlider(0.01, 2, 0.01, 1);
		scaleSliderZ = new JTextSlider(0.01, 2, 0.01, 1);
		createScalePanel(scalePanel, masterScaleSlider, "Master:");
		createScalePanel(scalePanel, scaleSliderX, "x:");
		createScalePanel(scalePanel, scaleSliderY, "y:");
		createScalePanel(scalePanel, scaleSliderZ, "z:");

		mainPanel.add(new JLabel("Scale:"));
		mainPanel.add(scalePanel);
	}

	private void createButtons(JPanel mainPanel) {
		JPanel buttonPanel = new JPanel(new GridLayout(1, 3));
		JButton placeHere = new JButton("Place here!");

		placeHere.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				presenter.placePropHere();
			}
		});
		buttonPanel.add(placeHere);
		JButton saveButton = new JButton("Save");
		saveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				presenter.saveAllZones();
			}
		});
		buttonPanel.add(saveButton);

		JButton removeButton = new JButton("remove");
		removeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				presenter.removeProp();
			}
		});


		buttonPanel.add(removeButton);

		mainPanel.add(buttonPanel);
	}

	@Override
	public void display() {
		setVisible(true);
	}

	@Override
	public void close() {
		dispose();
	}


	public void updateActiveProp() {
		updateRotationSlidersFromActiveProp();
		updateScaleSliderFromActiveProp();
		originalActiveLocation = activePropProvider.getActiveProp().getLocation();
		positionSliderX.setValue(0);
		positionSliderY.setValue(0);
		positionSliderZ.setValue(0);
	}


	@Override
	public String getSelectedXmoFileName() {
		return propList.getSelectedValue();
	}

	@Override
	public double getScaleValue() {
		return masterScaleSlider.getValue();
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public void addListItem(String s) {
		propList.addItem(s);
	}

	@Override
	public void setPreviewComponent(JComponent previewComponent) {
		sidePanel.add(previewComponent);
	}

	private void updateRotationSlidersFromActiveProp() {
		Prop activeProp = activePropProvider.getActiveProp();
		if (activePropProvider.hasActiveProp() && activeProp.getXmoEntity().getModel() != null) {
			Quaternion rot = new Quaternion();
			rot.fromRotationMatrix(activeProp.getXmoEntity().getModel().getRotation());
			double[] angles = new double[3];
			rot.toEulerAngles(angles);
			rotateSliderX.setValue(angles[0] * RAD_TO_DEGREES);
			rotateSliderY.setValue(angles[1] * RAD_TO_DEGREES);
			rotateSliderZ.setValue(angles[2] * RAD_TO_DEGREES);
		}
	}

	private void createRotatePanel(JPanel panel, JTextSlider slider, String text) {
		panel.add(new JLabel(text));
		slider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				updateActivePropFromRotationSliders();
			}
		});
		panel.add(slider);
	}

	private void createPositionPanel(JPanel panel, JTextSlider slider, String text) {
		panel.add(new JLabel(text));
		slider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				updateActivePropFromPositionSliders();
			}
		});
		panel.add(slider);
	}

	private void createScalePanel(JPanel panel, JTextSlider slider, String text) {
		panel.add(new JLabel(text));
		slider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				updateActivePropFromScaleSliders();
			}
		});
		panel.add(slider);
	}

	private void updateActivePropFromPositionSliders() {
		SpacedVector3 relativeMovement = new SpacedVector3(positionSliderX.getValue(),
				positionSliderY.getValue(),
				positionSliderZ.getValue());
		SpacedVector3 newLoc = originalActiveLocation.add(relativeMovement);
		Prop activeProp = activePropProvider.getActiveProp();
		Node node = activeProp.getXmoEntity().getModel();
		if (node != null) {
			node.setTranslation(newLoc);
		}
		activeProp.setLocation(newLoc);
	}

	private void updateActivePropFromRotationSliders() {
		Prop activeProp = activePropProvider.getActiveProp();
		Node node = activeProp.getXmoEntity().getModel();
		double yaw = rotateSliderX.getValue() * DEGREES_TO_RAD;
		double roll = rotateSliderY.getValue() * DEGREES_TO_RAD;
		double pitch = rotateSliderZ.getValue() * DEGREES_TO_RAD;
		if (node != null) {
			Quaternion rot = new Quaternion();
			rot.fromEulerAngles(yaw, roll, pitch);
			node.setRotation(rot);
			activeProp.setRotation(Rotations.fromArdor(rot));

		}
	}

	private void updateActivePropFromScaleSliders() {
		Prop activeProp = activePropProvider.getActiveProp();
		double masterScale = masterScaleSlider.getValue();
		double x = masterScale * scaleSliderX.getValue();
		double y = masterScale * scaleSliderY.getValue();
		double z = masterScale * scaleSliderZ.getValue();
		if (masterScale > 0) {
			Node node = activeProp.getXmoEntity().getModel();
			if (node != null) {
				node.setScale(x, y, z);
				activeProp.setScale(new SpacedVector3(x, y, z));
			}
		}
	}


	private void updateScaleSliderFromActiveProp() {
		Prop activeProp = activePropProvider.getActiveProp();
		SpacedVector3 scale = activeProp.getScale();
		double x = scale.getX();
		double y = scale.getY();
		double z = scale.getZ();
		double average = (x + y + z) / 3;
		masterScaleSlider.setValue(average);
		scaleSliderX.setValue(x / average);
		scaleSliderY.setValue(y / average);
		scaleSliderZ.setValue(z / average);
	}


}
