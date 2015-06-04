package se.spaced.shared.util.guice.dependencytool;

import com.google.inject.Injector;
import com.google.inject.grapher.InjectorGrapher;
import com.google.inject.grapher.Renderer;
import com.google.inject.grapher.TransitiveDependencyVisitor;

import java.io.IOException;
import java.io.PrintWriter;

public class DependencyTool {
	private final Injector injector;
	private final PrintWriter output;
	private final PrintWriter dotFile;

	public DependencyTool(Injector injector, PrintWriter output, PrintWriter dotFile) {
		this.injector = injector;
		this.output = output;
		this.dotFile = dotFile;
	}

	public void process() {
		try {
			TransitiveDependencyVisitor keyVisitor = new TransitiveDependencyVisitor();
			GraphingVisitor graphingVisitor = new GraphingVisitor();

			Renderer renderer = new DepRenderer(graphingVisitor, output);
			InjectorGrapher grapher = new InjectorGrapher(keyVisitor, graphingVisitor, renderer);
			grapher.of(injector).graph();

			renderer = new GraphvizDepRenderer(graphingVisitor, dotFile);
			grapher = new InjectorGrapher(keyVisitor, graphingVisitor, renderer);
			grapher.of(injector).graph();

		} catch (IOException e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		}
	}
}
