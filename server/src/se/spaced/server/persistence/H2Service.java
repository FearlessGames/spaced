package se.spaced.server.persistence;

import org.h2.tools.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class H2Service implements DBService {
	public static final String JDBC_URL = "jdbc:h2:tcp://localhost:8092/spaced_db";
	private final Logger log = LoggerFactory.getLogger(getClass());
	private Server server;

	@Override
	public void startDatabase() {
		try {
			if (!isRunning()) {
				server = Server.createTcpServer("-tcpAllowOthers",
						"-tcpPort",
						"8092",
						"-baseDir",
						getBaseDir()).start();
			}
		} catch (SQLException e) {
			log.error("Failed to start H2 process", e);
			throw new RuntimeException(e);
		}
	}

	private String getBaseDir() {
		return new File(".").getPath();
	}

	@Override
	public void shutdownDatabase() {
		if (server != null) {
			server.shutdown();
			server = null;
		}
	}

	public boolean isRunning() {
		try {
			Class.forName("org.h2.Driver");
			Connection conn = DriverManager.getConnection(JDBC_URL, "", "");
			conn.close();
			return true;
		} catch (SQLException e) {
			return false;
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("h2 lib not found!");
		}
	}
}
