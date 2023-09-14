package be.christophebernard.thermostat.bot.commands.administration.badwords;

import be.christophebernard.thermostat.bot.common.ICommandExecutor;
import be.christophebernard.thermostat.bot.common.annotations.DiscordCommand;
import be.christophebernard.thermostat.bot.common.exceptions.CommandExecutionError;
import be.christophebernard.thermostat.bot.common.utils.TREmbed;
import be.christophebernard.thermostat.database.MySQLConnector;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

@DiscordCommand(
		name = "ban_word",
		description = "Bannit un mot ou une expression du serveur",
		options = {
				"type: STRING|name: mot|description: Le mot ou l'expression à bannir|required: true",
		},
		adminOnly = true
)
public class AddBadWordCommand implements ICommandExecutor {
	private final Logger logger;
	private final MySQLConnector databaseConnector;

	public AddBadWordCommand() {
		logger = LoggerFactory.getLogger(getClass().getName());
		databaseConnector = MySQLConnector.getInstance();
	}

	@Override
	public void execute(SlashCommandInteractionEvent event) throws CommandExecutionError {
		OptionMapping wordPayload = event.getOption("mot");

		if (wordPayload == null) {
			// never happens because the option is required
			throw new CommandExecutionError("Vous devez spécifier un mot ou une expression à bannir");
		}

		String word = wordPayload.getAsString();

		logger.info("Command ban_word executed with `%s` as argument".formatted(word));

		if (!databaseConnector.addBadWord(Objects.requireNonNull(event.getGuild()).getId(), word)) {
			throw new CommandExecutionError(
					"Impossible d'ajouter le mot ou l'expression `%s` à la liste des mots bannis".formatted(word)
			);
		}

		EmbedBuilder embed = TREmbed
				.newEmbed(TREmbed.EmbedType.ADMIN, event.getJDA())
				.setTitle("Mot banni")
				.setDescription("Le mot ou l'expression `%s` a bien été interdit(e)".formatted(word));

		event.getHook().editOriginalEmbeds(embed.build()).queue();
	}
}
