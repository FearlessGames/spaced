package se.fearless.bender.commands;

import org.simpleframework.http.Request;

public interface Command {
	void execute(Request request);
}
