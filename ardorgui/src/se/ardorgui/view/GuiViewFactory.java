package se.ardorgui.view;

import com.ardor3d.image.Texture;
import com.ardor3d.math.ColorRGBA;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.ui.text.BMFont;
import com.ardor3d.ui.text.BMText;
import se.ardorgui.view.views.ButtonView;
import se.ardorgui.view.views.ComponentContainerView;
import se.ardorgui.view.views.CursorView;
import se.ardorgui.view.views.LabelView;
import se.ardorgui.view.views.PanelView;
import se.ardorgui.view.views.PictureView;
import se.ardorgui.view.views.ProgressCircleView;
import se.ardorgui.view.views.ProgressQuadView;
import se.ardorgui.view.views.RttView;
import se.ardortech.math.Rectangle;

import java.awt.Insets;

public interface GuiViewFactory {
	LabelView createLabelView(final String textString, final ColorRGBA color, final BMFont font, final float fontSize, final BMText.Align textAlignment);
	ProgressQuadView createProgressBarView(final int width, final int height, final float fillPercent, final Texture texture, final Rectangle visibleRect);
	ProgressCircleView createProgressCircleView(final int width, final int height, final float fillPercent, final Texture texture);
	PanelView createPanelView(final int width, final int height, final Insets insets, final Texture texture);
	CursorView createCursorView(final int width, final int height, ColorRGBA color);
	PictureView createPictureView(final Texture texture, final int width, final int height);
	RttView createRttView(final Texture texture, final int width, final int height);
	ButtonView createButtonView(final int width, final int height, final Insets insets, final Texture textureUp, final Texture textureDown, final Texture textureOver);
	ComponentContainerView createComponentContainerView();
	ComponentContainerView createComponentViewNode(final Node node);
	Texture getTexture(final String fileName);
}