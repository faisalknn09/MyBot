package com.example.MyBot.listener;

import com.example.MyBot.model.CommandContext;
import com.example.MyBot.service.CommandService;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * MessageListener - The Gateway Layer.
 *
 * Extends ListenerAdapter so we only override the methods we need.
 * JDA calls onMessageReceived() every time ANYONE sends a message
 * in any channel the bot can see.
 *
 * @Component = Spring will create this as a bean and inject it.
 * We register it manually in BotConfig.jda() because JDA
 * needs it at connection time, not at Spring startup time.
 */
@Component
public class MessageListener extends ListenerAdapter {

    private static final Logger log = LoggerFactory.getLogger(MessageListener.class);

    private final CommandService commandService;

    @Value("${discord.bot.prefix}")
    private String prefix;

    @Value("${discord.bot.ignore-bots}")
    private boolean ignoreBots;

    // Constructor injection — preferred over @Autowired on field
    // Reason: makes dependencies explicit, easier to test
    public MessageListener(CommandService commandService) {
        this.commandService = commandService;
    }

    /**
     * Called by JDA for EVERY message received.
     * This method runs on JDA's event thread — keep it fast!
     * Heavy work must be delegated async (CommandService handles that).
     */
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {

        // Guard 1: Ignore messages from bots (prevents bot loops!)
        if (ignoreBots && event.getAuthor().isBot()) {
            return;
        }

        String rawMessage = event.getMessage().getContentRaw().trim();

        // Guard 2: Only process messages starting with our prefix "!"
        if (!rawMessage.startsWith(prefix)) {
            return; // Not a command, ignore silently
        }

        log.debug("📨 Command received from [{}] in [{}]: {}",
                event.getAuthor().getName(),
                event.getChannel().getName(),
                rawMessage
        );

        // Parse the raw message into a structured CommandContext object
        // "!ask what is Java?" -> CommandContext{command="ask", args="what is Java?", ...}
        CommandContext context = parseCommand(rawMessage, event);

        // Delegate to service layer — this is async so it returns immediately
        // The JDA event thread is freed; actual processing happens on bot-async-* threads
        commandService.handle(context);
    }

    /**
     * Parses raw message string into a CommandContext.
     *
     * "!hello"           -> command="hello", args=""
     * "!ask how are you" -> command="ask",   args="how are you"
     */
    private CommandContext parseCommand(String rawMessage, MessageReceivedEvent event) {
        // Remove the prefix "!" from the start
        String withoutPrefix = rawMessage.substring(prefix.length()).trim();

        // Split into command name and arguments
        String[] parts = withoutPrefix.split("\\s+", 2); // max 2 parts
        String commandName = parts[0].toLowerCase();
        String args = parts.length > 1 ? parts[1] : "";

        return CommandContext.builder()
                .commandName(commandName)
                .args(args)
                .authorName(event.getAuthor().getName())
                .authorId(event.getAuthor().getId())
                .channel(event.getChannel().asTextChannel())
                .guild(event.isFromGuild() ? event.getGuild() : null)
                .build();
    }
}
//JDA event listener