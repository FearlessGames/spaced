package se.spaced.client.view.cursor;

import com.ardor3d.input.MouseCursor;
import com.ardor3d.input.MouseManager;
import se.spaced.client.model.Relation;
import se.spaced.client.model.player.TargetInfo;

import java.util.Map;

public class BasicCursorView implements CursorView {
	private final MouseManager mouseManager;
	private final Map<Cursor, MouseCursor> cursors;
	private MouseCursor activeCursor;

	public BasicCursorView(MouseManager mouseManager, Map<Cursor, MouseCursor> cursors) {
		this.mouseManager = mouseManager;
		this.cursors = cursors;
	}

	@Override
	public void newHover(final TargetInfo targetInfo) {
		if (targetInfo == null) {
			setCursor(Cursor.DEFAULT);
			return;
		}

		if (targetInfo.getRelation() == Relation.HOSTILE) {
			setCursor(Cursor.ATTACK);
			return;
		}
		setCursor(Cursor.HOVER);
	}

	private void setCursor(final Cursor cursor) {
		MouseCursor mouseCursor = cursors.get(cursor);
		if (mouseCursor.equals(activeCursor)) {
			return;
		}

		activeCursor = mouseCursor;

		mouseManager.setCursor(mouseCursor);
	}
}
