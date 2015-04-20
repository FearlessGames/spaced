package se.spaced.server.persistence.dao.impl.hibernate;

import se.spaced.server.persistence.DBService;
import se.spaced.server.persistence.DBSettings;

public class H2MemDBSettings implements DBSettings {

	@Override
	public String getDialect() {
		return "org.hibernate.dialect.H2Dialect";
	}

	@Override
	public String getDriver() {
		return "org.h2.Driver";
	}

	@Override
	public String getJdbcUrl() {
		return "jdbc:h2:mem:spaced_test_db";
	}

	@Override
	public String getUsername() {
		return "";
	}

	@Override
	public String getPassword() {
		return "";
	}

	@Override
	public void startDatabase() {
	}

	@Override
	public void shutdownDatabase() {
	}

	@Override
	public void setDbService(DBService dbService) {
	}

	@Override
	public void setDialect(String dialect) {
	}

	@Override
	public void setDriver(String driver) {
	}

	@Override
	public void setJdbcUrl(String jdbcUrl) {
	}

	@Override
	public void setUsername(String username) {
	}

	@Override
	public void setPassword(String password) {
	}
}
