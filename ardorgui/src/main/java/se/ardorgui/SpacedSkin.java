package se.ardorgui;

import com.ardor3d.extension.ui.*;
import com.ardor3d.extension.ui.backdrop.EmptyBackdrop;
import com.ardor3d.extension.ui.backdrop.GradientBackdrop;
import com.ardor3d.extension.ui.backdrop.ImageBackdrop;
import com.ardor3d.extension.ui.backdrop.SolidBackdrop;
import com.ardor3d.extension.ui.border.EmptyBorder;
import com.ardor3d.extension.ui.border.ImageBorder;
import com.ardor3d.extension.ui.border.SolidBorder;
import com.ardor3d.extension.ui.border.UIBorder;
import com.ardor3d.extension.ui.layout.RowLayout;
import com.ardor3d.extension.ui.skin.Skin;
import com.ardor3d.extension.ui.skin.SkinningTask;
import com.ardor3d.extension.ui.util.Alignment;
import com.ardor3d.extension.ui.util.Insets;
import com.ardor3d.extension.ui.util.SubTex;
import com.ardor3d.image.Texture;
import com.ardor3d.math.ColorRGBA;
import se.ardortech.TextureLoadCallback;
import se.ardortech.TextureManager;

public class SpacedSkin extends Skin {
	protected Texture sharedTex;

	public SpacedSkin(TextureManager textureManager) {
		textureManager.loadTexture("gui/skin.png", new TextureLoadCallback() {
			@Override
			public void loadedRequestedTexture(Texture texture) {
				sharedTex = texture;
			}
		}, false);
	}


