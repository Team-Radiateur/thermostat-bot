package be.christophebernard.thermostat.bot.commands.music;

import be.christophebernard.thermostat.bot.common.ICommandExecutor;
import be.christophebernard.thermostat.bot.common.annotations.DiscordCommand;
import be.christophebernard.thermostat.bot.common.exceptions.CommandExecutionError;
import be.christophebernard.thermostat.bot.common.utils.TREmbed;
import be.christophebernard.thermostat.bot.common.TrackScheduler;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@DiscordCommand(name = "now_playing", description = "Affiche la musique en cours de lecture")
public class NowPlayingCommand implements ICommandExecutor {
	@Override
	public void execute(SlashCommandInteractionEvent event) throws CommandExecutionError {
		EmbedBuilder embed = TREmbed.newEmbed(TREmbed.EmbedType.MUSIC, event.getJDA(), event.getUser());
		AudioTrack track = TrackScheduler.getInstance().currentTrack();

		if (track == null) {
			embed.setDescription("Aucune musique en cours de lecture");
		} else {
			int position = (int) (track.getPosition());
			long duration = track.getDuration();

			StringBuilder progress = new StringBuilder();
			for (int i = 0; i < 25; i++) {
				if (i == (position * 25L) / duration) {
					progress.append("🔘");
				} else {
					progress.append("▬");
				}
			}

			embed.setDescription(
					"En cours de lecture : %s]\n%s  `%s/%s`".formatted(
							track.getInfo().title,
							progress,
							formatTime(track.getPosition()),
							formatTime(track.getDuration())
					)
			);
		}

		event.getHook().editOriginalEmbeds(embed.build()).queue();
	}

	private String formatTime(long time) {
		int seconds = (int) (time / 1000);
		int minutes = seconds / 60;
		seconds %= 60;

		return String.format("%02d:%02d", minutes, seconds);
	}
}
