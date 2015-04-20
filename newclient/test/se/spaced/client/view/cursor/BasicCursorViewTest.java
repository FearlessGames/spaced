package se.spaced.client.view.cursor;

import com.ardor3d.input.MouseCursor;
import com.ardor3d.input.MouseManager;
import com.google.common.collect.Maps;
import org.junit.Before;
import org.junit.Test;
import se.spaced.client.model.Relation;
import se.spaced.client.model.player.TargetInfo;

import java.util.Map;

import static se.mockachino.Mockachino.*;


public class BasicCursorViewTest {
	private final Map<Cursor, MouseCursor> map = Maps.newHashMap();
	private MouseManager mouseManager;
	private CursorView cursorView;

	@Before
	public void setUp() throws Exception {
		mouseManager = mock(MouseManager.class);
		cursorView = new BasicCursorView(mouseManager, map);
	}

	@Test
	public void resetsCursor() {
		MouseCursor mc = mock(MouseCursor.class);
		map.put(Cursor.DEFAULT, mc);

		cursorView.newHover(null);

		verifyOnce().on(mouseManager).setCursor(mc);
	}

	@Test
	public void shouldSetAttackCursor() {
		MouseCursor mc = mock(MouseCursor.class);
		map.put(Cursor.ATTACK, mc);

		cursorView.newHover(new TargetInfo(null, Relation.HOSTILE, false, false));

		verifyOnce().on(mouseManager).setCursor(mc);
	}

	@Test
	public void shouldSetHoverCursor() {
		MouseCursor mc = mock(MouseCursor.class);
		map.put(Cursor.HOVER, mc);

		cursorView.newHover(new TargetInfo(null, Relation.FRIENDLY, false, false));

		verifyOnce().on(mouseManager).setCursor(mc);
	}

	@Test
	public void ignoresAlreadySetCursor() {
		MouseCursor mc = mock(MouseCursor.class);
		map.put(Cursor.DEFAULT, mc);

		cursorView.newHover(null);
		cursorView.newHover(null);

		verifyExactly(1).on(mouseManager).setCursor(mc);
	}
}
