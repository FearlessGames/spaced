package se.spaced.shared.util.guice.dependencytool;

import com.google.inject.Key;
import com.google.inject.grapher.Renderer;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GraphvizDepRenderer extends AbstractDepRenderer implements Renderer {

	public GraphvizDepRenderer(GraphingVisitor graphingVisitor, PrintWriter output) {
		super(graphingVisitor, output);
	}

	@Override
	public void render() throws IOException {
		output.println("digraph Dep {");

		Map<Key<?>, Collection<Key<?>>> dependencies = graphingVisitor.dependencies;

		for (Key<?> key : dependencies.keySet()) {
			output.println(withQuote(key) + " [shape=box, regular=1,style=filled,fillcolor=white];");
		}

		DependencySorter<Key<?>> dependencySorter = new DependencySorter(dependencies);
		List<List<VisitResult<Key<?>>>> list = dependencySorter.process();

		for (List<VisitResult<Key<?>>> resultsPerLevel : list) {
			for (VisitResult<Key<?>> keyVisitResult : resultsPerLevel) {
				Set<Key<?>> keyList = keyVisitResult.getElements();
				if (keyList.size() >= 2) {
					// TODO: add subgraph stuff
				}
				for (Key<?> key : keyList) {
					Collection<Key<?>> deps = dependencies.get(key);
					for (Key<?> dep : deps) {
						output.println(withQuote(dep) + " -> " + withQuote(key) + "[dir=back];");
					}
				}
			}
		}
		output.println("}");
		output.close();
	}

	private String withQuote(Key<?> key) {
		return "\"" + printKey(key) + "\"";
	}

}