package se.spaced.shared.util.guice.dependencytool;

import com.google.inject.Key;
import se.smrt.generator.Util;
import se.smrt.generator.types.Type;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AbstractDepRenderer {
	protected final GraphingVisitor graphingVisitor;
	protected final PrintWriter output;
	private static final Pattern PATTERN = Pattern.compile("^@([^(]*)(.*)$");

	public AbstractDepRenderer(GraphingVisitor graphingVisitor, PrintWriter output) {
		this.graphingVisitor = graphingVisitor;
		this.output = output;
	}

	protected String getDependencies(Key<?> key, Map<Key<?>, Collection<Key<?>>> dependencies, Set<Key<?>> keyList) {
		Set<Key<?>> deps = new HashSet<Key<?>>();
		deps.addAll(dependencies.get(key));
		deps.removeAll(keyList);
		StringBuilder builder = new StringBuilder();
		boolean first = true;
		for (Key<?> dep : deps) {
			if (first) {
				first = false;
				builder.append(" -> ");
			} else {
				builder.append(", ");
			}
			builder.append(printKey(dep));
		}
		return builder.toString();
	}

	protected String printKey(Key<?> key) {
		StringBuilder builder = new StringBuilder();

		if (key.getAnnotation() != null) {
			String annotation = key.getAnnotation().toString();
			Matcher matcher = PATTERN.matcher(annotation);
			if (matcher.matches()) {
				String className = matcher.group(1);
				className = Util.getSimpleName(className);
				String params = matcher.group(2);
				params = params.replace("value=", "");

				builder.append("@" + className + params + " ");
			}
		}


		getPrettyName(key, builder);

		return builder.toString();
	}

	private void getPrettyName(Key<?> key, StringBuilder builder) {
		Type type = new Type(key.getTypeLiteral().toString());
		List<String> imports = new ArrayList<String>();
		addImports(type, imports);
		builder.append(type.getNameWithGenerics("", imports));
	}

	private void addImports(Type type, List<String> imports) {
		imports.add(type.getFullName());
		for (Type type1 : type.getGenericParameters()) {
			addImports(type1, imports);
		}
	}
}
