package be.christophebernard.thermostat.bot.common.utils;

import be.christophebernard.thermostat.Configuration;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;

import java.util.GregorianCalendar;

public class TREmbed {
    public enum EmbedType {
        MUSIC,
        TEXT,
        INFO,
        ADMIN,
        HR,
        EVENT;

        @Override
        public String toString() {
            return switch (this) {
                case MUSIC -> "Valve thermostatique musicale";
                case TEXT -> "Valve thermostatique textuelle";
                case INFO -> "Valve thermostatique générale";
                case ADMIN -> "Valve thermostatique administrative";
                case HR -> "Valve thermostatique des ressources humaines";
                case EVENT -> "Valve thermostatique événementielle";
            };
        }
    }

    public static EmbedBuilder newEmbed(EmbedType type, JDA client, User user) {
        EmbedBuilder embed = new EmbedBuilder()
                                     .setColor(Configuration.getInstance().get("embedColor"))
                                     .setTimestamp(new GregorianCalendar().toInstant())
                                     .setTitle(type.toString())
                                     .setFooter(
                                             client.getSelfUser().getName(),
                                             client.getSelfUser().getEffectiveAvatarUrl()
                                     );

        if (user != null) {
            embed.setAuthor(user.getName(), null, user.getEffectiveAvatarUrl());
        }

        return embed;
    }

    public static EmbedBuilder newEmbed(EmbedType type, JDA client) {
        return newEmbed(type, client, null);
    }
}
