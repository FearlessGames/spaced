package se.spaced.shared.util.guice.dependencytool;

import com.google.common.collect.Sets;
import com.google.inject.Key;
import com.google.inject.spi.BindingTargetVisitor;
import com.google.inject.spi.ConstructorBinding;
import com.google.inject.spi.ConvertedConstantBinding;
import com.google.inject.spi.Dependency;
import com.google.inject.spi.ExposedBinding;
import com.google.inject.spi.HasDependencies;
import com.google.inject.spi.InstanceBinding;
import com.google.inject.spi.LinkedKeyBinding;
import com.google.inject.spi.ProviderBinding;
import com.google.inject.spi.ProviderInstanceBinding;
import com.google.inject.spi.ProviderKeyBinding;
import com.google.inject.spi.UntargettedBinding;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class GraphingVisitor implements BindingTargetVisitor<Object, Void> {


	final Map<Key<?>, Collection<Key<?>>> dependencies = new HashMap<Key<?>, Collection<Key<?>>>();

	private Collection<Key<?>> visitHasDependencies(HasDependencies hasDependencies) {
		Set<Key<?>> dependencies = Sets.newHashSet();

		for (Dependency<?> dependency : hasDependencies.getDependencies()) {
			dependencies.add(dependency.getKey());
		}

		return dependencies;
	}

	@Override
	public Void visit(InstanceBinding<?> instanceBinding) {
		dependencies.put(instanceBinding.getKey(), visitHasDependencies(instanceBinding));
		return null;
	}

	@Override
	public Void visit(ProviderInstanceBinding<?> providerInstanceBinding) {
		dependencies.put(providerInstanceBinding.getKey(), visitHasDependencies(providerInstanceBinding));
		return null;
	}

	@Override
	public Void visit(ProviderKeyBinding<?> providerKeyBinding) {
		Collection<Key<?>> set = new HashSet<Key<?>>();
		set.add(providerKeyBinding.getProviderKey());
		dependencies.put(providerKeyBinding.getKey(), set);
		return null;
	}

	@Override
	public Void visit(LinkedKeyBinding<?> linkedKeyBinding) {
		Collection<Key<?>> set = new HashSet<Key<?>>();
		set.add(linkedKeyBinding.getLinkedKey());
		dependencies.put(linkedKeyBinding.getKey(), set);
		return null;
	}

	@Override
	public Void visit(ExposedBinding<?> exposedBinding) {
		dependencies.put(exposedBinding.getKey(), visitHasDependencies(exposedBinding));
		return null;
	}

	@Override
	public Void visit(UntargettedBinding<?> untargettedBinding) {
		return null;
	}

	@Override
	public Void visit(ConstructorBinding<?> constructorBinding) {
		dependencies.put(constructorBinding.getKey(), visitHasDependencies(constructorBinding));
		return null;
	}

	@Override
	public Void visit(ConvertedConstantBinding<?> convertedConstantBinding) {
		dependencies.put(convertedConstantBinding.getKey(), visitHasDependencies(convertedConstantBinding));
		return null;
	}

	@Override
	public Void visit(ProviderBinding<?> providerBinding) {
		Collection<Key<?>> set = new HashSet<Key<?>>();
		set.add(providerBinding.getProvidedKey());
		dependencies.put(providerBinding.getKey(), set);
		return null;
	}
}
