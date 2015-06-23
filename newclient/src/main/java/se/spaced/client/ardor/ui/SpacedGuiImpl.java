package se.spaced.client.ardor.ui;

import com.ardor3d.extension.ui.UIContainer;
import com.ardor3d.extension.ui.UIHud;
import com.ardor3d.extension.ui.skin.Skin;
import com.ardor3d.extension.ui.skin.SkinManager;
import com.ardor3d.framework.Canvas;
import com.ardor3d.framework.DisplaySettings;
import com.ardor3d.framework.NativeCanvas;
import com.ardor3d.input.KeyEvent;
import com.ardor3d.input.MouseManager;
import com.ardor3d.input.PhysicalLayer;
import com.ardor3d.input.logical.InputTrigger;
import com.ardor3d.input.logical.LogicalLayer;
import com.ardor3d.input.logical.TriggerAction;
import com.ardor3d.input.logical.TwoInputStates;
import com.ardor3d.renderer.Camera;
import com.google.common.base.Predicate;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import se.ardorgui.lua.ArdorUIPrimitives;
import se.ardorgui.lua.bindings.LuaKeyBindings;
import se.ardortech.TextureManager;
import se.ardortech.math.SpacedRotation;
import se.ardortech.math.SpacedVector3;
import se.fearlessgames.common.lua.LuaRuntimeErrorLogger;
import se.fearlessgames.common.lua.LuaVm;
import se.krka.kahlua.integration.annotations.LuaMethod;
import se.krka.kahlua.integration.expose.LuaJavaClassExposer;
import se.spaced.client.ardor.InputManager;
import se.spaced.client.ardor.ui.events.WorldGuiEvents;
import se.spaced.client.ardor.ui.rtt.EntityModel;
import se.spaced.client.ardor.ui.rtt.EntityModelFactory;
import se.spaced.client.model.ClientEntity;
import se.spaced.client.model.ClientSpell;
import se.spaced.client.model.item.ClientInventory;
import se.spaced.client.model.item.ClientItem;
import se.spaced.client.settings.AccountSettings;
import se.spaced.shared.RangeConstants;
import se.spaced.shared.events.EventHandler;
import se.spaced.shared.model.items.ItemType;
import se.spaced.shared.resources.zone.Zone;
import se.spaced.shared.scheduler.Invoker;
import se.spaced.shared.scheduler.Job;
import se.spaced.shared.scheduler.JobManager;

@Singleton
public class SpacedGuiImpl implements SpacedGui {
	private final GuiApiExposer apiExposer;
	private final LuaVm luaVm;
	private final JobManager jobManager;
	private String[] initFiles;
	private final EventHandler eventHandler;
	private final AccountSettings accountSettings;
	private final LuaKeyBindings luaKeyBindings;
	private final UIHud hud;
	private final InputManager inputManager;
	private final ArdorUIPrimitives ardorUIPrimitives;
	private final EntityModelFactory entityModelFactory;

	@Inject
	public SpacedGuiImpl(
			GuiApiExposer apiExposer,
			@Named("gui") LuaVm luaVm, JobManager jobManager, EventHandler eventHandler, AccountSettings accountSettings,
			LuaKeyBindings luaKeyBindings,
			UIHud hud,
			Canvas canvas,
			PhysicalLayer physicalLayer,
			LogicalLayer logicalLayer,
			InputManager inputmanager,
			MouseManager mouseManager,
			DisplaySettings displaySettings,
			TextureManager textureManager,
			NativeCanvas nativeCanvas,
			@Named("rttCamera") Camera camera,
			EntityModelFactory entityModelFactory, Skin spacedSkin) {
		this.apiExposer = apiExposer;
		this.luaVm = luaVm;
		this.jobManager = jobManager;
		this.eventHandler = eventHandler;
		this.accountSettings = accountSettings;
		this.luaKeyBindings = luaKeyBindings;
		this.hud = hud;
		inputManager = inputmanager;
		this.entityModelFactory = entityModelFactory;

		SkinManager.setCurrentSkin(spacedSkin);
		ardorUIPrimitives = new ArdorUIPrimitives(displaySettings.getWidth(), displaySettings.getHeight(),
				hud, textureManager, luaVm, displaySettings, nativeCanvas, camera);

		hud.setupInput(canvas, physicalLayer, logicalLayer);
		logicalLayer.registerTrigger(new InputTrigger(new AllKeyEventsCondition(), new TriggerAction() {
			@Override
			public void perform(Canvas source, TwoInputStates inputState, double tpf) {
				KeyEvent keyEvent = inputState.getCurrent().getKeyboardState().getKeyEvent();
				inputManager.onKeyEvent(keyEvent);
			}
		}
		));
		Predicate<TwoInputStates> mousePredicate = new Predicate<TwoInputStates>() {
			@Override
			public boolean apply(TwoInputStates twoInputStates) {
				return !twoInputStates.getCurrent().getMouseState().equals(twoInputStates.getPrevious().getMouseState());
			}
		};
		logicalLayer.registerTrigger(new InputTrigger(mousePredicate, new TriggerAction() {
			@Override
			public void perform(Canvas source, TwoInputStates inputState, double tpf) {
				inputManager.onMouseEvent(inputState.getPrevious().getMouseState(),
						inputState.getCurrent().getMouseState());
			}
		}));
		hud.setIgnoreMouseInputOnGrabbed(true);
		hud.setMouseManager(mouseManager);
	}

