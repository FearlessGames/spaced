package se.spaced.shared.util.guice.dependencytool;

import com.google.inject.Key;
import com.google.inject.grapher.Renderer;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Set;

public class DepRenderer extends AbstractDepRenderer implements Renderer {

	public DepRenderer(GraphingVisitor graphingVisitor, PrintWriter output) {
		super(graphingVisitor, output);
	}

	@Override
	public void render() throws IOException {
		DependencySorter<Key<?>> dependencySorter = new DependencySorter(graphingVisitor.dependencies);
		List<List<VisitResult<Key<?>>>> list = dependencySorter.process();

		int level = 0;
		for (List<VisitResult<Key<?>>> resultsPerLevel : list) {
			output.println("Level: " + level);
			for (VisitResult<Key<?>> keyVisitResult : resultsPerLevel) {
				Set<Key<?>> keyList = keyVisitResult.getElements();
				String indent = "  ";
				if (keyList.size() >= 2) {
					output.println("  WARNING: Cycle of " + keyList.size() + ":");
					indent = "    ";
				}
				for (Key<?> key : keyList) {
					output.println(indent + printKey(key) + getDependencies(key, graphingVisitor.dependencies, keyList));
				}
			}
			level++;
		}
		output.close();
	}

}

