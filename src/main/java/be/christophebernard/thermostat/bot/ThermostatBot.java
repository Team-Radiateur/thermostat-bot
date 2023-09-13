package be.christophebernard.thermostat.bot;

import be.christophebernard.thermostat.Configuration;
import be.christophebernard.thermostat.bot.common.ICommandExecutor;
import be.christophebernard.thermostat.bot.common.utils.OptionParser;
import be.christophebernard.thermostat.bot.common.annotations.DiscordCommand;
import be.christophebernard.thermostat.bot.common.annotations.DiscordEvent;
import be.christophebernard.thermostat.bot.handlers.ActivityStatusUpdateHandler;
import be.christophebernard.thermostat.bot.handlers.CommandHandler;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class ThermostatBot {
    private final Logger logger;
    private final JDA client;
    private final CommandHandler commandHandler;

    public ThermostatBot(Configuration configuration) {
        logger = LoggerFactory.getLogger(getClass());

        logger.info("Starting bot...");

        commandHandler = new CommandHandler();
        client = JDABuilder.create(configuration.get("token"), GatewayIntent.getIntents(configuration.get("intents")))
                           .addEventListeners(commandHandler)
                           .build();

        Executors.newScheduledThreadPool(1)
                 .scheduleAtFixedRate(new ActivityStatusUpdateHandler(client), 0, 20, TimeUnit.SECONDS);

        logger.info("Registering commands and events...");
        registerCommandsAndEvents();
    }

    private void registerCommandsAndEvents() {
        Reflections reflections = new Reflections(getClass().getPackageName());
        Set<Class<?>> commands = reflections.getTypesAnnotatedWith(DiscordCommand.class);
        Set<Class<?>> events = reflections.getTypesAnnotatedWith(DiscordEvent.class);

        client.updateCommands()
              .addCommands(
                      commands.stream().map(commandClass -> {
                          ICommandExecutor commandInstance;
                          DiscordCommand annotation;

                          try {
                              if (!ICommandExecutor.class.isAssignableFrom(commandClass)) {
                                  throw new IllegalArgumentException(
                                          "Class `%s` does not implement ICommandExecutor, which is required"
                                                  .formatted(commandClass.getSimpleName())
                                  );
                              }

                              commandInstance = (ICommandExecutor) commandClass.getDeclaredConstructor().newInstance();
                              annotation = commandClass.getAnnotation(DiscordCommand.class);
                              logger.debug("Registering command `%s`".formatted(annotation.name()));
                          } catch (
                                    NoSuchMethodException | InvocationTargetException |
                                    InstantiationException | IllegalAccessException e
                          ) {
                              logger.error(
                                      "Failed to register command for class `%s`"
                                              .formatted(commandClass.getSimpleName())
                              );
                              return null;
                          }

                          commandHandler.registerCommand(annotation.name(), commandInstance);
                          SlashCommandData command = Commands.slash(annotation.name(), annotation.description());
                          Set<OptionData> options = new HashSet<>();

                          command.addOptions(
                                  Arrays.stream(annotation.options())
                                        .map(option -> {
                                            try {
                                                return OptionParser.parse(option);
                                            } catch (IllegalArgumentException exception) {
                                                logger.error(exception.getMessage());
                                                return null;
                                            }
                                        })
                                        .filter(Objects::nonNull)
                                        .collect(Collectors.toList())
                          );

                          command.addOptions(options);

                          return command;
                      }).filter(Objects::nonNull).collect(Collectors.toList())
              ).queue();


        client.addEventListener(events.stream().map(eventClass -> {
            try {
                DiscordEvent annotation = eventClass.getAnnotation(DiscordEvent.class);
                logger.debug("Registering event `%s`".formatted(annotation.name()));

                return eventClass.getDeclaredConstructor().newInstance();
            } catch (
                      NoSuchMethodException | InvocationTargetException |
                      InstantiationException | IllegalAccessException exception
            ) {
                logger.error(
                        "Failed to register event for class `%s`".formatted(eventClass.getSimpleName()),
                        exception
                );
                return null;
            }
        }).filter(Objects::nonNull).toList().toArray());

        try {
            client.awaitReady();
        } catch (InterruptedException exception) {
            logger.error("Error while waiting for bot to be finished", exception);
        }
    }
}
