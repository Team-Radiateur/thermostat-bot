package be.christophebernard.thermostat.bot.commands.misc;

import be.christophebernard.thermostat.bot.common.ICommandExecutor;
import be.christophebernard.thermostat.bot.common.annotations.DiscordCommand;
import be.christophebernard.thermostat.bot.common.utils.TREmbed;
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

        event.getHook().editOriginalEmbeds(
                TREmbed.newEmbed(TREmbed.EmbedType.INFO, event.getJDA(), event.getUser())
                       .setDescription(
                               "%s\n  %dms".formatted(
                                       messageOption != null ? messageOption.getAsString() : "Pong!",
                                       event.getJDA().getGatewayPing()
                               )
                       )
                       .build()
        ).queue();
    }
}
