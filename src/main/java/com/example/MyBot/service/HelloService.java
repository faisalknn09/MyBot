package com.example.MyBot.service;

import com.example.MyBot.model.CommandContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * HelloService - Handles the !hello command.
 *
 * This class represents a single feature.
 * As your bot grows, you'll have many service classes like this.
 *
 * Design principle: Each command = its own service class.
 * This is the Single Responsibility Principle (SRP) in action.
 */
@Service
public class HelloService {

    private static final Logger log = LoggerFactory.getLogger(HelloService.class);

    /**
     * Executes the !hello command.
     * Sends a personalized greeting back to whoever typed the command.
     *
     * @param context All information about who sent the command and where
     */
    public void execute(CommandContext context) {
        log.debug("👋 Executing !hello for user: {}", context.getAuthorName());

        // Build our reply string
        String response = String.format("Hello, **%s**! 👋 How can I help you today?",
                context.getAuthorName());

        // Send the message to Discord
        // .queue() = send asynchronously (non-blocking)
        // .complete() would block the thread waiting for Discord's response
        // Always prefer .queue() in bot development!
        context.getChannel()
                .sendMessage(response)
                .queue(
                        success -> log.debug("✅ Hello reply sent successfully"),
                        error   -> log.error("❌ Failed to send hello reply", error)
                );
    }
}
//!hello logic