package se.spaced.server.model.currency;

import com.google.common.collect.Maps;
import se.spaced.server.persistence.dao.impl.OwnedPersistableBase;
import se.spaced.server.persistence.dao.interfaces.Persistable;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.Collection;
import java.util.Map;

@Entity
public class Wallet extends OwnedPersistableBase {

	@OneToMany(cascade = CascadeType.ALL)
	private final Map<PersistedCurrency, WalletCompartment> compartments = Maps.newConcurrentMap();

	Wallet() {
	}

	public Wallet(Persistable owner) {
		super(owner);
	}

	public WalletCompartment getCompartment(PersistedCurrency currency) {
		WalletCompartment compartment = compartments.get(currency);
		if (compartment == null) {
			compartment = new WalletCompartment(new PersistedMoney(currency, 0L));
			compartments.put(currency, compartment);
		}
		return compartment;
	}

	public Collection<WalletCompartment> getCompartments() {
		return compartments.values();
	}

	public void addCompartment(WalletCompartment compartment) {
		compartments.put(compartment.getMoney().getCurrency(), compartment);
	}
}
