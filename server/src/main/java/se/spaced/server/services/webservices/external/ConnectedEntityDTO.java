package se.spaced.server.services.webservices.external;

import java.util.Date;

public class ConnectedEntityDTO {
	private EntityTemplateDTO entityTemplate;
	private Date connectedAt;

	public ConnectedEntityDTO() {
	}

	public EntityTemplateDTO getEntityTemplate() {
		return entityTemplate;
	}

	public void setEntityTemplate(EntityTemplateDTO entityTemplate) {
		this.entityTemplate = entityTemplate;
	}

	public Date getConnectedAt() {
		return connectedAt;
	}

	public void setConnectedAt(Date connectedAt) {
		this.connectedAt = connectedAt;
	}
}