	@Override
	protected void applyToTab(final UITab component) {

		component.setMargin(new Insets(1, 1, 1, 1));
		component.setPadding(new Insets(2, 14, 2, 14));

		// State values...
		final UIBorder defaultBorder = new ImageBorder(
				// left
				component.getPlacement() != UITabbedPane.TabPlacement.EAST ? new SubTex(sharedTex,
						47,
						11,
						4,
						10) : new SubTex(
						sharedTex, 0, 0, 0, 0),
				// right
				component.getPlacement() != UITabbedPane.TabPlacement.WEST ? new SubTex(sharedTex,
						77,
						11,
						4,
						10) : new SubTex(
						sharedTex, 0, 0, 0, 0),
				// top
				component.getPlacement() != UITabbedPane.TabPlacement.SOUTH ? new SubTex(sharedTex,
						51,
						7,
						26,
						4) : new SubTex(
						sharedTex, 0, 0, 0, 0),
				// bottom
				component.getPlacement() != UITabbedPane.TabPlacement.NORTH ? new SubTex(sharedTex,
						51,
						21,
						26,
						4) : new SubTex(
						sharedTex, 0, 0, 0, 0),
				// top left
				component.getPlacement() != UITabbedPane.TabPlacement.SOUTH && component.getPlacement() != UITabbedPane.TabPlacement.EAST ? new SubTex(
						sharedTex, 47, 7, 4, 4)
						: null,
				// top right
				component.getPlacement() != UITabbedPane.TabPlacement.SOUTH && component.getPlacement() != UITabbedPane.TabPlacement.WEST ? new SubTex(
						sharedTex, 77, 7, 4, 4)
						: null,
				// bottom left
				component.getPlacement() != UITabbedPane.TabPlacement.NORTH && component.getPlacement() != UITabbedPane.TabPlacement.EAST ? new SubTex(
						sharedTex, 47, 21, 4, 4)
						: null,
				// bottom right
				component.getPlacement() != UITabbedPane.TabPlacement.NORTH && component.getPlacement() != UITabbedPane.TabPlacement.WEST ? new SubTex(
						sharedTex, 77, 21, 4, 4)
						: null);

		final UIBorder overBorder = new ImageBorder(
				// left
				component.getPlacement() != UITabbedPane.TabPlacement.EAST ? new SubTex(sharedTex,
						47,
						33,
						4,
						10) : new SubTex(
						sharedTex, 0, 0, 0, 0),
				// right
				component.getPlacement() != UITabbedPane.TabPlacement.WEST ? new SubTex(sharedTex,
						77,
						33,
						4,
						10) : new SubTex(
						sharedTex, 0, 0, 0, 0),
				// top
				component.getPlacement() != UITabbedPane.TabPlacement.SOUTH ? new SubTex(sharedTex,
						51,
						29,
						26,
						4) : new SubTex(
						sharedTex, 0, 0, 0, 0),
				// bottom
				component.getPlacement() != UITabbedPane.TabPlacement.NORTH ? new SubTex(sharedTex,
						51,
						43,
						26,
						4) : new SubTex(
						sharedTex, 0, 0, 0, 0),
				// top left
				component.getPlacement() != UITabbedPane.TabPlacement.SOUTH && component.getPlacement() != UITabbedPane.TabPlacement.EAST ? new SubTex(
						sharedTex, 47, 29, 4, 4)
						: null,
				// top right
				component.getPlacement() != UITabbedPane.TabPlacement.SOUTH && component.getPlacement() != UITabbedPane.TabPlacement.WEST ? new SubTex(
						sharedTex, 77, 29, 4, 4)
						: null,
				// bottom left
				component.getPlacement() != UITabbedPane.TabPlacement.NORTH && component.getPlacement() != UITabbedPane.TabPlacement.EAST ? new SubTex(
						sharedTex, 47, 43, 4, 4)
						: null,
				// bottom right
				component.getPlacement() != UITabbedPane.TabPlacement.NORTH && component.getPlacement() != UITabbedPane.TabPlacement.WEST ? new SubTex(
						sharedTex, 77, 43, 4, 4)
						: null);

		final UIBorder pressedBorder = new ImageBorder(
				// left
				component.getPlacement() != UITabbedPane.TabPlacement.EAST ? new SubTex(sharedTex,
						47,
						55,
						4,
						10) : new SubTex(
						sharedTex, 0, 0, 0, 0),
				// right
				component.getPlacement() != UITabbedPane.TabPlacement.WEST ? new SubTex(sharedTex,
						77,
						55,
						4,
						10) : new SubTex(
						sharedTex, 0, 0, 0, 0),
				// top
				component.getPlacement() != UITabbedPane.TabPlacement.SOUTH ? new SubTex(sharedTex,
						51,
						51,
						26,
						4) : new SubTex(
						sharedTex, 0, 0, 0, 0),
				// bottom
				component.getPlacement() != UITabbedPane.TabPlacement.NORTH ? new SubTex(sharedTex,
						51,
						65,
						26,
						4) : new SubTex(
						sharedTex, 0, 0, 0, 0),
				// top left
				component.getPlacement() != UITabbedPane.TabPlacement.SOUTH && component.getPlacement() != UITabbedPane.TabPlacement.EAST ? new SubTex(
						sharedTex, 47, 51, 4, 4)
						: null,
				// top right
				component.getPlacement() != UITabbedPane.TabPlacement.SOUTH && component.getPlacement() != UITabbedPane.TabPlacement.WEST ? new SubTex(
						sharedTex, 77, 51, 4, 4)
						: null,
				// bottom left
				component.getPlacement() != UITabbedPane.TabPlacement.NORTH && component.getPlacement() != UITabbedPane.TabPlacement.EAST ? new SubTex(
						sharedTex, 47, 65, 4, 4)
						: null,
				// bottom right
				component.getPlacement() != UITabbedPane.TabPlacement.NORTH && component.getPlacement() != UITabbedPane.TabPlacement.WEST ? new SubTex(
						sharedTex, 77, 65, 4, 4)
						: null);

		final ColorRGBA upTop = new ColorRGBA(235 / 255f, 235 / 255f, 235 / 255f, 1);
		final ColorRGBA upBottom = new ColorRGBA(200 / 255f, 200 / 255f, 200 / 255f, 1);
		final GradientBackdrop upBack = new GradientBackdrop(upTop, upTop, upBottom, upBottom);
		final ColorRGBA downTop = new ColorRGBA(181 / 255f, 181 / 255f, 181 / 255f, 1);
		final ColorRGBA downBottom = new ColorRGBA(232 / 255f, 232 / 255f, 232 / 255f, 1);
		final GradientBackdrop downBack = new GradientBackdrop(downTop, downTop, downBottom, downBottom);
		// DEFAULT
		{
			component.getDefaultState().setBorder(defaultBorder);
			component.getDefaultState().setBackdrop(upBack);
			component.getDefaultState().setForegroundColor(ColorRGBA.BLACK);
		}
		// DISABLED
		{
			component.getDisabledState().setBorder(defaultBorder);
			component.getDisabledState().setBackdrop(upBack);
			component.getDisabledState().setForegroundColor(ColorRGBA.GRAY);

			component.getDisabledSelectedState().setBorder(pressedBorder);
			component.getDisabledSelectedState().setBackdrop(downBack);
			component.getDisabledSelectedState().setForegroundColor(ColorRGBA.GRAY);
		}
		// MOUSE OVER
		{
			final ColorRGBA top = new ColorRGBA(241 / 255f, 241 / 255f, 241 / 255f, 1);
			final ColorRGBA bottom = new ColorRGBA(216 / 255f, 216 / 255f, 216 / 255f, 1);
			final GradientBackdrop back = new GradientBackdrop(top, top, bottom, bottom);

			component.getMouseOverState().setBorder(overBorder);
			component.getMouseOverState().setBackdrop(back);
			component.getMouseOverState().setForegroundColor(ColorRGBA.BLACK);
		}
		// PRESSED AND SELECTED
		{
			component.getPressedState().setBorder(pressedBorder);
			component.getPressedState().setBackdrop(downBack);
			component.getPressedState().setForegroundColor(ColorRGBA.BLACK);

			component.getSelectedState().setBorder(pressedBorder);
			component.getSelectedState().setBackdrop(downBack);
			component.getSelectedState().setForegroundColor(ColorRGBA.BLACK);

			component.getMouseOverSelectedState().setBorder(pressedBorder);
			component.getMouseOverSelectedState().setBackdrop(downBack);
			component.getMouseOverSelectedState().setForegroundColor(ColorRGBA.GRAY);
		}
	}

