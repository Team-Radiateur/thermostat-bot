package be.christophebernard.thermostat.database;

import be.christophebernard.thermostat.database.caching.DataCache;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class MySQLConnector implements IDatabaseConnector {
	private final DataCache<String, Set<String>> badsWordsCache;
	private static MySQLConnector instance;
	private static Logger logger;

	private MySQLConnector() {
		badsWordsCache = new DataCache<>(300, 60, 200);
	}

	public static @Nullable MySQLConnector getInstance() {
		if (instance == null) {
			logger = LoggerFactory.getLogger(MySQLConnector.class);

			instance = new MySQLConnector();

			if (!instance.databaseSet()) {
				logger.error("An error occurred while setting up the database");
				return null;
			}
		}

		return instance;
	}


	@Override
	public boolean databaseSet() {
		try (
				PreparedStatement preparedStatement = ConnectionManager.getConnection().prepareStatement(
				"""
					create table if not exists `bad_word`(
						id int not null auto_increment,
						guild_id varchar(255) not null,
						word varchar(191) character set utf8mb4 collate utf8mb4_unicode_ci not null,
						enabled boolean default true,
						constraint bad_word_id_pk primary key(id)
					) engine = InnoDB character set utf8mb4 collate utf8mb4_unicode_ci;
					"""
		)) {
			logger.info("Verifying database tables presence...");

			preparedStatement.executeUpdate();

			return true;
		} catch (SQLException exception) {
			logger.error(exception.getMessage(), exception);
			return false;
		}
	}

	@Override
	public Set<String> getBadWords(String guildId) {
		try (
				PreparedStatement statement = ConnectionManager.getConnection().prepareStatement(
				"select word from `bad_word` where guild_id = ? and enabled = true;"
				)
		) {
			logger.info("Fetching prevented words for guild id %s...".formatted(guildId));
			if (badsWordsCache.get(guildId) != null) {
				logger.info("Prevented words for guild id {} found in cache", guildId);

				return badsWordsCache.get(guildId);
			}

			statement.setString(1, guildId);

			ResultSet result = statement.executeQuery();
			Set<String> words = new HashSet<>();

			while (result.next()) {
				logger.info("Prevented word {} found for guild id {}", result.getString("word"), guildId);
				words.add(result.getString("word"));
			}

			badsWordsCache.put(guildId, words);

			return words;
		} catch (SQLException exception) {
			logger.error("An error occurred while fetching prevented words", exception);
			return null;
		}
	}

	@Override
	public boolean addBadWord(String guildId, String word) {
		try (
				PreparedStatement statement = ConnectionManager.getConnection().prepareStatement(
				"insert into `bad_word`(guild_id, word) values(?, ?);"
				)
		){
			logger.info("Preventing word {} to be written in {}'s channels...", word, guildId);

			statement.setString(1, guildId);
			statement.setString(2, word);
			statement.executeUpdate();

			Set<String> badWords = badsWordsCache.get(guildId);

			if (badWords != null) {
				badWords.add(word);
			}

			return true;
		} catch (SQLException exception) {
			logger.error("An error occurred while adding the words to the prevented list", exception);
			return false;
		}
	}

	@Override
	public boolean enableBadWord(String guildId, String word) {
		try (
				PreparedStatement statement = ConnectionManager.getConnection().prepareStatement(
				"update `bad_word` set enabled = true where guild_id = ? and word = ?;"
				)
		){
			logger.info("Re-enabling prevention for word `{}` in guild {}...", word, guildId);

			statement.setString(1, guildId);
			statement.setString(2, word);
			statement.executeUpdate();

			Set<String> words = badsWordsCache.get(guildId);

			if (words != null) {
				words.add(word);
			}

			return true;
		} catch (SQLException exception) {
			logger.error("An error occurred while re-enabling word prevention", exception);
			return false;
		}
	}

	@Override
	public boolean disableBadWord(String guildId, String word) {
		try (
				PreparedStatement statement = ConnectionManager.getConnection().prepareStatement(
						"update `bad_word` set enabled = false where guild_id = ? and word = ?;"
				)
		) {
			logger.info("Disabling word prevention of {} for guild {}...", word, guildId);

			statement.setString(1, guildId);
			statement.setString(2, word);
			statement.executeUpdate();

			Set<String> words = badsWordsCache.get(guildId);

			if (words != null) {
				words.remove(word);
			}

			return true;
		} catch (SQLException exception) {
			logger.error("An error occurred while disabling word prevention", exception);
			return false;
		}
	}
}
