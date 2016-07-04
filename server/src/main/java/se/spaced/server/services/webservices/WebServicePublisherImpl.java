package se.spaced.server.services.webservices;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.apache.cxf.transport.http_jetty.JettyHTTPServerEngine;
import org.apache.cxf.transport.http_jetty.JettyHTTPServerEngineFactory;
import org.slf4j.Logger;
import se.fearless.common.lifetime.LifetimeListener;
import se.fearless.common.lifetime.LifetimeManager;

import javax.xml.ws.Endpoint;
import java.util.ArrayList;
import java.util.Collection;

import static org.slf4j.LoggerFactory.getLogger;

public class WebServicePublisherImpl implements WebServicePublisher {
	private final Logger logger = getLogger(getClass());
	private final String baseUrl;
	private final Collection<Endpoint> endpoints = new ArrayList<Endpoint>();

	@Inject
	public WebServicePublisherImpl(@Named("baseWebServiceUrl") String baseUrl, @Named("webServicePort") final int port, LifetimeManager lifetimeManager) {
		logger.info("Starting webservice on " + baseUrl + ":" + port);
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
