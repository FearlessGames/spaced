package se.spaced.server.services.webservices;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.apache.cxf.transport.http_jetty.JettyHTTPServerEngine;
import org.apache.cxf.transport.http_jetty.JettyHTTPServerEngineFactory;
import se.fearlessgames.common.lifetime.LifetimeListener;
import se.fearlessgames.common.lifetime.LifetimeManager;

import javax.xml.ws.Endpoint;
import java.util.ArrayList;
import java.util.Collection;

public class WebServicePublisherImpl implements WebServicePublisher {
	private final String baseUrl;
	private final Collection<Endpoint> endpoints = new ArrayList<Endpoint>();

	@Inject
	public WebServicePublisherImpl(@Named("baseWebServiceUrl") String baseUrl, @Named("webServicePort") final int port, LifetimeManager lifetimeManager) {
		this.baseUrl = baseUrl;

		lifetimeManager.addListener(new LifetimeListener() {
			@Override
			public void onStart() {
			}

			@Override
			public void onShutdown() {
				for (Endpoint endpoint : endpoints) {
					endpoint.stop();
				}

				JettyHTTPServerEngineFactory factory = new JettyHTTPServerEngineFactory();
				JettyHTTPServerEngine engine = factory.retrieveJettyHTTPServerEngine(port);
				if (engine != null) {
					engine.shutdown();
				}
			}
		});
	}

	@Override
	public void publish(Object impl, String serviceContext) {
		final Endpoint endpoint = Endpoint.publish(baseUrl + serviceContext, impl);
		endpoints.add(endpoint);
	}

}