	@Override
	protected void applyToButton(final UIButton component) {

		component.setAlignment(Alignment.MIDDLE);
		component.setMargin(new Insets(1, 1, 1, 1));
		component.setPadding(new Insets(2, 14, 2, 14));

		// State values...
		final UIBorder defaultBorder = new ImageBorder(
				// left
				new SubTex(sharedTex, 47, 11, 4, 10),
				// right
				new SubTex(sharedTex, 77, 11, 4, 10),
				// top
				new SubTex(sharedTex, 51, 7, 26, 4),
				// bottom
				new SubTex(sharedTex, 51, 21, 26, 4),
				// top left
				new SubTex(sharedTex, 47, 7, 4, 4),
				// top right
				new SubTex(sharedTex, 77, 7, 4, 4),
				// bottom left
				new SubTex(sharedTex, 47, 21, 4, 4),
				// bottom right
				new SubTex(sharedTex, 77, 21, 4, 4));

		final UIBorder overBorder = new ImageBorder(
				// left
				new SubTex(sharedTex, 47, 33, 4, 10),
				// right
				new SubTex(sharedTex, 77, 33, 4, 10),
				// top
				new SubTex(sharedTex, 51, 29, 26, 4),
				// bottom
				new SubTex(sharedTex, 51, 43, 26, 4),
				// top left
				new SubTex(sharedTex, 47, 29, 4, 4),
				// top right
				new SubTex(sharedTex, 77, 29, 4, 4),
				// bottom left
				new SubTex(sharedTex, 47, 43, 4, 4),
				// bottom right
				new SubTex(sharedTex, 77, 43, 4, 4));

		final UIBorder pressedBorder = new ImageBorder(
				// left
				new SubTex(sharedTex, 47, 55, 4, 10),
				// right
				new SubTex(sharedTex, 77, 55, 4, 10),
				// top
				new SubTex(sharedTex, 51, 51, 26, 4),
				// bottom
				new SubTex(sharedTex, 51, 65, 26, 4),
				// top left
				new SubTex(sharedTex, 47, 51, 4, 4),
				// top right
				new SubTex(sharedTex, 77, 51, 4, 4),
				// bottom left
				new SubTex(sharedTex, 47, 65, 4, 4),
				// bottom right
				new SubTex(sharedTex, 77, 65, 4, 4));

		final ColorRGBA upTop = new ColorRGBA(235 / 255f, 235 / 255f, 235 / 255f, 1);
		final ColorRGBA upBottom = new ColorRGBA(200 / 255f, 200 / 255f, 200 / 255f, 1);
		final GradientBackdrop upBack = new GradientBackdrop(upTop, upTop, upBottom, upBottom);
		final ColorRGBA downTop = new ColorRGBA(181 / 255f, 181 / 255f, 181 / 255f, 1);
		final ColorRGBA downBottom = new ColorRGBA(232 / 255f, 232 / 255f, 232 / 255f, 1);
		final GradientBackdrop downBack = new GradientBackdrop(downTop, downTop, downBottom, downBottom);
		// DEFAULT
		{
			component.getDefaultState().setBorder(defaultBorder);
			component.getDefaultState().setBackdrop(upBack);
			component.getDefaultState().setForegroundColor(ColorRGBA.BLACK);
		}
		// DISABLED
		{
			component.getDisabledState().setBorder(defaultBorder);
			component.getDisabledState().setBackdrop(upBack);
			component.getDisabledState().setForegroundColor(ColorRGBA.GRAY);

			component.getDisabledSelectedState().setBorder(pressedBorder);
			component.getDisabledSelectedState().setBackdrop(downBack);
			component.getDisabledSelectedState().setForegroundColor(ColorRGBA.GRAY);
		}
		// MOUSE OVER
		{
			final ColorRGBA top = new ColorRGBA(241 / 255f, 241 / 255f, 241 / 255f, 1);
			final ColorRGBA bottom = new ColorRGBA(216 / 255f, 216 / 255f, 216 / 255f, 1);
			final GradientBackdrop back = new GradientBackdrop(top, top, bottom, bottom);

			component.getMouseOverState().setBorder(overBorder);
			component.getMouseOverState().setBackdrop(back);
			component.getMouseOverState().setForegroundColor(ColorRGBA.BLACK);
		}
		// PRESSED AND SELECTED
		{
			component.getPressedState().setBorder(pressedBorder);
			component.getPressedState().setBackdrop(downBack);
			component.getPressedState().setForegroundColor(ColorRGBA.BLACK);

			component.getSelectedState().setBorder(pressedBorder);
			component.getSelectedState().setBackdrop(downBack);
			component.getSelectedState().setForegroundColor(ColorRGBA.BLACK);

			component.getMouseOverSelectedState().setBorder(pressedBorder);
			component.getMouseOverSelectedState().setBackdrop(downBack);
			component.getMouseOverSelectedState().setForegroundColor(ColorRGBA.GRAY);
		}
	}

