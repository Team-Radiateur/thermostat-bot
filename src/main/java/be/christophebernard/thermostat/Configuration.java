package be.christophebernard.thermostat;

import be.christophebernard.thermostat.bot.common.utils.KeyValuePair;
import be.christophebernard.thermostat.bot.common.annotations.ConfigurationField;
import io.github.cdimascio.dotenv.Dotenv;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Color;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;
import java.lang.reflect.*;

public class Configuration {
    private static Logger logger;
	private static Configuration configuration;

    @ConfigurationField
    private String token;
    @ConfigurationField(name = "bot_name")
    private String botName;
    @ConfigurationField(name = "embed_color")
    private Color embedColor;
    @ConfigurationField(name = "client_id")
    private String clientId;
    @ConfigurationField(name = "owner_id")
    private String ownerId;
    @ConfigurationField
    private Integer intents;
    @ConfigurationField(name = "announcement_channels_by_guild")
    private Set<KeyValuePair<String, String>> announcementChannelsByGuild;
    @ConfigurationField(name = "voice_logging_channel_by_guild")
    private Set<KeyValuePair<String, String>> voiceLoggingChannelByGuild;
    @ConfigurationField(name = "music_channels")
    private Set<String> acceptedMusicCommandChannels;
    @ConfigurationField(name = "role_to_tag_on_user_remove")
    private Set<KeyValuePair<String, String>> rolesToTagOnUserRemovalFromGuild;
    @ConfigurationField(name = "user_update_logging_channel_by_guild")
    private Set<KeyValuePair<String, String>> userUpdateLoggingChannelByGuild;

    @ConfigurationField(name = "roles_channel_id_by_guild")
    private Set<KeyValuePair<String, String>> rolesChannelIdsByGuild;

    @ConfigurationField(name = "mysql_url")
    private String databaseUrl;
    @ConfigurationField(name = "mysql_user")
    private String databaseUsername;
    @ConfigurationField(name = "mysql_password")
    private String databasePassword;

    private Configuration() {}

    private static @NotNull Configuration parse(List<KeyValuePair<String, String>> args) {
        Configuration configuration = new Configuration();

        Field[] configFields = Configuration.class.getDeclaredFields();

        logger.info("Retrieving configuration...");

        for (Field field : configFields) {
            String fieldName = field.getName();
            Annotation[] annotations = field.getAnnotations();
            ConfigurationField fieldAnnotation =
		            (ConfigurationField)
				            Arrays.stream(annotations)
				                  .filter(annotation -> annotation.annotationType() == ConfigurationField.class)
				                  .findFirst()
				                  .orElse(null);

            if (fieldAnnotation == null) {
                continue;
            }
            logger.debug("Searching for configuration for field `" + field.getName() + "`");

            KeyValuePair<String, String> arg = args
                    .stream()
                    .filter(argument -> argument.getKey().equals(
							fieldAnnotation.name().isEmpty() ? fieldName : fieldAnnotation.name())
                    )
                    .findFirst()
                    .orElse(null);

            if (arg == null) {
                throw new MissingResourceException(
                        "Missing environment variable for `" +
                                fieldName +
                                "` (needed `" +
                                (fieldAnnotation.name().isEmpty() ? fieldName : fieldAnnotation.name()).toUpperCase() +
                                "`)",
                        Configuration.class.getName(),
                        fieldName
                );
            }

            String value = arg.getValue();

            try {
                String fieldType = field.getType().getSimpleName();

                field.set(
						configuration,
                        switch (fieldType) {
                            case "String" -> value;
                            case "Color" -> Color.getColor(value);
                            case "Integer", "int" -> Integer.parseInt(value);
                            case "Set" -> {
                                Type genericType = field.getGenericType();
                                Type typeArgument = ((ParameterizedType) genericType).getActualTypeArguments()[0];

                                switch (typeArgument.getClass().getSimpleName()) {
                                    case "ParameterizedTypeImpl" -> {
                                        Set<KeyValuePair<String, String>> set = new HashSet<>();
                                        String[] keysValues = value.split(",");

                                        for (String keyValString : keysValues) {
                                            String[] keyValue = keyValueString(keyValString, fieldName);

                                            set.add(new KeyValuePair<>(keyValue[0], keyValue[1]));
                                        }

                                        yield set;
                                    }
                                    case "Class" -> {
                                        yield new HashSet<>(List.of(new String[]{value}));
                                    }
                                    default -> throw new IllegalArgumentException(
                                            "Unsupported type `" +
                                                    typeArgument +
                                                    "` for field `" +
                                                    fieldName +
                                                    "`"
                                    );
                                }
                            }
                            case "KeyValuePair" -> {
                                String[] keyValue = keyValueString(value, fieldName);

                                yield new KeyValuePair<>(keyValue[0], keyValue[1]);
                            }
                            default -> throw new IllegalArgumentException(
                                    "Unsupported type `" +
                                            fieldType +
                                            "` for field `" +
                                            fieldName +
                                            "`"
                            );
                        }
                );
            } catch (IllegalAccessException e) {
                throw new MissingResourceException(
                        "Failed to set configuration for field `" + fieldName + "`",
                        Configuration.class.getName(),
                        fieldName
                );
            }

            logger.debug("Configuration for field `" + fieldName + "` loaded...");
        }

        logger.info("Configuration loaded successfully!");

        return configuration;
    }

    private static String @NotNull [] keyValueString(@NotNull String keyValueString, String fieldName) {
        String[] keyValue = keyValueString.split(":");

        if (keyValue.length != 2) {
            throw new IllegalArgumentException(
                    "Invalid key-value pair `" +
                            keyValueString +
                            "` for field `" +
                            fieldName +
                            "`"
            );
        }
        return keyValue;
    }

    public static @NotNull Configuration getInstance() {
        if (configuration == null) {
            Dotenv dotenv = Dotenv.configure().load();

            logger = LoggerFactory.getLogger(Configuration.class);
            configuration = parse(
					dotenv.entries()
					      .stream()
                          .map(entry -> new KeyValuePair<>(entry.getKey().toLowerCase(), entry.getValue()))
                          .collect(Collectors.toList())
            );
        }

        return configuration;
    }

    public <T> T get(String fieldName) {
        try {
            Field field = Configuration.class.getDeclaredField(fieldName);

            return (T) field.get(this);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            logger.error("Failed to get configuration for field `" + fieldName + "`\n" + e.getMessage());

            return null;
        }
    }
}
