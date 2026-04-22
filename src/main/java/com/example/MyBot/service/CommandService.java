package com.example.MyBot.service;

import com.example.MyBot.exception.BotException;
import com.example.MyBot.exception.UnknownCommandException;
import com.example.MyBot.model.BotCommand;
import com.example.MyBot.model.CommandContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * CommandService - The central dispatcher.
 *
 * Receives parsed commands and routes them to specific services.
 * Handles ALL exceptions so errors are never propagated back to JDA's
 * event thread (which would crash the bot silently).
 *
 * @Service is the same as @Component but signals "this is business logic"
 * It's a semantic marker — Spring treats them identically.
 */
@Service
public class CommandService {

    private static final Logger log = LoggerFactory.getLogger(CommandService.class);

    private final HelloService helloService;
    private final AskService askService;
    private final HelpService helpService;
    private final JokeService jokeService;
    private final DefineService defineService;
    private final WeatherService weatherService;
    //private ForgetService forgetService;

    public CommandService(HelloService helloService, AskService askService, HelpService helpService,JokeService jokeService,DefineService defineService, WeatherService weatherService) {
        this.helloService = helloService;
        this.askService = askService;
        this.helpService = helpService;
        //this.forgetService=forgetService;
        this.defineService=defineService;
        this.jokeService=jokeService;
        this.weatherService=weatherService;
    }

    /**
     * Handle a command asynchronously.
     *
     * @Async = Spring creates a proxy around this method.
     * When called, instead of running on the caller's thread,
     * Spring submits it to the configured thread pool and returns immediately.
     *
     * WHY @Async here and not in each service?
     * Having it in ONE place means all commands are always async.
     * Services stay simple and synchronous — easier to test.
     */
    @Async
    public void handle(CommandContext context) {
        log.info("⚡ Processing command [{}] from user [{}]",
                context.getCommandName(), context.getAuthorName());

        try {
            // Convert the string command name to our BotCommand enum
            // BotCommand.fromString("hello") -> BotCommand.HELLO
            BotCommand command = BotCommand.fromString(context.getCommandName());

            // Route to correct service using a switch expression (Java 14+)
            switch (command) {
                case HELLO -> helloService.execute(context);
                case ASK   -> askService.execute(context);
                case HELP  -> helpService.execute(context);
                case JOKE    -> jokeService.execute(context);
                case DEFINE  -> defineService.execute(context);
                case WEATHER -> weatherService.execute(context);
                //case FORGET -> forgetService.execute(context);
                // Add new cases here as you add features
                default    -> throw new UnknownCommandException(context.getCommandName());
            }

        } catch (UnknownCommandException e) {
            log.warn("❓ Unknown command: {}", e.getMessage());
            sendErrorReply(context, "Unknown command: `!" + context.getCommandName() + "`. Try !hello or !ask");

        } catch (BotException e) {
            log.error("🔥 Bot error processing command [{}]: {}", context.getCommandName(), e.getMessage());
            sendErrorReply(context, "Something went wrong processing your command.");

        } catch (Exception e) {
            // Catch-all: NEVER let uncaught exceptions escape async methods
            // Spring's @Async silently swallows uncaught exceptions!
            log.error("💥 Unexpected error processing command [{}]", context.getCommandName(), e);
            sendErrorReply(context, "An unexpected error occurred. Please try again.");
        }
    }

    /**
     * Sends an error message back to the user's channel.
     * Wrapped in try-catch because Discord replies can also fail!
     */
    private void sendErrorReply(CommandContext context, String message) {
        try {
            context.getChannel().sendMessage("⚠️ " + message).queue();
        } catch (Exception e) {
            log.error("Failed to send error reply to channel", e);
        }
    }
}
    /**
    *Routes commands
    *@Async magic: When the listener calls commandService.handle(), Spring intercepts it and runs the method on a separate thread pool thread. The listener's thread returns immediately — Discord stays responsive even if !ask takes 2 seconds.
     */