package be.christophebernard.thermostat.bot.commands.misc;

import be.christophebernard.thermostat.bot.common.ICommandExecutor;
import be.christophebernard.thermostat.bot.common.annotations.DiscordCommand;
import be.christophebernard.thermostat.bot.common.exceptions.CommandExecutionError;
import be.christophebernard.thermostat.bot.common.utils.TREmbed;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.util.List;

@DiscordCommand(
		name = "goélette",
		description = "Tague une personne (au hasard si pas d'option fournie) en disant \"Goélette au rapport !\"",
		options = {
				"type: USER|name: personne|description: La personne à taguer|required: false"
		}
)
public class GoeletteCommand implements ICommandExecutor {
	@Override
	public void execute(SlashCommandInteractionEvent event) throws CommandExecutionError {
		if (event.getGuild() == null || event.getMember() == null) {
			throw new CommandExecutionError("Commande non disponible en message privé.");
		}

		OptionMapping personOption = event.getOption("personne");
		Member person = personOption != null ? personOption.getAsMember() : null;

		if (person == null) {
			List<Member> members = event.getChannel().asTextChannel().getMembers();

			person = members.get((int) (Math.random() * members.size()));
		}

		event.getMessageChannel().sendMessageEmbeds(
				TREmbed.newEmbed(TREmbed.EmbedType.INFO, event.getJDA(), event.getUser())
				       .setDescription("Goélette au rapport ! %s".formatted(person.getAsMention()))
				       .build()
		).mention(person).queue();

		event.getHook().deleteOriginal().queue();
	}
}
