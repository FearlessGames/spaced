package se.spaced.shared.model;

import se.fearless.common.uuid.UUID;

public class CreatureType {
	private UUID pk;
	private String name;
	public static final CreatureType NULL_TYPE = new CreatureType(new UUID(0, 0), "NullCreatureType");

	protected CreatureType() {
	}

	public CreatureType(UUID pk, String name) {
		this.pk = pk;
		this.name = name;
	}

	public CreatureType(String name) {
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
		return "CreatureType{" +
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

		CreatureType that = (CreatureType) o;

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
