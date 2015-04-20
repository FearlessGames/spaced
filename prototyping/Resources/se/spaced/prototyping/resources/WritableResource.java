package se.spaced.prototyping.resources;

import java.io.OutputStream;

public interface WritableResource extends Resource {
	OutputStream getOutputStream();
}
