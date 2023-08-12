package be.christophebernard.thermostat.bot.commands.music;

import be.christophebernard.thermostat.bot.common.ICommandExecutor;
import be.christophebernard.thermostat.bot.common.annotations.DiscordCommand;
import be.christophebernard.thermostat.bot.common.exceptions.CommandExecutionError;
import be.christophebernard.thermostat.bot.common.utils.TREmbed;
import be.christophebernard.thermostat.bot.common.TrackScheduler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@DiscordCommand(name = "playlist", description = "Affiche la playlist en cours de lecture")
public class PlaylistCommand implements ICommandExecutor {
	@Override
	public void execute(SlashCommandInteractionEvent event) throws CommandExecutionError {
		EmbedBuilder embed = TREmbed.newEmbed(TREmbed.EmbedType.MUSIC, event.getJDA(), event.getUser())
				                     .setDescription(
						                     TrackScheduler.getInstance().nowPlaying()
				                     );

		event.getHook().editOriginalEmbeds(embed.build()).queue();
	}
}
