package be.christophebernard.thermostat.bot.common;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class TrackScheduler extends AudioEventAdapter {
	private final Logger logger;
	private final Queue<AudioTrack> queue;
	private final AudioPlayerManager playerManager;
	private final AudioPlayer player;
	private static TrackScheduler instance;

	private TrackScheduler() {
		super();

		logger = LoggerFactory.getLogger(getClass());
		playerManager = new DefaultAudioPlayerManager();
		queue = new ConcurrentLinkedQueue<>();
		player = playerManager.createPlayer();

		player.addListener(this);
		AudioSourceManagers.registerRemoteSources(playerManager);
	}

	public static TrackScheduler getInstance() {
		if (instance == null) {
			instance = new TrackScheduler();
		}

		return instance;
	}

	public void enqueue(AudioTrack... tracks) {
		queue.addAll(Arrays.asList(tracks));
	}

	public void clearQueue() {
		queue.clear();
	}

	public void play() {
		player.playTrack(queue.poll());
	}

	public void play(AudioTrack track) {
		player.playTrack(track);
	}

	public AudioTrack currentTrack() {
		return player.getPlayingTrack();
	}
	public boolean isPaused() {
		return player.isPaused();
	}

	public AudioTrack nextTrack() {
		return queue.peek();
	}

	public AudioTrack playNextTrack() {
		if (player.getPlayingTrack() != null) {
			player.stopTrack();
		}

		AudioTrack track = queue.poll();

		if (track != null) {
			player.playTrack(track);
		}

		return track;
	}

	public AudioPlayer getPlayer() {
		return player;
	}

	public Queue<AudioTrack> getQueue() {
		return queue;
	}

	public String nowPlaying() {
		StringBuilder description = new StringBuilder();

		if (player.getPlayingTrack() != null || player.isPaused()) {
			description.append("En cours de lecture : ")
			           .append(player.getPlayingTrack().getInfo().title)
			           .append(" (")
			           .append(player.getPlayingTrack().getInfo().author)
			           .append(")")
			           .append(player.isPaused() ? " (en pause)" : "");
		} else {
			description.append("Aucune musique en cours de lecture.");
		}

		if (queue.isEmpty()) {
			return "";
		}


		int i = 0;
		for (AudioTrack track : queue.stream().limit(9).toList()) {
			description.append("\n")
			           .append(i)
			           .append(") ")
			           .append(track.getInfo().title)
			           .append(" (")
			           .append(track.getInfo().author)
			           .append("),");
			i++;
		}

		if (queue.size() > 9) {
			description.append("\n et ")
			           .append(queue.size() - 9)
			           .append(" autres musiques.");
		}

		return description.toString();
	}

	public AudioPlayerManager getPlayerManager() {
		return playerManager;
	}

	@Override
	public void onTrackStart(AudioPlayer player, AudioTrack track) {
		logger.info("Now playing: `%s`".formatted(track.getInfo().title));
	}

	@Override
	public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
		if (endReason.mayStartNext) {
			logger.info("Song `%s` ended, playing the next one.".formatted(track.getInfo().title));

			if (queue.isEmpty()) {
				return;
			}
			player.playTrack(queue.poll());
		}
	}

	@Override
	public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception) {
		logger.warn("An error occurred while playing `%s`".formatted(track.getInfo().title), exception);
	}

	@Override
	public void onTrackStuck(AudioPlayer player, AudioTrack track, long thresholdMs) {
		logger.error("Song `%s` hung, playing next song.".formatted(track.getInfo().title));

		if (queue.isEmpty()) {
			return;
		}
		player.playTrack(queue.poll());
	}
}
