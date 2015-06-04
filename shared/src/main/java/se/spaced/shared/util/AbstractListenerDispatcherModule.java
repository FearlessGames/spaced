package se.spaced.shared.util;

import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.AbstractMatcher;
import com.google.inject.matcher.Matcher;
import com.google.inject.matcher.Matchers;
import com.google.inject.spi.InjectionListener;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;
import com.google.inject.util.Types;

public abstract class AbstractListenerDispatcherModule extends AbstractModule {
	protected <T> void register(final Class<T> listenerClass) {
		final TypeLiteral<?> typeLiteral = TypeLiteral.get(Types.newParameterizedType(ListenerDispatcher.class, listenerClass));

		bind(typeLiteral).toProvider(new Provider() {
			@Override
			public Object get() {
				return ListenerDispatcher.create(listenerClass);
			}
		}).in(Scopes.SINGLETON);

		bindListener(type(Matchers.subclassesOf(listenerClass)), new TypeListener() {
			@Override
			public <I> void hear(final TypeLiteral<I> literal, final TypeEncounter<I> encounter) {
				final Provider<?> provider = encounter.getProvider(Key.get(typeLiteral.getType()));
				encounter.register(new InjectionListener<I>() {
					@SuppressWarnings("unchecked") // Safe due to I being registered just above
					@Override
					public void afterInjection(final I i) {
						final ListenerDispatcher<I> ld = (ListenerDispatcher<I>) provider.get();
						ld.addListener(i);
					}
				});
			}
		});
	}

	private Matcher<TypeLiteral<?>> type(final Matcher<? super Class<?>> matcher) {
		return new AbstractMatcher<TypeLiteral<?>>() {
			@Override
			public boolean matches(final TypeLiteral<?> literal) {
				return matcher.matches(literal.getRawType());
			}
		};
	}
}
