package be.christophebernard.thermostat.bot.events;

import be.christophebernard.thermostat.bot.common.annotations.DiscordEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@DiscordEvent(name = "events")
public class BotReadyEventHandler extends ListenerAdapter {
	private final Logger logger;

	public BotReadyEventHandler() {
		logger = LoggerFactory.getLogger(getClass());
	}

	@Override
	public void onReady(@NotNull ReadyEvent event) {
		logger.info("All functions loaded, listening to events.");
	}
}
