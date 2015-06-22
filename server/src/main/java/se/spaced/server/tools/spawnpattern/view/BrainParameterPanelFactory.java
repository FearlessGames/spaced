package se.spaced.server.tools.spawnpattern.view;

import com.google.common.collect.Maps;
import se.spaced.server.mob.brains.templates.BrainParameter;
import se.spaced.server.model.spawn.MobSpawnTemplate;
import se.spaced.server.model.spawn.MobTemplate;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JTextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

public class BrainParameterPanelFactory {
	
	private final Map<InputType, Factory> factories = Maps.newEnumMap(InputType.class);
	private BrainParameterView.Presenter presenter;

	public BrainParameterPanelFactory() {
		TextFieldFactory textFieldFactory = new TextFieldFactory();
		factories.put(InputType.TEXT, textFieldFactory);
		factories.put(InputType.NUMERIC, textFieldFactory);
		CheckboxFactory checkboxFactory = new CheckboxFactory();
		factories.put(InputType.BOOOLEAN, checkboxFactory);
		GeometryFactoryAdapter geometryFactoryAdapter = new GeometryFactoryAdapter();
		factories.put(InputType.GEOMETRY, geometryFactoryAdapter);
	}

	public ParameterInput createInput(
			final MobTemplate mobTemplate,
			final BrainParameter parameter,
			MobSpawnTemplate mobSpawnTemplate) {
		Object value = parameter.retrieveValue(mobTemplate, mobSpawnTemplate);
		Factory factory = factories.get(parameter.getType());
		if (factory != null) {
			return factory.create(parameter, value, mobTemplate, mobSpawnTemplate, presenter);
		}
		return new ParameterInput(new JTextField("Fake data"), new Runnable() {
			@Override
			public void run() {
			}
		});
	}

	public void setPresenter(BrainParameterView.Presenter presenter) {
		this.presenter = presenter;
	}

	static class ParameterInput {
		final JComponent component;
		final Runnable saveAction;

		private ParameterInput(JComponent component, Runnable saveAction) {
			this.component = component;
			this.saveAction = saveAction;
		}

		public JComponent getComponent() {
			return component;
		}

		public Runnable getSaveAction() {
			return saveAction;
		}
	}
	
	private interface Factory {
		ParameterInput create(
				BrainParameter parameter,
				Object value,
				MobTemplate mobTemplate,
				MobSpawnTemplate mobSpawnTemplate, final BrainParameterView.Presenter presenter);
	}

	private static class TextFieldFactory implements Factory {
		@Override
		public ParameterInput create(
				final BrainParameter parameter,
				Object value,
				final MobTemplate mobTemplate,
				final MobSpawnTemplate mobSpawnTemplate, final BrainParameterView.Presenter presenter) {
			final JTextField textField = new JTextField(20);
			textField.setText(value.toString());
			textField.setEditable(parameter.isEditable());
			Runnable action = new Runnable() {
				@Override
				public void run() {
					parameter.updateValue(mobSpawnTemplate, textField.getText());
				}
			};
			return new ParameterInput(textField, action);
		}
	}

	private static class CheckboxFactory implements Factory {
		@Override
		public ParameterInput create(
				final BrainParameter parameter,
				Object value,
				final MobTemplate mobTemplate,
				final MobSpawnTemplate mobSpawnTemplate, final BrainParameterView.Presenter presenter) {
			final JCheckBox checkBox = new JCheckBox();
			checkBox.setSelected((Boolean) value);
			checkBox.setEnabled(parameter.isEditable());
			Runnable action = new Runnable() {
				@Override
				public void run() {
					parameter.updateValue(mobSpawnTemplate, checkBox.isSelected());
				}
			};
			return new ParameterInput(checkBox, action);
		}
	}

	private static class GeometryFactoryAdapter implements Factory {
		private final GeometryPanelFactory geometryPanelFactory = new GeometryPanelFactory();
		@Override
		public ParameterInput create(
				BrainParameter parameter,
				Object value,
				MobTemplate mobTemplate,
				MobSpawnTemplate mobSpawnTemplate, final BrainParameterView.Presenter presenter) {
			if (value != null) {
				return new ParameterInput(geometryPanelFactory.create(mobSpawnTemplate.getGeometry()), new Runnable() {
					@Override
					public void run() {
					}
				});
			}
			JButton button = new JButton("Paste geometry data");
			button.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					presenter.changeGeometryData();
				}
			});
			return new ParameterInput(button, new Runnable() {
				@Override
				public void run() {
				}
			});
		}
	}
}
