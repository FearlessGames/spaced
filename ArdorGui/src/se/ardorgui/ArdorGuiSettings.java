package se.ardorgui;

import com.ardor3d.ui.text.BMFont;
import com.ardor3d.util.resource.URLResourceSource;
import se.ardortech.math.Rectangle;

import java.awt.Insets;
import java.io.IOException;

public class ArdorGuiSettings {
	private Insets panelInsets;
	private String panelTexture;
	private String panelTextureFilled;
	private String buttonTextureNormal;
	private String buttonTextureOver;
	private String buttonTextureDown;
	private String progressBarTexture;
	private Rectangle progressBarTextureUV;
	private String progressCircleTexture;
	private String cursor;
	private BMFont font;
	private int fontSize;

	public ArdorGuiSettings(Insets panelInsets, String panelTexture, String panelTextureFilled, String buttonTextureNormal, String buttonTextureOver,
			String buttonTextureDown, String progressBarTexture, Rectangle progressBarTextureUV, String progressCircleTexture, String cursor, int fontSize) {
		this.panelInsets = panelInsets;
		this.panelTexture = panelTexture;
		this.panelTextureFilled = panelTextureFilled;
		this.buttonTextureNormal = buttonTextureNormal;
		this.buttonTextureOver = buttonTextureOver;
		this.buttonTextureDown = buttonTextureDown;
		this.progressBarTexture = progressBarTexture;
		this.progressBarTextureUV = progressBarTextureUV;
		this.progressCircleTexture = progressCircleTexture;
		this.cursor = cursor;
		this.fontSize = fontSize;
	}

	public Insets getPanelInsets() {
		return panelInsets;
	}

	public void setPanelInsets(Insets panelInsets) {
		this.panelInsets = panelInsets;
	}

	public String getPanelTexture() {
		return panelTexture;
	}

	public void setPanelTexture(String panelTexture) {
		this.panelTexture = panelTexture;
	}

	public String getPanelTextureFilled() {
		return panelTextureFilled;
	}

	public void setPanelTextureFilled(String panelTextureFilled) {
		this.panelTextureFilled = panelTextureFilled;
	}

	public String getButtonTextureNormal() {
		return buttonTextureNormal;
	}

	public void setButtonTextureNormal(String buttonTextureNormal) {
		this.buttonTextureNormal = buttonTextureNormal;
	}

	public String getButtonTextureOver() {
		return buttonTextureOver;
	}

	public void setButtonTextureOver(String buttonTextureOver) {
		this.buttonTextureOver = buttonTextureOver;
	}

	public String getButtonTextureDown() {
		return buttonTextureDown;
	}

	public void setButtonTextureDown(String buttonTextureDown) {
		this.buttonTextureDown = buttonTextureDown;
	}

	public String getCursorTexture() {
		return cursor;
	}

	public void setCursor(String cursor) {
		this.cursor = cursor;
	}

	public int getFontSize() {
		return fontSize;
	}

	public void setFontSize(int fontSize) {
		this.fontSize = fontSize;
	}

	public String getProgressBarTexture() {
		return progressBarTexture;
	}

	public String getProgressCircleTexture() {
		return progressCircleTexture;
	}

	public void setProgressBarTexture(String progressBarTexture) {
		this.progressBarTexture = progressBarTexture;
	}

	public void setProgressCircleTexture(String progressCircleTexture) {
		this.progressCircleTexture = progressCircleTexture;
	}

	public Rectangle getProgressBarTextureUV() {
		return progressBarTextureUV;
	}

	private BMFont createFont() {
		try {
			return new BMFont(new URLResourceSource(ArdorGuiSettings.class.getClassLoader().getResource("com/ardor3d/ui/text/arial-24-bold-regular.fnt")), true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public BMFont getFont() {
		if (font == null) {
			font = createFont();
		}
		return font;
	}

	public void setFont(BMFont font) {
		this.font = font;
	}

	public void setProgressBarTextureUV(Rectangle progressBarTextureUV) {
		this.progressBarTextureUV = progressBarTextureUV;
	}

	public String getCursor() {
		return cursor;
	}
}