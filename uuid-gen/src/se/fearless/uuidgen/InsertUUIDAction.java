package se.fearless.uuidgen;

import com.intellij.openapi.editor.actionSystem.EditorAction;
import se.spaced.shared.util.SystemTimeProvider;
import se.spaced.shared.util.uuid.UUIDFactoryImpl;

import java.util.Random;

public class InsertUUIDAction extends EditorAction {
	protected InsertUUIDAction() {
		super(new InsertUUIDActionHandler(new UUIDFactoryImpl(new SystemTimeProvider(), new Random())));
	}
}
