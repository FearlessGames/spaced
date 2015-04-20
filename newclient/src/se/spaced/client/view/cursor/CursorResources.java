package se.spaced.client.view.cursor;

import com.google.common.collect.ImmutableMap;

import java.util.Map;
import java.util.Set;

public class CursorResources {
	private static final Map<Cursor, String> CURSORS = ImmutableMap.of(
			Cursor.ATTACK, "textures/gui/cursors/cursor_select_hostile.png",
			Cursor.DEFAULT, "textures/gui/cursors/cursor.png",
			Cursor.HOVER, "textures/gui/cursors/cursor_select_friendly.png"
	);

	private CursorResources() {
	}
	
	public static Set<Map.Entry<Cursor, String>> cursors() {
		return CURSORS.entrySet();
	}
}
