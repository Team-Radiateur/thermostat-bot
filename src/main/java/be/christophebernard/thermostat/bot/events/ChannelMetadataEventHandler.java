package be.christophebernard.thermostat.bot.events;

import be.christophebernard.thermostat.Configuration;
import be.christophebernard.thermostat.bot.common.annotations.DiscordEvent;
import be.christophebernard.thermostat.bot.common.utils.KeyValuePair;
import be.christophebernard.thermostat.bot.common.utils.TREmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.channel.ChannelCreateEvent;
import net.dv8tion.jda.api.events.channel.ChannelDeleteEvent;
import net.dv8tion.jda.api.events.channel.GenericChannelEvent;
import net.dv8tion.jda.api.events.channel.update.*;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;

@DiscordEvent(name = "channel")
public class ChannelMetadataEventHandler extends ListenerAdapter {
	private final Logger logger;

	public ChannelMetadataEventHandler() {
		logger = LoggerFactory.getLogger(getClass());
	}

	private void logEvent(GenericChannelEvent event, String status) {
		String guildId = event.getGuild().getId();
		HashSet<KeyValuePair<String, String>> rolesToTag = Configuration.getInstance()
		                                                                .get("rolesToTagOnUserRemovalFromGuild");
		HashSet<KeyValuePair<String, String>> channels = Configuration.getInstance()
		                                                              .get("userUpdateLoggingChannelByGuild");

		logger.info(status);

		if (
				rolesToTag.stream().anyMatch(keyValuePair -> keyValuePair.getKey().equals(guildId)) &&
						channels.stream().anyMatch(keyValuePair -> keyValuePair.getKey().equals(guildId))
		) {
			TextChannel channel = event.getJDA().getTextChannelById(
					channels
							.stream()
							.filter(keyValuePair -> keyValuePair.getKey().equals(guildId))
							.findFirst()
							.get()
							.getValue()
			);

			if (channel == null) {
				return;
			}

			channel.sendMessageEmbeds(
					TREmbed.newEmbed(TREmbed.EmbedType.HR, event.getJDA())
					       .setDescription(status)
					       .build()
			).queue();
		}
	}

	@Override
	public void onChannelCreate(@NotNull ChannelCreateEvent event) {
		String status = "Canal créé: " + event.getChannel().getName() + " (" + event.getChannel().getType() + ")";

		logEvent(event, status);
	}

	@Override
	public void onChannelDelete(@NotNull ChannelDeleteEvent event) {
		String status = "Canal supprimé: " + event.getChannel().getName() + " (" + event.getChannel().getType() + ")";

		logEvent(event, status);
	}

	@Override
	public void onChannelUpdateBitrate(@NotNull ChannelUpdateBitrateEvent event) {
		String status = "Bitrate du canal " + event.getChannel().getName() + " modifié: " + event.getOldValue() + " -> " + event.getNewValue();

		logEvent(event, status);
	}

	@Override
	public void onChannelUpdateName(@NotNull ChannelUpdateNameEvent event) {
		String status = "Nom du canal " + event.getChannel().getName() + " modifié: " + event.getOldValue() + " -> " + event.getNewValue();

		logEvent(event, status);
	}

	@Override
	public void onChannelUpdateFlags(@NotNull ChannelUpdateFlagsEvent event) {
		String status = "Flags du canal " + event.getChannel().getName() + " modifié: " + event.getOldValue() + " -> " + event.getNewValue();

		logEvent(event, status);
	}

	@Override
	public void onChannelUpdateParent(@NotNull ChannelUpdateParentEvent event) {
		String status = "Parent du canal " + event.getChannel().getName() + " modifié: " + event.getOldValue() + " -> " + event.getNewValue();

		logEvent(event, status);
	}

	@Override
	public void onChannelUpdatePosition(@NotNull ChannelUpdatePositionEvent event) {
		String status = "Position du canal " + event.getChannel().getName() + " modifié: " + event.getOldValue() + " -> " + event.getNewValue();

		logEvent(event, status);
	}

	@Override
	public void onChannelUpdateRegion(@NotNull ChannelUpdateRegionEvent event) {
		String status = "Région du canal " + event.getChannel().getName() + " modifié: " + event.getOldValue() + " -> " + event.getNewValue();

		logEvent(event, status);
	}

	@Override
	public void onChannelUpdateSlowmode(@NotNull ChannelUpdateSlowmodeEvent event) {
		String status = "Slowmode du canal " + event.getChannel().getName() + " modifié: " + event.getOldValue() + " -> " + event.getNewValue();

		logEvent(event, status);
	}

	@Override
	public void onChannelUpdateDefaultThreadSlowmode(@NotNull ChannelUpdateDefaultThreadSlowmodeEvent event) {
		String status = "Slowmode par défaut du canal " + event.getChannel().getName() + " modifié: " + event.getOldValue() + " -> " + event.getNewValue();

		logEvent(event, status);
	}

	@Override
	public void onChannelUpdateDefaultReaction(@NotNull ChannelUpdateDefaultReactionEvent event) {
		String status = "Réaction par défaut du canal " + event.getChannel().getName() + " modifié: " + event.getOldValue() + " -> " + event.getNewValue();

		logEvent(event, status);
	}

	@Override
	public void onChannelUpdateDefaultLayout(@NotNull ChannelUpdateDefaultLayoutEvent event) {
		String status = "Layout par défaut du canal " + event.getChannel().getName() + " modifié: " + event.getOldValue() + " -> " + event.getNewValue();

		logEvent(event, status);
	}

	@Override
	public void onChannelUpdateTopic(@NotNull ChannelUpdateTopicEvent event) {
		String status = "Topic du canal " + event.getChannel().getName() + " modifié: " + event.getOldValue() + " -> " + event.getNewValue();

		logEvent(event, status);
	}

	@Override
	public void onChannelUpdateType(@NotNull ChannelUpdateTypeEvent event) {
		String status = "Type du canal " + event.getChannel().getName() + " modifié: " + event.getOldValue() + " -> " + event.getNewValue();

		logEvent(event, status);
	}

	@Override
	public void onChannelUpdateUserLimit(@NotNull ChannelUpdateUserLimitEvent event) {
		String status = "Limite d'utilisateurs du canal " + event.getChannel().getName() + " modifié: " + event.getOldValue() + " -> " + event.getNewValue();

		logEvent(event, status);
	}

	@Override
	public void onChannelUpdateArchived(@NotNull ChannelUpdateArchivedEvent event) {
		String status = "Canal " + event.getChannel().getName() + " archivé";

		logEvent(event, status);
	}

	@Override
	public void onChannelUpdateAutoArchiveDuration(@NotNull ChannelUpdateAutoArchiveDurationEvent event) {
		String status = "Durée d'archivage automatique du canal " + event.getChannel().getName() + " modifié: " + event.getOldValue() + " -> " + event.getNewValue() + " secondes";

		logEvent(event, status);
	}

	@Override
	public void onChannelUpdateLocked(@NotNull ChannelUpdateLockedEvent event) {
		String status = "Canal " + event.getChannel().getName() + " verrouillé";

		logEvent(event, status);
	}

	@Override
	public void onChannelUpdateInvitable(@NotNull ChannelUpdateInvitableEvent event) {
		String status = "Canal " + event.getChannel().getName() + " rendu invitable";

		logEvent(event, status);
	}
}
