package se.spaced.server.tools.spawnpattern.view;

import com.google.inject.Inject;
import se.spaced.server.model.spawn.MobTemplate;
import se.spaced.server.model.spawn.schedule.SpawnScheduleTemplate;
import se.spaced.shared.tools.ui.TwoColumnBuilder;

import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.Dimension;

public class MobSpawnTemplateViewImpl extends JPanel implements MobSpawnTemplateView {
	private Presenter presenter;
	private JTextField maxCount;
	private JTextField minCount;
	private JTextField maxWaitTime;
	private final MobTemplatePanel mobTemplatePanel;

	@Inject
	public MobSpawnTemplateViewImpl(BorderBuilder borderBuilder, BrainParameterView brainParameterView) {
		this.setBorder(borderBuilder.getTitleBorder("Spawn schedule"));
		TwoColumnBuilder twoColumnBuilder = new TwoColumnBuilder(this);
		twoColumnBuilder.addRow("Max Count", createMaxCount());
		twoColumnBuilder.addRow("Min Count", createMinCount());
		twoColumnBuilder.addRow("Max Wait Time", createMaxWaitTime());
		twoColumnBuilder.addRow(mobTemplatePanel = new MobTemplatePanel(borderBuilder, "Mob template data"));

		twoColumnBuilder.addRow(brainParameterView.asPanel());
	}

	private JTextField createMaxCount() {
		return maxCount = createTextField(new TextFieldChange() {
			@Override
			public void onChange(String text) {
				presenter.changeMaxCount(parseInt(text));
			}
		});
	}

	private JTextField createMinCount() {
		return minCount = createTextField(new TextFieldChange() {
			@Override
			public void onChange(String text) {
				presenter.changeMinCount(parseInt(text));
			}
		});
	}

	private JTextField createMaxWaitTime() {
		return maxWaitTime = createTextField(new TextFieldChange() {
			@Override
			public void onChange(String text) {
				presenter.changeMaxWaitTime(parseInt(text));
			}
		});
	}

	private int parseInt(String text) {
		if (text == null || text.isEmpty()) {
			return 0;
		}
		return Integer.parseInt(text);
	}

	private interface TextFieldChange {
		void onChange(String text);
	}

	private JTextField createTextField(final TextFieldChange textFieldChange) {
		final JTextField textField = new JTextField();
		textField.setPreferredSize(new Dimension(50, 20));
		textField.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				textFieldChange.onChange(textField.getText());
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				textFieldChange.onChange(textField.getText());
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				textFieldChange.onChange(textField.getText());
			}
		});
		return textField;
	}

	@Override
	public void setSpawnScheduleTemplateData(SpawnScheduleTemplate spawnScheduleTemplate) {
		if (spawnScheduleTemplate == null) {
			maxCount.setText("");
			maxWaitTime.setText("");
			minCount.setText("");
		} else {
			maxCount.setText(String.valueOf(spawnScheduleTemplate.getMaxCount()));
			maxWaitTime.setText(String.valueOf(spawnScheduleTemplate.getMaxWaitTime()));
			minCount.setText(String.valueOf(spawnScheduleTemplate.getMinCount()));
		}
	}

	@Override
	public void setMobTemplateData(MobTemplate mobTemplate) {
		mobTemplatePanel.setMobTemplateData(mobTemplate);
	}


	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public JPanel asPanel() {
		return this;
	}
}
