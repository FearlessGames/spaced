package se.spaced.spacedit.ui.presenter.propertyeditor.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Slider {
	int from();

	int start();

	int to();

	float scaleFactor();
}
