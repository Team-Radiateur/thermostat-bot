package be.christophebernard.thermostat.bot.events;

import be.christophebernard.thermostat.bot.common.annotations.DiscordEvent;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@DiscordEvent(name = "message")
public class MessageEventHandler extends ListenerAdapter {
    private final Logger logger;

    public MessageEventHandler() {
        logger = LoggerFactory.getLogger(getClass());
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.getAuthor().isBot() || event.getMember() == null) {
            return;
        }

        logger.info("Message de %s: %s".formatted(
		        event.getAuthor().getEffectiveName(),
		        event.getMessage().getContentRaw()
        ));
    }

    @Override
    public void onMessageUpdate(@NotNull MessageUpdateEvent event) {
        if (event.getAuthor().isBot() || event.getMember() == null) {
            return;
        }

        logger.info(
                "%s a modifié son message: %s".formatted(
                        event.getAuthor().getEffectiveName(),
		                event.getMessage().getContentRaw()
                )
        );
    }

    @Override
    public void onMessageDelete(@NotNull MessageDeleteEvent event) {
        logger.info("Suppression du message: %s".formatted(event.getMessageId()));
    }
}
