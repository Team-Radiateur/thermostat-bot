package be.christophebernard.thermostat.bot.common.annotations;

import net.dv8tion.jda.api.Permission;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DiscordCommand {
    @NotNull String name();
    @NotNull String description();
    @NotNull String[] aliases() default {};
    @NotNull Permission[] permissions() default {};
    @NotNull String[] options() default {};
    boolean adminOnly() default false;
    boolean ephemeral() default false;
}
