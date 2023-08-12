package be.christophebernard.thermostat.bot.commands.music;

import be.christophebernard.thermostat.bot.common.ICommandExecutor;
import be.christophebernard.thermostat.bot.common.annotations.DiscordCommand;
import be.christophebernard.thermostat.bot.common.exceptions.CommandExecutionError;
import be.christophebernard.thermostat.bot.common.utils.TREmbed;
import be.christophebernard.thermostat.bot.common.TrackScheduler;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;

import java.util.Objects;

@DiscordCommand(name = "skip", description = "Passer à la musique suivante")
public class SkipCommand implements ICommandExecutor {
	@Override
	public void execute(SlashCommandInteractionEvent event) throws CommandExecutionError {
		EmbedBuilder builder = TREmbed.newEmbed(TREmbed.EmbedType.MUSIC, event.getJDA(), event.getUser());
		InteractionHook hook = event.getHook();

		if (event.getGuild() == null || event.getMember() == null) {
			hook.sendMessageEmbeds(
					builder.setDescription("Vous devez être dans un serveur pour utiliser cette commande")
					       .build()
			).queue();
			return;
		}

		if (event.getMember().getVoiceState() == null || event.getMember().getVoiceState().getChannel() == null) {
			hook.sendMessageEmbeds(
					builder.setDescription("Vous devez être dans un salon vocal pour utiliser cette commande")
					       .build()
			).queue();
			return;
		}

		if (!event.getGuild().getAudioManager().isConnected()) {
			hook.sendMessageEmbeds(
					builder.setDescription("Le bot n'est pas connecté à un salon vocal")
					       .build()
			).queue();
			return;
		}

		if (event.getGuild().getAudioManager().getConnectedChannel() != null &&
				    event.getGuild().getAudioManager().getConnectedChannel().getMembers().size() == 2 &&
				    event.getGuild().getAudioManager().getConnectedChannel().getMembers().contains(event.getMember())
		) {
			hook.sendMessageEmbeds(
					builder.setDescription("Vous devez être au moins deux dans le salon vocal pour utiliser cette commande")
					       .build()
			).queue();
			return;
		}

		if (!Objects.requireNonNull(event.getMember()).hasPermission(Permission.MANAGE_SERVER)) {
			hook.sendMessageEmbeds(
					builder.setDescription("Vous n'avez pas la permission de skipper la musique")
					       .build()
			).queue();
			return;
		}

		AudioTrack track = TrackScheduler.getInstance().playNextTrack();

		if (track == null) {
			hook.sendMessageEmbeds(
					builder.setDescription("Il n'y a pas de musique suivante")
					       .build()
			).queue();
			return;
		}

		hook.sendMessageEmbeds(
				builder.setDescription("Musique suivante: " + track.getInfo().title)
				       .setImage("https://img.youtube.com/vi/" + track.getIdentifier() + "/maxresdefault.jpg")
				       .build()
		).queue();
	}
}
