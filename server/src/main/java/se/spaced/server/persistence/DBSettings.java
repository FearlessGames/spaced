package se.spaced.server.persistence;

public interface DBSettings {

	String getDialect();

	String getDriver();

	String getJdbcUrl();

	String getUsername();

	String getPassword();

	void startDatabase();

	void shutdownDatabase();

	void setDbService(DBService dbService);

	void setDialect(String dialect);

	void setDriver(String driver);

	void setJdbcUrl(String jdbcUrl);

	void setUsername(String username);

	void setPassword(String password);
}
