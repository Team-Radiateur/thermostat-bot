package be.christophebernard.thermostat.bot.events;

import be.christophebernard.thermostat.bot.common.annotations.DiscordEvent;
import be.christophebernard.thermostat.bot.common.utils.TREmbed;
import be.christophebernard.thermostat.database.IDatabaseConnector;
import be.christophebernard.thermostat.database.MySQLConnector;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

@DiscordEvent(name = "message")
public class MessageEventHandler extends ListenerAdapter {
    private final Logger logger;
    private final IDatabaseConnector databaseConnector;

    public MessageEventHandler() {
        logger = LoggerFactory.getLogger(getClass());
        databaseConnector = MySQLConnector.getInstance();
    }

    private boolean messageContainsBadWord(String guildId, String message) {
        return databaseConnector.getBadWords(guildId).stream().anyMatch(message::contains);
    }

    private boolean messagePassesCheck(Member member, Guild guild, Message message) {
        if (
                !Objects.requireNonNull(member).hasPermission(Permission.KICK_MEMBERS) &&
                        messageContainsBadWord(guild.getId(), message.getContentRaw())
        ) {
            EmbedBuilder embed = TREmbed
                    .newEmbed(TREmbed.EmbedType.ADMIN, message.getJDA())
                    .setTitle("Message supprimé")
                    .setDescription(
                            "Le message de %s a été supprimé car il contenait un mot interdit"
                                    .formatted(member.getEffectiveName())
                    );

            message.replyEmbeds(embed.build()).queue();
            message.delete().queue();
            return false;
        }
        return true;
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.getAuthor().isBot() || event.getMember() == null) {
            return;
        }

        if (messagePassesCheck(event.getMember(), event.getGuild(), event.getMessage())) {
            logger.info("Message de %s: %s".formatted(
                    event.getAuthor().getEffectiveName(),
                    event.getMessage().getContentRaw()
            ));
        }
    }

    @Override
    public void onMessageUpdate(@NotNull MessageUpdateEvent event) {
        if (event.getAuthor().isBot() || event.getMember() == null) {
            return;
        }

        if (messagePassesCheck(event.getMember(), event.getGuild(), event.getMessage())) {
            logger.info(
                    "%s a modifié son message: %s".formatted(
                            event.getAuthor().getEffectiveName(),
                            event.getMessage().getContentRaw()
                    )
            );
        }
    }

    @Override
    public void onMessageDelete(@NotNull MessageDeleteEvent event) {
        logger.info("Suppression du message: %s".formatted(event.getMessageId()));
    }
}
