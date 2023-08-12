package be.christophebernard.thermostat.bot.common;

import be.christophebernard.thermostat.bot.common.exceptions.CommandExecutionError;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public interface ICommandExecutor {
    void execute(SlashCommandInteractionEvent event) throws CommandExecutionError;
}
