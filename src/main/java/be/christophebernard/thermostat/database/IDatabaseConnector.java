package be.christophebernard.thermostat.database;

import java.util.Set;

public interface IDatabaseConnector {
	boolean databaseSet();
	Set<String> getBadWords(String guildId);
	boolean addBadWord(String guildId, String word);
	boolean enableBadWord(String guildId, String word);
	boolean disableBadWord(String guildId, String word);
}
