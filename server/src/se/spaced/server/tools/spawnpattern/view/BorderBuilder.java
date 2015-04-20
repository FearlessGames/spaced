package se.spaced.server.tools.spawnpattern.view;

import com.google.inject.Inject;

import javax.swing.BorderFactory;
import javax.swing.border.Border;

public class BorderBuilder {
	private final Border baseStyle;

	@Inject
	public BorderBuilder(Border baseStyle) {
		this.baseStyle = baseStyle;
	}
	
	public Border getTitleBorder(String title) {
		return BorderFactory.createTitledBorder(baseStyle, title);
	}
	
	public Border getPlainBorder() {
		return baseStyle;
	}
}
