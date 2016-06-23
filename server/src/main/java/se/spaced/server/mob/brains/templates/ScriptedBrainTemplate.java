package se.spaced.server.mob.brains.templates;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.fearless.common.io.StreamLocator;
import se.krka.kahlua.luaj.compiler.LuaCompiler;
import se.krka.kahlua.vm.LuaClosure;
import se.spaced.server.mob.MobOrderExecutor;
import se.spaced.server.mob.brains.MobBrain;
import se.spaced.server.mob.brains.MobScriptEnvironment;
import se.spaced.server.mob.brains.NullBrain;
import se.spaced.server.mob.brains.ScriptedMobBrain;
import se.spaced.server.model.Mob;
import se.spaced.server.model.spawn.BrainParameterProvider;
import se.spaced.server.model.spawn.MobSpawnTemplate;
import se.spaced.server.model.spawn.MobTemplate;
import se.spaced.server.model.spawn.area.SpawnArea;
import se.spaced.server.tools.spawnpattern.view.InputType;

import javax.persistence.Entity;
import javax.persistence.Transient;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Supplier;

@Entity
public class ScriptedBrainTemplate extends BrainTemplate {
	private static final Logger log = LoggerFactory.getLogger(ScriptedBrainTemplate.class);
	@Transient
	private final StreamLocator locator;
	@Transient
	private final MobScriptEnvironment scriptEnv;
	@Transient
	private final MobOrderExecutor orderExecutor;
	public static final String SCRIPT_PATH = "Script path";

	@Inject
	public ScriptedBrainTemplate(StreamLocator locator, MobScriptEnvironment scriptEnv, MobOrderExecutor orderExecutor) {
		super(null, null);
		this.locator = locator;
		this.scriptEnv = scriptEnv;
		this.orderExecutor = orderExecutor;
	}

	@Override
	public MobBrain createBrain(Mob mob, SpawnArea spawnArea, BrainParameterProvider brainParameterProvider) {
		String scriptPath = brainParameterProvider.getScriptPath();
		Supplier<? extends InputStream> supplier = locator.getInputStreamSupplier(scriptPath);
		try {
			LuaClosure closure = LuaCompiler.loadis(supplier.get(), scriptPath, scriptEnv.getVm().getEnvironment());
			return new ScriptedMobBrain(mob, orderExecutor, scriptEnv, closure);
		} catch (IOException e) {
			log.error("Failed to load script for brain @ " + scriptPath);
			return new NullBrain(mob, e);
		}
	}

	@Override
	public ImmutableSet<BrainParameter> getRequiredParameters() {
		BrainParameter parameter = new BrainParameter() {
			@Override
			public Class<? extends BrainTemplate> getBrain() {
				return ScriptedBrainTemplate.class;
			}

			@Override
			public String getName() {
				return SCRIPT_PATH;
			}

			@Override
			public Object retrieveValue(MobTemplate mobTemplate, MobSpawnTemplate mobSpawnTemplate) {
				return mobTemplate.getScriptPath();
			}

			@Override
			public void updateValue(
					MobSpawnTemplate mobSpawnTemplate,
					Object parameter) {
				throw new UnsupportedOperationException("Can't update mobtemplate data from this view");
			}

			@Override
			public InputType getType() {
				return InputType.TEXT;
			}

			@Override
			public boolean isEditable() {
				return false;
			}
		};
		return ImmutableSet.of(parameter);
	}
}