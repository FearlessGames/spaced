package se.spaced.server.services.webservices.external;

import java.util.Date;

public class SpellActionEntryDTO {

	private String performerTemplatePk;

	private String performerTemplateName;

	private String targetTemplatePk;

	private String targetTemplateName;

	private String spellName;

	private String spellPk;

	private Date startTime;

	private Date endTime;

	private boolean completed;


	public String getPerformerTemplatePk() {
		return performerTemplatePk;
	}

	public void setPerformerTemplatePk(String performerTemplatePk) {
		this.performerTemplatePk = performerTemplatePk;
	}

	public String getPerformerTemplateName() {
		return performerTemplateName;
	}

	public void setPerformerTemplateName(String performerTemplateName) {
		this.performerTemplateName = performerTemplateName;
	}

	public String getTargetTemplatePk() {
		return targetTemplatePk;
	}

	public void setTargetTemplatePk(String targetTemplatePk) {
		this.targetTemplatePk = targetTemplatePk;
	}

	public String getTargetTemplateName() {
		return targetTemplateName;
	}

	public void setTargetTemplateName(String targetTemplateName) {
		this.targetTemplateName = targetTemplateName;
	}

	public String getSpellName() {
		return spellName;
	}

	public void setSpellName(String spellName) {
		this.spellName = spellName;
	}

	public String getSpellPk() {
		return spellPk;
	}

	public void setSpellPk(String spellPk) {
		this.spellPk = spellPk;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
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
