package be.christophebernard.thermostat.database;

import be.christophebernard.thermostat.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionManager {
	private static Connection connection;
	private static final Logger logger;

	static {
		logger = LoggerFactory.getLogger(ConnectionManager.class);
	}

	public static Connection getConnection() throws SQLException {
		if (!isConnectionValid()) {
			connection = DriverManager.getConnection(
					Configuration.getInstance().get("databaseUrl"),
					Configuration.getInstance().get("databaseUsername"),
					Configuration.getInstance().get("databasePassword")
			);
		}

		return connection;
	}

	private static boolean isConnectionValid() {
		try {
			return connection != null && connection.isValid(0);
		} catch (SQLException e) {
			logger.error("Error while checking if connection is valid", e);
			return false;
		}
	}
}
