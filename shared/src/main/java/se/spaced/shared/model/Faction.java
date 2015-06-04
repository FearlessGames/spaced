package se.spaced.shared.model;

import se.fearless.common.uuid.UUID;

public class Faction {
	private UUID pk;
	private String name;
	public static final Faction NULL_TYPE = new Faction(new UUID(0, 0), "NullFaction");

	protected Faction() {
	}

	public Faction(UUID pk, String name) {
		this.pk = pk;
		this.name = name;
	}

	public Faction(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public UUID getPk() {
		return pk;
	}


	@Override
	public String toString() {
		return "Faction{" +
				"name='" + name + '\'' +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		Faction that = (Faction) o;

		if (name != null ? !name.equals(that.name) : that.name != null) {
			return false;
		}
		if (pk != null ? !pk.equals(that.pk) : that.pk != null) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		int result = pk != null ? pk.hashCode() : 0;
		result = 31 * result + (name != null ? name.hashCode() : 0);
		return result;
	}
}