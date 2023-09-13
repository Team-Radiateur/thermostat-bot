package be.christophebernard.thermostat.bot.events;

import be.christophebernard.thermostat.Configuration;
import be.christophebernard.thermostat.bot.common.annotations.DiscordEvent;
import be.christophebernard.thermostat.bot.common.utils.KeyValuePair;
import be.christophebernard.thermostat.bot.common.utils.TREmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Optional;

@DiscordEvent(name = "audioChannel")
public class AudioChannelEventHandler extends ListenerAdapter {
	private final Logger logger;

	public AudioChannelEventHandler() {
		logger = LoggerFactory.getLogger(getClass());
	}

	@Override
	public void onGuildVoiceUpdate(@NotNull GuildVoiceUpdateEvent event) {
		if (event.getMember().getUser().isBot()) {
			return;
		}

		String status = event.getMember().getEffectiveName();

		if (event.getOldValue() == null) {
			if (event.getNewValue() != null) {
				status += " s'est connecté au salon %s".formatted(event.getNewValue().getName());
			}
			return;
		} else if (event.getNewValue() == null) {
			status += " s'est déconnecté du salon %s".formatted(event.getOldValue().getName());
		} else if (event.getOldValue().getIdLong() != event.getNewValue().getIdLong()) {
			status += " a changé de salon : %s => %s".formatted(
					event.getOldValue().getName(),
					event.getNewValue().getName()
			);
		}

		String guildId = event.getGuild().getId();
		HashSet<KeyValuePair<String, String>> channels = Configuration.getInstance().get("voiceLoggingChannelByGuild");

		logger.info(status);

		Optional<KeyValuePair<String, String>> guildChannelPair = channels.stream()
		                                                                  .filter(
																				  keyValuePair ->
																						  keyValuePair.getKey()
				                                                                                      .equals(guildId)
		                                                                  ).findFirst();

		if (guildChannelPair.isPresent()) {
			TextChannel channel = event.getJDA().getTextChannelById(guildChannelPair.get().getValue());

			if (channel == null) {
				return;
			}

			channel.sendMessageEmbeds(
					TREmbed.newEmbed(TREmbed.EmbedType.INFO, event.getJDA())
					       .setDescription(status)
					       .build()
			).queue();
		}
	}
}
