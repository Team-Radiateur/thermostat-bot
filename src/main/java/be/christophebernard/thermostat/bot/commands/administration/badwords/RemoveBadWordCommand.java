package be.christophebernard.thermostat.bot.commands.administration.badwords;

import be.christophebernard.thermostat.bot.common.ICommandExecutor;
import be.christophebernard.thermostat.bot.common.annotations.DiscordCommand;
import be.christophebernard.thermostat.bot.common.exceptions.CommandExecutionError;
import be.christophebernard.thermostat.bot.common.utils.TREmbed;
import be.christophebernard.thermostat.database.IDatabaseConnector;
import be.christophebernard.thermostat.database.MySQLConnector;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

@DiscordCommand(
		name = "permet_mot",
		description = "Désactive l'interdiction d'envoyer un mot ou une phrase",
		options = {
				"type: STRING|name: mot|description: Le mot ou la phrase à désactiver|required: true"
		},
		adminOnly = true
)
public class RemoveBadWordCommand implements ICommandExecutor {
	private final Logger logger;
	private final IDatabaseConnector connector;

	public RemoveBadWordCommand() {
		logger = LoggerFactory.getLogger(getClass());
		connector = MySQLConnector.getInstance();
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

		if (connector.getBadWords(Objects.requireNonNull(event.getGuild()).getId()).contains(word)) {
			connector.disableBadWord(event.getGuild().getId(), word);
		} else {
			EmbedBuilder embed = TREmbed.newEmbed(TREmbed.EmbedType.ADMIN, event.getJDA(), event.getUser())
					.setTitle("Mot non banni")
					.setDescription(
							"Le mot ou l'expression `%s` n'est pas présent dans la liste des mots interdits"
									.formatted(word)
					);

			event.getHook().editOriginalEmbeds(embed.build()).queue();
			return;
		}

		EmbedBuilder embed = TREmbed
				.newEmbed(TREmbed.EmbedType.ADMIN, event.getJDA(), event.getUser())
				.setTitle("Mot autorisé")
				.setDescription("Le mot ou l'expression `%s` a bien été autorisé(e)".formatted(word));

		event.getHook().editOriginalEmbeds(embed.build()).queue();
	}
}
