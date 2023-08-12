package be.christophebernard.thermostat.bot.handlers;

import be.christophebernard.thermostat.bot.common.ICommandExecutor;
import be.christophebernard.thermostat.bot.common.utils.TREmbed;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class CommandHandler extends ListenerAdapter {
    private final Hashtable<String, ICommandExecutor> commands;
    private final Hashtable<String, ICommandExecutor> adminCommands;
    private final List<ICommandExecutor> allCommands;
    private final Logger logger;

    public CommandHandler() {
        commands = new Hashtable<>();
        adminCommands = new Hashtable<>();
        allCommands = new ArrayList<>();
        logger = LoggerFactory.getLogger(getClass());
    }

    public void registerCommand(String name, ICommandExecutor command) {
       registerCommand(name, command, false);
    }

    public void registerCommand(String name, ICommandExecutor command, boolean adminOnly) {
        (adminOnly ? adminCommands : commands).put(name, command);
        allCommands.add(command);
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        event.deferReply().queue();

        if (event.getUser().isBot() || event.getMember() == null) {
            EmbedBuilder embed = TREmbed.newEmbed(TREmbed.EmbedType.INFO, event.getJDA(), event.getUser())
                                         .appendDescription("Vous ne pouvez pas utiliser de commandes.");

            event.getHook().editOriginalEmbeds(embed.build()).queue();
            return;
        }

        String commandName = event.getName();

        if (!commands.containsKey(commandName) && !adminCommands.containsKey(commandName)) {
            logger.info(event.getMember().toString() + " a utilisé une commande inconnue: " + commandName);

            EmbedBuilder embed = TREmbed.newEmbed(
                    TREmbed.EmbedType.INFO,
                    event.getJDA(),
                    event.getUser()
            ).appendDescription("Commande inconnue.");

            event.getHook().editOriginalEmbeds(embed.build()).queue();
            return;
        }

        if (adminCommands.containsKey(commandName)) {
            if (!event.getMember().hasPermission(Permission.VOICE_MUTE_OTHERS)) {
                EmbedBuilder embed = TREmbed.newEmbed(
                        TREmbed.EmbedType.INFO,
                        event.getJDA(),
                        event.getUser()
                ).appendDescription("Vous n'avez pas la permission d'utiliser cette commande.");

                event.getHook().editOriginalEmbeds(embed.build()).queue();
                return;
            }

            adminCommands.get(commandName).execute(event);
        } else if (commands.containsKey(commandName)) {
            commands.get(commandName).execute(event);
        }
    }
}
