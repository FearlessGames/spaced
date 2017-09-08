package se.ardorgui.lua;

import com.ardor3d.extension.ui.*;
import com.ardor3d.extension.ui.backdrop.GradientBackdrop;
import com.ardor3d.extension.ui.backdrop.ImageBackdrop;
import com.ardor3d.extension.ui.backdrop.MultiImageBackdrop;
import com.ardor3d.extension.ui.backdrop.SolidBackdrop;
import com.ardor3d.extension.ui.backdrop.UIBackdrop;
import com.ardor3d.extension.ui.border.ImageBorder;
import com.ardor3d.extension.ui.event.ActionEvent;
import com.ardor3d.extension.ui.event.ActionListener;
import com.ardor3d.extension.ui.event.DragListener;
import com.ardor3d.extension.ui.event.FrameDragListener;
import com.ardor3d.extension.ui.event.FrameResizeListener;
import com.ardor3d.extension.ui.layout.AnchorLayoutData;
import com.ardor3d.extension.ui.layout.BorderLayout;
import com.ardor3d.extension.ui.layout.BorderLayoutData;
import com.ardor3d.extension.ui.layout.UILayoutData;
import com.ardor3d.extension.ui.text.StyleConstants;
import com.ardor3d.extension.ui.util.Alignment;
import com.ardor3d.extension.ui.util.Insets;
import com.ardor3d.extension.ui.util.SubTex;
import com.ardor3d.extension.ui.util.TransformedSubTex;
import com.ardor3d.framework.DisplaySettings;
import com.ardor3d.framework.NativeCanvas;
import com.ardor3d.image.Texture;
import com.ardor3d.math.ColorRGBA;
import com.ardor3d.math.Quaternion;
import com.ardor3d.math.Vector3;
import com.ardor3d.math.type.ReadOnlyColorRGBA;
import com.ardor3d.renderer.Camera;
import com.ardor3d.renderer.ContextManager;
import com.ardor3d.renderer.TextureRenderer;
import com.ardor3d.renderer.TextureRendererFactory;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.Spatial;
import com.ardor3d.scenegraph.hint.PickingHint;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.ardorgui.FixedAnchorLayout;
import se.ardorgui.components.ardoreventwrapper.UIEButton;
import se.ardorgui.components.ardoreventwrapper.UIEContainer;
import se.ardorgui.components.ardoreventwrapper.UIEFrame;
import se.ardorgui.components.ardoreventwrapper.UIEPanel;
import se.ardorgui.components.ardoreventwrapper.UIEPasswordField;
import se.ardorgui.components.ardoreventwrapper.UIETextArea;
import se.ardorgui.components.ardoreventwrapper.UIETextField;
import se.ardorgui.components.ardoreventwrapper.UIParentPanel;
import se.ardorgui.components.ardoreventwrapper.UIProgressCircle;
import se.ardorgui.components.area.AnchorPoint;
import se.ardorgui.components.area.BasicComponentArea;
import se.ardorgui.components.area.ComponentArea;
import se.ardorgui.components.rtt.Rtt;
import se.ardortech.TextureManager;
import se.fearless.common.lua.LuaVm;
import se.krka.kahlua.integration.annotations.LuaMethod;
import se.krka.kahlua.integration.expose.ReturnValues;
import se.spaced.client.ardor.font.ttf.FontFamily;
import se.spaced.shared.events.EventHandler;
import se.spaced.shared.events.LuaEventHandler;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Future;

import static com.google.common.base.Preconditions.checkNotNull;

public class ArdorUIPrimitives {
	private final Logger log = LoggerFactory.getLogger(getClass());
	private final UIContainer uiParent;
	private final UIHud hud;
	private final TextureManager textureManager;
	private final Map<UIComponent, EventHandler> eventHandlers = new WeakHashMap<UIComponent, EventHandler>();
	private final DisplaySettings displaySettings;
	private final NativeCanvas nativeCanvas;
	private final Camera rttCamera;
	private final LuaVm luaVm;
	private int tooltipPopupTime = 500;
	private final Queue<Runnable> deferredJobs = new ConcurrentLinkedQueue<Runnable>();

