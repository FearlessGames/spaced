package se.spaced.contentserver;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;

public class ContentServer {

	public static void main(String[] args) throws Exception {
		if (args.length < 1) {
			System.out.println("Need a resource base path");
			return;
		}

		Server server = new Server(9090);
		ResourceHandler resource_handler = new ResourceHandler();

		resource_handler.setDirectoriesListed(true);
		resource_handler.setWelcomeFiles(new String[]{"index.html"});

		resource_handler.setResourceBase(args[0]);

		HandlerList handlers = new HandlerList();
		handlers.setHandlers(new Handler[]{resource_handler, new DefaultHandler()});
		server.setHandler(handlers);

		server.start();
		server.join();
	}
}