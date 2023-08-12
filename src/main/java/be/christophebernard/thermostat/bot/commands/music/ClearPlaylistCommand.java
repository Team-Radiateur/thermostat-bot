package be.christophebernard.thermostat.bot.commands.music;

import be.christophebernard.thermostat.bot.common.ICommandExecutor;
import be.christophebernard.thermostat.bot.common.annotations.DiscordCommand;
import be.christophebernard.thermostat.bot.common.exceptions.CommandExecutionError;
import be.christophebernard.thermostat.bot.common.utils.TREmbed;
import be.christophebernard.thermostat.bot.common.TrackScheduler;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@DiscordCommand(name = "clear", description = "Vide la playlist actuelle et stoppe la lecture", adminOnly = true)
public class ClearPlaylistCommand implements ICommandExecutor {
	@Override
	public void execute(SlashCommandInteractionEvent event) throws CommandExecutionError {
		TrackScheduler.getInstance().clearQueue();
		TrackScheduler.getInstance().playNextTrack();

		event.getHook().editOriginalEmbeds(
				TREmbed.newEmbed(TREmbed.EmbedType.MUSIC, event.getJDA(), event.getUser())
				       .setDescription("La playlist a été vidée")
				       .build()
		).queue();
	}
}
