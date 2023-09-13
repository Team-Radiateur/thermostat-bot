package be.christophebernard.thermostat.database;

import java.util.List;

public interface IDatabaseConnector {
	boolean databaseSet();
	List<String> getBadWords(String guildId);
	boolean addBadWord(String guildId, String word);
	boolean enableBadWord(String guildId, String word);
	boolean disableBadWord(String guildId, String word);
}
