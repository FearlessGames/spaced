package se.spaced.server.stats;


import se.spaced.server.model.spawn.EntityTemplate;
import se.spaced.server.model.spell.ServerSpell;
import se.spaced.server.persistence.dao.impl.PersistableBase;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.util.Date;

@Entity
public class SpellActionEntry extends PersistableBase {
	@ManyToOne
	private final EntityTemplate performer;

	@ManyToOne
	private final EntityTemplate target;

	@ManyToOne
	private final ServerSpell spell;

	private final Date startTime;

	private Date endTime;

	private boolean completed;

	protected SpellActionEntry() {
		this(null, null, null);
	}

	public SpellActionEntry(EntityTemplate performer, EntityTemplate target, ServerSpell spell) {
		this.performer = performer;
		this.target = target;
		this.spell = spell;
		startTime = new Date();
	}

	public EntityTemplate getPerformer() {
		return performer;
	}

	public EntityTemplate getTarget() {
		return target;
	}

	public ServerSpell getSpell() {
		return spell;
	}

	public Date getStartTime() {
		return startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public boolean isCompleted() {
		return completed;
	}

	public void setCompleted(boolean completed) {
		this.completed = completed;
	}
}
