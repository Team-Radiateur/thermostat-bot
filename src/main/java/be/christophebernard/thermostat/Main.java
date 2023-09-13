package be.christophebernard.thermostat;

import be.christophebernard.thermostat.bot.ThermostatBot;
import be.christophebernard.thermostat.database.IDatabaseConnector;
import be.christophebernard.thermostat.database.MySQLConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        Logger logger = LoggerFactory.getLogger("Main");

        logger.info("Starting bot...");
        Configuration configuration = null;

        try {
            configuration = Configuration.getInstance();
        } catch (MissingResourceException exception) {
            logger.error("Missing environment variable to complete configuration", exception);
            System.exit(1);
        }

        IDatabaseConnector connector = MySQLConnector.getInstance();

        if (connector == null) {
            logger.error("Unable to connect to database");
            System.exit(1);
        }

        new ThermostatBot(configuration);
    }
}
