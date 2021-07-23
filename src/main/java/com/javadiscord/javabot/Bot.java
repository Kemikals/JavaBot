package com.javadiscord.javabot;

import com.javadiscord.javabot.events.*;
import com.javadiscord.javabot.properties.MultiProperties;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import java.nio.file.Path;
import java.util.Properties;

/**
 * The main class where the bot is initialized.
 */
public class Bot {
    /**
     * Loads the bot properties, first from the internal classpath properties
     * file, and then any properties file in the current working directory will
     * take precedence over that.
     */
    private static final Properties properties = new MultiProperties(
        MultiProperties.getClasspathResource("bot.properties").orElseThrow(),
        Path.of("bot.props")
    );

    /**
     * A reference to the slash command listener that's the main point of
     * interaction for users with this bot. It's marked as a publicly accessible
     * reference so that {@link SlashCommands#registerSlashCommands(Guild)} can
     * be called wherever it's needed.
     */
    public static SlashCommands slashCommands;

    /**
     * The main method that starts the bot. This involves a few steps:
     * <ol>
     *     <li>Initializing the {@link SlashCommands} listener (which reads command data from a YAML file).</li>
     *     <li>Creating and configuring the {@link JDA} instance that enables the bot's Discord connectivity.</li>
     *     <li>Adding event listeners to the bot.</li>
     * </ol>
     * @param args Command-line arguments.
     * @throws Exception If any exception occurs during bot creation.
     */
    public static void main(String[] args) throws Exception {
        slashCommands = new SlashCommands();

        JDA jda = JDABuilder.createDefault(properties.getProperty("token", "null"))
                .setStatus(OnlineStatus.DO_NOT_DISTURB)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .enableCache(CacheFlag.ACTIVITY)
                .enableIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_PRESENCES)
                .addEventListeners(slashCommands)
                .build();
        addEventListeners(jda);
    }

    /**
     * Adds all of the bot's event listeners to the JDA instance, except for the
     * main {@link SlashCommands} listener.
     * @param jda The JDA bot instance to add listeners to.
     */
    private static void addEventListeners(JDA jda) {
        jda.addEventListener(new GuildJoin());
        jda.addEventListener(new UserJoin());
        jda.addEventListener(new UserLeave());
        jda.addEventListener(new Startup());
        jda.addEventListener(PresenceUpdater.standardActivities());
        jda.addEventListener(new SuggestionListener());
        jda.addEventListener(new AutoMod());
        jda.addEventListener(new SubmissionListener());
        jda.addEventListener(new StarboardListener());
    }

    /**
     * Gets the value of a property from the bot's loaded properties.
     * @see Properties#getProperty(String)
     * @param key The name of the property to get.
     * @return The value of the property, or <code>null</code> if none was found.
     */
    public static String getProperty(String key) {
        return properties.getProperty(key);
    }

    /**
     * Gets the value of a property from the bot's loaded properties.
     * @see Properties#getProperty(String, String)
     * @param key The name of the property to get.
     * @param defaultValue The value to return if no property was found.
     * @return The value of the property, or the default value.
     */
    public static String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
}

