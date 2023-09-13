package be.christophebernard.thermostat.bot.handlers;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Activity;

import java.util.Random;

public class ActivityStatusUpdateHandler implements Runnable {
	private JDA client;
	private Random random;

	public ActivityStatusUpdateHandler(JDA client) {
		this.client = client;
		random = new Random();
	}

	@Override
	public void run() {
		client.getPresence()
		      .setActivity(
					  Activity.watching("la température. %d°C".formatted(random.nextInt(60, 80)))
		      );
	}
}
