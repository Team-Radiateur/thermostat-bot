package be.christophebernard.thermostat.bot.commands.information;

import be.christophebernard.thermostat.bot.common.ICommandExecutor;
import be.christophebernard.thermostat.bot.common.annotations.DiscordCommand;
import be.christophebernard.thermostat.bot.common.exceptions.CommandExecutionError;
import be.christophebernard.thermostat.bot.common.utils.KeyValuePair;
import be.christophebernard.thermostat.bot.common.utils.TREmbed;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.stream.Collectors;

@DiscordCommand(
		name = "help",
		description = "Affiche la liste des commandes disponibles ainsi que leur description",
	    ephemeral = true
)
public class HelpCommand implements ICommandExecutor {
	private final Logger logger;
	public Map<String, String> commands;

	public HelpCommand() {
		logger = LoggerFactory.getLogger(getClass());

		Reflections reflections = new Reflections(getClass().getPackageName());
		commands = reflections.getTypesAnnotatedWith(DiscordCommand.class)
		           .stream()
		           .map(command -> {
                       DiscordCommand annotation = command.getAnnotation(DiscordCommand.class);
		               return new KeyValuePair<>(annotation.name(), annotation.description());
		           })
                   .collect(
						   Collectors.toMap(
								   KeyValuePair::getKey,
								   KeyValuePair::getValue,
								   (a, b) -> a.compareTo(b) > 0 ? a : b
						   )
                   );
	}

	@Override
	public void execute(SlashCommandInteractionEvent event) throws CommandExecutionError {
		logger.info("Executing command `help`");

		EmbedBuilder embed = TREmbed.newEmbed(TREmbed.EmbedType.INFO, event.getJDA(), event.getUser());
		String helpMessage = commands.entrySet()
				                     .stream()
				                     .map(command -> "`/%s` - %s\n".formatted(command.getKey(), command.getValue()))
				                     .collect(
											 Collectors.joining(
													 "",
													 "Voici la liste des commandes disponibles:\n",
													 ""
											 )
				                     );

		event.getHook().editOriginalEmbeds(embed.setDescription(helpMessage).build()).queue();
	}
}