	@Override
	protected void applyToCheckBox(final UICheckBox component) {

		component.setMargin(new Insets(1, 1, 1, 1));
		component.setPadding(new Insets(1, 1, 1, 1));
		component.setBorder(new EmptyBorder());
		component.setBackdrop(new EmptyBackdrop());
		component.setAlignment(Alignment.LEFT);
		component.setGap(4);

		// DEFAULT
		{
			component.getDefaultState().setForegroundColor(ColorRGBA.BLACK);
			component.getDefaultState().setIcon(new SubTex(sharedTex, 94, 9, 14, 14));
		}
		// DISABLED
		{
			component.getDisabledState().setForegroundColor(ColorRGBA.GRAY);
			component.getDisabledState().setIcon(new SubTex(sharedTex, 132, 9, 14, 14));
		}
		// MOUSEOVER
		{
			component.getMouseOverState().setForegroundColor(ColorRGBA.BLACK);
			component.getMouseOverState().setIcon(new SubTex(sharedTex, 113, 9, 14, 14));
		}
		// SELECTED
		{
			component.getSelectedState().setForegroundColor(ColorRGBA.BLACK);
			component.getSelectedState().setIcon(new SubTex(sharedTex, 94, 25, 14, 14));
		}
		// MOUSEOVER SELECTED
		{
			component.getMouseOverSelectedState().setForegroundColor(ColorRGBA.BLACK);
			component.getMouseOverSelectedState().setIcon(new SubTex(sharedTex, 113, 25, 14, 14));
		}
		// DISABLED SELECTED
		{
			component.getDisabledSelectedState().setForegroundColor(ColorRGBA.GRAY);
			component.getDisabledSelectedState().setIcon(new SubTex(sharedTex, 132, 25, 14, 14));
		}
	}

