package be.christophebernard.thermostat.bot.commands.music;

import be.christophebernard.thermostat.bot.common.utils.TREmbed;
import be.christophebernard.thermostat.bot.common.ICommandExecutor;
import be.christophebernard.thermostat.bot.common.annotations.DiscordCommand;
import be.christophebernard.thermostat.bot.handlers.AudioPlayerSendHandler;
import be.christophebernard.thermostat.bot.common.TrackScheduler;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.managers.AudioManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@DiscordCommand(
		name = "play",
		description = "Joue ou reprend la lecture d'une musique",
		options = {
				"type: STRING|name: musique|description: La musique à faire lire au bot|required: false"
		}
)
public class PlayCommand implements ICommandExecutor {
	private final Logger logger;
	private final TrackScheduler scheduler;
	private final Map<String, AudioPlayerSendHandler> playerHandlers;

	public PlayCommand() {
		logger = LoggerFactory.getLogger(getClass());
		scheduler = TrackScheduler.getInstance();
		playerHandlers = new HashMap<>();
	}

	@Override
	public void execute(SlashCommandInteractionEvent event) {
		OptionMapping music = event.getOption("musique");
		EmbedBuilder embed = TREmbed.newEmbed(TREmbed.EmbedType.MUSIC, event.getJDA(), event.getUser());
		InteractionHook hook = event.getHook();

		if (event.getMember() == null || event.getGuild() == null) {
			hook.editOriginalEmbeds(embed.setDescription("Vous devez être connecté à un serveur pour utiliser cette commande.").build()).queue();
			return;
		}

		GuildVoiceState voiceState = event.getMember().getVoiceState();

		if (voiceState == null || !voiceState.inAudioChannel()) {
			hook.editOriginalEmbeds(
					embed.setDescription("Vous devez être connecté à un salon vocal pour utiliser cette commande.")
					     .build()
			).queue();
			return;
		}

		if (!playerHandlers.containsKey(event.getGuild().getId())) {
			playerHandlers.put(event.getGuild().getId(), new AudioPlayerSendHandler(
					TrackScheduler.getInstance().getPlayer())
			);
		}

		if (Objects.isNull(music)) {
			if (scheduler.currentTrack() == null && scheduler.getQueue().isEmpty()) {
				hook.editOriginalEmbeds(
						embed.setDescription("Il n'y a aucune musique à lire.").build()
				).queue();
				return;
			}

			AudioManager manager = Objects.requireNonNull(event.getGuild()).getAudioManager();

			if (!manager.isConnected()) {
				manager.openAudioConnection(voiceState.getChannel());
				manager.setSendingHandler(playerHandlers.get(event.getGuild().getId()));
			}

			scheduler.play();

			hook.editOriginalEmbeds(embed.setDescription(
					new StringBuilder("Reprise de la lecture.\n")
							.append(scheduler.currentTrack().getInfo().title)
							.append(scheduler.nowPlaying())
			).build()).queue();
		} else {
			AudioManager manager = Objects.requireNonNull(event.getGuild()).getAudioManager();

			scheduler.getPlayerManager().loadItem(music.getAsString(), new AudioLoadResultHandler() {
				@Override
				public void trackLoaded(AudioTrack audioTrack) {
					if (scheduler.getQueue().isEmpty() && scheduler.currentTrack() == null) {
						scheduler.play(audioTrack);
					} else {
						scheduler.enqueue(audioTrack);
					}

					logger.info("%s added to tracklist.".formatted(audioTrack.getInfo().title));
					embed.setDescription(
							"%s de %s ajouté à la file d'attente.".formatted(
									audioTrack.getInfo().title,
									audioTrack.getInfo().author
							)
					).appendDescription(scheduler.nowPlaying()).setImage(
							audioTrack.getInfo().uri.contains("youtube") ? "https://i.ytimg.com/vi/%s/hqdefault.jpg".formatted(audioTrack.getInfo().identifier) : null
					);

					connect();
					sendEmbed();
				}

				@Override
				public void playlistLoaded(AudioPlaylist audioPlaylist) {
					logger.info("%d songs found.".formatted(audioPlaylist.getTracks().size()));
					embed.setDescription(
							"%d musiques de %s ajoutées à la file d'attente.".formatted(
									audioPlaylist.getTracks().size(),
									audioPlaylist.getName()
							)
					);
					scheduler.enqueue(audioPlaylist.getTracks().toArray(new AudioTrack[0]));

					connect();
					scheduler.play();
					sendEmbed();
				}

				@Override
				public void noMatches() {
					logger.info("No song matching url found.");
					embed.setDescription("Aucun résultat trouvé pour cette recherche.");
					sendEmbed();
				}

				@Override
				public void loadFailed(FriendlyException e) {
					logger.error(e.getMessage(), e);
					embed.setDescription("Une erreur est survenue lors de la lecture de la musique.");
					sendEmbed();
				}

				public void sendEmbed() {
					hook.editOriginalEmbeds(embed.build()).queue();
				}

				public void connect() {
					if (!manager.isConnected()) {
						manager.openAudioConnection(voiceState.getChannel());
						manager.setSendingHandler(playerHandlers.get(event.getGuild().getId()));
					}
				}
			});
		}
	}
}
