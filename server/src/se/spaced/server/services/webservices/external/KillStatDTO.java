package se.spaced.server.services.webservices.external;

public class KillStatDTO {
	private String entityPk;
	private String entityName;
	private long killCount;


	public KillStatDTO() {
	}

	public String getEntityPk() {
		return entityPk;
	}

	public void setEntityPk(String entityPk) {
		this.entityPk = entityPk;
	}

	public String getEntityName() {
		return entityName;
	}

	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}

	public long getKillCount() {
		return killCount;
	}

	public void setKillCount(long killCount) {
		this.killCount = killCount;
	}
}
