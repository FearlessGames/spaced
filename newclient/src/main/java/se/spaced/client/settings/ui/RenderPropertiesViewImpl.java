package se.spaced.client.settings.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.ardortech.render.module.WindowMode;
import se.spaced.shared.tools.ui.TwoColumnBuilder;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.UIManager;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;

public class RenderPropertiesViewImpl extends JDialog implements RenderPropertiesView {
	private final Logger log = LoggerFactory.getLogger(getClass());
	private final JComboBox colorCombo;
	private final JComboBox displayResCombo;
	private final JComboBox samplesCombo;
	private final JComboBox modeCombo;
	private Presenter presenter;

	public RenderPropertiesViewImpl() {

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (final Exception e) {
			log.warn("Could not set native look and feel.");
		}

		setTitle("Select Display Settings");
		final JPanel mainPanel = new JPanel();

		TwoColumnBuilder twoColumnBuilder = new TwoColumnBuilder(mainPanel);
		twoColumnBuilder.addRow("Mode", modeCombo = createModeCombo());
		twoColumnBuilder.addRow("Resolution", displayResCombo = createDisplayResCombo());
		twoColumnBuilder.addRow("Color depth", colorCombo = createColorCombo());
		twoColumnBuilder.addRow("Samples", samplesCombo = createSamplesCombo());

		twoColumnBuilder.addRow(createSaveButton(), createCancelButton());

		getContentPane().add(mainPanel);


	}

	@Override
	public void showDialog() {
		pack();
		center();

		toFront();
		setModal(true);
		setVisible(true);
	}

	@Override
	public void close() {
		this.dispose();
	}

	@Override
	public void setLockedResolutions(boolean b) {
		displayResCombo.setEnabled(!b);
	}

	private void center() {
		int x = (Toolkit.getDefaultToolkit().getScreenSize().width - getWidth()) / 2;
		int y = (Toolkit.getDefaultToolkit().getScreenSize().height - getHeight()) / 2;
		this.setLocation(x, y);
	}


	private JButton createSaveButton() {
		final JButton button = new JButton("Save");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				presenter.onSave();
			}
		});
		return button;
	}

	private JButton createCancelButton() {
		final JButton button = new JButton("Cancel");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				presenter.onCancel();
			}
		});
		return button;
	}

	private JComboBox createModeCombo() {
		JComboBox comboBox = new JComboBox();
		comboBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				presenter.onModeChanged();
			}
		});
		return comboBox;
	}

	private JComboBox createColorCombo() {
		JComboBox comboBox = new JComboBox();
		comboBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				presenter.onColorChanged();
			}
		});
		return comboBox;
	}

	private JComboBox createDisplayResCombo() {
		JComboBox comboBox = new JComboBox();
		comboBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				presenter.onDisplayResChanged();
			}
		});
		return comboBox;
	}

	private JComboBox createSamplesCombo() {
		JComboBox comboBox = new JComboBox();
		comboBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				presenter.onSamplesChanged();
			}
		});
		return comboBox;
	}


	@Override
	public void setColors(List<ColorMode> values) {
		setComboValues(colorCombo, values);
	}

	@Override
	public void setDisplayReses(List<Resolution> values) {
		setComboValues(displayResCombo, values);
	}

	@Override
	public void setSamples(List<SampleValue> values) {
		setComboValues(samplesCombo, values);
	}

	@Override
	public void setModes(List<WindowMode> values) {
		setComboValues(modeCombo, values);
	}

	private void setComboValues(JComboBox comboBox, Iterable<?> items) {
		comboBox.removeAllItems();
		for (Object o : items) {
			comboBox.addItem(o);
		}
	}

	@Override
	public void setColor(ColorMode value) {
		colorCombo.setSelectedItem(value);
	}

	@Override
	public ColorMode getColor() {
		return (ColorMode) colorCombo.getSelectedItem();
	}

	@Override
	public void setDisplayRes(Resolution value) {
		displayResCombo.setSelectedItem(value);
	}

	@Override
	public Resolution getDisplayRes() {
		return (Resolution) displayResCombo.getSelectedItem();
	}

	@Override
	public void setSample(SampleValue value) {
		samplesCombo.setSelectedItem(value);
	}

	@Override
	public SampleValue getSample() {
		return (SampleValue) samplesCombo.getSelectedItem();
	}

	@Override
	public void setMode(WindowMode value) {
		modeCombo.setSelectedItem(value);
	}

	@Override
	public WindowMode getMode() {
		return (WindowMode) modeCombo.getSelectedItem();
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}
}