	@Override
	protected void applyToFrame(final UIFrame component) {
		component.setOpacity(1.0f);
		// TITLE BAR
		{
			final UIFrameBar titleBar = component.getTitleBar();
			// Make sure exists and is attached
			if (titleBar != null && titleBar.getParent() == component) {
				titleBar.setMargin(new Insets(0, 0, 0, 0));
				titleBar.setPadding(new Insets(0, 0, 0, 0));
				final UIBorder border = new ImageBorder(
						// left
						new SubTex(sharedTex, 4, 11, 6, 6),
						// right
						new SubTex(sharedTex, 30, 11, 6, 6),
						// top
						new SubTex(sharedTex, 10, 5, 20, 6),
						// bottom
						new SubTex(sharedTex, 9, 9, 20, 1),
						// top left
						new SubTex(sharedTex, 4, 5, 6, 6),
						// top right
						new SubTex(sharedTex, 30, 5, 6, 6),
						// bottom left
						new SubTex(sharedTex, 4, 16, 6, 1),
						// bottom right
						new SubTex(sharedTex, 30, 16, 6, 1));
				titleBar.setBorder(border);
				final ColorRGBA top = new ColorRGBA(203 / 255f, 203 / 255f, 203 / 255f, 1);
				final ColorRGBA bottom = new ColorRGBA(208 / 255f, 208 / 255f, 208 / 255f, 1);
				final GradientBackdrop grad = new GradientBackdrop(top, top, bottom, bottom);
				titleBar.setBackdrop(grad);

				titleBar.getTitleLabel().setMargin(new Insets(0, 5, 0, 0));
				titleBar.getTitleLabel().setForegroundColor(ColorRGBA.BLACK);

				// CLOSE BUTTON
				{
					final UIButton closeButton = titleBar.getCloseButton();
					if (closeButton != null) {
						closeButton.setButtonText("");
						closeButton.setButtonIcon(new SubTex(sharedTex, 94, 76, 16, 16));
						closeButton.getPressedState().setIcon(new SubTex(sharedTex, 94, 94, 16, 16));
						for (final UIState state : closeButton.getStates()) {
							state.setBackdrop(new EmptyBackdrop());
							state.setBorder(new EmptyBorder());
							state.setPadding(new Insets(0, 0, 0, 0));
							state.setMargin(new Insets(1, 1, 1, 1));
						}
						closeButton.refreshState();
						closeButton.updateMinimumSizeFromContents();
						closeButton.compact();
						closeButton
								.setMaximumContentSize(closeButton.getContentWidth(), closeButton.getContentHeight());
					}
				}

				// MINIMIZE BUTTON
				{
					final UIButton minimizeButton = titleBar.getMinimizeButton();
					if (minimizeButton != null) {
						minimizeButton.setButtonText("");
						minimizeButton.setButtonIcon(new SubTex(sharedTex, 113, 76, 16, 16));
						minimizeButton.getPressedState().setIcon(new SubTex(sharedTex, 113, 94, 16, 16));
						for (final UIState state : minimizeButton.getStates()) {
							state.setBackdrop(new EmptyBackdrop());
							state.setBorder(new EmptyBorder());
							state.setPadding(new Insets(0, 0, 0, 0));
							state.setMargin(new Insets(1, 1, 1, 1));
						}
						minimizeButton.refreshState();
						minimizeButton.updateMinimumSizeFromContents();
						minimizeButton.compact();
						minimizeButton.setMaximumContentSize(minimizeButton.getContentWidth(), minimizeButton
								.getContentHeight());
					}
				}

				// EXPAND BUTTON
				{
					final UIButton expandButton = titleBar.getExpandButton();
					if (expandButton != null) {
						expandButton.setButtonText("");
						expandButton.setButtonIcon(new SubTex(sharedTex, 132, 76, 16, 16));
						expandButton.getPressedState().setIcon(new SubTex(sharedTex, 132, 94, 16, 16));
						for (final UIState state : expandButton.getStates()) {
							state.setBackdrop(new EmptyBackdrop());
							state.setBorder(new EmptyBorder());
							state.setPadding(new Insets(0, 0, 0, 0));
							state.setMargin(new Insets(1, 1, 1, 1));
						}
						expandButton.refreshState();
						expandButton.updateMinimumSizeFromContents();
						expandButton.compact();
						expandButton.setMaximumContentSize(expandButton.getContentWidth(), expandButton
								.getContentHeight());
					}
				}

				// MINIMIZE BUTTON
				{
					final UIButton helpButton = titleBar.getHelpButton();
					if (helpButton != null) {
						helpButton.setButtonText("");
						helpButton.setButtonIcon(new SubTex(sharedTex, 151, 76, 16, 16));
						helpButton.getPressedState().setIcon(new SubTex(sharedTex, 151, 94, 16, 16));
						for (final UIState state : helpButton.getStates()) {
							state.setBackdrop(new EmptyBackdrop());
							state.setBorder(new EmptyBorder());
							state.setPadding(new Insets(0, 0, 0, 0));
							state.setMargin(new Insets(1, 1, 1, 1));
						}
						helpButton.refreshState();
						helpButton.updateMinimumSizeFromContents();
						helpButton.compact();
						helpButton.setMaximumContentSize(helpButton.getContentWidth(), helpButton.getContentHeight());
					}
				}
			}
		}

		// BASE PANEL
		{
			final UIPanel base = component.getBasePanel();

			base.setMargin(new Insets(0, 0, 0, 0));
			base.setPadding(new Insets(0, 0, 0, 0));

			final UIBorder border = new ImageBorder(
					// left
					new SubTex(sharedTex, 4, 17, 6, 29),
					// right
					new SubTex(sharedTex, 30, 17, 6, 29),
					// top
					new SubTex(sharedTex, 0, 0, 0, 0),
					// bottom
					new SubTex(sharedTex, 10, 46, 20, 7),
					// top left
					null,
					// top right
					null,
					// bottom left
					new SubTex(sharedTex, 4, 46, 6, 7),
					// bottom right
					new SubTex(sharedTex, 30, 46, 6, 7));
			base.setBorder(border);
			final ColorRGBA top = new ColorRGBA(210 / 255f, 210 / 255f, 210 / 255f, 1);
			final ColorRGBA bottom = new ColorRGBA(244 / 255f, 244 / 255f, 244 / 255f, 1);
			final GradientBackdrop grad = new GradientBackdrop(top, top, bottom, bottom);
			base.setBackdrop(grad);
		}

		// STATUS BAR
		{
			final UIFrameStatusBar statusBar = component.getStatusBar();
			// Make sure exists and is attached
			if (statusBar != null && statusBar.getParent() == component.getBasePanel()) {
				statusBar.setLocalComponentHeight(12);
				statusBar.setMaximumContentHeight(statusBar.getContentHeight());

				final UIButton resize = statusBar.getResizeButton();
				if (resize != null && resize.getParent() == statusBar) {
					for (final UIState state : resize.getStates()) {
						state.setBackdrop(new EmptyBackdrop());
						state.setBorder(new EmptyBorder());
						state.setPadding(new Insets(0, 0, 0, 0));
						state.setMargin(new Insets(0, 0, 0, 0));
						state.setForegroundColor(ColorRGBA.GRAY);
					}
					resize.refreshState();
					resize.updateMinimumSizeFromContents();
					resize.setMinimumContentSize(resize.getContentWidth(), resize.getContentHeight());
					resize.setMaximumContentSize(resize.getContentWidth(), resize.getContentHeight());
				}
			}
		}
	}