	@Override
	public void start(String[] initFiles) {
		this.initFiles = initFiles;
		doReload();
	}

	@Override
	@LuaMethod(global = true, name = "ReloadUi")
	public void reload() {
		Job job = new Job(0, false);
		job.setJob(new Invoker() {
			@Override
			public void invoke() {
				doReload();
			}

		});
		jobManager.addJob(job);
	}

	private void doReload() {
		// Out with the old...
		teardown();

		// ... and in with the new
		setup();
	}

	public void setup() {
		luaVm.clean();
		setupBindings();
		luaVm.runLuaFiles(initFiles);
		eventHandler.fireAsynchEvent(WorldGuiEvents.UI_READY);
	}

	@Override
	public void teardown() {
		releaseUi();
		luaKeyBindings.clearBinds();
		luaVm.setRuntimeErrorListener(null);
	}

	@Override
	public LuaJavaClassExposer setupBindings() {
		luaVm.setRuntimeErrorListener(new LuaRuntimeErrorLogger());

		LuaJavaClassExposer luaExposer = luaVm.getExposer();

		luaExposer.exposeGlobalFunctions(this);
		luaExposer.exposeGlobalFunctions(luaExposer);
		luaExposer.exposeGlobalFunctions(entityModelFactory);

		luaExposer.exposeClass(EntityModel.class);
		luaExposer.exposeClass(AccountSettings.class);
		luaExposer.exposeClass(Zone.class);
		luaExposer.exposeClass(ClientInventory.class);
		luaExposer.exposeClassUsingJavaEquals(ClientItem.class);
		luaExposer.exposeClass(ItemType.class);
		luaExposer.exposeClass(ClientSpell.class);
		luaExposer.exposeClass(ClientEntity.class);
		luaExposer.exposeClass(SpacedRotation.class);
		luaExposer.exposeClass(SpacedVector3.class);
		luaExposer.exposeGlobalFunctions(ardorUIPrimitives);
		luaVm.getEnvironment().rawset("INTERACTION_RANGE", RangeConstants.INTERACTION_RANGE);
		luaVm.getEnvironment().rawset("accountSettings", accountSettings);

		apiExposer.expose(luaExposer);
		return luaExposer;
	}


	@Override
	public void onUpdate(double timePerFrame) {
		hud.getLogicalLayer().checkTriggers(timePerFrame);
		ardorUIPrimitives.onUpdateRoot(ardorUIPrimitives.getUiParent(), timePerFrame);
	}


	private void releaseUi() {
		if (ardorUIPrimitives != null) {
			UIContainer guiContainer = ardorUIPrimitives.getUiParent();
			if (guiContainer != null) {
				guiContainer.removeAllComponents();
			}
		}
		ardorUIPrimitives.clear();
		eventHandler.reset();
	}

	@Override
	public void toggle() {
		UIContainer uiParent = ardorUIPrimitives.getUiParent();
		uiParent.setVisible(!uiParent.isVisible());
	}
}