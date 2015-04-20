package se.spaced.server.services.webservices.external;

public class EntityTemplateDTO {
	private String templatePk;
	private String templateName;

	public EntityTemplateDTO() {
	}

	public String getTemplatePk() {
		return templatePk;
	}

	public void setTemplatePk(String templatePk) {
		this.templatePk = templatePk;
	}

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}
}