	@Override
	protected void applyToLabel(final UILabel component) {
		component.getDefaultState().setForegroundColor(ColorRGBA.BLACK);
		component.getDisabledState().setForegroundColor(ColorRGBA.GRAY);
	}

	@Override
	protected void applyToTextField(final UITextField component) {

		final SolidBorder border = new SolidBorder(1, 1, 1, 1);
		border.setLeftColor(ColorRGBA.GRAY);
		border.setTopColor(ColorRGBA.GRAY);
		border.setRightColor(ColorRGBA.LIGHT_GRAY);
		border.setBottomColor(ColorRGBA.LIGHT_GRAY);

		final SolidBackdrop backdrop = new SolidBackdrop(ColorRGBA.WHITE);

		component.setPadding(new Insets(1, 1, 1, 1));

		for (final UIState state : component.getStates()) {
			state.setBorder(border);
			state.setBackdrop(backdrop);
			if (state.equals(component.getDisabledState())) {
				state.setForegroundColor(ColorRGBA.GRAY);
			} else {
				state.setForegroundColor(ColorRGBA.BLACK);
			}
		}

	}

	@Override
	protected void applyToPasswordField(final UIPasswordField component) {
		applyToTextField(component);
	}

	@Override
	protected void applyToTextArea(final UITextArea component) {

		final SolidBorder border = new SolidBorder(1, 1, 1, 1);
		border.setLeftColor(ColorRGBA.GRAY);
		border.setTopColor(ColorRGBA.GRAY);
		border.setRightColor(ColorRGBA.LIGHT_GRAY);
		border.setBottomColor(ColorRGBA.LIGHT_GRAY);

		final SolidBackdrop backdrop = new SolidBackdrop(ColorRGBA.WHITE);

		component.setPadding(new Insets(1, 1, 1, 1));

		for (final UIState state : component.getStates()) {
			state.setBorder(border);
			state.setBackdrop(backdrop);
			if (state.equals(component.getDisabledState())) {
				state.setForegroundColor(ColorRGBA.GRAY);
			} else {
				state.setForegroundColor(ColorRGBA.BLACK);
			}
		}

	}

	@Override
	protected void applyToPanel(final UIPanel component) {
		// nothing to do
	}

	@Override
	protected void applyToProgressBar(final UIProgressBar component) {
		final ColorRGBA top = new ColorRGBA(235 / 255f, 235 / 255f, 235 / 255f, 1);
		final ColorRGBA bottom = new ColorRGBA(200 / 255f, 200 / 255f, 200 / 255f, 1);
		final GradientBackdrop mainBack = new GradientBackdrop(top, top, bottom, bottom);
		component.getMainPanel().setBackdrop(mainBack);
		component.getMainPanel().setBorder(new EmptyBorder(0, 0, 0, 0));

		final ImageBackdrop barBack = new ImageBackdrop(new SubTex(sharedTex, 11, 59, 22, 15));
		component.getBar().setBackdrop(barBack);
	}

	@Override
	protected void applyToRadioButton(final UIRadioButton component) {

		component.setMargin(new Insets(1, 1, 1, 1));
		component.setPadding(new Insets(1, 1, 1, 1));
		component.setBorder(new EmptyBorder());
		component.setBackdrop(new EmptyBackdrop());
		component.setAlignment(Alignment.LEFT);
		component.setGap(4);

		// DEFAULT
		{
			component.getDefaultState().setForegroundColor(ColorRGBA.BLACK);
			component.getDefaultState().setIcon(new SubTex(sharedTex, 94, 42, 14, 14));
		}
		// DISABLED
		{
			component.getDisabledState().setForegroundColor(ColorRGBA.GRAY);
			component.getDisabledState().setIcon(new SubTex(sharedTex, 132, 42, 14, 14));
		}
		// MOUSEOVER
		{
			component.getMouseOverState().setForegroundColor(ColorRGBA.BLACK);
			component.getMouseOverState().setIcon(new SubTex(sharedTex, 113, 42, 14, 14));
		}
		// SELECTED
		{
			component.getSelectedState().setForegroundColor(ColorRGBA.BLACK);
			component.getSelectedState().setIcon(new SubTex(sharedTex, 94, 59, 14, 14));
		}
		// MOUSEOVER SELECTED
		{
			component.getMouseOverSelectedState().setForegroundColor(ColorRGBA.BLACK);
			component.getMouseOverSelectedState().setIcon(new SubTex(sharedTex, 113, 59, 14, 14));
		}
		// DISABLED SELECTED
		{
			component.getDisabledSelectedState().setForegroundColor(ColorRGBA.GRAY);
			component.getDisabledSelectedState().setIcon(new SubTex(sharedTex, 132, 59, 14, 14));
		}
	}

