package com.example.MyBot.service;

import com.example.MyBot.model.CommandContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class HelpService {

    private static final Logger log = LoggerFactory.getLogger(HelpService.class);

    public void execute(CommandContext context) {
        log.debug("📋 Help requested by: {}", context.getAuthorName());

        String help = """
    📖 **Chintu Bot — Commands**
    
        `!hello` — Say hello to the bot
        `!ask <question>` — Ask the AI anything
        `!joke` — Get a random programming joke
        `!define <word>` — Get the definition of a word
        `!weather <city>` — Get current weather
        `!forget` — Clear your conversation memory
        `!help` — Show this help menu
        
        💡 **Examples:**
        `!ask what is recursion?`
        `!define ephemeral`
        `!weather Karachi`
        `!joke`
    """;

        context.getChannel().sendMessage(help).queue(
                ok  -> log.debug("✅ Help sent to {}", context.getAuthorName()),
                err -> log.error("❌ Failed to send help", err)
        );
    }
}