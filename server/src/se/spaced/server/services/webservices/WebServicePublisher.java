package se.spaced.server.services.webservices;

public interface WebServicePublisher {

	void publish(Object impl, String serviceContext);

}