	public ArdorUIPrimitives(
			final int width,
			final int height,
			UIHud hud,
			TextureManager textureManager,
			LuaVm luaVm,
			DisplaySettings displaySettings,
			NativeCanvas nativeCanvas,
			Camera rttCamera) {
		this.hud = hud;
		this.textureManager = textureManager;
		this.luaVm = luaVm;
		this.displaySettings = displaySettings;
		this.nativeCanvas = nativeCanvas;
		this.rttCamera = rttCamera;
		UIComponent.setUseTransparency(true);
		LuaEventHandler eventHandler = new LuaEventHandler(luaVm);
		uiParent = new UIParentPanel(eventHandler);
		uiParent.setContentSize(width, height);
		uiParent.setDoClip(false);
		uiParent.setName("UIParent");
		getOrCreateEventHandler(uiParent);
		eventHandlers.put(uiParent, eventHandler);
		log.info("UIParent {}x{}", width, height);

		hud.add(uiParent);
		hud.setFocusedComponent(uiParent);
	}

	@LuaMethod(global = true, name = "CreateFrame")
	public UIFrame createFrame(
			final UIContainer parent,
			final int width,
			final int height,
			String title,
			boolean closable) {

		EnumSet<UIFrame.FrameButtons> buttons = closable ? EnumSet.of(UIFrame.FrameButtons.CLOSE) : EnumSet.noneOf(UIFrame.FrameButtons.class);

		final UIFrame component = new UIEFrame(title, buttons);
		component.setDragListener(new SpacedFrameDragListener(component));
		component.getStatusBar().setResizeListener(new SpacedFrameResizeListener(component, new FrameResizeListener(component.getStatusBar())));
		component.pack(width, height);
		component.getContentPanel().setLayout(new FixedAnchorLayout());
		UIButton closeButton = component.getTitleBar().getCloseButton();
		if (closeButton != null) {
			closeButton.removeAllListeners();
			closeButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent event) {
					setVisible(component, false);
					fireEvent(component, "OnClose");
				}
			});
		}
		addToParent(component, parent);
		return component;
	}

	private void addToParent(UIComponent component, UIContainer parent) {
		checkNotNull(parent, "Parent was null");
		if (parent instanceof UIFrame) {
			UIFrame frame = (UIFrame) parent;
			frame.getContentPanel().add(component);
		} else {
			parent.add(component);
		}
		component.setTooltipPopTime(tooltipPopupTime);
	}

	@LuaMethod(global = true, name = "CreateTextArea")
	public UITextArea createTextArea(final UIContainer parent, final int width, final int height) {
		UITextArea component = new UIETextArea();
		component.setEditable(false);
		component.setStyledText(true);
		component.setConsumeMouseEvents(false);
		setSize(component, width, height);
		addToParent(component, parent);
		return component;
	}

	@LuaMethod(global = true, name = "CreateProgressCircle")
	public UIProgressCircle createProgressCircle(
			final UIContainer parent, final int width, final int height) {
		UIProgressCircle component = new UIProgressCircle();
		component.setContentSize(width, height);
		String textureName = "gui/progresscircle.png";
		Future<Texture> textureFuture = textureManager.loadTexture(textureName, null, false);
		try {
			Texture texture = textureFuture.get();
			component.setBackdrop(new ImageBackdrop(new SubTex(texture)));
		} catch (Exception e) {
			throw new RuntimeException("Failed to load texture " + textureName, e);
		}
		addToParent(component, parent);
		return component;
	}

	@LuaMethod(global = true, name = "SetTitle")
	public void setTitle(UIFrame frame, String title) {
		frame.setTitle(title);
	}

	@LuaMethod(global = true, name = "SetProgressCircleProgress")
	public void setProgress(UIProgressCircle progressCircle, double progress) {
		if (progress < 0) {
			progress = 0;
		} else if (progress > 1.0) {
			progress = 1.0;
		}
		progressCircle.setPercentFilled((float) progress);
	}

	@LuaMethod(global = true, name = "GetProgressCircleProgress")
	public double getProgress(UIProgressCircle progressCircle) {
		return progressCircle.getPercentFilled();
	}

	// TODO: make a common interface that both progress classes can implement
	@LuaMethod(global = true, name = "SetProgressBarProgress")
	public void setProgress(UIProgressBar progressBar, double progress) {
		if (progress < 0) {
			progress = 0;
		} else if (progress > 1.0) {
			progress = 1.0;
		}
		progressBar.setPercentFilled((float) progress);
		UIContainer parent = (UIContainer) progressBar.getParent();
		parent.layout();
		parent.setDirty(true);
	}

	@LuaMethod(global = true, name = "GetProgressBarProgress")
	public double getProgress(UIProgressBar bar) {
		return bar.getPercentFilled();
	}


	@LuaMethod(global = true, name = "CreateProgressBar")
	public UIProgressBar createProgressBar(
			final UIContainer parent, final int width, final int height, float red, float green, float blue, float alpha) {
		UIProgressBar component = new UIProgressBar();
		component.getBarLabel().setAlignment(Alignment.MIDDLE);
		ImageBackdrop backdrop = (ImageBackdrop) component.getBar().getBackdrop();
		backdrop.setTintColor(new ColorRGBA(red, green, blue, alpha));
		component.setContentSize(width, height);
		addToParent(component, parent);
		component.layout();
		return component;
	}

	@LuaMethod(global = true, name = "GetProgressBarLabel")
	public UILabel getProgressBarText(UIProgressBar progressBar) {
		UILabel barLabel = progressBar.getBarLabel();
		return barLabel;
	}

	@LuaMethod(global = true, name = "SetProgressBarBackgroundAlpha")
	public void setProgressBarBackgroundAlpha(UIProgressBar progressBar, float alpha) {
		GradientBackdrop backdrop = (GradientBackdrop) progressBar.getMainPanel().getBackdrop();
		ColorRGBA bottomLeft = new ColorRGBA(backdrop.getBottomLeft());
		ColorRGBA bottomRight = new ColorRGBA(backdrop.getBottomRight());
		ColorRGBA topRight = new ColorRGBA(backdrop.getTopRight());
		ColorRGBA topLeft = new ColorRGBA(backdrop.getTopLeft());
		bottomLeft.setAlpha(alpha);
		bottomRight.setAlpha(alpha);
		topRight.setAlpha(alpha);
		topLeft.setAlpha(alpha);

		backdrop.setBottomLeft(bottomLeft);
		backdrop.setBottomRight(bottomRight);
		backdrop.setTopLeft(topLeft);
		backdrop.setTopRight(topRight);
		progressBar.setDirty(true);
	}

	@LuaMethod(global = true, name = "SetProgressBarHorizontal")
	public void setProgressBarHorizontal(UIProgressBar progressBar, boolean horizontal) {
		progressBar.setHorizontal(horizontal);
		progressBar.setDirty(true);
	}

	@LuaMethod(global = true, name = "CreateEditBox")
	public UITextField createEditBox(
			final UIContainer parent, final int width, final int height, boolean password, int fontSize, FontFamily font) {
		LuaEventHandler eventHandler = new LuaEventHandler(luaVm);
		final UITextField component = password ? new UIEPasswordField(eventHandler) : new UIETextField(eventHandler);
		setFont(component, fontSize, font);
		eventHandlers.put(component, eventHandler);
		component.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				fireEvent(component, "OnAction", component.getText());
			}
		});
		component.setMinimumContentSize(width, height);
		addToParent(component, parent);
		return component;
	}

	@LuaMethod(global = true, name = "SetFont")
	public void setFont(UIComponent component, int fontSize, FontFamily font) {
		Map<String, Object> styles = Maps.newHashMap();
		styles.put(StyleConstants.KEY_SIZE, fontSize);
		if (font != null) {
			styles.put(StyleConstants.KEY_FAMILY, font.getName());
		}
		component.setFontStyles(styles);
	}

	@LuaMethod(global = true, name = "CreateButton")
	public UIButton createButton(
			final UIContainer parent, final int width, final int height, String text) {
		LuaEventHandler eventHandler = new LuaEventHandler(luaVm);
		UIButton component = new UIEButton(text, eventHandler);
		eventHandlers.put(component, eventHandler);
		component.setMinimumContentSize(width, height);
		addToParent(component, parent);
		return component;
	}

	@LuaMethod(global = true, name = "CreatePicture")
	public UIPanel createPicture(final UIContainer parent, String textureName) {
		LuaEventHandler eventHandler = new LuaEventHandler(luaVm);
		final UIPanel component = new UIEPanel(eventHandler);
		eventHandlers.put(component, eventHandler);

		component.setName("Picture: " + textureName);
		component.setDoClip(false);

		setTexture(component, textureName);
		addToParent(component, parent);
		return component;
	}


	@LuaMethod(global = true, name = "CreateScrollPanel")
	public UIScrollPanel createScrollPanel(UIContainer parent, int width, int height, UIComponent viewport) {
		setParent(viewport, null);
		viewport.setLayoutData(BorderLayoutData.CENTER);
		UIScrollPanel component = new UIScrollPanel(viewport);
		setSize(component, width, height);
		addToParent(component, parent);
		return component;
	}

	@LuaMethod(global = true, name = "CreateLabel")
	public UILabel createLabel(
			final UIContainer parent, final String caption, int fontSize, FontFamily font) {
		UILabel label = new UILabel(caption);
		label.setForegroundColor(new ColorRGBA(1, 1, 1, 1));
		setFont(label, fontSize, font);
		label.setMinimumContentSize(0, 0);
		label.setContentSize(0, 0);
		label.setConsumeMouseEvents(false);
		label.getCurrentState().setConsumeMouseEvents(false);
		label.getSceneHints().setPickingHint(PickingHint.Pickable, false);
		addToParent(label, parent);
		return label;
	}

	@LuaMethod(global = true, name = "CreateFilledPanel")
	public UIPanel createFilledPanel(
			final UIContainer parent,
			final int width,
			final int height,
			final String textureName) {
		LuaEventHandler eventHandler = new LuaEventHandler(luaVm);
		final UIPanel component = new UIEPanel(eventHandler);
		eventHandlers.put(component, eventHandler);
		component.setName("Filled panel: " + textureName);
		component.setDoClip(false);
		component.setConsumeMouseEvents(false);
		Future<Texture> textureFuture = textureManager.loadTexture(textureName, null, false);
		try {
			Texture texture = textureFuture.get();
			component.setBackdrop(new ImageBackdrop(new SubTex(texture)));
		} catch (Exception e) {
			throw new RuntimeException("Failed to load texture " + textureName, e);
		}
		component.setMinimumContentSize(width, height);
		addToParent(component, parent);
		return component;
	}

	@LuaMethod(global = true, name = "CreateNineSlicePanel")
	public UIEPanel createNineSlicePanel(UIContainer parent, int width, int height, String textureName, int inset) {
		return createNineSlicePanel(width, height, textureName, new Insets(inset, inset, inset, inset), parent);
	}

	private UIEPanel createNineSlicePanel(
			final int width, final int height, final String textureName, final Insets insets, final UIContainer parent) {
		LuaEventHandler eventHandler = new LuaEventHandler(luaVm);
		final UIEPanel component = new UIEPanel(eventHandler);
		eventHandlers.put(component, eventHandler);
		component.setDoClip(false);
		component.setConsumeMouseEvents(false);
		Future<Texture> textureFuture = textureManager.loadTexture(textureName, null, false);
		try {
			Texture texture = textureFuture.get();
			int texWidth = texture.getImage().getWidth();
			int texHeight = texture.getImage().getHeight();
			int left = insets.getLeft();
			int right = insets.getRight();
			int top = insets.getTop();
			int bottom = insets.getBottom();
			int innerWidth = texWidth - (left + right);
			int innerHeight = texHeight - (top + bottom);
			component.setBackdrop(new ImageBackdrop(new SubTex(texture, left, top, innerWidth, innerHeight)));
			ImageBorder border = new ImageBorder(
					new SubTex(texture, 0, top, left, innerHeight),
					new SubTex(texture, texWidth - right, top, right, innerHeight),
					new SubTex(texture, left, 0, innerWidth, top),
					new SubTex(texture, left, texHeight - bottom, innerWidth, bottom),
					new SubTex(texture, 0, 0, left, top),
					new SubTex(texture, texWidth - right, 0, right, top),
					new SubTex(texture, 0, texHeight - bottom, left, bottom),
					new SubTex(texture, texWidth - right, texHeight - bottom, right, bottom)
			);
			component.setBorder(border);
			component.setPadding(new Insets(-insets.getTop(), -insets.getLeft(), -insets.getBottom(), -insets.getRight()));
		} catch (Exception e) {
			throw new RuntimeException("Failed to load texture " + textureName, e);
		}
		component.setMinimumContentSize(width, height);
		addToParent(component, parent);
		return component;
	}

	@LuaMethod(global = true, name = "CreateContainer")
	public UIPanel createContainer(UIContainer parent) {
		LuaEventHandler eventHandler = new LuaEventHandler(luaVm);
		UIPanel component = new UIEContainer(eventHandler);
		eventHandlers.put(component, eventHandler);

		component.setName("Container");
		component.setDoClip(false);
		component.setConsumeMouseEvents(false);
		addToParent(component, parent);
		return component;
	}

	@LuaMethod(global = true, name = "CreateArea")
	public UIPanel createArea(UIContainer parent, int width, int height) {
		LuaEventHandler eventHandler = new LuaEventHandler(luaVm);
		UIPanel component = new UIEPanel(eventHandler);
		eventHandlers.put(component, eventHandler);
		component.setName("Area");
		component.setMinimumContentSize(width, height);
		component.setDoClip(false);
		component.setConsumeMouseEvents(true);
		addToParent(component, parent);
		return component;
	}

	@LuaMethod(global = true, name = "Debug_SetBackdropColor")
	public void setBackgroundColor(UIComponent component, double r, double g, double b, double a) {
		component.setBackdrop(new SolidBackdrop(new ColorRGBA((float) r, (float) g, (float) b, (float) a)));
	}

	@LuaMethod(global = true, name = "SetPoint")
	public void setPoint(
			UIComponent component,
			final AnchorPoint anchor,
			final UIComponent frame,
			final AnchorPoint relativeAnchor,
			final double dx,
			final double dy) {
		log.debug("SetPoint {} - {} {}  {},{}", component, anchor, frame, relativeAnchor, dx, dy);
		component.setLayoutData(new AnchorLayoutData(anchor.getArdorAlignment(),
				frame,
				relativeAnchor.getArdorAlignment(),
				(int) dx,
				(int) dy));
		Node parent = component.getParent();
		if (parent instanceof UIComponent) {
			((UIComponent) parent).layout();
		}
	}

	@LuaMethod(global = true, name = "SetBorderLayoutData")
	public void setBorderLayoutData(UIComponent component, BorderLayoutData data) {
		component.setLayoutData(data);
		getParent(component).layout();
	}


	@LuaMethod(global = true, name = "SetVisible")
	public void setVisible(UIComponent component, boolean visible) {
		component.setVisible(visible);
		if (visible) {
			fireEvent(component, "OnShow");
		} else {
			fireEvent(component, "OnHide");
		}
	}

	@LuaMethod(global = true, name = "IsVisible")
	public boolean isVisible(UIComponent component) {
		return component.isVisible();
	}

	@LuaMethod(global = true, name = "GetComponentSize")
	public void getSize(ReturnValues ret, UIComponent component) {
		ret.push(component.getContentWidth());
		ret.push(component.getContentHeight());
	}


	@LuaMethod(global = true, name = "SetSize")
	public void setSize(UIComponent component, final int width, final int height) {
		int preWidth = component.getContentWidth();
		int preHeight = component.getContentHeight();
		component.setMinimumContentSize(width, height);
		component.setMaximumContentSize(width, height);
		component.setContentSize(width, height);
		component.fireComponentDirty();
		fireResizeEventIfNeeded(component, preWidth, preHeight);

	}

	private void fireResizeEventIfNeeded(UIComponent component, int preWidth, int preHeight) {
		int postWidth = component.getContentWidth();
		int postHeight = component.getContentHeight();
		if (preWidth != postWidth || preHeight != postHeight) {
			fireEvent(component, "OnResize", postWidth, postHeight);
		}
	}

	@LuaMethod(global = true, name = "BringToFront")
	public void bringToFront(UIComponent component) {
		UIContainer parent = (UIContainer) component.getParent();
		component.removeFromParent();
		parent.add(component);
	}

	@LuaMethod(global = true, name = "SetParent")
	public void setParent(final UIComponent component, final UIContainer parent) {
		if (component.getParent() == parent) {
			return;
		}
		Runnable job = new Runnable() {
			@Override
			public void run() {
				log.info("SetParent {} => {}", component, parent);
				component.removeFromParent();
				if (parent != null) {
					parent.attachChild(component);
				}
			}
		};
		deferredJobs.offer(job);
	}

	@LuaMethod(global = true, name = "GetParent")
	public UIContainer getParent(UIComponent component) {
		return (UIContainer) component.getParent();
	}

	@LuaMethod(global = true, name = "SetCanBeActive")
	public void setCanBeActive(UIComponent component, boolean canBeActive) {
		component.setConsumeMouseEvents(canBeActive);
	}

	@LuaMethod(global = true, name = "DoLayout")
	public void doLayout(UIComponent component) {
		component.layout();
	}

	@LuaMethod(global = true, name = "SetEnabled")
	public void enable(UIComponent component, boolean enabled) {
		component.setEnabled(enabled);
	}

	@LuaMethod(global = true, name = "IsEnabled")
	public boolean isEnabled(UIComponent component) {
		return component.isEnabled();
	}

	@LuaMethod(global = true, name = "SetAlpha")
	public void setAlpha(UIComponent component, final float alpha) {
		component.setOpacity(alpha);
		component.fireComponentDirty();
	}

	@LuaMethod(global = true, name = "GetAlpha")
	public double getAlpha(UIComponent component) {
		return component.getLocalOpacity();
	}

	@LuaMethod(global = true, name = "SetColor")
	public void setColor(UIComponent component, final Double red, final Double green, final Double blue, final Double alpha) {
		final ReadOnlyColorRGBA color = new ColorRGBA(red.floatValue(),
				green.floatValue(),
				blue.floatValue(),
				alpha.floatValue());
		UIBackdrop backdrop = component.getBackdrop();
		if (backdrop instanceof ImageBackdrop) {
			ImageBackdrop imageBackdrop = (ImageBackdrop) backdrop;
			imageBackdrop.setTintColor(color);
		} else if (backdrop instanceof MultiImageBackdrop) {
			MultiImageBackdrop multiImageBackdrop = (MultiImageBackdrop) backdrop;
			List<TransformedSubTex> images = multiImageBackdrop.getImages();
			for (TransformedSubTex image : images) {
				image.setTint(color);
			}
		} else if (backdrop instanceof SolidBackdrop) {
			SolidBackdrop solid = (SolidBackdrop) backdrop;
			solid.setColor(color);
		}
		component.setForegroundColor(color);
		component.fireComponentDirty();
	}

	@LuaMethod(global = true, name = "GetColor")
	public void getColor(ReturnValues ret, UIComponent component) {
		ReadOnlyColorRGBA color = component.getForegroundColor();
		ret.push(color.getRed());
		ret.push(color.getGreen());
		ret.push(color.getBlue());
		ret.push(color.getAlpha());
	}

	@LuaMethod(global = true, name = "SetName")
	public void setName(UIComponent component, String name) {
		component.setName(name);
	}

	@LuaMethod(global = true, name = "GetName")
	public String getName(UIComponent component) {
		return component.getName();
	}

	@LuaMethod(global = true, name = "Rotate")
	public void rotate(UIComponent component, double radians) {
		Quaternion rot = new Quaternion();
		rot.fromAngleNormalAxis(radians, new Vector3(0, 0, 1));
		component.setRotation(rot);
		component.fireComponentDirty();
	}

	@LuaMethod(global = true, name = "SetBorderLayout")
	public void setBorderLayout(UIContainer container) {
		container.setLayout(new BorderLayout());
	}

	@LuaMethod(global = true, name = "GetContentPanel")
	public UIPanel getContentPanel(UIFrame frame) {
		return frame.getContentPanel();
	}

	@LuaMethod(global = true, name = "SetResizable")
	public void setResizable(UIFrame frame, boolean resizable) {
		frame.setResizeable(resizable);
	}

	@LuaMethod(global = true, name = "GetUiParent")
	public UIContainer getUiParent() {
		return uiParent;
	}

	@LuaMethod(global = true, name = "GetHudPosition")
	public void getPosition(ReturnValues returnValues, UIComponent component) {
		int hudX = component.getHudX();
		int hudY = component.getHudY();
		returnValues.push(hudX, hudY);
	}

	@LuaMethod(name = "SetPosition", global = true)
	public void setPosition(UIComponent component, int x, int y) {
		component.setLayoutData(null);
		component.setLocalXY(x, y);
		component.updateWorldTransform(true);
	}

	@LuaMethod(global = true, name = "GetRect")
	public void getRect(ReturnValues returnValues, UIComponent component) {
		getPosition(returnValues, component);
		getSize(returnValues, component);
	}

	@LuaMethod(global = true, name = "GetPoint")
	public void getPoint(ReturnValues returnValues, UIComponent component) {
		UILayoutData layoutData = component.getLayoutData();
		if (layoutData instanceof AnchorLayoutData) {
			AnchorLayoutData anchorLayoutData = (AnchorLayoutData) layoutData;
			returnValues.push(anchorLayoutData.getXOffset());
			returnValues.push(anchorLayoutData.getYOffset());
			returnValues.push(AnchorPoint.fromArdor(anchorLayoutData.getMyPoint()));
			returnValues.push(anchorLayoutData.getParent());
			returnValues.push(AnchorPoint.fromArdor(anchorLayoutData.getParentPoint()));
		} else {
			log.warn("No point set when getting, using default");
			returnValues.push(0);
			returnValues.push(0);
			returnValues.push(AnchorPoint.fromArdor(Alignment.BOTTOM_LEFT));
			returnValues.push(component.getParent());
			returnValues.push(AnchorPoint.fromArdor(Alignment.BOTTOM_LEFT));
		}
	}

	@LuaMethod(global = true, name = "SetTexture")
	public void setTexture(UIPanel component, String textureName) {
		if (!textureName.contains(".")) {
			textureName = textureName + ".png";
		}
		Future<Texture> textureFuture = textureManager.loadTexture(textureName, null, false);
		try {
			Texture texture = textureFuture.get();
			int width = texture.getImage().getWidth();
			int height = texture.getImage().getHeight();
			component.setContentSize(width, height);
			component.setBackdrop(new ImageBackdrop(new SubTex(texture)));
			component.fireComponentDirty();
		} catch (Exception e) {
			throw new RuntimeException("Failed to load texture " + textureName, e);
		}
	}

	@LuaMethod(global = true, name = "SetButtonText")
	public void setButtonText(UIButton button, String text) {
		button.setText(text);
	}


	@LuaMethod(global = true, name = "SetLabelText")
	public void setText(UILabel label, final String text) {
		int preWidth = label.getContentWidth();
		int preHeight = label.getContentHeight();
		// In order for the size of the text to be correct if the new text is shorter than the old
		// we need to make it minimum and then let setText increase it
		label.setMinimumContentSize(0, 0);
		label.setContentSize(0, 0);
		label.setText(text);
		Node parent = label.getParent();
		if (parent instanceof UIComponent) {
			((UIComponent) parent).layout();
		}
		fireResizeEventIfNeeded(label, preWidth, preHeight);
	}

	@LuaMethod(global = true, name = "GetLabelText")
	public String getText(UILabel label) {
		return label.getText();
	}


	@LuaMethod(global = true, name = "SetAlign")
	public void setAlign(UILabel label, Alignment align) {
		label.setAlignment(align);
	}

	@LuaMethod(global = true, name = "GetEditBoxText")
	public String getEditBoxText(UITextField textField) {
		return textField.getText();
	}

	@LuaMethod(global = true, name = "SetEditBoxText")
	public void setEditBoxText(UITextField textField, final String text) {
		textField.setText(text);
	}

	@LuaMethod(global = true, name = "GetTextAreaText")
	public String getEditBoxText(UITextArea textArea) {
		return textArea.getText();
	}

	@LuaMethod(global = true, name = "SetTextAreaText")
	public void setEditBoxText(UITextArea textArea, final String text) {
		textArea.setText(text);
	}

	@LuaMethod(global = true, name = "AddListener")
	public void addEventListener(UIComponent component, final String event, final Object functionObject) {
		EventHandler handler = getOrCreateEventHandler(component);
		handler.subscribe(event, functionObject);
	}

	private EventHandler getOrCreateEventHandler(UIComponent component) {
		EventHandler handler = eventHandlers.get(component);
		if (handler == null) {
			handler = new LuaEventHandler(luaVm);
			eventHandlers.put(component, handler);
		}
		return handler;
	}

	@LuaMethod(global = true, name = "RemoveListener")
	public void removeScript(UIComponent component, final String script) {
		log.debug("RemoveListener {} from {}", script, component);
		EventHandler handler = getOrCreateEventHandler(component);
		handler.unsubscribe(script);
	}

	@LuaMethod(global = true, name = "FireEvent")
	public void fireEvent(UIComponent component, String event, Object... params) {
		EventHandler luaEventHandler = eventHandlers.get(component);
		if (luaEventHandler != null) {
			luaEventHandler.fireEvent(event, params);
		}
	}

	@LuaMethod(global = true, name = "SetFocus")
	public void focusOnComponent(UIComponent component) {
		hud.setFocusedComponent(component);
	}

	@LuaMethod(global = true, name = "HasFocus")
	public boolean hasFocus(UIComponent component) {
		return component.equals(hud.getFocusedComponent());
	}

	@LuaMethod(global = true, name = "SetTooltipText")
	public void setTooltipText(UIComponent component, String text) {
		component.setTooltipText(text);
	}

	@LuaMethod(global = true, name = "SetTooltipPopupWaitTime")
	public void setTooltipTextTime(double time) {
		tooltipPopupTime = (int) (time * 1000);
		setTooltipTextTime(hud, tooltipPopupTime);
	}

	private void setTooltipTextTime(Node node, int time) {
		if (node instanceof UIComponent) {
			UIComponent component = (UIComponent) node;
			component.setTooltipPopTime(time);
		}
		List<Spatial> children = node.getChildren();
		for (Spatial child : children) {
			if (child instanceof UIComponent) {
				setTooltipTextTime((UIComponent) child, time);
			}
		}
	}

	@LuaMethod(global = true, name = "CreateRtt")
	public Rtt createRtt(
			UIContainer parent,
			String xmoFile, int componentWidth, int componentHeight, int rttWidth, int rttHeight) {

		TextureRenderer textureRenderer = TextureRendererFactory.INSTANCE.createTextureRenderer(displaySettings,
				false,
				nativeCanvas.getCanvasRenderer().getRenderer(),
				ContextManager.getCurrentContext().getCapabilities());

		ComponentArea componentArea = new BasicComponentArea(componentWidth, componentHeight);
		ComponentArea rttArea = new BasicComponentArea(rttWidth, rttHeight);
		LuaEventHandler eventHandler = new LuaEventHandler(luaVm);
		Rtt rtt = new Rtt(componentArea,
				rttArea,
				new Node(xmoFile),
				rttCamera,
				textureRenderer,
				nativeCanvas.getCanvasRenderer(), eventHandler);
		eventHandlers.put(rtt, eventHandler);
		addToParent(rtt, parent);
		return rtt;
	}

	public void onUpdateRoot(UIComponent uiParent, double timePerFrame) {
		fireOnUpdateEvent(timePerFrame);
		for (Runnable job : deferredJobs) {
			job.run();
		}
		deferredJobs.clear();
	}

	public void fireOnUpdateEvent(double timePerFrame) {
		for (EventHandler handler : eventHandlers.values()) {
			handler.fireEvent("OnUpdate", timePerFrame);
		}
	}

	public void clear() {
		EventHandler uiParentEventHandler = eventHandlers.get(uiParent);
		uiParentEventHandler.reset();
		eventHandlers.clear();
		eventHandlers.put(uiParent, uiParentEventHandler);
	}


	private class SpacedFrameDragListener extends FrameDragListener {
		private final UIFrame component;

		public SpacedFrameDragListener(UIFrame component) {
			super(component);
			this.component = component;
		}

		@Override
		public void startDrag(int mouseX, int mouseY) {
			uiFrame.setLayoutData(null);
			super.startDrag(mouseX, mouseY);
			fireEvent(component, "OnBeginMove");
		}

		@Override
		public void drag(int mouseX, int mouseY) {
			super.drag(mouseX, mouseY);
			fireEvent(component, "OnMove", mouseX, mouseY);
		}

		@Override
		public void endDrag(UIComponent component2, int mouseX, int mouseY) {
			super.endDrag(component2,
					mouseX,
					mouseY);
			fireEvent(component, "OnEndMove", mouseX, mouseY);
		}
	}

	private class SpacedFrameResizeListener implements DragListener {

		private final UIComponent component;
		private final DragListener resizeListener;

		public SpacedFrameResizeListener(UIComponent component, DragListener resizeListener) {
			this.component = component;
			this.resizeListener = resizeListener;
		}

		@Override
		public boolean isDragHandle(UIComponent uiComponent, int x, int y) {
			return resizeListener.isDragHandle(uiComponent, x, y);
		}

		@Override
		public void startDrag(int x, int y) {
			resizeListener.startDrag(x, y);
			fireEvent(component, "OnBeginResize", x, y);
		}

		@Override
		public void drag(int x, int y) {
			resizeListener.drag(x, y);
			fireEvent(component, "OnResize", x, y);
		}

		@Override
		public void endDrag(UIComponent uiComponent, int x, int y) {
			resizeListener.endDrag(uiComponent, x, y);
			fireEvent(component, "OnEndResize", x, y);
		}
	}
}
