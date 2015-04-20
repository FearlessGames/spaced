package se.spaced.server;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.name.Names;
import se.spaced.server.net.listeners.auth.AuthenticatorService;
import se.spaced.server.net.listeners.auth.FakeAuthenticatorService;
import se.spaced.server.net.listeners.auth.FameAuthenticatorService;
import se.spaced.server.persistence.DBSettings;
import se.spaced.server.persistence.EmptyDBService;
import se.spaced.server.persistence.H2Service;

public class CommandLineParser {
	private Class<? extends AuthenticatorService> authenticatorServiceClass = FakeAuthenticatorService.class;
	private DBSettings dbSettings = new DBSettingsImpl(new H2Service());
	private int gameServerPort = 9234;
	private int webServicePort = 9000;

	public CommandLineParser(DBSettings dbSettings) {
		this.dbSettings = dbSettings;
	}

	public CommandLineParser() {
	}

	public void parse(String[] args) {
		if (args == null) {
			return;
		}

		for (String arg : args) {
			if (arg.equals("--auth=FAME")) {
				authenticatorServiceClass = FameAuthenticatorService.class;

			} else if (arg.startsWith("--dbDriver=")) {
				dbSettings.setDriver(getValue(arg));

			} else if (arg.startsWith("--dbDialect=")) {
				dbSettings.setDialect(getValue(arg));

			} else if (arg.startsWith("--dbJdbcUrl=")) {
				dbSettings.setJdbcUrl(getValue(arg));

			} else if (arg.startsWith("--dbUser=")) {
				dbSettings.setUsername(getValue(arg));

			} else if (arg.startsWith("--dbPassword=")) {
				dbSettings.setPassword(getValue(arg));

			} else if (arg.equals("--dbMYSQL")) {
				dbSettings.setDbService(new EmptyDBService());
				dbSettings.setDialect("org.hibernate.dialect.MySQL5Dialect");
				dbSettings.setDriver("com.mysql.jdbc.Driver");
				dbSettings.setJdbcUrl("jdbc:mysql://localhost/spaceddb?autoReconnect=true");
				dbSettings.setUsername("root");
				dbSettings.setPassword("");

			} else if (arg.startsWith("--gameServerPort=")) {
				String port = getValue(arg);
				gameServerPort = Integer.parseInt(port);

			} else if (arg.startsWith("--webServicePort=")) {
				String port = getValue(arg);
				webServicePort = Integer.parseInt(port);
			}
		}
	}

	private String getValue(String arg) {
		return arg.substring(arg.indexOf("=") + 1);
	}

	public AbstractModule getModule() {
		return new AbstractModule() {
			@Override
			protected void configure() {
				bind(AuthenticatorService.class).to(authenticatorServiceClass).in(Singleton.class);
				bind(se.spaced.server.persistence.DBSettings.class).toInstance(dbSettings);
				bindConstant().annotatedWith(Names.named("gameServerPort")).to(gameServerPort);
				bind(String.class).annotatedWith(Names.named("baseWebServiceUrl")).toInstance("http://0.0.0.0:" + webServicePort + "/");
				bind(int.class).annotatedWith(Names.named("webServicePort")).toInstance(webServicePort);
			}
		};
	}


}
