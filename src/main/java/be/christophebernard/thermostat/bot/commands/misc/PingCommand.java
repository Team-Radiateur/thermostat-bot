package be.christophebernard.thermostat.bot.commands.misc;

import be.christophebernard.thermostat.bot.common.ICommandExecutor;
import be.christophebernard.thermostat.bot.common.annotations.DiscordCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

@DiscordCommand(
        name = "ping",
        description = "Ping le bot",
        options = {
                "type: STRING|name: message|description: Le message à faire envoyer avec la latence|required: false"
        },
        ephemeral = true
)
public class PingCommand implements ICommandExecutor {
    @Override
    public void execute(SlashCommandInteractionEvent event)  {
        OptionMapping messageOption = event.getOption("message");
        String message = messageOption != null ? messageOption.getAsString() : "Pong!";

        message += "\n  " + event.getJDA().getGatewayPing() + "ms";

        event.reply(message).queue();
    }
}
