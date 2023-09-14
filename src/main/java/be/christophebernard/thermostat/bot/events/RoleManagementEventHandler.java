package be.christophebernard.thermostat.bot.events;

import be.christophebernard.thermostat.Configuration;
import be.christophebernard.thermostat.bot.common.annotations.DiscordEvent;
import be.christophebernard.thermostat.bot.common.utils.KeyValuePair;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleAddEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleRemoveEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.events.role.RoleCreateEvent;
import net.dv8tion.jda.api.events.role.RoleDeleteEvent;
import net.dv8tion.jda.api.events.role.update.GenericRoleUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.stream.Collectors;

@DiscordEvent(name = "role")
public class RoleManagementEventHandler extends ListenerAdapter {
	private final Logger logger;

	public RoleManagementEventHandler() {
		logger = LoggerFactory.getLogger(getClass());
	}

	@Override
	public void onMessageReactionAdd(@NotNull MessageReactionAddEvent event) {
		if (!event.isFromGuild()) {
			return;
		}

		TextChannel channel = event.getChannel().asTextChannel();
	}

	private boolean isRoleManagementChannel(TextChannel channel) {
		return Configuration.getInstance().<Set<KeyValuePair<String, String>>>get("rolesChannelIdsByGuild")
		                     .stream()
		                     .filter(keyValuePair -> keyValuePair.getKey().equals(channel.getGuild().getId()))
		                     .findFirst()
		                     .map(keyValuePair -> keyValuePair.getValue().equals(channel.getId()))
		                     .orElse(false);
	}

	@Override
	public void onMessageReactionRemove(@NotNull MessageReactionRemoveEvent event) {
		if (!event.isFromGuild()) {
			return;
		}

		TextChannel channel = event.getChannel().asTextChannel();

		if (!isRoleManagementChannel(channel)) {
			return;
		}

		Guild guild = event.getGuild();

	}

	@Override
	public void onGuildMemberRoleAdd(@NotNull GuildMemberRoleAddEvent event) {
		logger.info("New roles added to `%s`:\n%s".formatted(
				            event.getMember().getEffectiveName(),
				            event.getRoles().stream()
				                 .map(role -> "%s (%s)".formatted(role.getName(), role.getId()))
				                 .collect(Collectors.joining(
						                 "",
						                 "- ",
						                 ""
				                 ))
		            )
		);

	}

	@Override
	public void onGuildMemberRoleRemove(@NotNull GuildMemberRoleRemoveEvent event) {
		logger.info(
				"Rôles retirés de %s:\n%s".formatted(
						event.getMember().getEffectiveName(),
						event.getRoles().stream()
						     .map(role -> "%s (%s)".formatted(role.getName(), role.getId()))
						     .collect(
									 Collectors.joining(
											 "",
											 "- ",
											 ""
									 )
						     )
				)
		);
	}

	@Override
	public void onRoleCreate(@NotNull RoleCreateEvent event) {
		logger.info("New role created: %s (%s)".formatted(event.getRole().getName(), event.getRole().getId()));
	}

	@Override
	public void onRoleDelete(@NotNull RoleDeleteEvent event) {
		logger.info("Role deleted: %s (%s)".formatted(event.getRole().getName(), event.getRole().getId()));
	}

	@Override
	public void onGenericRoleUpdate(@NotNull GenericRoleUpdateEvent event) {
		logger.info("Role %s (%s) updated".formatted(event.getRole().getName(), event.getRole().getId()));
	}
}
