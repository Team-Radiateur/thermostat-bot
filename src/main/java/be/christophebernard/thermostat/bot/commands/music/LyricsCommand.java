package be.christophebernard.thermostat.bot.commands.music;

import be.christophebernard.thermostat.bot.common.ICommandExecutor;
import be.christophebernard.thermostat.bot.common.annotations.DiscordCommand;
import be.christophebernard.thermostat.bot.common.exceptions.CommandExecutionError;
import be.christophebernard.thermostat.bot.common.utils.TREmbed;
import be.christophebernard.thermostat.bot.common.TrackScheduler;
import com.jagrosh.jlyrics.Lyrics;
import com.jagrosh.jlyrics.LyricsClient;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;

import java.util.concurrent.ExecutionException;

@DiscordCommand(name = "lyrics", description = "Affiche les paroles de la musique en cours de lecture")
public class LyricsCommand implements ICommandExecutor {
	@Override
	public void execute(SlashCommandInteractionEvent event) throws CommandExecutionError {
		EmbedBuilder embed = TREmbed.newEmbed(TREmbed.EmbedType.MUSIC, event.getJDA(), event.getUser());
		InteractionHook hook = event.getHook();

		if (event.getGuild() == null || event.getMember() == null) {
			hook.editOriginalEmbeds(
					embed.setDescription("Cette commande ne peut être utilisée que sur un serveur")
							.build()
			).queue();
			return;
		}

		AudioTrack track = TrackScheduler.getInstance().currentTrack();

		if (track == null) {
			hook.editOriginalEmbeds(
					embed.setDescription("Aucune musique n'est en cours de lecture")
					     .build()
			).queue();
			return;
		}

		Lyrics lyrics;

		try {
			lyrics = new LyricsClient().getLyrics(track.getInfo().title).get();
		} catch (InterruptedException | ExecutionException e) {
			throw new CommandExecutionError(e);
		}

		if (lyrics == null) {
			hook.editOriginalEmbeds(
					embed.setDescription("Aucune parole n'a été trouvée pour " + track.getInfo().title)
					     .build()
			).queue();
			return;
		}

		hook.editOriginalEmbeds(
				embed.setDescription("Voici les paroles pour " + track.getInfo().title + ":\n\n" +lyrics.getContent())
				     .build()
		).queue();
	}
}
