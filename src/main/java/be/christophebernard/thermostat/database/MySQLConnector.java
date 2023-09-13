package be.christophebernard.thermostat.database;

import be.christophebernard.thermostat.Configuration;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MySQLConnector implements IDatabaseConnector {
	private static MySQLConnector instance;
	private final Connection connection;
	private static Logger logger;

	private MySQLConnector(Connection connection) {
		this.connection = connection;
	}

	public static @Nullable MySQLConnector getInstance() {
		if (instance == null) {
			logger = LoggerFactory.getLogger(MySQLConnector.class);

			logger.info("Connecting to MySQL database...");
			try {
				instance = new MySQLConnector(
						DriverManager.getConnection(
								Configuration.getInstance().get("databaseUrl"),
								Configuration.getInstance().get("databaseUsername"),
								Configuration.getInstance().get("databasePassword")
						)
				);

				instance.databaseSet();
			} catch (SQLException exception) {
				logger.error("An error occurred while connecting", exception);
				return null;
			}
		}

		return instance;
	}

	@Override
	public boolean databaseSet() {
		try {
			logger.info("Verifying database tables presence...");

			return connection.createStatement().execute(
			"""
				create table if not exists `bad_word`(
					id int not null auto_increment,
				    guild_id varchar(255) not null,
				    word varchar(255) not null,
				    enabled boolean default true,
				    constraint bad_word_id_pk primary key(id)
				);
				"""
			);
		} catch (SQLException exception) {
			logger.error(exception.getMessage(), exception);
			return false;
		}
	}

	@Override
	public List<String> getBadWords(String guildId) {
		try {
			logger.info("Fetching prevented words for guild id %s...".formatted(guildId));

			PreparedStatement statement = (PreparedStatement) connection.createStatement();
			String query = "select word from `bad_word` where guild_id = ? and enabled = true;";
			statement.setString(1, guildId);

			ResultSet result = statement.executeQuery(query);
			List<String> words = new ArrayList<>();

			while (result.next()) {
				words.add(result.getString("word"));
			}

			return words;
		} catch (SQLException exception) {
			logger.error("An error occurred while fetching prevented words", exception);
			return null;
		}
	}

	@Override
	public boolean addBadWord(String guildId, String word) {
		try {
			logger.info("Preventing word %s to be written in %s's channels...".formatted(word, guildId));

			PreparedStatement statement = (PreparedStatement) connection.createStatement();
			String query = "insert into `bad_word`(guild_id, word) values(?, ?);";

			statement.setString(1, guildId);
			statement.setString(2, word);

			return statement.execute(query);
		} catch (SQLException exception) {
			logger.error("An error occurred while adding the words to the prevented list", exception);
			return false;
		}
	}

	@Override
	public boolean enableBadWord(String guildId, String word) {
		try {
			logger.info("Re-enabling prevention for word `%s` in guild %s...".formatted(word, guildId));

			PreparedStatement statement = (PreparedStatement) connection.createStatement();
			String query = "update `bad_word` set enabled = true where guild_id = ? and word = ?;" ;

			statement.setString(1, guildId);
			statement.setString(2, word);

			return statement.execute(query);
		} catch (SQLException exception) {
			logger.error("An error occurred while re-enabling word prevention", exception);
			return false;
		}
	}

	@Override
	public boolean disableBadWord(String guildId, String word) {
		try {
			logger.info("Disabling word prevention %s for guild %s...".formatted(word, guildId));

			PreparedStatement statement = (PreparedStatement) connection.createStatement();
			String query = "update `bad_word` set enabled = false where guild_id = ? and word = ?;" ;

			statement.setString(1, guildId);
			statement.setString(2, word);

			return statement.execute(query);
		} catch (SQLException exception) {
			logger.error("An error occurred while disabling word prevention", exception);
			return false;
		}
	}
}
