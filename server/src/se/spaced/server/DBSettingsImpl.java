package se.spaced.server;

import se.spaced.server.persistence.DBService;
import se.spaced.server.persistence.DBSettings;
import se.spaced.server.persistence.H2Service;

public class DBSettingsImpl implements DBSettings {
	private String dialect = "org.hibernate.dialect.H2Dialect";
	private String driver = "org.h2.Driver";
	private String jdbcUrl = H2Service.JDBC_URL;
	private String username = "";
	private String password = "";
	private DBService dbService;

	public DBSettingsImpl(DBService dbService) {
		this.dbService = dbService;
	}

	@Override
	public String getDialect() {
		return dialect;
	}

	@Override
	public String getDriver() {
		return driver;
	}

	@Override
	public String getJdbcUrl() {
		return jdbcUrl;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public void startDatabase() {
		dbService.startDatabase();
	}

	@Override
	public void shutdownDatabase() {
		dbService.shutdownDatabase();
	}

	@Override
	public void setDbService(DBService dbService) {
		this.dbService = dbService;
	}

	@Override
	public void setDialect(String dialect) {
		this.dialect = dialect;
	}


	@Override
	public void setDriver(String driver) {
		this.driver = driver;
	}


	@Override
	public void setJdbcUrl(String jdbcUrl) {
		this.jdbcUrl = jdbcUrl;
	}

	@Override
	public void setUsername(String username) {
		this.username = username;
	}

	@Override
	public void setPassword(String password) {
		this.password = password;
	}
}