	@Override
	protected void applyToTooltip(final UITooltip component) {
		component.setBackdrop(new SolidBackdrop(ColorRGBA.LIGHT_GRAY));
		component.setBorder(new SolidBorder(1, 1, 1, 1));
		component.setForegroundColor(ColorRGBA.BLACK);
		component.setOpacity(1.0f);
	}

	@Override
	protected void applyToSlider(final UISlider component) {
		final UISliderKnob knob = component.getKnob();
		knob.setBackdrop(null);
		knob.setPadding(new Insets(0, 0, 0, 0));

		if (component.getOrientation() == Orientation.Horizontal) {
			knob.getKnobLabel().setIcon(new SubTex(sharedTex, 42, 80, 16, 14));
			knob.setMargin(new Insets(0, 1, 0, 1));
		} else {
			knob.getKnobLabel().setIcon(new SubTex(sharedTex, 69, 72, 14, 16));
			knob.setMargin(new Insets(1, 0, 1, 0));
		}

		final UIPanel back = component.getBackPanel();
		if (component.getOrientation() == Orientation.Horizontal) {
			final UIBorder border = new ImageBorder(
					// left
					new SubTex(sharedTex, 7, 85, 4, 7),
					// right
					new SubTex(sharedTex, 33, 85, 4, 7),
					// top
					new SubTex(sharedTex, 11, 79, 22, 6),
					// bottom
					new SubTex(sharedTex, 11, 92, 22, 4),
					// top left
					new SubTex(sharedTex, 7, 79, 4, 6),
					// top right
					new SubTex(sharedTex, 33, 79, 4, 6),
					// bottom left
					new SubTex(sharedTex, 7, 92, 4, 4),
					// bottom right
					new SubTex(sharedTex, 33, 92, 4, 4));
			back.setBorder(border);
			back.setMinimumContentSize(1, 7);
		} else {
			final UIBorder border = new ImageBorder(
					// left
					new SubTex(sharedTex, 67, 97, 5, 19),
					// right
					new SubTex(sharedTex, 80, 97, 5, 19),
					// top
					new SubTex(sharedTex, 72, 91, 8, 6),
					// bottom
					new SubTex(sharedTex, 72, 117, 8, 4),
					// top left
					new SubTex(sharedTex, 67, 91, 5, 6),
					// top right
					new SubTex(sharedTex, 80, 91, 5, 6),
					// bottom left
					new SubTex(sharedTex, 67, 117, 5, 4),
					// bottom right
					new SubTex(sharedTex, 80, 117, 5, 4));
			back.setBorder(border);
			back.setMinimumContentSize(8, 1);
		}
		back.setLayout(null);
		back.setBackdrop(new SolidBackdrop(ColorRGBA.WHITE));
	}

	@Override
	protected void applyToPopupMenu(final UIPopupMenu component) {
		component.getTitleBar().removeFromParent();
		component.getStatusBar().removeFromParent();
		applyToFrame(component);
	}

	@Override
	protected void applyToComboBox(final UIComboBox component) {
		final ColorRGBA upTop = new ColorRGBA(235 / 255f, 235 / 255f, 235 / 255f, 1);
		final ColorRGBA upBottom = new ColorRGBA(200 / 255f, 200 / 255f, 200 / 255f, 1);
		final GradientBackdrop upBack = new GradientBackdrop(upTop, upTop, upBottom, upBottom);

		// value label
		{
			final UIBorder labelBorder = new ImageBorder(
					// left
					new SubTex(sharedTex, 155, 11, 4, 10),
					// right
					new SubTex(sharedTex, 185, 11, 4, 10),
					// top
					new SubTex(sharedTex, 159, 7, 16, 4),
					// bottom
					new SubTex(sharedTex, 159, 21, 16, 4),
					// top left
					new SubTex(sharedTex, 155, 7, 4, 4),
					// top right
					new SubTex(sharedTex, 177, 7, 1, 4),
					// bottom left
					new SubTex(sharedTex, 155, 21, 4, 4),
					// bottom right
					new SubTex(sharedTex, 177, 21, 1, 4));

			final UILabel label = component.getValueLabel();
			label.setBackdrop(upBack);
			label.setBorder(labelBorder);
			label.setAlignment(Alignment.LEFT);
			label.setPadding(new Insets(0, 2, 0, 2));
		}

		// drop down button
		{
			final UIBorder buttonBorder = new ImageBorder(
					// left
					new SubTex(sharedTex, 177, 11, 1, 10),
					// right
					new SubTex(sharedTex, 185, 11, 4, 10),
					// top
					new SubTex(sharedTex, 178, 7, 7, 4),
					// bottom
					new SubTex(sharedTex, 178, 21, 7, 4),
					// top left
					new SubTex(sharedTex, 177, 7, 1, 4),
					// top right
					new SubTex(sharedTex, 185, 7, 4, 4),
					// bottom left
					new SubTex(sharedTex, 177, 21, 1, 4),
					// bottom right
					new SubTex(sharedTex, 185, 21, 4, 4));

			final UIButton button = component.getOpenButton();
			button.setButtonText("");
			button.setButtonIcon(new SubTex(sharedTex, 196, 12, 10, 9));
			button.getMouseOverState().setIcon(new SubTex(sharedTex, 210, 12, 10, 9));
			button.setBorder(buttonBorder);
			button.setBackdrop(upBack);
			button.setMargin(new Insets(0, 0, 0, 0));
			button.setPadding(new Insets(0, 1, 0, 1));
			for (final UIState state : button.getStates()) {
				state.setBorder(buttonBorder);
				state.setBackdrop(upBack);
			}
		}

		// skin for menuitems
		{
			final EmptyBorder itemBorder = new EmptyBorder();
			final EmptyBackdrop itemBackdrop = new EmptyBackdrop();
			final SolidBackdrop overBackdrop = new SolidBackdrop(new ColorRGBA(50 / 255f, 50 / 255f, 200 / 255f, 1));
			component.setItemSkinCallback(new SkinningTask() {
				@Override
				public void skinComponent(final UIComponent c) {
					c.setBorder(itemBorder);
					c.setBackdrop(itemBackdrop);
					c.setMargin(new Insets(0, 0, 0, 0));
					c.setPadding(new Insets(0, 2, 0, 2));
					c.setForegroundColor(ColorRGBA.BLACK);
					if (c instanceof UIButton) {
						final UIButton button = (UIButton) c;
						button.setAlignment(Alignment.LEFT);
						for (final UIState state : button.getStates()) {
							state.setBorder(null);
							state.setBackdrop(itemBackdrop);
							state.setForegroundColor(ColorRGBA.BLACK);
						}
						final LabelState over = button.getMouseOverState();
						over.setForegroundColor(ColorRGBA.WHITE);
						over.setBackdrop(overBackdrop);
					}
				}
			});
		}
	}

