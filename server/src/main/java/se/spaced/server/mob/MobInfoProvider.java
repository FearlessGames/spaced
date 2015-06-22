package se.spaced.server.mob;

import se.ardortech.math.SpacedVector3;
import se.krka.kahlua.integration.annotations.LuaMethod;
import se.spaced.server.model.ServerEntity;
import se.spaced.server.model.spell.ServerSpell;
import se.spaced.shared.util.math.interval.IntervalInt;

public class MobInfoProvider {

	@LuaMethod(name = "IsInRangeForSpell", global = true)
	public SpellRange isInRangeForSpell(ServerEntity performer, ServerEntity target, ServerSpell spell) {
		double dSq = SpacedVector3.distanceSq(performer.getPosition(), target.getPosition());
		IntervalInt ranges = spell.getRanges();
		int start = ranges.getStart();
		if (dSq < start * start) {
			return SpellRange.TOO_CLOSE;
		}
		int end = ranges.getEnd();
		if (dSq > end * end) {
			return SpellRange.TOO_FAR_AWAY;
		}
		return SpellRange.IN_RANGE;
	}
}
