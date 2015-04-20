package se.fearless.uuidgen;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorWriteActionHandler;
import se.spaced.shared.util.uuid.UUIDFactory;

public class InsertUUIDActionHandler extends EditorWriteActionHandler {
	private final UUIDFactory uuidFactory;

	public InsertUUIDActionHandler(UUIDFactory uuidFactory) {
		assert uuidFactory != null;
		this.uuidFactory = uuidFactory;
	}

	@Override
	public void executeWriteAction(Editor editor, DataContext dataContext) {
		if (editor != null) {
			CaretModel caretModel = editor.getCaretModel();
			int position = caretModel.getOffset();
			Document doc = editor.getDocument();
			if (doc.isWritable()) {
				String uuid = uuidFactory.combUUID().toString();
				doc.insertString(position, uuid);
				caretModel.moveCaretRelatively(uuid.length(), 0, false, false, false);
			}
		}
	}
}
