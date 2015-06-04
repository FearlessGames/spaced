package se.spaced.shared.util;

import com.google.inject.AbstractModule;
import se.mockachino.*;

import java.lang.annotation.Annotation;

public abstract class AbstractMockModule extends AbstractModule {
	protected <T> void bindMock(Class<T> clazz) {
		bind(clazz).toInstance(Mockachino.mock(clazz));
	}

	protected <T> void bindMock(Class<T> clazz, Annotation annotation) {
		bind(clazz).annotatedWith(annotation).toInstance(Mockachino.mock(clazz));
	}

}
