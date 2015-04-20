package se.spaced.server.model.cooldown;

import com.thoughtworks.xstream.annotations.XStreamImplicit;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import se.spaced.server.persistence.dao.impl.PersistableBase;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import java.util.ArrayList;
import java.util.Collection;

@Entity
public class CooldownSetTemplate extends PersistableBase {

	@ManyToMany(targetEntity = CooldownTemplate.class, fetch = FetchType.EAGER)
	@Fetch(FetchMode.SUBSELECT)
	@XStreamImplicit(itemFieldName = "cooldown")
	private final Collection<CooldownTemplate> cooldowns = new ArrayList<CooldownTemplate>();

	public CooldownSetTemplate() {
	}

	public CooldownSetTemplate add(CooldownTemplate cooldownTemplate) {
		cooldowns.add(cooldownTemplate);
		return this;
	}

	public Collection<CooldownTemplate> getCooldownTemplates() {
		return cooldowns;
	}
}
