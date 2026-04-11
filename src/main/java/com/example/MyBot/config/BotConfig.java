package com.example.MyBot.config;

import com.example.MyBot.listener.MessageListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * BotConfig - The "factory" for our Discord connection.
 *
 * @Configuration  = This class defines Spring Beans
 * @EnableAsync    = Allows methods marked @Async to run on separate threads
 *
 * Design choice: Keeping JDA creation here (not in main) keeps
 * the entry point clean and makes the bot testable.
 */
@Configuration
@EnableAsync
public class BotConfig {

    private static final Logger log = LoggerFactory.getLogger(BotConfig.class);

    // Reads discord.bot.token from application.yml (or env variable)
    @Value("${discord.bot.token}")
    private String botToken;

    @Value("${discord.bot.status}")
    private String botStatus;

    /**
     * Creates the JDA instance — our live connection to Discord.
     *
     * @Bean = Spring manages this object's lifecycle
     * @throws Exception if token is invalid or connection fails
     *
     * WHY inject MessageListener here?
     * JDA needs listeners registered BEFORE it connects.
     * Spring creates MessageListener first (it's a @Component),
     * then passes it here to register with JDA.
     */
    @Bean
    public JDA jda(MessageListener messageListener) throws Exception {
        log.info("🤖 Initializing Discord bot connection...");

        JDA jda = JDABuilder.createDefault(botToken)
                // GATEWAY INTENTS: Tell Discord what events we want to receive
                // MESSAGE_CONTENT is required to read message text (privileged intent)
                // Must be enabled in Discord Developer Portal too!
                .enableIntents(
                        GatewayIntent.GUILD_MESSAGES,
                        GatewayIntent.MESSAGE_CONTENT,
                        GatewayIntent.DIRECT_MESSAGES
                )
                // Register our event listener
                .addEventListeners(messageListener)
                // Set bot's status shown in Discord
                .setStatus(OnlineStatus.ONLINE)
                .setActivity(Activity.watching(botStatus))
                // Disable compression for simpler debugging
                .setCompression(net.dv8tion.jda.api.utils.Compression.NONE)
                .build();

        // Wait until JDA is fully connected before returning
        // This prevents race conditions at startup
        jda.awaitReady();
        log.info("✅ Bot connected as: {}", jda.getSelfUser().getName());

        return jda;
    }
}
//JDA bean, async config
/*What this does: Creates the JDA (Discord connection) as a Spring Bean. Spring manages its lifecycle — it's created once on startup and injected wherever needed. Also enables async processing for all @Async methods.*/