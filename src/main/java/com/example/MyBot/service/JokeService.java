package com.example.MyBot.service;

import com.example.MyBot.model.CommandContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
public class JokeService {

    private static final Logger log = LoggerFactory.getLogger(JokeService.class);
    private static final Random random = new Random();

    private static final List<String> JOKES = List.of(
            "Why do Java developers wear glasses?\nBecause they don't C#! 😄",
            "Why did the programmer quit his job?\nBecause he didn't get arrays! 😂",
            "How many programmers does it take to change a light bulb?\nNone, that's a hardware problem! 💡",
            "Why do programmers prefer dark mode?\nBecause light attracts bugs! 🐛",
            "What's a computer's favorite snack?\nMicrochips! 🍟",
            "Why was the JavaScript developer sad?\nBecause he didn't Node how to Express himself! 😢",
            "What did the Java code say to the C code?\nYou've got no class! 😏",
            "Why do Python programmers prefer snake_case?\nBecause they can't C the difference! 🐍",
            "A SQL query walks into a bar, walks up to two tables and asks...\nCan I join you? 🍺",
            "Why did the developer go broke?\nBecause he used up all his cache! 💸"
    );

    public void execute(CommandContext context) {
        log.debug("😄 Joke requested by: {}", context.getAuthorName());

        String joke = JOKES.get(random.nextInt(JOKES.size()));

        context.getChannel().sendMessage("😂 **Here's a joke for you:**\n\n" + joke)
                .queue(
                        ok  -> log.debug("✅ Joke sent"),
                        err -> log.error("❌ Failed to send joke", err)
                );
    }
}