package se.spaced.server.tools.spawnpattern.view;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.spaced.server.mob.brains.templates.BrainParameter;
import se.spaced.server.mob.brains.templates.BrainTemplate;
import se.spaced.server.model.spawn.MobSpawnTemplate;
import se.spaced.server.model.spawn.MobTemplate;
import se.spaced.shared.tools.ui.TwoColumnBuilder;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class BrainParameterViewImpl extends JPanel implements BrainParameterView {
	private final Logger log = LoggerFactory.getLogger(getClass());

	private final BrainParameterPanelFactory brainParameterPanelFactory = new BrainParameterPanelFactory();
	private final BorderBuilder borderBuilder;


	@Inject
	public BrainParameterViewImpl(BorderBuilder borderBuilder) {
		this.borderBuilder = borderBuilder;
		this.setBorder(borderBuilder.getTitleBorder("Brain parameters"));
	}

	@Override
	public void setPresenter(Presenter presenter) {
		brainParameterPanelFactory.setPresenter(presenter);
	}

	@Override
	public void setBrainParameters(
			ImmutableSet<BrainParameter> brainParameters,
			MobTemplate mobTemplate,
			MobSpawnTemplate mobSpawnTemplate) {
		removeAll();
		ImmutableMap<Class<? extends BrainTemplate>, Collection<BrainParameter>> parametersPerBrain = Multimaps.index(
				brainParameters,
				new Function<BrainParameter, Class<? extends BrainTemplate>>() {
					@Override
					public Class<? extends BrainTemplate> apply(BrainParameter brainParameter) {
						return brainParameter.getBrain();
					}
				}).asMap();

		if (!parametersPerBrain.isEmpty()) {
			TwoColumnBuilder columnBuilder = new TwoColumnBuilder(this);
			final Set<Runnable> updateActions = Sets.newHashSet();
			for (Map.Entry<Class<? extends BrainTemplate>, Collection<BrainParameter>> entry : parametersPerBrain.entrySet()) {
				Class<? extends BrainTemplate> brainClass = entry.getKey();
				JPanel panel = buildPanel(brainClass, entry.getValue(), mobTemplate, updateActions, mobSpawnTemplate);
				columnBuilder.addRow(panel);
			}
			JButton saveButton = new JButton("Save brain parameters");
			saveButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					log.info("Save params");
					for (Runnable updateAction : updateActions) {
						updateAction.run();
					}
				}
			});
			columnBuilder.addRow(saveButton);
		} else {
			this.add(new JLabel("No data to display"));
		}
	}

	private JPanel buildPanel(
			Class<? extends BrainTemplate> brainClass,
			Iterable<BrainParameter> parameters,
			MobTemplate mobTemplate, Collection<Runnable> updateActions, MobSpawnTemplate mobSpawnTemplate) {
		JPanel panel = new JPanel();
		panel.setBorder(borderBuilder.getTitleBorder(brainClass.getSimpleName()));
		TwoColumnBuilder columnBuilder = new TwoColumnBuilder(panel);

		for (BrainParameter parameter : parameters) {
			BrainParameterPanelFactory.ParameterInput parameterInput = brainParameterPanelFactory.createInput(mobTemplate,
					parameter, mobSpawnTemplate);
			columnBuilder.addRow(parameter.getName(), parameterInput.getComponent());
			updateActions.add(parameterInput.getSaveAction());
		}
		return panel;
	}

	@Override
	public JPanel asPanel() {
		return this;
	}
}
