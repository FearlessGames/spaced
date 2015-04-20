package se.spaced.spacedit.ui.presenter.display;

import com.ardor3d.framework.Canvas;
import com.ardor3d.input.InputState;
import com.ardor3d.input.Key;
import com.ardor3d.input.MouseState;
import com.ardor3d.input.logical.InputTrigger;
import com.ardor3d.input.logical.KeyHeldCondition;
import com.ardor3d.input.logical.LogicalLayer;
import com.ardor3d.input.logical.TriggerAction;
import com.ardor3d.input.logical.TriggerConditions;
import com.ardor3d.input.logical.TwoInputStates;
import com.ardor3d.intersection.PickResults;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.Spatial;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import se.ardortech.pick.Picker;
import se.fearlessgames.common.ui.Action;
import se.spaced.spacedit.ui.view.display.ArdorView;
import se.spaced.spacedit.xmo.XmoManager;
import se.spaced.spacedit.xmo.model.NodeHolder;

@Singleton
public class ArdorPresenterImpl implements ArdorPresenter {
	private final LogicalLayer logicalLayer;
	private final CameraHandler cameraHandler;
	private final ArdorSceneHandler colladaSceneHandler;
	private final Picker picker;
	private final XmoManager xmoManager;
	private final Node rootNode;

	@Inject
	public ArdorPresenterImpl(final ArdorView view, final LogicalLayer logicalLayer, CameraHandler cameraHandler, final ArdorSceneHandler colladaSceneHandler, final Picker picker, final XmoManager xmoManager, Node rootNode) {
		this.logicalLayer = logicalLayer;
		this.cameraHandler = cameraHandler;
		this.colladaSceneHandler = colladaSceneHandler;
		this.picker = picker;
		this.xmoManager = xmoManager;
		this.rootNode = rootNode;
		registerKeyTriggers();
		registerMouseTriggers();

		view.setResizeAction(new Action() {
			@Override
			public void act() {
				view.setCanvasSize(view.getSize());
			}
		});
	}

	public void registerMouseTriggers() {
		final Predicate<TwoInputStates> mouseMovedAndOneButtonPressed = Predicates.and(TriggerConditions.mouseMoved(), TriggerConditions.leftButtonDown());
		final Predicate<TwoInputStates> mouseClicked = Predicates.and(TriggerConditions.rightButtonDown());
		logicalLayer.registerTrigger(new InputTrigger(mouseMovedAndOneButtonPressed, new TriggerAction() {
			public void perform(final Canvas source, final TwoInputStates inputState, final double tpf) {
				final MouseState mouseState = inputState.getCurrent().getMouseState();
				cameraHandler.turn(source, mouseState.getDx() * tpf);
				cameraHandler.rotateUpDown(source, mouseState.getDy() * tpf);
			}
		}));
		//TODO:REFACTOR
		logicalLayer.registerTrigger(new InputTrigger(mouseClicked, new TriggerAction() {
			public void perform(final Canvas source, final TwoInputStates inputStates, final double tpf) {
				InputState inputState = inputStates.getCurrent();
				final MouseState mouseState = inputState.getMouseState();
				PickResults pickResults = picker.pickWithBoundingBox(mouseState.getX(), mouseState.getY(), rootNode);
				pickResults.setCheckDistance(true);
				Spatial spatial = Picker.getSpatial(pickResults.getPickData(0));
				findActiveXmo(spatial.getParent());
				pickResults.processPick();
			}
		}));
	}

	private void findActiveXmo(Node node) {
		if (node != null) {
			NodeHolder nodeHolder = xmoManager.getObject(node);
			if (nodeHolder != null) {
				xmoManager.selectObject(nodeHolder);
			} else {
				findActiveXmo(node.getParent());
			}
		}
	}

	private void registerKeyTriggers() {
		logicalLayer.registerTrigger(new InputTrigger(new KeyHeldCondition(Key.H), new TriggerAction() {
			@Override
			public void perform(Canvas canvas, TwoInputStates TwoInputStates, double v) {
				colladaSceneHandler.moveOut();
			}

		}));
		logicalLayer.registerTrigger(new InputTrigger(new KeyHeldCondition(Key.Y), new TriggerAction() {
			@Override
			public void perform(Canvas canvas, TwoInputStates TwoInputStates, double v) {
				colladaSceneHandler.moveIn();
			}
		}));
		logicalLayer.registerTrigger(new InputTrigger(new KeyHeldCondition(Key.G), new TriggerAction() {
			@Override
			public void perform(Canvas canvas, TwoInputStates TwoInputStates, double v) {
				colladaSceneHandler.moveLeft();
			}
		}));
		logicalLayer.registerTrigger(new InputTrigger(new KeyHeldCondition(Key.J), new TriggerAction() {
			@Override
			public void perform(Canvas canvas, TwoInputStates TwoInputStates, double v) {
				colladaSceneHandler.moveRight();
			}
		}));
		logicalLayer.registerTrigger(new InputTrigger(new KeyHeldCondition(Key.I), new TriggerAction() {
			@Override
			public void perform(Canvas canvas, TwoInputStates TwoInputStates, double v) {
				colladaSceneHandler.moveUp();
			}
		}));
		logicalLayer.registerTrigger(new InputTrigger(new KeyHeldCondition(Key.K), new TriggerAction() {
			@Override
			public void perform(Canvas canvas, TwoInputStates TwoInputStates, double v) {
				colladaSceneHandler.moveDown();
			}
		}));
		logicalLayer.registerTrigger(new InputTrigger(new KeyHeldCondition(Key.W), new TriggerAction() {
			@Override
			public void perform(Canvas canvas, TwoInputStates TwoInputStates, double v) {
				cameraHandler.moveForward(canvas, v);
			}
		}));
		logicalLayer.registerTrigger(new InputTrigger(new KeyHeldCondition(Key.S), new TriggerAction() {
			@Override
			public void perform(Canvas canvas, TwoInputStates TwoInputStates, double v) {
				cameraHandler.moveBack(canvas, v);
			}
		}));
		logicalLayer.registerTrigger(new InputTrigger(new KeyHeldCondition(Key.D), new TriggerAction() {
			@Override
			public void perform(Canvas canvas, TwoInputStates TwoInputStates, double v) {
				cameraHandler.moveRight(canvas, v);
			}
		}));
		logicalLayer.registerTrigger(new InputTrigger(new KeyHeldCondition(Key.A), new TriggerAction() {
			@Override
			public void perform(Canvas canvas, TwoInputStates TwoInputStates, double v) {
				cameraHandler.moveLeft(canvas, v);
			}
		}));
		logicalLayer.registerTrigger(new InputTrigger(new KeyHeldCondition(Key.R), new TriggerAction() {
			@Override
			public void perform(Canvas canvas, TwoInputStates TwoInputStates, double v) {
				cameraHandler.moveUp(canvas, v);
			}
		}));
		logicalLayer.registerTrigger(new InputTrigger(new KeyHeldCondition(Key.F), new TriggerAction() {
			@Override
			public void perform(Canvas canvas, TwoInputStates TwoInputStates, double v) {
				cameraHandler.moveDown(canvas, v);
			}
		}));
	}
}