	@Override
	protected void applyToScrollBar(final UIScrollBar component) {
		final SolidBorder border = new SolidBorder(1, 1, 1, 1);
		border.setColor(new ColorRGBA(165 / 255f, 165 / 255f, 165 / 255f, 1f));
		component.setMargin(new Insets());
		component.setPadding(new Insets());
		component.setBorder(border);
		{
			final UIButton button = component.getBtTopLeft();
			button.setBackdrop(null);
			button.setBorder(new EmptyBorder());
			button.setPadding(new Insets(0, 0, 0, 0));
			button.setMargin(new Insets(0, 0, 0, 0));
			for (final UIState state : button.getStates()) {
				state.setBorder(null);
				state.setBackdrop(null);
			}
			button.setButtonText("");
			if (component.getOrientation() == Orientation.Horizontal) {
				button.setButtonIcon(new SubTex(sharedTex, 130, 121, 16, 15));
				button.getMouseOverState().setIcon(new SubTex(sharedTex, 130, 137, 16, 15));
			} else {
				button.setButtonIcon(new SubTex(sharedTex, 97, 120, 15, 16));
				button.getMouseOverState().setIcon(new SubTex(sharedTex, 113, 120, 15, 16));
			}
		}
		{
			final UIButton button = component.getBtBottomRight();
			button.setBackdrop(null);
			button.setBorder(new EmptyBorder());
			button.setPadding(new Insets(0, 0, 0, 0));
			button.setMargin(new Insets(0, 0, 0, 0));
			for (final UIState state : button.getStates()) {
				state.setBorder(null);
				state.setBackdrop(null);
			}
			button.setButtonText("");
			if (component.getOrientation() == Orientation.Horizontal) {
				button.setButtonIcon(new SubTex(sharedTex, 147, 121, 16, 15));
				button.getMouseOverState().setIcon(new SubTex(sharedTex, 147, 137, 16, 15));
			} else {
				button.setButtonIcon(new SubTex(sharedTex, 97, 137, 15, 16));
				button.getMouseOverState().setIcon(new SubTex(sharedTex, 113, 137, 15, 16));
			}
		}
		{
			final UISlider slider = component.getSlider();
			slider.getBackPanel().setBorder(new EmptyBorder());
			slider.setMargin(new Insets());
			slider.setPadding(new Insets());
			slider.getBackPanel().setLayout(new RowLayout(false));

			final UISliderKnob knob = slider.getKnob();
			knob.getKnobLabel().setIcon(null);
			knob.setPadding(new Insets(0, 0, 0, 0));
			knob.setMargin(new Insets());
			final ColorRGBA colorTop = new ColorRGBA(235 / 255f, 235 / 255f, 235 / 255f, 1);
			final ColorRGBA colorBtm = new ColorRGBA(200 / 255f, 200 / 255f, 200 / 255f, 1);
			final GradientBackdrop knobColor = new GradientBackdrop(colorTop, colorTop, colorBtm, colorBtm);
			knob.getKnobLabel().setBackdrop(knobColor);
			knob.getKnobLabel().setBorder(border);
		}
	}
}
